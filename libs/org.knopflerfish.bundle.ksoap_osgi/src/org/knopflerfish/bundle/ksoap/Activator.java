/*
 * Copyright (c) 2003-2004, KNOPFLERFISH project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above/*
 * Copyright (c) 2003-2004, KNOPFLERFISH project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 *
 * - Neither the name of the KNOPFLERFISH project nor the names of its
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.knopflerfish.bundle.ksoap;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.knopflerfish.ksoap.ObjectSOAPService;
import org.knopflerfish.service.ksoap.KSoapAdmin;
import org.knopflerfish.util.servlet.ServletDescriptor;
import org.knopflerfish.util.servlet.WebApp;
import org.knopflerfish.util.servlet.WebAppDescriptor;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.MarshalHashtable;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.servlet.SoapServlet;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/** The <code>Activator</code> is the activator for the SOAP OSGi bundle.
 *  Further it handles service registration events for SOAP services.
 * @author Lasse Helander (lars-erik.helander@home.se)
 */
public class Activator implements BundleActivator, ServiceListener {

   public static BundleContext bc = null;
   private static ServiceTracker log = null;
   private static SoapServlet soapServlet = null;
   private WebApp webApp = null;

   private KSoapAdminImpl admin;

   public static SoapServlet getSoapServlet() {
      return soapServlet;
   }

   public void start(BundleContext bc) throws BundleException {
      try {
         log = new ServiceTracker(bc, LogService.class.getName(), null); //new LogRef(bc, true);
         log.open();
         Activator.bc = bc;
         setup();
      } catch (Exception e) {
        logError("Exception when starting bundle", e);
        throw new BundleException("Failed to start server");
      }
   }

  void setup() throws Exception {
    ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();

    try {
      soapServlet = new ServicesServlet();
      SoapSerializationEnvelope envelope = soapServlet.getEnvelope();
      new MarshalHashtable().register(envelope);
      new MarshalBase64().register(envelope);

      logInfo("SOAP server started.");
      webApp = new WebApp(getWebAppDescriptor());
      webApp.start(bc);
      logInfo("Web application started.");
      bc.addServiceListener(this);

      // Make sure we get services already registered
      ServiceReference[] srl = bc.getServiceReferences(null, null);
      for(int i = 0; srl != null && i < srl.length; i++) {
        serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, srl[i]));
      }

      admin = new KSoapAdminImpl(this);

      Hashtable props = new Hashtable();
      props.put(KSoapAdmin.SOAP_SERVICE_NAME, "soapadmin");

      bc.registerService(KSoapAdmin.class.getName(), admin, props);
    } finally {
      Thread.currentThread().setContextClassLoader(oldLoader);
    }
   }

   public void stop(BundleContext bc)
             throws BundleException {
      try {
         this.bc.removeServiceListener(this);
         webApp.stop(bc);
         webApp = null;
         this.bc = null;
         //log.close();
         log = null;
      } catch (Exception e) {
         //log.error("Exception when stopping bundle", e);
         throw new BundleException("Failed to stop server", e);
      }
   }

   private WebAppDescriptor getWebAppDescriptor() {
      WebAppDescriptor wad = new WebAppDescriptor();

      wad.servlet = new ServletDescriptor[1];
      wad.context = "/soap";
      wad.servlet[0] = new ServletDescriptor("/services",
                                             getSoapServlet());
      return wad;
   }


  Map exportedServices = new HashMap();

  public void serviceChanged(ServiceEvent event) {
    try {
      switch(event.getType()) {
      case ServiceEvent.REGISTERED:
        {
          ServiceReference sr = event.getServiceReference();
          String serviceName = (String) sr.getProperty(KSoapAdmin.SOAP_SERVICE_NAME);
          String[] classes   = (String[]) sr.getProperty(Constants.OBJECTCLASS);
          String   allowedMethods   = (String) sr.getProperty(KSoapAdmin.SOAP_SERVICE_METHODS);
          if (serviceName != null) {
            logInfo("added service "+serviceName);

            // throws excpetion if name is invalid
            assertServiceName(serviceName);

            Object serviceObj = bc.getService(sr);
            ObjectSOAPService oss = new ObjectSOAPService(null, serviceName, serviceObj, classes, allowedMethods);
            exportedServices.put(sr, oss);
            
            soapServlet.publishInstance("/" + serviceName, serviceObj);
            soapServlet.publishClass(serviceObj.getClass(), null);
          }
        }
        break;
        case ServiceEvent.UNREGISTERING:
          {
            ServiceReference sr = event.getServiceReference();
            String serviceName  = (String) sr.getProperty(KSoapAdmin.SOAP_SERVICE_NAME);
            if (serviceName != null) {

              ObjectSOAPService soapService
                = (ObjectSOAPService)exportedServices.get(sr);
              if(soapService != null) {
                Object serviceObj = soapService.getServiceObject();
                
              }
            }
          }
          break;
      }
    } catch (Exception e) {
        logError("serviceChanged() failed", e);
    }
  }

  /**
   * Check if service name is OK for publishing as SOAP service.
   *
   * This incluced checking for previous registrations at the same name.
   *
   * @throws IllegalArgumentException if name is not valid
   */
  void assertServiceName(String serviceName) {
    if(serviceName == null) {
      throw new IllegalArgumentException("Service name cannot be null");
    }
    if("".equals(serviceName)) {
      throw new IllegalArgumentException("Service name cannot be empty string");
    }

    for(int i = 0; i < serviceName.length(); i++) {
      if(Character.isWhitespace(serviceName.charAt(i))) {
        throw new IllegalArgumentException("Service name '" + serviceName +
                                           "' cannot contain whitespace");
      }
    }

    synchronized(exportedServices) {
      for(Iterator it = exportedServices.keySet().iterator(); it.hasNext();) {
        ServiceReference sr         = (ServiceReference)it.next();
        String           name       = (String)sr.getProperty(KSoapAdmin.SOAP_SERVICE_NAME);
        if(name.equals(serviceName)) {
          throw new IllegalArgumentException("Service '" + name +
                                             "' is already exported");
        }
      }
    }
  }
  
  public static void logInfo(String msg) {
      LogService l = (LogService) log.getService();
      
      if (l != null) { 
          l.log(LogService.LOG_INFO, msg);
      } else {
          System.out.println(msg);
      }
  }
  
  public static void logError(String msg, Throwable e) {
      LogService l = (LogService) log.getService();
      
      if (l != null) { 
          l.log(LogService.LOG_ERROR, msg, e);
      } else {
          System.err.println(msg);
          e.printStackTrace();
      }
  }
}

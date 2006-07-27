/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 10, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.remoting.frontend;

import java.util.Dictionary;
import java.util.Hashtable;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.LocalCIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;
import org.cishell.reference.remoting.CIShellServer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.MetaTypeProvider;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class CIShellServerAlg implements AlgorithmFactory, AlgorithmProperty {
    private Dictionary props;
    private ServiceRegistration reg;
    private BundleContext bContext;
    private CIShellServer server;

    protected void activate(ComponentContext ctxt) {
        bContext = ctxt.getBundleContext();
        
        props = new Hashtable();
        props.put(LABEL, "Start Server");
        props.put(DESCRIPTION, "Start a CIShell Server");
        props.put(MENU_PATH, "file/additions");
        props.put(Constants.SERVICE_PID, getClass().getName());
        
        reg = bContext.registerService(AlgorithmFactory.class.getName(), this, props);

        if ("true".equals(System.getProperty("org.cishell.remoting.server"))) {
            CIShellContext ciContext = new LocalCIShellContext(bContext);
            startServer(ciContext);
        }
    }
    protected void deactivate(ComponentContext ctxt) {
        if (server != null) {
            stopServer(new LocalCIShellContext(ctxt.getBundleContext()));
        }
        reg.unregister();
    }
    
    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createAlgorithm(org.cishell.framework.data.Data[], java.util.Dictionary, org.cishell.framework.CIShellContext)
     */
    public Algorithm createAlgorithm(Data[] dm, Dictionary parameters,
            final CIShellContext context) {
        
        Algorithm algorithm = new Algorithm() {
            public Data[] execute() {
                if (server == null) {
                    startServer(context);
                } else {
                    stopServer(context);
                }
                
                return null;
            }};
        
        return algorithm;
    }
    
    private void startServer(CIShellContext context) {
        server = new CIShellServer();
        server.start(bContext, context);
        
        String port = System.getProperty("org.osgi.service.http.port");
        log(context,"CIShell Server started on port "+port+".");
        
        props.put(LABEL, "Stop Server");
        reg.setProperties(props);
    }
    
    private void stopServer(CIShellContext context) {
        server.stop();
        server = null;
        
        log(context,"CIShell Server stopped (not really)");
        
        props.put(LABEL, "Start Server");
        reg.setProperties(props);
    } 
    
    private void log(CIShellContext context, String msg) {
        LogService log = (LogService)context.getService(LogService.class.getName());
        
        log.log(LogService.LOG_INFO, msg);        
    }

    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createParameters(org.cishell.framework.data.Data[])
     */
    public MetaTypeProvider createParameters(Data[] dm) {
        return null;
    }}

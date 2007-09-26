package org.cishell.reference.scripting;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.LocalCIShellContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PySystemState;
import org.python.util.InteractiveConsole;

public class Activator implements BundleActivator {

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext context) throws Exception {
        CIShellContext ciContext = new LocalCIShellContext(context);
        
        LogListener listener = new LogListener() {
            public void logged(LogEntry e) {
                if (goodMessage(e.getMessage())) {
                    System.out.println(e.getMessage());
                }
            }
            
            public boolean goodMessage(String msg) {
                if (msg == null || 
                        msg.startsWith("ServiceEvent ") || 
                        msg.startsWith("BundleEvent ") || 
                        msg.startsWith("FrameworkEvent ")) {
                    return false;
                } else {
                    return true;   
                }
            }
        };
        
        ServiceReference ref = context.getServiceReference(LogReaderService.class.getName());
        LogReaderService reader = (LogReaderService) context.getService(ref);
        if (reader != null) {
            reader.addLogListener(listener);
        }
        
        Py.initPython();
        PySystemState sys = Py.getSystemState();
        sys.setClassLoader(this.getClass().getClassLoader());
        
        Dictionary headers = context.getBundle().getHeaders();
        String importPackages = (String)headers.get("Import-Package");
        if(importPackages!=null) {
            String[] result = importPackages.split(",");
            for(int i=0; i<result.length; i++) {
                String[] extra = result[i].split(";");
                
                //if there are extra ;version="blah" options
                //then get rid of them
                if (extra != null && extra.length > 1) {
                    result[i] = extra[0];
                }
                
                PySystemState.add_package(result[i].trim());
            }  
        }
        
        InteractiveConsole interp = new InteractiveConsole();
        //ReadlineConsole interp = new ReadlineConsole();
        
        interp.exec("import sys");
        interp.exec("def exit(): sys.exit()");
        interp.set("bContext", context);
        interp.set("ciContext", ciContext);

        try {
            String version = headers.get("Bundle-Version").toString();
            interp.interact("CIShell Console v" + version + "\n");
        } catch (PyException e) {}
        
        ref = context.getServiceReference(LogReaderService.class.getName());
        reader = (LogReaderService) context.getService(ref);
        if (reader != null) {
            reader.removeLogListener(listener);
        }
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception { }
}

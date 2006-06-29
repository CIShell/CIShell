package org.cishell.compatibility;

import java.util.Hashtable;

import org.cishell.compatibility.log.OSGiLogListener;
import org.cishell.compatibility.menu.MenuAdapter;
import org.cishell.framework.CIShellContext;
import org.cishell.framework.LocalCIShellContext;
import org.cishell.reference.service.conversion.DataConversionServiceImpl;
import org.cishell.service.conversion.DataConversionService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;

import edu.iu.iv.gui.IVCApplication;

public class Activator {
    private BundleContext bContext;
    private CIShellContext ciContext;
    private MenuAdapter menuAdapter;
    private OSGiLogListener logListener;
    private ServiceRegistration conversionReg;

    protected void activate(ComponentContext ctxt) {
        this.bContext = ctxt.getBundleContext();
        this.ciContext = new LocalCIShellContext(bContext);
        
        this.logListener = new OSGiLogListener(this.bContext, ciContext);

        //delay a bit so old cishell can create its UI
        do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (IVCApplication.getShell() == null);
        
        this.menuAdapter = new MenuAdapter(IVCApplication.getMenuManager(),
                IVCApplication.getShell(), this.bContext, ciContext);
        
        DataConversionService conversionService = 
                new DataConversionServiceImpl(bContext, ciContext);
        conversionReg = bContext.registerService(
                DataConversionService.class.getName(), conversionService, new Hashtable());
    }
    
    protected void deactivate(ComponentContext ctxt) {
        menuAdapter.stop();
        logListener.stop();
        conversionReg.unregister();
        
        bContext = null;
        ciContext = null;
        menuAdapter = null;
        logListener = null;
        conversionReg = null;
    }
}

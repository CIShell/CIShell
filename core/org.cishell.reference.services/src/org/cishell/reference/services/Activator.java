package org.cishell.reference.services;

import java.util.Hashtable;

import org.cishell.app.service.datamanager.DataManagerService;
import org.cishell.app.service.scheduler.SchedulerService;
import org.cishell.framework.CIShellContext;
import org.cishell.framework.LocalCIShellContext;
import org.cishell.reference.app.service.datamanager.DataManagerServiceImpl;
import org.cishell.reference.app.service.scheduler.SchedulerServiceImpl;
import org.cishell.reference.service.conversion.DataConversionServiceImpl;
import org.cishell.service.conversion.DataConversionService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {
    private ServiceRegistration conversionReg;
    private ServiceRegistration schedulerReg;
    private ServiceRegistration dataManagerReg;

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bContext) throws Exception {
        CIShellContext ciContext = new LocalCIShellContext(bContext);
        
        DataConversionService conversionService = 
            new DataConversionServiceImpl(bContext, ciContext);
        conversionReg = bContext.registerService(
                DataConversionService.class.getName(), conversionService, new Hashtable());
        
        SchedulerService scheduler = new SchedulerServiceImpl();
        schedulerReg = bContext.registerService(
                SchedulerService.class.getName(), scheduler, new Hashtable());

        DataManagerService dataManager = new DataManagerServiceImpl();
        dataManagerReg = bContext.registerService(
                DataManagerService.class.getName(), dataManager, new Hashtable());
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bContext) throws Exception {
        conversionReg.unregister();
        schedulerReg.unregister();
        dataManagerReg.unregister();
	}
}

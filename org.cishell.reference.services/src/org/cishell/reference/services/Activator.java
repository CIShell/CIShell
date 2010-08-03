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
    private ServiceRegistration conversionRegistration;
    private ServiceRegistration schedulerRegistration;
    private ServiceRegistration dataManagerRegistration;
//    private ServiceRegistration algorithmInvokerRegistration;

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
        CIShellContext ciShellContext = new LocalCIShellContext(bundleContext);
        
        DataConversionService conversionService =
        	new DataConversionServiceImpl(bundleContext, ciShellContext);
        this.conversionRegistration = bundleContext.registerService(
        	DataConversionService.class.getName(),
        	conversionService,
        	new Hashtable<String, Object>());
        
        SchedulerService scheduler = new SchedulerServiceImpl();
        this.schedulerRegistration = bundleContext.registerService(
        	SchedulerService.class.getName(), scheduler, new Hashtable<String, Object>());

        DataManagerService dataManager = new DataManagerServiceImpl();
        this.dataManagerRegistration = bundleContext.registerService(
        	DataManagerService.class.getName(), dataManager, new Hashtable<String, Object>());

//        AlgorithmInvocationService algorithmInvoker = new AlgorithmInvocationServiceImpl();
//        this.algorithmInvokerRegistration = bundleContext.registerService(
//        	AlgorithmInvocationService.class.getName(),
//        	algorithmInvoker,
//        	new Hashtable<String, Object>());
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		this.conversionRegistration.unregister();
		this.schedulerRegistration.unregister();
		this.dataManagerRegistration.unregister();
//		this.algorithmInvokerRegistration.unregister();
	}
}

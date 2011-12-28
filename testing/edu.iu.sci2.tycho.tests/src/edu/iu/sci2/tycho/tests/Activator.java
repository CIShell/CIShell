package edu.iu.sci2.tycho.tests;

import java.util.Arrays;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.LocalCIShellContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}
	
	static CIShellContext getCIShellContext() {
		return new LocalCIShellContext(getContext());
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		startAutoStartService();
	}

	private void startAutoStartService() throws BundleException {
		Bundle[] bundles = getContext().getBundles();
		Bundle autoStartBundle = null;
		for (Bundle b : bundles) {
			System.err.println(b.getSymbolicName() + " " + b.getState());
			if (b.getSymbolicName().equals("org.cishell.service.autostart")) {
				System.out.println("Found autostart service");
				autoStartBundle = b;
			}
			
			if (b.getSymbolicName().contains("nwb")) {
				ServiceReference[] services = b.getRegisteredServices();
				String serviceString = services == null ? "(none)" : Arrays.asList(services).toString();
				System.out.println(String.format("\n%s: %s\n Services %s",
						b.getSymbolicName(), b.getHeaders().keys(), 
						serviceString));
			}
		}
		System.err.println("AFTER------------------------------------");
		if (autoStartBundle == null) {
			throw new IllegalStateException("I require an autostart bundle!  SO there!");
		}
		autoStartBundle.start();
		for (Bundle b : bundles) {
			System.err.println(b.getSymbolicName() + " " + b.getState());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}

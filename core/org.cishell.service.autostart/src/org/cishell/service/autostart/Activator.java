package org.cishell.service.autostart;

import java.util.Dictionary;
import java.util.Enumeration;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;

public class Activator implements BundleActivator, BundleListener {

	/**
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
        context.addBundleListener(this);
        Bundle[] bundles = context.getBundles();
        
        for (int i=0; i < bundles.length; i++) {
            if (bundles[i].getState() == Bundle.RESOLVED)
                startBundle(bundles[i]);
        }
	}

	/**
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
        context.removeBundleListener(this);
	}


    /**
     * @see org.osgi.framework.BundleListener#bundleChanged(org.osgi.framework.BundleEvent)
     */
    public void bundleChanged(BundleEvent e) {
        if (e.getType() == BundleEvent.RESOLVED) {
            startBundle(e.getBundle());
        }
    }
    
    private void startBundle(Bundle bundle) {
        Dictionary header = bundle.getHeaders();

        for (Enumeration iter = header.keys(); iter.hasMoreElements(); ) {
            String key = iter.nextElement().toString();
            
            if ("x-autostart".equalsIgnoreCase(key) && 
                    "true".equals(header.get(key))) {
                try {
                    bundle.start();
                } catch (BundleException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}

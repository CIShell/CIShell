package org.cishell.reference.gui.persistence.load;

import java.util.Dictionary;
import java.util.Hashtable;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentContext;

public class FileLoadFactory implements AlgorithmFactory, ManagedService {
    private BundleContext bundleContext;
    private Dictionary properties = new Hashtable();

    protected void activate(ComponentContext componentContext) {
        bundleContext = componentContext.getBundleContext();
    }
    
    public Algorithm createAlgorithm(
    		Data[] data, Dictionary parameters, CIShellContext ciShellContext) {
        return new FileLoadAlgorithm(ciShellContext, this.bundleContext, this.properties);
    }

	public void updated(Dictionary properties) throws ConfigurationException {
		if (properties != null) {
			this.properties = properties;
		}
		//printPreferences(properties);
	}
	
//	private void printPreferences(Dictionary properties) {
//		System.out.println("  Preferences are as follows for File Load:");
//		if (properties == null) {
//			System.out.println("    Dictionary is null!");
//		} else {
//			Enumeration propertiesKeys = properties.keys();
//			
//			while (propertiesKeys.hasMoreElements()) {
//				String propertiesKey = (String) propertiesKeys.nextElement();
//				
//				Object propertiesValue = properties.get(propertiesKey);
//				System.out.println("    " + propertiesKey + ":" + propertiesValue);
//			}
//		}
//	}
}
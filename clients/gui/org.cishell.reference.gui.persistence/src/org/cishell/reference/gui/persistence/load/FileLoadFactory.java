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

/* 
 * @author Weixia(Bonnie) Huang (huangb@indiana.edu)
 */
public class FileLoadFactory implements AlgorithmFactory, ManagedService {
    private BundleContext bcontext;
    private Dictionary properties = new Hashtable();

    protected void activate(ComponentContext ctxt) {
        bcontext = ctxt.getBundleContext();
    }
    
    public Algorithm createAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
        return new FileLoad(context, bcontext, properties);
    }

	public void updated(Dictionary properties) throws ConfigurationException {
		if (properties != null) {
			this.properties = properties;
		}
		//printPreferences(properties);
	}
	
//	private void printPreferences(Dictionary properties) {
//			System.out.println("  Preferences are as follows for File Load:");
//			if (properties == null) {
//				System.out.println("    Dictionary is null!");
//			} else {
//				Enumeration propertiesKeys = properties.keys();
//				
//				while (propertiesKeys.hasMoreElements()) {
//					String propertiesKey = (String) propertiesKeys.nextElement();
//					
//					Object propertiesValue = properties.get(propertiesKey);
//					System.out.println("    " + propertiesKey + ":" + propertiesValue);
//				}
//			}
//		}
}
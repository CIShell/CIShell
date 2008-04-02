package org.cishell.testalgorithm3;

import java.util.Dictionary;
import java.util.Enumeration;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.MetaTypeService;


public class TestAlgorithmFactory implements AlgorithmFactory, ManagedService {
    private MetaTypeProvider provider;
    private Dictionary preferences;

    protected void activate(ComponentContext ctxt) {
    //	System.out.println("TestAlgorithm 3 beginning activation");
        //You may delete all references to metatype service if 
        //your algorithm does not require parameters and return
        //null in the createParameters() method
        MetaTypeService mts = (MetaTypeService)ctxt.locateService("MTS");
        provider = mts.getMetaTypeInformation(ctxt.getBundleContext().getBundle());       
        
  Dictionary properties = ctxt.getProperties();
        
        Enumeration propertiesKeys = properties.keys();
		
		while (propertiesKeys.hasMoreElements()) {
			String propertiesKey = (String) propertiesKeys.nextElement();
			
			Object propertiesValue = properties.get(propertiesKey);
			
		}
		
	//	System.out.println("TestAlgorithm 3 done activating");
    }
    protected void deactivate(ComponentContext ctxt) {
        provider = null;
    }

    public Algorithm createAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
    	System.out.println("TestAlgorithm3 executed!");
        return new TestAlgorithm(data, parameters, context, this.preferences);
    }
    public MetaTypeProvider createParameters(Data[] data) {
        return provider;
    }
    
    private void printPreferences(Dictionary properties) {
		System.out.println("  Preferences are as follows:");
		if (properties == null) {
			System.out.println("    Dictionary is null!");
		} else {
			Enumeration propertiesKeys = properties.keys();
			
			while (propertiesKeys.hasMoreElements()) {
				String propertiesKey = (String) propertiesKeys.nextElement();
				
				Object propertiesValue = properties.get(propertiesKey);
				System.out.println("    " + propertiesKey + ":" + propertiesValue);
			}
		}
	}
    
	public void updated(Dictionary properties) throws ConfigurationException {

		this.preferences = properties;
		//System.out.println("TestAlgorithm 3 updated!");
		//printPreferences(properties);
	}
}
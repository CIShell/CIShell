package org.cishell.testalgorithm2;

import java.util.Dictionary;
import java.util.Enumeration;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;


public class TestAlgorithm2Factory implements AlgorithmFactory, ManagedService {
    private MetaTypeProvider provider;

    private BundleContext bContext;
    private Dictionary preferences;
    
    protected void activate(ComponentContext ctxt) {
    	//System.out.println("TestAlgorithm 2 beginning activation");
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
		
		this.bContext = ctxt.getBundleContext();
		
		//System.out.println("TestAlgorithm 2 done activating");
    }
    
    protected void deactivate(ComponentContext ctxt) {
        provider = null;
    }

    public Algorithm createAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
        
//    	//not renaming everything parametersX for sake of ease
//    	
//    	Enumeration propertiesKeys = parameters.keys();
//		
//		while (propertiesKeys.hasMoreElements()) {
//			String propertiesKey = (String) propertiesKeys.nextElement();
//			
//			Object propertiesValue = parameters.get(propertiesKey);
//		}
    	
    	System.out.println("TestAlgorithm 2 executing!");
    	return new TestAlgorithm2(data, parameters, context, this.preferences);
    }
    public MetaTypeProvider createParameters(Data[] data) {
        return provider;
    }
	public void updated(Dictionary properties) throws ConfigurationException {
	//	System.out.println("TestAlgorithm 2 updated!");
	
		
		this.preferences = properties;
		
	//	printPreferences(this.preferences);
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

	public String[] getLocales() {
		return null;
	}
}
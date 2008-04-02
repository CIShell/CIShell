package org.cishell.testalgorithm4;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.MetaTypeService;


public class TestAlgorithm4Factory implements AlgorithmFactory, ManagedService {
    private MetaTypeProvider provider;
    private ConfigurationAdmin ca;
    private Dictionary preferences;
    private LogService log;
    String pid;

    protected void activate(ComponentContext ctxt) {
    	//System.out.println("TestAlgorithm 4 beginning activation");
        //You may delete all references to metatype service if 
        //your algorithm does not require parameters and return
        //null in the createParameters() method
        MetaTypeService mts = (MetaTypeService)ctxt.locateService("MTS");
        
        this.log = (LogService) ctxt.locateService("LOG");
        this.ca = (ConfigurationAdmin) ctxt.locateService("CA");
        provider = mts.getMetaTypeInformation(ctxt.getBundleContext().getBundle());       
        this.pid = (String) ctxt.getProperties().get("service.pid");
       // System.out.println("TestAlgorithm 4 done activating");
    }
    protected void deactivate(ComponentContext ctxt) {
        provider = null;
    }

    public Algorithm createAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {

    	System.out.println("TestAlgorithm4 executed!");
        return new TestAlgorithm4(data, parameters, context, this.preferences);
    }
    public MetaTypeProvider createParameters(Data[] data) {
        return provider;
    }
	public void updated(Dictionary preferences) throws ConfigurationException {
		
		this.preferences = preferences;
//		//System.out.println("TestAlgorithm4 updated!");
//		printProperties(this.preferences);
	}
	
	 private void printProperties(Dictionary properties) {
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
}
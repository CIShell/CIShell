package org.cishell.templates.jythonrunner;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.MetaTypeService;
/**
 * 
 * @author mwlinnem
 *
 */


public class JythonAlgorithmFactory implements AlgorithmFactory {
    private BundleContext myBundleContext;
    private Bundle myBundle;
	private MetaTypeProvider provider;
    private Dictionary properties;
    
    protected void activate(ComponentContext ctxt) {
    	
        MetaTypeService mts = (MetaTypeService)ctxt.locateService("MTS");
		this.myBundleContext = ctxt.getBundleContext();
        this.myBundle = myBundleContext.getBundle();
        this.provider = mts.getMetaTypeInformation(myBundle);
        this.properties = ctxt.getProperties();  
    }

    
    protected void deactivate(ComponentContext ctxt) {
        provider = null;
    }

    public Algorithm createAlgorithm(Data[] data, Dictionary parameters, 
    		CIShellContext context) {
    	return new JythonRunnerAlgorithm(data, parameters, context,
    			properties, myBundle);
    }
    
    public MetaTypeProvider createParameters(Data[] data) {
        return provider;
    }
}
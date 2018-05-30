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
    private Dictionary properties;
    
    protected void activate(ComponentContext ctxt) {
		this.myBundleContext = ctxt.getBundleContext();
        this.myBundle = myBundleContext.getBundle();
        this.properties = ctxt.getProperties();  
    }

    public Algorithm createAlgorithm(Data[] data, Dictionary parameters, 
    		CIShellContext context) {
    	return new JythonRunnerAlgorithm(data, parameters, context,
    			properties, myBundle);
    }
}
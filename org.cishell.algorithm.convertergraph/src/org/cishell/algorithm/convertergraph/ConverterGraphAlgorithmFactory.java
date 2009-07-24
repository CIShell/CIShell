package org.cishell.algorithm.convertergraph;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

/**
 * @author cdtank
 *
 */

public class ConverterGraphAlgorithmFactory implements AlgorithmFactory {
	
    private BundleContext bundleContext;

	protected void activate(ComponentContext ctxt) {
        bundleContext = ctxt.getBundleContext();
    }
    
	public Algorithm createAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
		return new ConverterGraphAlgorithm(data, parameters, context, bundleContext);
	}
}
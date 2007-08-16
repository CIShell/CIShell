package org.cishell.testing.convertertester.algorithm;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.MetaTypeService;


public class ConverterTesterAlgorithmFactory implements AlgorithmFactory {
    private BundleContext bContext;

    protected void activate(ComponentContext ctxt) {
        this.bContext = ctxt.getBundleContext();;       
    }
    
    protected void deactivate(ComponentContext ctxt) {
    }

    public Algorithm createAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
        return new ConverterTesterAlgorithm(data, parameters, context, bContext);
    }
    public MetaTypeProvider createParameters(Data[] data) {
        return null;
    }
}
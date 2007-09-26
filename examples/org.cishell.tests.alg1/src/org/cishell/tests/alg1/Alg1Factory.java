package org.cishell.tests.alg1;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.MetaTypeService;

public class Alg1Factory implements AlgorithmFactory {
    private MetaTypeProvider provider;

    protected void activate(ComponentContext ctxt) {
        MetaTypeService mts = (MetaTypeService)ctxt.locateService("MTS");
        provider = mts.getMetaTypeInformation(ctxt.getBundleContext().getBundle());       
    }
    protected void deactivate(ComponentContext ctxt) {
        provider = null;
    }

    public Algorithm createAlgorithm(Data[] dm, Dictionary parameters, CIShellContext context) {
        return new Alg(context, parameters);
    }
    public MetaTypeProvider createParameters(Data[] dm) {
        return provider;
    }
}

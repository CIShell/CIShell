package org.cishell.reference.gui.persistence.save;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.LocalCIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.DataValidator;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.metatype.MetaTypeProvider;


public class SaveFactory implements AlgorithmFactory, DataValidator {
    private CIShellContext context;	

    protected void activate(ComponentContext ctxt) {
        context = new LocalCIShellContext(ctxt.getBundleContext());
    }
    protected void deactivate(ComponentContext ctxt) {}

    public Algorithm createAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
        this.context = context;
        return new Save(data, parameters, context);
    }
    public MetaTypeProvider createParameters(Data[] data) {
        return null;
    }
    
	public String validate(Data[] data) {
		DataConversionService conversionManager = (DataConversionService) context.getService(
        		DataConversionService.class.getName());

    	Converter[] converters = conversionManager.findConverters(data[0], "file-ext:*");
    	if (converters.length == 0) {
    		return "No valid converters from " + 
    				data[0].getData().getClass().getName() + " to any file extension";
    	}
    	else {
    		return "";
    	}
	}

}
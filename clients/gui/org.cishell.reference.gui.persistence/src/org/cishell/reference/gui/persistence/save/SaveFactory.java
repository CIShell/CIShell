package org.cishell.reference.gui.persistence.save;

import java.io.File;
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

/**
 * Create a Save object
 * @author bmarkine
 *
 */
public class SaveFactory implements AlgorithmFactory, DataValidator {
    private CIShellContext context;	

    /**
     * Create a local CIShell context
     * @param ctxt The current CIShell context
     */
    protected void activate(ComponentContext ctxt) {
        context = new LocalCIShellContext(ctxt.getBundleContext());
    }
    
    /**
     * Deactivate the plugin
     * @param ctxt Current CIShell context
     */
    protected void deactivate(ComponentContext ctxt) {}

    /**
     * Create a Save algorithm
     * @param data The data objects to save
     * @param parameters The parameters for the algorithm
     * @param context Reference to services provided by CIShell
     * @return An instance of the Save algorithm
     */
    public Algorithm createAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
        this.context = context;
        return new Save(data, parameters, context);
    }
    
    /**
     * Create parameters (this returns null only)
     * data input data
     * @return null;
     */
    public MetaTypeProvider createParameters(Data[] data) {
        return null;
    }
    
    /**
     * Validate the SaveFactory can handle the incoming file type
     * @param data The data to save
     * @return empty string on success
     */
	public String validate(Data[] data) {
		DataConversionService conversionManager = (DataConversionService) context.getService(
        		DataConversionService.class.getName());

		//Fix me
		//Bonnie:why only check data[0]? An user can select multiple objects from data manager.
    	Converter[] converters = conversionManager.findConverters(data[0], "file-ext:*");
    	if (converters.length == 0 && !(data[0].getData() instanceof File)) {
    		return "No valid converters from " + 
    				data[0].getData().getClass().getName() + " to any file extension";
    	}
    	else {
    		return "";
    	}
	}

}
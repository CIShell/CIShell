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
 * 
 * TODO: Should also support if we can convert to file, but have
 * no final file:X->file-ext:* converter.
 * 
 * @author bmarkine
 *
 */
public class SaveFactory implements AlgorithmFactory {
    private CIShellContext context;	

    /**
     * Create a local CIShell context
     * @param ctxt The current CIShell context
     */
    protected void activate(ComponentContext ctxt) {
        context = new LocalCIShellContext(ctxt.getBundleContext());
    }

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
}
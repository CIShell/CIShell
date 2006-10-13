package org.cishell.reference.gui.persistence.save;

import java.util.Dictionary;
import java.util.Set;
import java.util.TreeSet;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

/**
 * Save algorithm for persisting a data object
 * 
 * @author bmarkine
 */
public class Save implements Algorithm {
    Data[] data;
    Dictionary parameters;
    CIShellContext context;
    
    final Shell parentShell;
    
    private GUIBuilderService guiBuilder;    
    private DataConversionService conversionManager;
    
    /**
     * Sets up default services for the algorithm
     * 
     * @param data The data array to persist
     * @param parameters Parameters for the algorithm
     * @param context Provides services to CIShell services
     */
    public Save(Data[] data, Dictionary parameters, CIShellContext context) {
        this.data = data;
        this.parameters = parameters;
        this.context = context;
        
        this.parentShell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();

        this.conversionManager = (DataConversionService) context.getService(
        		DataConversionService.class.getName());
        
        this.guiBuilder = (GUIBuilderService)context.getService(GUIBuilderService.class.getName());
    }

    /**
     * Executes the algorithm
     * 
     * @return Null for this algorithm
     */
    public Data[] execute() {
    	//This only checks the first Data in the array
    	final Converter[] converters = conversionManager.findConverters(data[0], "file-ext:*");

    	Set conversionSet = new TreeSet();
    	for (int i=0; i < converters.length; i++) {
    		if (!conversionSet.contains(converters[i])) {
    			conversionSet.add(converters[i]);
    			
    			ServiceReference[] refs = converters[i].getConverterChain();
    			for (int j=0; j < refs.length; j++) {
    				System.out.print(refs[j].getProperty(Constants.SERVICE_PID+"->"));
    			}
    			
    		}
    	}
    	
    	if (converters.length < 1) {
    		guiBuilder.showError("No Converters", 
    				"No valid converters for data type: " + 
    				data[0].getData().getClass().getName(), 
    				"Please install a plugin that will save the data type to a file");
    	}
    	else {
    		if (!parentShell.isDisposed()) {
    			parentShell.getDisplay().syncExec(new Runnable() {
    				public void run() {
    					SaveDataChooser sdc = new SaveDataChooser(data[0],
    		    			                     			parentShell, converters,
    		    			                     			"title",
    		    			                     			context);
    					sdc.createContent(parentShell);
    					sdc.open();
    				}});
    		}
    	}
        return null;
    }
}
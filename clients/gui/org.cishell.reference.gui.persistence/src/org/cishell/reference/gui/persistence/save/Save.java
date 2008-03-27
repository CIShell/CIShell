package org.cishell.reference.gui.persistence.save;

import java.io.File;
import java.util.Dictionary;
import java.util.Hashtable;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
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
    Shell parentShell;
    
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
        
        this.conversionManager = (DataConversionService) context.getService(
        		DataConversionService.class.getName());
        
        this.guiBuilder = (GUIBuilderService)context.getService(GUIBuilderService.class.getName());
    }

    /**
     * Executes the algorithm
     * 
     * @return Null for this algorithm
     */
    public Data[] execute() throws AlgorithmExecutionException {
    try {
    	//This only checks the first Data in the array
    	final Converter[] converters = conversionManager.findConverters(data[0], "file-ext:*");
    	
    	if (converters.length < 1 && !(data[0].getData() instanceof File)) {
    		guiBuilder.showError("No Converters", 
    				"No valid converters for data type: " + 
    				data[0].getFormat(), 
    				"Please install a plugin that will save the data type to a file");
    	} else {
            parentShell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
            
    		if (!parentShell.isDisposed()) {
    			guiRun(new Runnable() {
    				public void run() {
    					if (converters.length == 0) {
    						FileSaver saver = new FileSaver(parentShell, context);
                            saver.save(new NoConversionConverter(), data[0]);
    					} else if (converters.length == 1) {
                            final FileSaver saver = new FileSaver(parentShell, context);
                            saver.save(converters[0], data[0]);
                        } else {
                            SaveDataChooser sdc = new SaveDataChooser(data[0],
                                    parentShell, converters,
                                    "Save",
                                    context);
                            sdc.createContent(new Shell(parentShell));
                            sdc.open(); 
                        }
    				}});
    		}
    	}
        return null;
    } catch (Throwable e) {
    	throw new AlgorithmExecutionException(e);
    }
    }
    
    private void guiRun(Runnable run) {
        if (Thread.currentThread() == Display.getDefault().getThread()) {
            run.run();
        } else {
            parentShell.getDisplay().syncExec(run);
        }
    }
    
    private class NoConversionConverter implements Converter {
            Dictionary props = new Hashtable();

            public Data convert(Data data) {
                return data;
            }

            public AlgorithmFactory getAlgorithmFactory() {
                return null;
            }

            public ServiceReference[] getConverterChain() {
                return null;
            }

            public Dictionary getProperties() {
                props.put(AlgorithmProperty.OUT_DATA, "file:*");
                return props;
            }
    }
}
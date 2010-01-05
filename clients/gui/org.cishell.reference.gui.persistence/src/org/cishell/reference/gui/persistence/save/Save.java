package org.cishell.reference.gui.persistence.save;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
/**
 * Save algorithm for persisting a data object
 * 
 * @author bmarkine
 */
public class Save implements Algorithm {
    public static final String ANY_FILE_EXTENSION = "file-ext:*";
	public static final String SAVE_DIALOG_TITLE = "Save";
	private Data[] data;
    private CIShellContext context;
    private Shell parentShell;
    
    private DataConversionService conversionManager;
    
    /**
     * Sets up default services for the algorithm
     * 
     * @param data The data array to persist
     * @param parameters Parameters for the algorithm
     * @param ciShellContext Provides services to CIShell services
     */
    public Save(Data[] data, Dictionary parameters, CIShellContext context) {
        this.data = data;
        this.context = context;
        
        this.conversionManager = (DataConversionService)
        	context.getService(DataConversionService.class.getName());
    }

    /**
     * @return Null when successful
     */
    public Data[] execute() throws AlgorithmExecutionException {
    	Data outData = data[0];

    	tryToSave(outData, ANY_FILE_EXTENSION);
    	
		return null;
	}

    private void tryToSave(final Data outData, String outFormat)
    		throws AlgorithmExecutionException {
    	final Converter[] converters =
    		conversionManager.findConverters(outData, outFormat);
    	if (converters.length == 0) {
    		throw new AlgorithmExecutionException(
    				"Error: Calculated an empty converter chain.");
    	}
    	
    	parentShell =
    		PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
    	if (parentShell.isDisposed()) {
    		throw new AlgorithmExecutionException(
    				"Attempted to use disposed parent shell.");
    	}
    	
    	try {
	    	guiRun(new Runnable() {
	    		public void run() {
	    			if (converters.length == 1) {
	    				// Only one possible choice in how to save data.  Do it.
	    				Converter onlyConverter = converters[0];
	    				final FileSaver saver =
	    					new FileSaver(parentShell, context);
	    				saver.save(onlyConverter, outData);
	    			} else {
	    				// Multiple ways to save the data. Let user choose.
	    				SaveDataChooser saveChooser =
	    					new SaveDataChooser(outData,
	    										parentShell,
	    										converters,
	    										SAVE_DIALOG_TITLE,
	    										context);
						saveChooser.createContent(new Shell(parentShell));
						saveChooser.open();
	    			}
	    		}
	    	});
    	} catch (Exception e) {
    		throw new AlgorithmExecutionException(e.getMessage(), e);
    	}
    }
    
	private void guiRun(Runnable run) {
        if (Thread.currentThread() == Display.getDefault().getThread()) {
            run.run();
        } else {
            parentShell.getDisplay().syncExec(run);
        }
    }
}
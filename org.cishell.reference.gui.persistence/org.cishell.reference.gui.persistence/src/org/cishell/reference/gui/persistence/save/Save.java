package org.cishell.reference.gui.persistence.save;

import java.util.Dictionary;
import java.util.Hashtable;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
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
    private LogService log;
    
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
        
        this.log = (LogService) context.getService(LogService.class.getName());
        this.guiBuilder = (GUIBuilderService)context.getService(GUIBuilderService.class.getName());
    }

    /**
     * Executes the algorithm
     * 
     * @return Null for this algorithm
     */
    public Data[] execute() throws AlgorithmExecutionException {
    	//NOTE: A "converter" here is actually a converter path
    	//starting with the format for the data we want to save
    	//and ending in a output format
    	Data dataToSave = data[0];
    	
    	//first, try to save the normal way, which is using validators to validate the output.
    	String saveThroughValidators = "file-ext:*";
    	Object firstAttemptResult = tryToSave(dataToSave, saveThroughValidators);
    	if (firstAttemptResult instanceof Boolean) {
    		boolean succeeded = ((Boolean) firstAttemptResult).booleanValue();
    		if (succeeded) {
    			System.out.println("Success");
    			return null; //FILE SAVED SUCCESSFULLY. DONE.
    		} else {
    			System.out.println("No converter");
    			this.log.log(LogService.LOG_WARNING, "No converters found that can save file through a validator." +
    					" Attempting to save without validating.");
    		}
    	} else { //result instanceof Exception
    		Exception reasonForFailure = (Exception) firstAttemptResult;
    		this.log.log(LogService.LOG_WARNING, "Exception occurred while attempting to save" +
    				" file using a validator. Attempting to save without validating.");
    		System.out.println("Exception");
    	}
    	
    	System.out.println("Trying without validators");
    	
    	//if saving with validators didn't work, try to save it without using validators
    	String saveWithoutValidators = "file:*";
    	Object secondAttemptResult = tryToSave(dataToSave, saveWithoutValidators);
    	if (secondAttemptResult instanceof Boolean) {
    		boolean succeeded = ((Boolean) secondAttemptResult).booleanValue();
    		if (succeeded) {
    			return null; //FILE SAVED SUCCESSFULLY. DONE.
    		} else {
    			throw new AlgorithmExecutionException("No converters found that could save file. Save failed");
    		}
    	} else { //result instanceof Exception
    		Exception reasonForFailure2 = (Exception) secondAttemptResult;
    		throw new AlgorithmExecutionException("Exception occurred while attempting to save", reasonForFailure2);
    	}   	
	}

	//returns True if save was successful
    //return False if there are no converter chains available to save to the given format
    //return an Exception if an exception occurred while attempting to save
    private Object tryToSave(final Data dataToSave, String formatToSaveTo) {
    	final Converter[] converters = conversionManager.findConverters(dataToSave, formatToSaveTo);
    	if (converters.length == 0) {return new Boolean(false);};
    	
    	parentShell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
    	if (parentShell.isDisposed()) {return makeParentShellDisposedException();};
    	
    	try {
    	guiRun(new Runnable() {
    		public void run() {
    			if (converters.length == 1){
    				//only one possible choice in how to save data. Just do it.
    				Converter onlyConverter = converters[0];
    				final FileSaver saver = new FileSaver(parentShell, context);
    				saver.save(onlyConverter, dataToSave);
    			} else { //converters.length > 1
    				//multiple ways to save the data. Let user choose.
    				SaveDataChooser saveChooser = new SaveDataChooser(dataToSave,
    						parentShell, converters, "Save", context);
    					saveChooser.createContent(new Shell(parentShell));
    					saveChooser.open(); 
    				}
    			}});
    	} catch (Exception e) {
    		return e;
    	}
    	
    	return new Boolean(true);
    }
    
    private AlgorithmExecutionException makeParentShellDisposedException() {
		return new AlgorithmExecutionException("Attempted to use disposed parent shell");
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
    
    private Data removeExtension(Data data) {
    	return new BasicData(data.getMetadata(), data, "");
    }
}
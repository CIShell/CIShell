package org.cishell.reference.gui.persistence.save;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class Save implements Algorithm {
    public static final String ANY_FILE_EXTENSION = "file-ext:*";
	public static final String SAVE_DIALOG_TITLE = "Save";
	private Data data;
	private CIShellContext ciShellContext;

    private Shell parentShell;
    
    private DataConversionService conversionManager;

    public Save(
    		Data data, CIShellContext ciShellContext, DataConversionService conversionManager) {
        this.data = data;
        this.ciShellContext = ciShellContext;
        
        this.conversionManager = conversionManager;
    }

    public Data[] execute() throws AlgorithmExecutionException {
    	tryToSave(this.data, ANY_FILE_EXTENSION);
    	
		return null;
	}

    private void tryToSave(final Data outData, String outFormat)
    		throws AlgorithmExecutionException {
    	final Converter[] converters = conversionManager.findConverters(outData, outFormat);

    	if (converters.length == 0) {
    		throw new AlgorithmExecutionException("Error: Calculated an empty converter chain.");
    	}

    	this.parentShell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();

    	if (this.parentShell.isDisposed()) {
    		throw new AlgorithmExecutionException("Attempted to use disposed parent shell.");
    	}
    	
    	try {
	    	guiRun(new Runnable() {
	    		public void run() {
	    			if (converters.length == 1) {
	    				// Only one possible choice in how to save data.  Do it.
	    				Converter onlyConverter = converters[0];
	    				FileSaver saver = new FileSaver(Save.this.parentShell, ciShellContext);
	    				saver.save(onlyConverter, outData);
	    			} else {
	    				// Multiple ways to save the data. Let user choose.
	    				SaveDataChooser saveChooser = new SaveDataChooser(
	    					outData,
	    					Save.this.parentShell,
	    					converters,
	    					SAVE_DIALOG_TITLE,
	    					Save.this.ciShellContext);
						saveChooser.createContent(new Shell(Save.this.parentShell));
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
            this.parentShell.getDisplay().syncExec(run);
        }
    }
}
package org.cishell.reference.gui.persistence.save;

import java.io.File;

import org.cishell.app.service.filesaver.FileSaveException;
import org.cishell.app.service.filesaver.FileSaverService;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmCanceledException;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.data.Data;
import org.osgi.service.log.LogService;

public class Save implements Algorithm {
	public static final String SAVE_DIALOG_TITLE = "Save";
	
	private Data data;
    private LogService logger;
    private FileSaverService fileSaver;

    public Save(
    		Data data,
    		LogService logger,
    		FileSaverService fileSaver) {
        this.data = data;

        this.logger = logger;
        this.fileSaver = fileSaver;
    }

    public Data[] execute() throws AlgorithmExecutionException {
    	tryToSave(this.data, FileSaverService.ANY_FILE_EXTENSION);
    	
		return null;
	}

    private void tryToSave(final Data outData, String outFormat)
    		throws AlgorithmExecutionException {
    	try {
    		File outputFile = this.fileSaver.saveData(outData);
//    		Converter userChosenConverter =
//    			this.fileSaver.promptForConverter(outData, outFormat);
//
//    		if (userChosenConverter == null) {
//    			throw new AlgorithmCanceledException(
//    				"User canceled file saving when choosing what kind of file to save as.");
//    		}
//
//    		File userChosenFile = this.fileSaver.promptForTargetFile(outData);
//
    		if (outputFile == null) {
    			throw new AlgorithmCanceledException(
    				"User canceled file saving when choosing the destination of the file.");
    		}
//
//    		Data outputDatum =
//    			this.fileSaver.save(userChosenConverter, outData, userChosenFile);
//
//    		// TODO: Should we bother handling this?  sure, why not.  maybe algexec would be appropriate
//    		if (outputDatum == null) {
//    			return;
//    		}

    		String logMessage = String.format("Saved: %s", outputFile.getPath());
    		this.logger.log(LogService.LOG_INFO, logMessage);
    	} catch (FileSaveException e) {
    		String logMessage = String.format(
    			"Error occurred while converting data to saved format:\n    %s", e.getMessage());
    		this.logger.log(LogService.LOG_ERROR, logMessage, e);
//    		throw new AlgorithmExecutionException(e.getMessage(), e);
    		throw new RuntimeException(e.getMessage(), e);
    	}
    }
}
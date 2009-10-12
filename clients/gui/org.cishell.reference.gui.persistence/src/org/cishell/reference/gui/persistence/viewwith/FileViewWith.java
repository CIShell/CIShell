package org.cishell.reference.gui.persistence.viewwith;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.ConversionException;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.log.LogService;

/* 
 * @author Felix Terkhorn (terkhorn@gmail.com), Weixia Huang (huangb@indiana.edu)
 */
public class FileViewWith implements Algorithm {
	public static final String VIEW_WITH_PARAMETER_KEY = "viewWith";
	
    private Data[] dataToView;
    private Dictionary parameters;
    private CIShellContext context;
    private DataConversionService conversionManager;
    private static GUIBuilderService guiBuilder;
    private LogService logger;
    private Program textProgram;
    private Program wordProgram;
    private Program webBrowserProgram;
    private Program spreadsheetProgram;
    private File temporaryFile;
     
    public FileViewWith(Data[] data, Dictionary parameters, CIShellContext context) {
        this.dataToView = data;
        this.parameters = parameters;
        this.context = context;
        
        conversionManager = (DataConversionService) context.getService(
        		DataConversionService.class.getName());
        
        logger = (LogService)context.getService(LogService.class.getName());
        guiBuilder = (GUIBuilderService)context.getService(GUIBuilderService.class.getName());

    }
    
    public File getTempFile(){
    	File tempFile;
    
    	String tempPath = System.getProperty("java.io.tmpdir");
    	File tempDir = new File(tempPath + File.separator + "temp");
    	
    	if (!tempDir.exists()) {
    		tempDir.mkdir();
    	}
    	
    	try {
    		tempFile = File.createTempFile("xxx-Session-", ".txt", tempDir);
		
    	} catch (IOException ioException) {
    		logger.log(
    			LogService.LOG_ERROR, ioException.toString(), ioException);
    		
    		String separator = File.separator;
    		String temporaryFileName =
    			tempPath + separator + "temp" + separator + "temp.txt";
    		tempFile = new File(temporaryFileName);

    	}
    	
    	return tempFile;
    }

    public Data[] execute() throws AlgorithmExecutionException {
    	// TODO: Refactor this code so it and FileView use the same code.
        boolean temporaryFileWasCreated = false;
        String format;
        
        String viewWithType = (String)parameters.get(VIEW_WITH_PARAMETER_KEY);
        
        Display display;
        IWorkbenchWindow[] windows;
        final Shell parentShell;
        
        windows = PlatformUI.getWorkbench().getWorkbenchWindows();
        
        if (windows.length == 0) {
        	return null;
        }
        
        parentShell = windows[0].getShell();
        display = PlatformUI.getWorkbench().getDisplay();
        temporaryFile = getTempFile();
               
        for (int ii = 0; ii < this.dataToView.length; ii++){
        	Data data = this.dataToView[ii];
        	Object theData = data.getData();
        	format = data.getFormat();
        	
        	if (theData instanceof File ||
        		format.startsWith("file:text/") || 
        		format.startsWith("file-ext:")){
           		copy((File)data.getData(), temporaryFile);
        		temporaryFileWasCreated = true;    
        	} else {
        		final Converter[] converters =
        			conversionManager.findConverters(data, "file-ext:*");

        		if (converters.length == 1) {
             		/*
             		 * If length is 1, use the unique path to save it directly 
            		 *  and bring the text editor.
            		 */
        		
            		try {
	            	    Data newData = converters[0].convert(data);
	                    copy((File)newData.getData(), temporaryFile);     
	            		temporaryFileWasCreated = true; 
            		} catch (ConversionException conversionException) {
            			String warningMessage =
            				"Warning: Unable to convert to target save " +
            				"format (" + conversionException.getMessage() +
            				").  Will attempt to use other " +
            				"available converters.";
            			this.logger.log(LogService.LOG_WARNING,
            							warningMessage,
            							conversionException);
            		}
            	} else if (converters.length > 1) {
             		if (!parentShell.isDisposed()) {
             			try {
            				DataViewer dataViewer =
            					new DataViewer(parentShell, data, converters);
            				display.syncExec(dataViewer);
             			
             				temporaryFileWasCreated = dataViewer.isSaved;
            				temporaryFile = dataViewer.theFile;
             			} catch (Throwable thrownObject) {
             				throw new AlgorithmExecutionException(
             					thrownObject);
             			}
            		}
            	}
            	else {
            		String errorMessage =
            			"No valid converters for data type: " + 
            			data.getData().getClass().getName();
            		String errorDetail =
            			"Please install a plugin that will save the " +
            			"data type to a file";
            		guiBuilder.showError(
            			"No Converters",  errorMessage, errorDetail);
            	}
        	}
            
            //TODO: holy code duplication, batman!
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    textProgram = Program.findProgram("txt");
                }});
 
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    wordProgram = Program.findProgram("doc");
                }});
            
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    webBrowserProgram = Program.findProgram("htm");
                }});

            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    spreadsheetProgram = Program.findProgram("csv");
                }});
            
            if ((textProgram == null) &&
            		(wordProgram == null) &&
            		(webBrowserProgram == null) &&
            		(webBrowserProgram == null)) {
            	String errorTitle = "No Viewers for TXT, DOC, or HTM";
            	String errorMessage =
            		"No valid viewers for .txt, .doc, or .htm files. " +
    				"The file is located at: " + temporaryFile.getAbsolutePath();
            	String errorDetail =
            		"Unable to open default text viewer.  " +
            		"File is located at: " +
    				temporaryFile.getAbsolutePath();
            	guiBuilder.showError(
            		errorTitle, errorMessage, errorDetail);
            }
            else {
            	if (temporaryFileWasCreated) { 
            		final String filePath = temporaryFile.getAbsolutePath();
            		
            		//TODO: . . . I already said "holy code duplication batman!", didn't I?
            		if (viewWithType.equals("txt")) {
            			Display.getDefault().syncExec(new Runnable() {
            				public void run() {
            					textProgram.execute(filePath);
            				}
            			});
            		} else if (viewWithType.equals("doc")) {
            			Display.getDefault().syncExec(new Runnable() {
            				public void run() {
            					wordProgram.execute(filePath);
            				}
            			});
            		} else if (viewWithType.equals("html")) {
            			Display.getDefault().syncExec(new Runnable() {
            				public void run() {
            					webBrowserProgram.execute(filePath);
            				}
            			});
            		} else if (viewWithType.equals("csv")) {
            			Display.getDefault().syncExec(new Runnable() {
            				public void run() {
            					spreadsheetProgram.execute(filePath);
            				}
            			});
            		} else {
            			Display.getDefault().syncExec(new Runnable() {
            				public void run() {
            					textProgram.execute(filePath);
            				}
            			});
            		}
            	}
            }
        }
        
        return null;   
    }
    
    public static boolean copy(File in, File out) {
    	try {
    		FileInputStream  fis = new FileInputStream(in);
    		FileOutputStream fos = new FileOutputStream(out);
    		
    		FileChannel readableChannel = fis.getChannel();
    		FileChannel writableChannel = fos.getChannel();
    		
    		writableChannel.truncate(0);
    		writableChannel.transferFrom(readableChannel, 0, readableChannel.size());
    		fis.close();
    		fos.close();
    		
    		return true;
    	}
    	catch (IOException ioe) {
    		guiBuilder.showError("Copy Error", "IOException during copy", ioe.getMessage());
            return false;
    	}
    }
    
	final class DataViewer implements Runnable {
		Shell shell;
		boolean isSaved;
		Data theData;
		File theFile = getTempFile();
		Converter[] theConverters;
			
		DataViewer (Shell parentShell, Data data, Converter[] converters){
			this.shell = parentShell;
			this.theData = data;
			this.theConverters = converters;
		}
		
		public void run() {
    		// lots of persisters found, return the chooser
			ViewWithDataChooser vdc = new ViewWithDataChooser("View As...", theFile, shell, 
       				    theData, theConverters, context);
			vdc.open();
			isSaved = vdc.isSaved();
		}
	}
	



}
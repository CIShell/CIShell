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
    Data[] data;
    Dictionary parameters;
    CIShellContext context;
    DataConversionService conversionManager;
    static GUIBuilderService guiBuilder;
    LogService logger;
    Program program;
    Program programTwo;
    Program programThree;
    Program programFour; //TC181
    File tempFile;
     
    public FileViewWith(Data[] data, Dictionary parameters, CIShellContext context) {
        this.data = data;
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
    	File tempDir = new File(tempPath+File.separator+"temp");
    	if(!tempDir.exists())
    		tempDir.mkdir();
    	try{
    		tempFile = File.createTempFile("xxx-Session-", ".txt", tempDir);
		
    	}catch (IOException e){
    		logger.log(LogService.LOG_ERROR, e.toString(), e);
    		tempFile = new File (tempPath+File.separator+"temp"+File.separator+"temp.txt");

    	}
    	return tempFile;
    }

    public Data[] execute() throws AlgorithmExecutionException {
        boolean lastSaveSuccessful = false;
        String format;
        
        String viewWith = (String) parameters.get("viewWith");
        
        Display display;
        IWorkbenchWindow[] windows;
        final Shell parentShell;
        
        windows = PlatformUI.getWorkbench().getWorkbenchWindows();
        if (windows.length == 0){
        	return null;
        }
        parentShell = windows[0].getShell();
        display = PlatformUI.getWorkbench().getDisplay();
        tempFile = getTempFile();
               
        for (int i = 0; i < data.length; i++){
        	Object theData = data[i].getData();
        	format = data[i].getFormat();
        	if (theData instanceof File ||
        		format.startsWith("file:text/") || 
        		format.startsWith("file-ext:")){
           		copy((File)data[i].getData(), tempFile);
        		lastSaveSuccessful = true;    
        	}else{
        		final Converter[] converters = conversionManager.findConverters(data[i], "file-ext:*");

            	if (converters.length < 1) {
            		guiBuilder.showError("No Converters", 
            				"No valid converters for data type: " + 
            				data[i].getData().getClass().getName(), 
            				"Please install a plugin that will save the data type to a file");
            	}
            	else if (converters.length == 1){
             		//If length=1, use the unique path to save it directly 
            		//and bring the text editor.
            		try {
            	    Data newData = converters[0].convert(data[i]);
                    copy((File)newData.getData(), tempFile);     
            		lastSaveSuccessful = true; 
            		} catch (ConversionException e) {
            			this.logger.log(LogService.LOG_WARNING, "Error while converting to target save format. Will attempt to use other available converters.", e);
            		}
            	}
            	else {
             		if (!parentShell.isDisposed()) {
             			try {
            			DataViewer dataViewer = new DataViewer(parentShell, data[i], converters);
            			display.syncExec(dataViewer);
             			
             			lastSaveSuccessful = dataViewer.isSaved;
            			tempFile = dataViewer.theFile;
             			} catch (Throwable e1) {
             				throw new AlgorithmExecutionException(e1);
             			}
            		}
            	}
        	}
            
            
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    program = Program.findProgram("txt");
                }});
 
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    programTwo = Program.findProgram("doc");
                }});
            
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    programThree = Program.findProgram("htm");
                }});
            //TC181
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    programFour = Program.findProgram("csv");
                }});
            
            //TC181
            if (program == null && programTwo == null && programThree == null && programThree == null) {
            		guiBuilder.showError("No Viewers for TXT, DOC, or HTM", 
    					"No valid viewers for .txt, .doc, or .htm files. " +
    					"The file is located at: "+tempFile.getAbsolutePath(), 
    					"Unable to open default text viewer.  File is located at: "+
    					tempFile.getAbsolutePath());
            }
            else {
            	if (lastSaveSuccessful == true) { 
            		if (viewWith.equals("txt")) {
            			Display.getDefault().syncExec(new Runnable() {
            				public void run() {
            					program.execute(tempFile.getAbsolutePath());
            				}});
            		} else if (viewWith.equals("doc")) {
            			Display.getDefault().syncExec(new Runnable() {
            				public void run() {
            					programTwo.execute(tempFile.getAbsolutePath());
            				}});
            		} else if (viewWith.equals("html")) {
            			Display.getDefault().syncExec(new Runnable() {
            				public void run() {
            					programThree.execute(tempFile.getAbsolutePath());
            				}});
            		//TC181	
            		} else if (viewWith.equals("csv")) {
            			Display.getDefault().syncExec(new Runnable() {
            				public void run() {
            					programFour.execute(tempFile.getAbsolutePath());
            				}});
            		} else {
            			// Try to run it with txt viewer...
            			Display.getDefault().syncExec(new Runnable() {
            				public void run() {
            					program.execute(tempFile.getAbsolutePath());
            				}});
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
    
	final class DataViewer  implements Runnable {
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
package org.cishell.reference.gui.persistence.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.data.Data;
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
 * @author Weixia(Bonnie) Huang (huangb@indiana.edu)
 */
public class FileView implements Algorithm {
    Data[] data;
    Dictionary parameters;
    CIShellContext context;
    DataConversionService conversionManager;
    static GUIBuilderService guiBuilder;
    LogService logger;
    Program program;
 //   Program programTwo;
    File tempFile;
     
    public FileView(Data[] data, Dictionary parameters, CIShellContext context) {
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
    		logger.log(LogService.LOG_ERROR, e.toString());
    		tempFile = new File (tempPath+File.separator+"temp"+File.separator+"temp.txt");

    	}
    	return tempFile;
    }
    
    public File getTempFileCSV(){ //TC181
    	File tempFile;
    
    	String tempPath = System.getProperty("java.io.tmpdir");
    	File tempDir = new File(tempPath+File.separator+"temp");
    	if(!tempDir.exists())
    		tempDir.mkdir();
    	try{
    		tempFile = File.createTempFile("xxx-Session-", ".csv", tempDir);
		
    	}catch (IOException e){
    		logger.log(LogService.LOG_ERROR, e.toString());
    		tempFile = new File (tempPath+File.separator+"temp"+File.separator+"temp.csv");

    	}
    	return tempFile;
    }

    public Data[] execute() {
        boolean lastSaveSuccessful = false;
        boolean isCSVFile          = false;//TC181
        String format;
        
        Display display;
        IWorkbenchWindow[] windows;
        final Shell parentShell;
        
        windows = PlatformUI.getWorkbench().getWorkbenchWindows();
        if (windows.length == 0){
        	return null;
        }
        parentShell = windows[0].getShell();
        display = PlatformUI.getWorkbench().getDisplay();
        //tempFile = getTempFile(); TC181
               
        for (int i = 0; i < data.length; i++){
        	Object theData = data[i].getData();
        	format = data[i].getFormat();
        	 
        	if (theData instanceof File ||
        		format.startsWith("file:text/") || 
        		format.startsWith("file-ext:")){
        		 
        		if (format.startsWith("file:text/csv") || format.startsWith("file-ext:csv"))
        		{
        		   tempFile = getTempFileCSV();
        		   isCSVFile = true;
        		    
        		}
        		else
        		{
        		   tempFile = getTempFile(); 
        		}
        		copy((File)data[i].getData(), tempFile);
        		lastSaveSuccessful = true; 
        		
        		 
        		
        	}else{
        		final Converter[] convertersCSV = conversionManager.findConverters(data[i], "file-ext:csv");
        		//logger.log(LogService.LOG_ERROR, "convertersCSV's length = " + convertersCSV.length);
        		if (convertersCSV.length == 1)
        		{
        			Data newDataCSV = convertersCSV[0].convert(data[i]);
        			tempFile = getTempFileCSV();
        		    isCSVFile = true;
        		    copy((File)newDataCSV.getData(), tempFile);    
         		    lastSaveSuccessful = true; 
        			
        		}
        		else if (convertersCSV.length > 1)
        		{
        			Data newDataCSV = convertersCSV[0].convert(data[i]);
        			for (int j = 1; j < convertersCSV.length; j++ )
        			{
        				newDataCSV = convertersCSV[j].convert(newDataCSV);
        			}
        			tempFile = getTempFileCSV();
        		    isCSVFile = true;
        		    copy((File)newDataCSV.getData(), tempFile);    
         		    lastSaveSuccessful = true; 
        		}
        		else{
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
            	        Data newData = converters[0].convert(data[i]);     
                        tempFile = getTempFile(); 
            	        copy((File)newData.getData(), tempFile);    
            	  	    lastSaveSuccessful = true; 
            	   }
            	   else {
             		  if (!parentShell.isDisposed()) {
            		    	DataViewer dataViewer = new DataViewer(parentShell, data[i], converters);
            		    	display.syncExec(dataViewer);
             		    	lastSaveSuccessful = dataViewer.isSaved;
            			    tempFile = dataViewer.theFile;
            		      }
            	    }
        	    }
        	}
        	if (isCSVFile){//TC181
        		Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        program = Program.findProgram("csv");
                    }});
        		 
        	}else
        	{
        		 Display.getDefault().syncExec(new Runnable() {
                     public void run() {
                         program = Program.findProgram("txt");
                     }});
        		  
        	}
           
            /*
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    programTwo = Program.findProgram("doc");
                }});
            
            if (programTwo == null) {
            	System.out.println("***\nYO!\nNo doc viewer\n");
            } else {
            	System.out.println("!!!\nHEY!\nDoc viewer found\n");
            	
            }
            */
            if (program == null) {
            		guiBuilder.showError("No Text Viewer", 
    					"No valid text viewer for the .txt file. " +
    					"The file is located at: "+tempFile.getAbsolutePath(), 
    					"Unable to open default text viewer.  File is located at: "+
    					tempFile.getAbsolutePath());
            }
            else {
            	if (lastSaveSuccessful == true) { 
                    Display.getDefault().syncExec(new Runnable() {
                        public void run() {
                            program.execute(tempFile.getAbsolutePath());
                        }});
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
			ViewDataChooser vdc = new ViewDataChooser("View", theFile, shell, 
       				    theData, theConverters, context);
			vdc.open();
			isSaved = vdc.isSaved();
		}
	}
	



}
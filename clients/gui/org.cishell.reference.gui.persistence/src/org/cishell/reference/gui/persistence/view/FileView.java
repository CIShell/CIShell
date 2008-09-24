package org.cishell.reference.gui.persistence.view;

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
import org.cishell.framework.data.DataProperty;
import org.cishell.reference.gui.persistence.FileUtil;
import org.cishell.service.conversion.ConversionException;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
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
    	}

    //show the contents of a file to the user
    public Data[] execute() throws AlgorithmExecutionException {
    	try {
        boolean lastSaveSuccessful = false;
        boolean isCSVFile          = false;
        String format;
        
        //tempFile = getTempFile(); TC181
        
               
        //for each data item we want to view...
        for (int i = 0; i < data.length; i++){
        	Object theData = data[i].getData();
        	format = data[i].getFormat();
        	String label = (String) data[i].getMetadata().get(DataProperty.LABEL);
        	 
        	//if it is a text file...
        	if (theData instanceof File ||
        		format.startsWith("file:text/") || 
        		format.startsWith("file-ext:")){
        		 
        		//if it is a csv text file...
        		if (format.startsWith("file:text/csv") || format.startsWith("file-ext:csv"))
        		{
        			//prepare to open it like a csv file
        		   tempFile = getTempFileCSV();
        		   isCSVFile = true;
        		    
        		}
        		else //it is just a regular text file
        		{ 
        			//prepare to open it like a normal text file
        			String fileName = FileUtil.extractFileName(label);
        			String extension = FileUtil.extractExtension(format);
        		   tempFile = FileUtil.getTempFile(fileName, extension, logger);  
        		}
        		
        		//copy out data into the temp file we just created.
        		copy((File)data[i].getData(), tempFile);
        		lastSaveSuccessful = true; 
        		
        		 
        		
        	}else {//the data item is in an in-memory format, and must be converted to a file format before the user can see it
        		
        		final Converter[] convertersCSV = conversionManager.findConverters(data[i], "file-ext:csv");
        		
        		//if the data item can be converted to a csv file ... do it.
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
        		} else { //it cannot be converted to a .csv
        			
        			//try to convert it to any other file format
        			
        		   final Converter[] converters = conversionManager.findConverters(data[i], "file-ext:*");
                
        		   //if it can't be converted to any file format...
            	   if (converters.length < 1) {
            		   //throw an error
            		  throw new AlgorithmExecutionException("No valid converters for data type: " + 
            				     data[i].getData().getClass().getName() +
            				    ". Please install a plugin that will save the data type to a file");
            	   }
            	   else if (converters.length == 1){ //if there is only file format it can be converted to
            		   //go ahead and convert the data item to that format
            	        Data newData = converters[0].convert(data[i]);  
            	        
            	        String fileName = FileUtil.extractFileName(label);
            	        String extension = FileUtil.extractExtension(newData.getFormat());
                        tempFile = FileUtil.getTempFile(fileName, extension, logger); 
            	        copy((File)newData.getData(), tempFile);    
            	  	    lastSaveSuccessful = true; 
            	   }
            	   else { //there is more than one format that the data item could be converted to
            		  
            		   //let the user choose
            		   
            		   //(get some eclipse UI stuff that we need to open the data viewer)
            		   
            		   Display display;
            	        IWorkbenchWindow[] windows;
            	        final Shell parentShell;
            	        
            	        windows = PlatformUI.getWorkbench().getWorkbenchWindows();
            	        if (windows.length == 0){
            	        	throw new AlgorithmExecutionException("Cannot get workbench window.");
            	        }
            	        parentShell = windows[0].getShell();
            	        display = PlatformUI.getWorkbench().getDisplay();
            	        
            	        //(open the data viewer, which lets the user choose which format they want to see the data item in.)
            	        
             		  if (!parentShell.isDisposed()) {
            		    	DataViewer dataViewer = new DataViewer(parentShell, data[i], converters);
            		    	display.syncExec(dataViewer);
             		    	lastSaveSuccessful = dataViewer.isSaved;
            			    tempFile = dataViewer.outputFile;
            		      }
            	    }
        	    }
        	}
        	
        	//if it's a CSV file
        	if (isCSVFile){//TC181
        		//prepare to open the file with the default csv program
        		Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        program = Program.findProgram("csv");
                    }});
        		 
        	}else 
        	{//it's any other file
        		//prepare to open it with the standard text editor.
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
        	
        	//if we can't find any program to open the file...
            if (program == null) {
            	//throw an error
            		throw new AlgorithmExecutionException( 
    					"No valid text viewer for the .txt file. " +
    					"The file is located at: "+tempFile.getAbsolutePath() +  
    					". Unable to open default text viewer.  File is located at: "+
    					tempFile.getAbsolutePath());
            }
            else {//we found a program to open the file
            	//open it, for real.
            	if (lastSaveSuccessful == true) { 
                    Display.getDefault().syncExec(new Runnable() {
                        public void run() {
                            program.execute(tempFile.getAbsolutePath());
                        }});
            	}
            }
        }
        return null;
    	} catch (ConversionException e1) {
    		throw new AlgorithmExecutionException("Error converting data to target view format.", e1);
    	} catch (Throwable e2){
    		throw new AlgorithmExecutionException(e2);
    	}
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
    
    public static boolean copy(File in, File out) throws AlgorithmExecutionException{
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
    	    throw new AlgorithmExecutionException("IOException during copy", ioe);
       	}
    }
    
	final class DataViewer  implements Runnable {
		Shell shell;
		boolean isSaved;
		File outputFile;
		Data theData;
		Converter[] theConverters;
			
		DataViewer (Shell parentShell, Data data, Converter[] converters){
			this.shell = parentShell;
			this.theData = data;
			this.theConverters = converters;
		}
		
		public void run() {
    		// lots of persisters found, return the chooser
			ViewDataChooser vdc = new ViewDataChooser("View", shell, 
       				    theData, theConverters, context, logger);
			vdc.open();
			isSaved = vdc.isSaved();
			outputFile = vdc.outputFile;
		}
	}
	



}
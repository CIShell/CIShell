package org.cishell.reference.gui.persistence.load;

import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/* 
 * @author Weixia(Bonnie) Huang (huangb@indiana.edu)
 */
public class FileLoad implements Algorithm {
	
    private final LogService logger;
    private final GUIBuilderService guiBuilder;
    
    private BundleContext bContext;
    private CIShellContext ciContext;
    private static String defaultLoadDirectory;
    
    public FileLoad(CIShellContext ciContext, BundleContext bContext, Dictionary prefProperties) {
        this.ciContext = ciContext;
        this.bContext = bContext;
       	logger = (LogService) ciContext.getService(LogService.class.getName());
        guiBuilder = (GUIBuilderService)ciContext.getService(GUIBuilderService.class.getName());
        
        //unpack preference properties
        if (defaultLoadDirectory == null) {
        	defaultLoadDirectory = (String) prefProperties.get("loadDir");
        }
    }

    
    public Data[] execute() throws AlgorithmExecutionException {
    	//prepare to run load dialog in GUI thread
    		
    	IWorkbenchWindow window = getFirstWorkbenchWindow();
    	Display display = PlatformUI.getWorkbench().getDisplay();
    	FileLoadRunnable fileLoader = new FileLoadRunnable (window);
    
    	//run load dialog in gui thread.

    	if (Thread.currentThread() != display.getThread()) {
    		display.syncExec(fileLoader);
		} else {
			fileLoader.run();
		}
    	
    	//return loaded file data
    	
    	Data[] loadedFileData = extractLoadedFileData(fileLoader);
    	return loadedFileData;
    }
    
    final class FileLoadRunnable implements Runnable{
    	boolean loadFileSuccess = false;
    	IWorkbenchWindow window;
    	//this is how we return values from the runnable
    	public ArrayList returnList = new ArrayList();
    	
    	FileLoadRunnable (IWorkbenchWindow window){
    		this.window = window;    		
    }    	
    	
    	public void run (){
   	    	FileDialog dialog = new FileDialog(window.getShell(), SWT.OPEN);
//		        if (currentDir == null) {
//		        	currentDir = new File(System.getProperty("osgi.install.area").replace("file:","")
//		            		+ "sampledata");
//                    
//                    if (!currentDir.exists()) {
//                    	currentDir = new File(System.getProperty("osgi.install.area").replace("file:","")
//    		            		+ "sampledata" +File.separator + "anything");
//                    }                   
//		        }
   	    	System.err.println("defaultLoadDirectory is " + defaultLoadDirectory);
   	    		File currentDir = new File(defaultLoadDirectory); //? good way to do this?
   	    		String absolutePath = currentDir.getAbsolutePath();
   	    		System.err.println("absolutePath:" + absolutePath);
   	    		String name = currentDir.getName();
   	    		System.err.println("name:" + name);
		        dialog.setFilterPath(absolutePath);
		       // dialog.setFilterPath(name);
		        dialog.setText("Select a File");
		        String fileName = dialog.open();
		        System.out.println("Resulting file name!:" + fileName);
		        if (fileName == null) {
		        	return;
		        }
		        
		      
		   	   File file = new File(fileName);
		   	   if (file.isDirectory()) {
		   		   System.out.println("directory");
		   		   defaultLoadDirectory = file.getAbsolutePath();
		   	   } else {
		   		   
		   		 //  File parentFile = file.getParentFile();
		   		  // if (parentFile != null) {
		   			System.out.println("file");
		   			   defaultLoadDirectory = file.getParentFile().getAbsolutePath();
		   		  // }
		   	   }
                
		   		String fileExtension = getFileExtension(file).toLowerCase();
	       		String filter = "(&(type=validator)(in_data=file-ext:"+fileExtension+"))";
		        try {

		       		// set the properties for the resource descriptor.
		       		// note that this relies on the fact that the compression is set
		       		// to nocompression by default.

		       		// get all the service references of converters that can load this type of file.
		            ServiceReference[] serviceRefList = bContext.getAllServiceReferences(
		                    AlgorithmFactory.class.getName(), filter);
	        		

		       		// no converters found means the file format is not supported
		       		if (serviceRefList == null || serviceRefList.length == 0){
		       			guiBuilder.showError("Unsupported File Format", "Sorry, the file format: *."+fileExtension+" is not yet supported.", 
		       					"Sorry, the file format: *."+fileExtension+" is not yet supported. \n"+
		       					"Please send your requests to cishell-developers@lists.sourceforge.net. \n"
		       					+"Thank you.");
	        		}
		       		
	    			//<filename>[.<data model type>][.<index>]
	    			// only one persister found, so load the model
		       		else if (serviceRefList.length == 1){	        			
	        			AlgorithmFactory persister = (AlgorithmFactory)bContext.getService(serviceRefList[0]);
	    	            Data[] dm = new Data[]{new BasicData(file.getPath(), String.class.getName()) };
	    	            dm = persister.createAlgorithm(dm, null, ciContext).execute();
	    	            if (dm != null){
	    	            	loadFileSuccess = true;
	    	            	logger.log(LogService.LOG_INFO, "Loaded: "+file.getPath());
	    	            	for (int i=0; i<dm.length; i++)
	    	            		returnList.add(dm[i]);
	    	            }
		    			
		    			
	        		}
	  
	        		// lots of persisters found, return the chooser
		       		else if (serviceRefList.length > 1){
//		       			new LoadDataChooser("Load", file, window.getShell(), 
//	    							ciContext, bContext, serviceRefList, returnList).open();
		       			for (int index=0; index<serviceRefList.length; index++){
		        			AlgorithmFactory persister = (AlgorithmFactory)bContext.getService(serviceRefList[index]);
		        			Data[] dm = new Data[]{new BasicData(file.getPath(), String.class.getName()) };
		    	            dm = persister.createAlgorithm(dm, null, ciContext).execute();
		    	            if (dm != null ){
		    	            	loadFileSuccess = true;
		    	            	logger.log(LogService.LOG_INFO, "Loaded: "+file.getPath());
		    	            	for (int i=0; i<dm.length; i++){
			    	            	returnList.add(dm[i]);
		    	            	}
		    	            	break;
		    	            }
		       			}

		       		}
		       		/*
		       		 * Bonnie: I commented out the following functions since when
		       		 * the application failed to load an nwb file, etc, the reader
		       		 * has report the error. It does not need this second error display.
		       		 * But maybe not all file readers will generate the error display if
		       		 * a failure occurs...
		       		 */
/*		       		if (serviceRefList != null){
		       			if(serviceRefList.length >0 && !loadFileSuccess){
		       			guiBuilder.showError("Can Not Load The File", 
		       					"Sorry, it's very possible that you have a wrong file format," +
		       					"since the file can not be loaded to the application.",
		       					
	       					"Please check Data Formats that this application can support at "+
	       					"https://nwb.slis.indiana.edu/community/?n=Algorithms.HomePage." +
	       					"And send your requests or report the problem to "+
	       					"cishell-developers@lists.sourceforge.net. \n"+"Thank you.");
		       			}

		       		}
*/		       		


	        	}catch (Exception e){
	        		throw new RuntimeException(e);   	
	        	}

			}//end run()
   
    	public String getFileExtension(File theFile) {
    	    String fileName = theFile.getName() ;
    	    String extension ;
    		if (fileName.lastIndexOf(".") != -1)
    		    extension = fileName.substring(fileName.lastIndexOf(".")+1) ;
    		else
    		    extension = "" ;
    		return extension ;
    	}
    } //end class
    
    private IWorkbenchWindow getFirstWorkbenchWindow() throws AlgorithmExecutionException {
    	final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
    	if (windows.length ==0){
    		throw new AlgorithmExecutionException("Cannot obtain workbench window needed to open dialog.");
    	} else {
    		return windows[0];
    	}
    }
    
    private Data[] extractLoadedFileData(FileLoadRunnable dataUpdater) throws AlgorithmExecutionException {
    	Data[] loadedFileData;
    	try {
    	if (!dataUpdater.returnList.isEmpty()){
    		int size = dataUpdater.returnList.size();
    		loadedFileData = new Data[size];
    		for(int index=0; index<size; index++){
    			loadedFileData[index]=(Data)dataUpdater.returnList.get(index);
    		}
    		return loadedFileData;
    	}
    	else {
    		this.logger.log(LogService.LOG_WARNING, "File loading canceled");
    		return new Data[0];
    	} 
    	} catch (Throwable e2) {
    		throw new AlgorithmExecutionException(e2);
    	}
    }
}
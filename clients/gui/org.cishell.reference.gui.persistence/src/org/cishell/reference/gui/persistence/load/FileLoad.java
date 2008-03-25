package org.cishell.reference.gui.persistence.load;

import java.io.File;
import java.util.ArrayList;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
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
public class FileLoad implements Algorithm{

    private static File currentDir;
    
    private final LogService logger;
    private final GUIBuilderService guiBuilder;
    
    private BundleContext bContext;
    private CIShellContext ciContext;
    
    public FileLoad(CIShellContext ciContext, BundleContext bContext) {
        this.ciContext = ciContext;
        this.bContext = bContext;
       	logger = (LogService) ciContext.getService(LogService.class.getName());
        guiBuilder = (GUIBuilderService)ciContext.getService(GUIBuilderService.class.getName());

    }
 
    public void selectionChanged(IAction action, ISelection selection) {

    }
    
    public Data[] execute() throws AlgorithmExecutionException {
    	try {
//    	int counter = PlatformUI.getWorkbench().getWorkbenchWindowCount();
//    	System.out.println("counter is "+counter);
//      ?? why getActiveWorkbenchWindow() didn't work??
    	Data[] returnDM;

    	final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
    	if (windows.length ==0){
    		throw new AlgorithmExecutionException("Cannot obtain workbench window needed to open dialog.");
    	}
    
    	Display display = PlatformUI.getWorkbench().getDisplay();
    	DataUpdater dataUpdater = new DataUpdater (windows[0]);
    	
    	if (Thread.currentThread() != display.getThread()) {
    		display.syncExec(dataUpdater);
		} else {
			dataUpdater.run();
		}
    	
    	if (!dataUpdater.returnList.isEmpty()){
    		int size = dataUpdater.returnList.size();
    		returnDM = new Data[size];
    		for(int index=0; index<size; index++){
    			returnDM[index]=(Data)dataUpdater.returnList.get(index);
    		}
    		return returnDM;
    	}
    	else {
    		throw new AlgorithmExecutionException("No data could be loaded.");
    	}
    	} catch (AlgorithmExecutionException e1) {
    		throw e1;
    	} catch (Throwable e2) {
    		throw new AlgorithmExecutionException(e2);
    	}
    }
    
	public static String getFileExtension(File theFile) {
	    String fileName = theFile.getName() ;
	    String extension ;
		if (fileName.lastIndexOf(".") != -1)
		    extension = fileName.substring(fileName.lastIndexOf(".")+1) ;
		else
		    extension = "" ;
		return extension ;
	}
    
    final class DataUpdater implements Runnable{
    	boolean loadFileSuccess = false;
    	IWorkbenchWindow window;
    	ArrayList returnList = new ArrayList();
    	
    	DataUpdater (IWorkbenchWindow window){
    		this.window = window;    		
    	}    	
    	
    	public void run (){
    	
    	    	FileDialog dialog = new FileDialog(window.getShell(), SWT.OPEN);
		        if (currentDir == null) {
		        	currentDir = new File(System.getProperty("osgi.install.area").replace("file:","")
		            		+ "sampledata");
                    
                    if (!currentDir.exists()) {
                    	currentDir = new File(System.getProperty("osgi.install.area").replace("file:","")
    		            		+ "sampledata" +File.separator + "anything");
                    }                   
		        }
		        dialog.setFilterPath(currentDir.getPath());
		        dialog.setText("Select a File");
		        String fileName = dialog.open();
		        if (fileName == null) {
		        	return;
		        }
		        
		   		File file = new File(fileName);
		        
                if (file.isDirectory()) {
                    currentDir = new File(file + File.separator + "anything");
                } else {
                    currentDir = new File(file.getParent() + File.separator + "anything");
                }
                
		   		String fileExtension = getFileExtension(file).toLowerCase();
	       		String filter = "(&(type=converter)(in_data=file-ext:"+fileExtension+"))";
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
    } //end class
    	

}
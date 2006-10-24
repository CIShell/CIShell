package org.cishell.reference.gui.persistence.load;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;


import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.BasicData;
import org.cishell.service.guibuilder.GUIBuilderService;

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
    
    public Data[] execute() {
//    	int counter = PlatformUI.getWorkbench().getWorkbenchWindowCount();
//    	System.out.println("counter is "+counter);
//      ?? why getActiveWorkbenchWindow() didn't work??
    	Data[] returnDM;

    	final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
    	if (windows.length ==0){
    		return null;
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
    		return null;
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
		            currentDir = new File(System.getProperty("user.dir") + File.separator + "sampledata");
                    
                    if (!currentDir.exists()) {
                        currentDir = new File(System.getProperty("user.home") + File.separator + "anything");
                    } else {
                        currentDir = new File(System.getProperty("user.dir") + File.separator + "sampledata" + File.separator + "anything");
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
                
		   		String fileExtension = getFileExtension(file);
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
		       			guiBuilder.showError("Unsupported File Format", "Sorry, the file format: *."+fileExtension+" is not supported so far.", 
		       					"Sorry, the file format: *."+fileExtension+" is not supported so far. \n"+
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
		       		if (serviceRefList != null){
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
		       		


	        	}catch (Exception e){
	        		e.printStackTrace();    	
	        	}

			}//end run()
    } //end class
    	

}
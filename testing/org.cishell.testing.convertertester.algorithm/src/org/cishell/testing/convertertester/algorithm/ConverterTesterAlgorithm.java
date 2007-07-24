package org.cishell.testing.convertertester.algorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.data.Data;
import org.cishell.testing.convertertester.core.tester.ConverterTester;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;

//TODO: OMG Fix this whole thing as soon as possible.

public class ConverterTesterAlgorithm implements Algorithm {
    private static File currentDir;
    
    Data[] data;
    Dictionary parameters;
    CIShellContext context;
    ConverterTester tester;
    
    public ConverterTesterAlgorithm(Data[] data, Dictionary parameters,
    		CIShellContext csContext, BundleContext bContext ) {
        this.data = data;
        this.parameters = parameters;
        this.context = csContext;
        
        this.tester  = new ConverterTester(bContext, csContext);
//        ServiceReference ctReference = bContext.getServiceReference(ConverterTester.class.getName());
//        this.tester = (ConverterTester) bContext.getService(ctReference);
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
		   		
		   		try {
		   		tester.runTests(file);
		   		} catch (Exception e) {
		   			System.out.println("Why oh why am I catching type Exception?");
		   			System.out.println(e);
		   		}
    }
   }
    
}
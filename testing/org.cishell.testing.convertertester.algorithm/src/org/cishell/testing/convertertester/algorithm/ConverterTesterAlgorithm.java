package org.cishell.testing.convertertester.algorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.testing.convertertester.core.tester2.ConverterTester2;
import org.cishell.testing.convertertester.core.tester2.reportgen.ConverterReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.OverviewReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.ReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.SampleResultReportGenerator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

//TODO: Only just barely usable to run converter tester from GUI. 
//TODO: Make it nice eventually

public class ConverterTesterAlgorithm implements Algorithm, AlgorithmProperty {
    private static File currentDir;
    
    private Data[] data;
    private Dictionary parameters;
    private CIShellContext cContext;
    private BundleContext bContext;
    private LogService logger;
    
    public ConverterTesterAlgorithm(Data[] data, Dictionary parameters,
    		CIShellContext cContext, BundleContext bContext ) {
        this.data = data;
        this.parameters = parameters;
        this.cContext = cContext;
        this.bContext = bContext;
        
        this.logger = (LogService) cContext.getService(
				LogService.class.getName());
    }

    public Data[] execute() {
    	
    	Data[] returnDM;

    	final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
    	if (windows.length == 0) {
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
		        dialog.setText("Select a File: Too bad we aren't using it, haha!");
		        String fileName = dialog.open();
		        if (fileName == null) {
		        	return;
		        }
		   		
		   		try {
		   			ConverterTester2 ct = new ConverterTester2(logger);
		   			ServiceReference[] refs = getServiceReferences();
					OverviewReportGenerator overviewGen = 
						new OverviewReportGenerator();
					SampleResultReportGenerator sampleGen =
						new SampleResultReportGenerator(10, 5);
		   			ConverterReportGenerator convGen = 
		   				new ConverterReportGenerator();
					
		   			ct.execute(refs, cContext, bContext, logger,
		   					new ReportGenerator[] {overviewGen, sampleGen, convGen});
		   			
		   			File overviewReport = overviewGen.getReport();	
		   			BasicData overviewReportData = 
		   				createReportData(overviewReport, "Overview");
		   			returnList.add(overviewReportData);
		   			
		   			
		   			File sampleReport   = sampleGen.getReport();
		   			BasicData sampleReportData = 
		   				createReportData(sampleReport, "Sample Test Results");	
		   			returnList.add(sampleReportData);
		   			

		   			File convReport = convGen.getReport();
		   			BasicData convReportData = 
		   				createReportData(convReport, "Basic Converter Results");
		   			returnList.add(convReportData);
		   			
		   		} catch (Exception e) {
		   			System.out.println("Why oh why am I catching type Exception?");
		   			System.out.println(e);
		   			e.printStackTrace();
		   		}
    }
   }
    
    private BasicData createReportData(Object report, String label) {
			BasicData reportData = new BasicData(report, "file:text/plain");
			Dictionary metadata = reportData.getMetaData();
			metadata.put(DataProperty.LABEL, label);
			metadata.put(DataProperty.TYPE, DataProperty.TEXT_TYPE);		
			return reportData;
    }
    
    private ServiceReference[] getServiceReferences() {
		  String filter = "(&("+ALGORITHM_TYPE+"="+TYPE_CONVERTER+"))";// +

		  try {
		  ServiceReference[] refs = bContext.getServiceReferences(
				  AlgorithmFactory.class.getName(), filter);
		  
		  return refs;
		  } catch (InvalidSyntaxException e) {
			  System.out.println("OOPS!");
			  System.out.println(e);
			  return null;
		  }
	}
    
}
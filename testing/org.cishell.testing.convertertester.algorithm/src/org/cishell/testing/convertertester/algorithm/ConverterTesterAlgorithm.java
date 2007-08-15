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
import org.cishell.testing.convertertester.core.converter.graph.ConverterGraph;
import org.cishell.testing.convertertester.core.tester2.ConverterTester2;
import org.cishell.testing.convertertester.core.tester2.reportgen.ReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.allconvs.AllConvsReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.alltests.AllTestsReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.convgraph.GraphReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.AllConvsReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.AllTestsReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.ConvReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.FilePassReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.TestReport;
import org.eclipse.swt.widgets.Display;
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
    private LogService log;
    
    public ConverterTesterAlgorithm(Data[] data, Dictionary parameters,
    		CIShellContext cContext, BundleContext bContext ) {
        this.data = data;
        this.parameters = parameters;
        this.cContext = cContext;
        this.bContext = bContext;
        
        this.log = (LogService) cContext.getService(
				LogService.class.getName());
    }

    public Data[] execute() {
    	
    	Data[] returnDM;

    	final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
    	if (windows.length == 0) {
    		return null;
    	}
    
    	Display display = PlatformUI.getWorkbench().getDisplay();
    	DataUpdater dataUpdater = new DataUpdater (windows[0], this.log);
    	
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
    	LogService log;
    	
    	DataUpdater (IWorkbenchWindow window, LogService log){
    		this.log = log;
    		this.window = window;    		
    	}    	
    	
    	public void run (){
    	
//    	    	FileDialog dialog = new FileDialog(window.getShell(), SWT.OPEN);
//		        if (currentDir == null) {
//		            currentDir = new File(System.getProperty("user.dir") + File.separator + "sampledata");
//                    
//                    if (!currentDir.exists()) {
//                        currentDir = new File(System.getProperty("user.home") + File.separator + "anything");
//                    } else {
//                        currentDir = new File(System.getProperty("user.dir") + File.separator + "sampledata" + File.separator + "anything");
//                    }
//		        }
//		        dialog.setFilterPath(currentDir.getPath());
//		        dialog.setText("Select a File: Too bad we aren't using it, haha!");
//		        String fileName = dialog.open();
//		        if (fileName == null) {
//		        	return;
//		        }
		   		
		   		try {
		   			ConverterTester2 ct = new ConverterTester2(log);
		   			ServiceReference[] refs = getServiceReferences();
		   			
		   			ConverterGraph converterGraph = new ConverterGraph(refs, bContext, log);
		   			
		   			File nwbGraph = converterGraph.asNWB();
		   			
		   			AllTestsReportGenerator allGen     = new AllTestsReportGenerator(this.log);
		   			AllConvsReportGenerator allConvGen = new AllConvsReportGenerator(this.log);
		   			GraphReportGenerator    graphGen   = new GraphReportGenerator(nwbGraph, this.log);
		   			
		   			ct.execute(converterGraph, new ReportGenerator[] {allGen, allConvGen, graphGen}, cContext, bContext);
		   			
		   			AllTestsReport allReport = allGen.getAllTestsReport();
		   			File allReportFile = allReport.getAllTestsReport();
		   			Data allReportData = createReportData(allReportFile,
		   					allReport.getName() , null);
		   			addReturn(allReportData);
		   			
		   			TestReport[] sTestReports = allReport.getSuccessfulTestReports();
		   			addFilePasses(sTestReports, allReportData);
		   			
		   			TestReport[] ppTestReports = allReport.getPartialSuccessTestReports();
		   			addFilePasses(ppTestReports, allReportData);
		   			
		   			TestReport[] fTestReports = allReport.getFailedTestReports();
		   			addFilePasses(fTestReports, allReportData);
		   			
		   			//all conv report
		   			
		   			AllConvsReport allConvReport = allConvGen.getAllConvsReport();
		   			File allConvReportFile = allConvReport.getReport();
		   			Data allConvReportData = createReportData(allConvReportFile, allConvReport.getName(),
		   					null);
		   			addReturn(allConvReportData);
		   			
		   			ConvReport[] convReports = allConvReport.getConverterReports();
		   			for (int ii = 0; ii < convReports.length; ii++) {
		   				ConvReport convReport = convReports[ii];
		   				File convReportFile = convReport.getReport();
		   				Data convReportData = createReportData(convReportFile, convReport.getName(), allConvReportData);
		   				addReturn(convReportData);
		   				
		   				TestReport[] trs = convReport.getTestReports();
		   				addFilePasses(trs, convReportData);
		   			}
		   			
		   			File graphReportFile = graphGen.getGraphReport();
		   			Data graphReport = createReportData(graphReportFile, "Annotated Graph Report", null,
		   					"file:text/nwb", DataProperty.NETWORK_TYPE);
		   			addReturn(graphReport);
		   			
		   		} catch (Exception e) {
		   			System.out.println("Why oh why am I catching type Exception?");
		   			System.out.println(e);
		   			e.printStackTrace();
		   		}
    }
    	
        private void addReturn(Data report) {
        	this.returnList.add(report);
        }
        
        
        private void addFilePasses(TestReport[] testReports, Data allReportData) {
    			for (int ii = 0; ii < testReports.length; ii++) {
    				TestReport tr = testReports[ii];
    				File testReportFile = tr.getTestReport();
    				System.out.println("In algorithm, file pass name is : " + tr.getName());
    				System.out.println("In algorithm FILE name is : " + testReportFile.getName());
    				Data testReportData = createReportData(testReportFile,
    						tr.getName(), allReportData);
    				addReturn(testReportData);
    				
    				FilePassReport[] sFilePassReports = tr.getSuccessfulFilePassReports();
    				for (int kk = 0; kk < sFilePassReports.length; kk++) {
    					FilePassReport fp = sFilePassReports[kk];
    					File fpFile = fp.getFilePassReport();
    					Data fpData = createReportData(fpFile, fp.getName(),
    							testReportData);
    					addReturn(fpData);
    				}
    				
    				FilePassReport[] fFilePassReports = tr.getFailedFilePassReports();	
    				for (int kk = 0; kk < fFilePassReports.length; kk++) {
    					FilePassReport fp = fFilePassReports[kk];
    					File fpFile = fp.getFilePassReport();
    					Data fpData = createReportData(fpFile, fp.getName(),
    							testReportData);
    					addReturn(fpData);
    				}
    			}
        }
        
        private Data createReportData(Object report, String label, Data parent, String format, String type) {
        	Data reportData = new BasicData(report, format);
			Dictionary metadata = reportData.getMetaData();
			metadata.put(DataProperty.LABEL, label);
			metadata.put(DataProperty.TYPE, type);
			if (parent != null) {
				metadata.put(DataProperty.PARENT, parent);
			}
			return reportData;
        }
        
        private Data createReportData(Object report, String label, Data parent) {
        	return createReportData(report, label, parent, "file:text/plain", DataProperty.TEXT_TYPE);
        }
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
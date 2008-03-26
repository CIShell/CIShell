package org.cishell.testing.convertertester.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.testing.convertertester.algorithm.pathfilter.ConvAndHopFilter;
import org.cishell.testing.convertertester.algorithm.pathfilter.HopFilter;
import org.cishell.testing.convertertester.core.tester2.ConverterTester2;
import org.cishell.testing.convertertester.core.tester2.reportgen.ReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.allconvs.AllConvsReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.allerrors.AllErrorReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.alltests.AllTestsReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.convgraph.AnnotatedGraphReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.convgraph.GraphReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.readme.ReadMeReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.AllConvsReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.AllErrorReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.AllTestsReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.ConvReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.ConvertedDataReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.FilePassReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.ReadMeReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.TestReport;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

//TODO: Maybe let the user specify which converters he/she wants to test, or other things
//TODO: Make it progress-trackable

public class ConverterTesterAlgorithm implements Algorithm,
	AlgorithmProperty {

    private CIShellContext cContext;
    private BundleContext bContext;
    private LogService log;
    
    private boolean testAllConvs;
    private String selectedConvName;
    private int numHops;
    
    public ConverterTesterAlgorithm(Data[] data, Dictionary parameters,
    		CIShellContext cContext, BundleContext bContext ) {
        this.cContext = cContext;
        this.bContext = bContext;
        
        this.log = (LogService) cContext.getService(
				LogService.class.getName());
        
        
        this.testAllConvs = ((Boolean) parameters.get(
        		ConverterTesterAlgorithmFactory.TEST_ALL_CONVS_PARAM_ID)).booleanValue();
        this.selectedConvName = ((String) parameters.get(
        		ConverterTesterAlgorithmFactory.SELECTED_CONVERTER_PARAM_ID));
        this.numHops = ((Integer) parameters.get(
        		ConverterTesterAlgorithmFactory.NUM_HOPS_PARAM_ID)).intValue();
        
    }

    public Data[] execute() {
//    	this.log.log(LogService.LOG_INFO, 
//    			                                               "\r\n" +
//    			"-------NOTICE-------" +                       "\r\n" + 	
//    			"The Converter Tester will take " +
//    			"some time to run all the tests. \r\n" +
//    			"Thank you for waiting :)" +                   "\r\n" +
//    			"-----END NOTICE-----                           \r\n");
    	
    	Data[] returnDM;

    	final IWorkbenchWindow[] windows = 
    		PlatformUI.getWorkbench().getWorkbenchWindows();
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
    
    final class DataUpdater implements Runnable {
    	boolean loadFileSuccess = false;
    	IWorkbenchWindow window;
    	ArrayList returnList = new ArrayList();
    	LogService log;
    	
    	DataUpdater (IWorkbenchWindow window, LogService log){
    		this.log = log;
    		this.window = window;    		
    	}    	
    	
    	public void run () {
    		
    		try {
    			System.out.println("getting converter references....");
    				//get all the converters
		   			ServiceReference[] convRefs = 
		   				ConverterTesterAlgorithmUtil.
		   				getConverterReferences(bContext);
		   			

		   			
		   			//initialize all the report generators
		   			
		   			AllTestsReportGenerator       allGen     = 
		   				new AllTestsReportGenerator(this.log);
		   			AllConvsReportGenerator       allConvGen = 
		   				new AllConvsReportGenerator(this.log);
		   			AllErrorReportGenerator      allErrGen  =
		   				new AllErrorReportGenerator(this.log);
		   			GraphReportGenerator origGraphGen        =
		   				new GraphReportGenerator(this.log);
		   			AnnotatedGraphReportGenerator graphGen   = 
		   				new AnnotatedGraphReportGenerator(this.log);
		   			ReadMeReportGenerator         readmeGen  = 
		   				new ReadMeReportGenerator(this.log);
		   			
		   			/*
		   			 * execute the tests, and provide the results to the 
		   			 * report generators
		   			 */
		   			
		   			System.out.println("Executing tests...");
		   			
		   			ConverterTester2 ct = new ConverterTester2(log);
		   	
		   			if (testAllConvs) {
		   				ct.execute(convRefs,
			   					new ReportGenerator[] 
			   					   {allGen, allConvGen, allErrGen,
			   					graphGen, origGraphGen, readmeGen},
			   					cContext, bContext,
			   					new HopFilter(numHops));
		   			} else {
		   				ct.execute(convRefs,
			   					new ReportGenerator[] 
			   					   {allGen, allConvGen, allErrGen,
			   					graphGen, origGraphGen, readmeGen},
			   					cContext, bContext,
			   					new ConvAndHopFilter(selectedConvName, numHops));
		   			}
		   			/*
		   			 * report generators have now been supplied with the test
		   			 * results, and their reports can now be extracted.
		   			 */
		   			
		   			System.out.println("Returning reports...");
		   			
		   			//return readme report
		   			
		   			ReadMeReport readmeReport = readmeGen.getReadMe();
		   			File readmeFile = readmeReport.getReportFile();
		   			Data readMeData = createReportData(readmeFile,
		   					readmeReport.getName(), null);
		   			addReturn(readMeData);
		   			
		   			//return all converters report
		   			
		   			AllConvsReport allConvReport = 
		   				allConvGen.getAllConvsReport();
		   			File allConvReportFile = allConvReport.getReport();
		   			Data allConvReportData = 
		   				createReportData(allConvReportFile,
		   						allConvReport.getName(),
		   					null);
		   			addReturn(allConvReportData);
		   			
		   			ConvReport[] convReports = 
		   				allConvReport.getConverterReports();
		   			for (int ii = 0; ii < convReports.length; ii++) {
		   				ConvReport convReport = convReports[ii];
		   				File convReportFile = convReport.getReport();
		   				Data convReportData =
		   					createReportData(convReportFile,
		   							convReport.getName(), allConvReportData);
		   				addReturn(convReportData);
		   				
		   				TestReport[] trs = convReport.getTestReports();
		   				addFilePasses(trs, convReportData);
		   			}
		   			
		   			//return all tests report
		   			
		   			AllTestsReport allReport = allGen.getAllTestsReport();
		   			File allReportFile = allReport.getAllTestsReport();
		   			Data allReportData = createReportData(allReportFile,
		   					allReport.getName() , null);
		   			addReturn(allReportData);
		   			
		   			TestReport[] sTestReports = 
		   				allReport.getSuccessfulTestReports();
		   			addFilePasses(sTestReports, allReportData);
		   			
		   			TestReport[] ppTestReports = 
		   				allReport.getPartialSuccessTestReports();
		   			addFilePasses(ppTestReports, allReportData);
		   			
		   			TestReport[] fTestReports = 
		   				allReport.getFailedTestReports();
		   			addFilePasses(fTestReports, allReportData);
		   			
		   			//return all errors report
		   			
		   			AllErrorReport allErrorReport = 
		   				allErrGen.getAllErrorsReport();
		   			File allErrReportFile = allErrorReport.getReportFile();
		   			Data allErrReport = createReportData(allErrReportFile,
		   					allErrorReport.getName(), null);
		   			addReturn(allErrReport);
		   			
		   			
		   			//return annotated graph report
		   			
		   			File graphReportFile = graphGen.getGraphReport();
		   			Data graphReport = createReportData(graphReportFile,
		   					"Annotated Converter Graph", null,
		   					"file:text/nwb", DataProperty.NETWORK_TYPE);
		   			addReturn(graphReport);
		   			
		   			//return original graph report
		   			
		   			File origGraphReportFile = origGraphGen.getGraphReport();
		   			Data origGraphReport = createReportData(
		   					origGraphReportFile,
		   					"Original Converter Graph", null,
		   					"file:text/nwb", DataProperty.NETWORK_TYPE);
		   			addReturn(origGraphReport);
		   			
    		} catch (Exception e) {
    			this.log.log(LogService.LOG_ERROR, "Converter Tester Failed.",
    					e);
    			e.printStackTrace();
    		}
    }
    	
    	/**
    	 * Add a report to a list of reports that are later returned.
    	 * @param report the report to be returned from this algorithm
    	 */
        private void addReturn(Data report) {
        	if (report != null) {
        		this.returnList.add(report);
        	}
        }
        
        
        /**
         * Returns file pass reports associated with tests or converters.
         * @param testReports reports to be returned as children or test or 
         * converter
         * @param parent the parent of the file pass
         */
        private void addFilePasses(TestReport[] testReports, Data parent) {
    			for (int ii = 0; ii < testReports.length; ii++) {
    				TestReport tr = testReports[ii];
    				File testReportFile = tr.getTestReport();
    				Data testReportData = createReportData(testReportFile,
    						tr.getName(), parent);
    				addReturn(testReportData);
    				
    				FilePassReport[] sFilePassReports =
    					tr.getSuccessfulFilePassReports();
    				for (int kk = 0; kk < sFilePassReports.length; kk++) {
    					FilePassReport fp = sFilePassReports[kk];
    					File fpFile = fp.getFilePassReport();
    					Data fpData = createReportData(fpFile, fp.getName(),
    							testReportData);
    					addReturn(fpData);
    					addAllConvertedDataReports(fp, fpData);
    					}
    				
    				FilePassReport[] fFilePassReports = 
    					tr.getFailedFilePassReports();	
    				for (int kk = 0; kk < fFilePassReports.length; kk++) {
    					FilePassReport fp = fFilePassReports[kk];
    					File fpFile = fp.getFilePassReport();
    					Data fpData = createReportData(fpFile, fp.getName(),
    							testReportData);
    					addReturn(fpData);
    					addAllConvertedDataReports(fp, fpData);
    					}
    			}
        }
        
        private void addConvertedDataReports(ConvertedDataReport[] cdrs, Data fpData) {
    		if (cdrs != null) {
    			for (int mm = 0; mm < cdrs.length; mm++) {
    				File cdrFile = cdrs[mm].getReport();
    				Data cdrData = createReportData(cdrFile, cdrs[mm].getName(), fpData);
    				addReturn(cdrData);
    			}
    		}
    	}
        
    	private void addAllConvertedDataReports(FilePassReport fp, Data fpData) {
    		try {
    		ConvertedDataReport[] testCDRs = fp.getTestConvertedDataReports();
			File testDummyParentFile = File.createTempFile("testdummyfile", "");
			Data testDummyParentData = createReportData(testDummyParentFile, "test phase", fpData);
			addReturn(testDummyParentData);
			addConvertedDataReports(testCDRs, testDummyParentData);
			ConvertedDataReport[] origCompareCDRs = fp.getOrigCompareConvertedDataReports();
			File origCompareDummyParentFile = File.createTempFile("origcomparedummyfile", "");
			Data origCompareDummyParentData = createReportData(origCompareDummyParentFile, "compare phase for original file", fpData);
			addReturn(origCompareDummyParentData);
			addConvertedDataReports(origCompareCDRs, origCompareDummyParentData);
			ConvertedDataReport[] resultCompareCDRs = fp.getResultCompareConvertedDataReports();
			File resultCompareDummyParentFile = File.createTempFile("origresultdummyfile", "");
			Data resultCompareDummyParentData = createReportData(resultCompareDummyParentFile, "compare phase for resulting file", fpData);
			addReturn(resultCompareDummyParentData);
			addConvertedDataReports(resultCompareCDRs, resultCompareDummyParentData);
    		} catch (IOException e) {
    			this.log.log(LogService.LOG_WARNING, "Unable to write converted data reports due to IO Error");
    		}
			
    }
        
        /**
         * Wraps the report with metadata in a form that is ready to be 
         * returned from the algorithm.
         * 
         * @param report the report to be turned into data
         * @param label how the report will be labeled in the data manager 
         * window
         * @param parent which report this report will hang from 
         * (null if it is not a child of any report)
         * @param format The file format or class name of the report
         * @param type whether the report is a network or text file
         * @return the report encapsulated in data, ready to be returned.
         */
        private Data createReportData(Object report, String label,
        		Data parent, String format, String type) {
        	Data reportData = new BasicData(report, format);
			Dictionary metadata = reportData.getMetadata();
			if (label == null) {
				label = "no label";
			}
			
			if (type == null) {
				type = "No type";
			}
			metadata.put(DataProperty.LABEL, label);
			metadata.put(DataProperty.TYPE, type);
			if (parent != null) {
				metadata.put(DataProperty.PARENT, parent);
			}
			return reportData;
        }
        
        /**
         * Alternate version of createReportData that assumes the report
         *  is a plain text file
         * @param report the report to be turned into data
         * @param label how the report will be labeled in the data manager
         *  window
         * @param parent which report this report will hang from 
         * (null if it is not a child of any report)
         * @return the report encapsulated in data, ready to be returned.
         */
        private Data createReportData(Object report, String label,
        		Data parent) {
        	return createReportData(report, label, parent, "file:text/plain",
        			DataProperty.TEXT_TYPE);
        }
        
       
   }
    
  
    
    
}
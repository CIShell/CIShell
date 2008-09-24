package org.cishell.testing.convertertester.core.tester2;

import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.testing.convertertester.core.converter.graph.Converter;
import org.cishell.testing.convertertester.core.converter.graph.ConverterGraph;
import org.cishell.testing.convertertester.core.converter.graph.ConverterPath;
import org.cishell.testing.convertertester.core.tester2.graphcomparison.IdsNotPreservedComparer;
import org.cishell.testing.convertertester.core.tester2.graphcomparison.IdsPreservedComparer;
import org.cishell.testing.convertertester.core.tester2.graphcomparison.LossyComparer;
import org.cishell.testing.convertertester.core.tester2.graphcomparison.NewGraphComparer;
import org.cishell.testing.convertertester.core.tester2.pathfilter.PathFilter;
import org.cishell.testing.convertertester.core.tester2.reportgen.ReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.faultanalysis.ChanceAtFaultHeuristic;
import org.cishell.testing.convertertester.core.tester2.reportgen.faultanalysis.ErrorProximityHeuristic;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllConvsResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllTestsResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.TestResult;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * @author mwlinnem
 *
 */
public class ConverterTester2 implements AlgorithmProperty {

	private LogService log;
	
	private TestFileKeeper testFileKeeper;
	private TestRunner testRunner;
	
	public ConverterTester2(LogService log) {
		
		//TODO: Make this not a huge hack
		new File(ReportGenerator.TEMP_DIR).mkdir();
		
		this.log = log;
		this.testFileKeeper = 
			new TestFileKeeper(TestFileKeeper.DEFAULT_ROOT_DIR, log);
		this.testRunner = new DefaultTestRunner(log);
	}
	
	public void execute(
			ServiceReference[] converterRefs,
			ReportGenerator[] reportGenerators,
			CIShellContext cContext,
			BundleContext bContext) {
		execute(converterRefs, reportGenerators, cContext, bContext,
				new AcceptAllFilter());
	}
	
	/**
	 * Tests the provided converters, and passes the results of those tests to
	 * the report generators. Report Generators are side-effected, which takes
	 * the place of a return value.
	 * @param converterRefs service reference for all the converters.
	 * @param reportGenerators process the test results.
	 * @param cContext the CIShell Context
	 * @param bContext the Bundle Context
	 */
	public void execute(
			ServiceReference[] converterRefs,
			ReportGenerator[] reportGenerators,
			CIShellContext cContext,
			BundleContext bContext,
			PathFilter testPathFilter) {
		
		//generate all the converter paths
	
		ConverterGraph converterGraph = new ConverterGraph(converterRefs,
				bContext, this.log);
		
		//run the tests
	
		TestResult[] rawResults = 
			runAllTests(converterGraph, testPathFilter, cContext, bContext);
		
		AllTestsResult allTestsResult = new AllTestsResult(rawResults);
		
		//analyze the test results to extract more useful info
		
		Converter[] allConverters = converterGraph.getAllConverters();
		
		ChanceAtFaultHeuristic faultHeuristic = 
			new ErrorProximityHeuristic();
		AllConvsResult allConvertersResult = 
			ConvResultMaker.generate(allTestsResult, allConverters,
					faultHeuristic);
		
		//feed test results to the report generators
		
		for (int ii = 0; ii < reportGenerators.length; ii++) {
			ReportGenerator reportGenerator = reportGenerators[ii];
			
			reportGenerator.generateReport(allTestsResult,
					allConvertersResult, converterGraph.asNWB());
		}
	}
	
	public TestResult[] runAllTests(
			ConverterGraph convGraph,
			PathFilter testPathFilter, 
			CIShellContext cContext,
			BundleContext bContext) {
		
		Map fileFormatToTestConvs = convGraph.getTestMap();
		Map fileFormatToCompareConvs = convGraph.getCompareMap();
		
		List testResults = new ArrayList();
		
		Set fileFormats = fileFormatToTestConvs.keySet();
		
		/*
		 * for each file format, get the corresponding test converter paths
		 * and comparison converter path.
		 */
		
		int numTestsSoFar = 0;
		
		Iterator iter = fileFormats.iterator();
		while(iter.hasNext()) {
			String fileFormat = (String) iter.next();
			
			ArrayList testConvList = 
				(ArrayList) fileFormatToTestConvs.get(fileFormat);
			
			ConverterPath[] testConvs  =
				(ConverterPath[]) testConvList.toArray(new ConverterPath[0]);
			
			ConverterPath[] filteredTestConvs = 
				testPathFilter.filter(testConvs);
			
			ConverterPath compareConv = 
				(ConverterPath) fileFormatToCompareConvs.get(fileFormat); 
			
			/*
			 * For each test converter, use that test converter and
			 * the corresponding comparison converter to run a test.
			 */
			if (filteredTestConvs != null &&
					compareConv != null) {
				for (int kk = 0; kk < filteredTestConvs.length; kk++) {
					System.out.println("Running converter path " + kk);
					numTestsSoFar++;
					ConverterPath testConv = filteredTestConvs[kk];
					
					TestResult testResult = 
						runATest(testConv, compareConv, cContext, bContext,
								numTestsSoFar);
					
					if (testResult != null) {
						testResults.add(testResult);
					}
				}
			}
		}
		
		return (TestResult[]) testResults.toArray(new TestResult[0]);
	}
	
	
	private TestResult runATest(ConverterPath testConvs,
			ConverterPath compareConvs, CIShellContext cContext,
			BundleContext bContext, int numTestsSoFar) {
		
		//get test file data corresponding to the format these converters accept.
		
		String fileFormat = testConvs.getAcceptedFileFormat();
		String[] testFilePaths = testFileKeeper.getTestFilePaths(fileFormat);
		Data[][] testFileData = wrapInData(testFilePaths, fileFormat);
		
		//determine how we will compare the graphs
		
		boolean isLossy = testConvs.isLossy() || compareConvs.isLossy();
		boolean preserveIDs = testConvs.preservesIDs() &&
			compareConvs.preservesIDs();
		
		NewGraphComparer comparer = getComparer(isLossy, preserveIDs);
        
		//pack all the data relevant to the test into a single object.
        TestConfigData testBasicData = new TestConfigData(comparer, testConvs,
        		compareConvs, cContext, testFileData);
        
        //run the test
        FilePassResult[] results = this.testRunner.runTest(testBasicData);     
        
        //return the results of the test
        return new TestResult(results, testConvs, compareConvs,
        		numTestsSoFar);    
	}
		
	private Data[][] wrapInData(String[] testFilePaths, String format) {
		
		Data[][] results = new Data[testFilePaths.length][1];
		for (int ii = 0; ii < testFilePaths.length; ii++) {
			String filePath = testFilePaths[ii];
			
			results[ii] = 
				new Data[] {new BasicData(new File(filePath), format)};
			
			Dictionary metadata = results[ii][0].getMetadata();
			metadata.put(DataProperty.LABEL, filePath);
		}
		
		return results;
	}
	
	private NewGraphComparer getComparer(boolean areLossy,
			boolean idsPreserved) {
		
		if (areLossy) {
			return new LossyComparer();
		} else if (! idsPreserved) {
			return new IdsNotPreservedComparer();
		} else {
			return new IdsPreservedComparer();
		}
	}
	
	private class AcceptAllFilter implements PathFilter {

		public ConverterPath[] filter(ConverterPath[] testPaths) {
			return testPaths;
		}
		
	}
	
}

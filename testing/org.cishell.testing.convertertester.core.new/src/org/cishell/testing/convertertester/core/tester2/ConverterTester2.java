package org.cishell.testing.convertertester.core.tester2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.testing.convertertester.core.converter.graph.ConverterGraph;
import org.cishell.testing.convertertester.core.converter.graph.ConverterPath;
import org.cishell.testing.convertertester.core.tester2.filepassresults.FilePassResult;
import org.cishell.testing.convertertester.core.tester2.graphcomparison.IdsNotPreservedComparer;
import org.cishell.testing.convertertester.core.tester2.graphcomparison.IdsPreservedComparer;
import org.cishell.testing.convertertester.core.tester2.graphcomparison.LossyComparer;
import org.cishell.testing.convertertester.core.tester2.graphcomparison.NewGraphComparer;
import org.cishell.testing.convertertester.core.tester2.reportgen.ReportGenerator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * Test Suites are arrays of test paths, where each test path starts
 * from the same file format.
 * 
 * Compare Paths are paths of converters that convert from the original file
 * format to the in-memory comparison format. For each test suite there
 * is a single corresponding compare path.
 * 
 * @author mwlinnem
 *
 */
public class ConverterTester2 implements AlgorithmProperty {

	private LogService log;
	
	private TestFileKeeper testFileKeeper;
	private TestRunner testRunner;
	
	
	public ConverterTester2(LogService log) {
		this.log = log;
		this.testFileKeeper = 
			new TestFileKeeper(TestFileKeeper.DEFAULT_ROOT_DIR, log);
		this.testRunner = new DefaultTestRunner(log);
	}
	
	
	/**
	 * Tests the provided converters, and passes the results of those tests to
	 * the report generators. Report Generators are side-effected, which takes
	 * the place of a return value.
	 * 
	 * @param converterRefs the converters to be tested
	 * @param cContext the CIShell Context
	 * @param bContext the Bundle Context
	 * @param log The log written to should an error occur (other than the 
	 * errors resulting from the converters, which are included in the test
	 * results)
	 * @param reportGenerators process the test results.
	 */
	public void execute(ServiceReference[] converterRefs,
			CIShellContext cContext,
			BundleContext bContext,
			LogService log, 
			ReportGenerator[] reportGenerators) {
		
		this.log = log;
		
		//generate test paths
		
		ConverterGraph converterGraph = new ConverterGraph(converterRefs);
		
		ConverterPath[][] testSuites = converterGraph.getTestPaths();
		ConverterPath[] comparePaths = converterGraph.getComparePaths();
			
		//run tests on test paths
		
		TestResult[] results = runTests(testSuites, comparePaths, 
				cContext, bContext);
		
		//generate reports based on test results
		
		for (int ii = 0; ii < reportGenerators.length; ii++) {
			ReportGenerator reportGenerator = reportGenerators[ii];
			
			reportGenerator.generateReport(results);
		}
	}
	
	
	public TestResult[] runTests(ConverterPath[][] testSuites,
			ConverterPath[] comparePaths, CIShellContext cContext,
			BundleContext bContext) {
		
		List testResults = new ArrayList();
		
		for (int ii = 0; ii < testSuites.length; ii++) {
			ConverterPath[] testSuite = testSuites[ii];
			ConverterPath testSuiteComparePath = comparePaths[ii];
			
			for (int jj = 0; jj < testSuite.length; jj++) {
				ConverterPath testPath = testSuite[jj];
				
				TestResult testResult = runATest(testPath,
						testSuiteComparePath, cContext, bContext);
				
				if (testResult != null) {
					testResults.add(testResult);
				}
			}
			
		}
		
		return (TestResult[]) testResults.toArray(new TestResult[0]);
	}
	
	
	private TestResult runATest(ConverterPath testConvs,
			ConverterPath comparisonConvs, CIShellContext cContext,
			BundleContext bContext) {
		
		ServiceReference[] testConvRefs = testConvs.getPathAsArray();
		ServiceReference[] compareConvRefs = comparisonConvs.getPathAsArray();
		
		AlgorithmFactory[] testConvAlgs = extractAlgorithms(
				testConvRefs, bContext);
		AlgorithmFactory[] compareConvAlgs = extractAlgorithms(
				compareConvRefs, bContext);
		
		if (testConvRefs.length <= 0) {
			System.out.println("Must have at least one test converter..");
			return null;
		}	
		
		//get test files corresponding to the format these converters accept.
		
		String fileFormat = 
			(String) testConvRefs[0].getProperty(AlgorithmProperty.OUT_DATA);
		String[] testFilePaths = testFileKeeper.getTestFilePaths(fileFormat);
		Data[][] fileData = wrapInData(testFilePaths, fileFormat);
		
		//determine how we will compare the graphs
		
		boolean areLossy = areLossy(testConvRefs) && areLossy(compareConvRefs);
		boolean preserveIds = false; //TODO: determine this somehow
		NewGraphComparer comparer = getComparer(areLossy, preserveIds);
        
		//pack all the data relevant to the test into a single object.
        TestConfigData testData = new TestConfigData(comparer, testConvAlgs,
        		compareConvAlgs, cContext, fileData);
        
        //run the test
        FilePassResult[] results = this.testRunner.runTest(testData);     
        
        //return the results of the test
        return new TestResult(results);    
	}
	
	
	private AlgorithmFactory[] extractAlgorithms(ServiceReference[] convRefs,
			BundleContext bContext) {
		AlgorithmFactory[] results = new AlgorithmFactory[convRefs.length];
		for (int ii = 0; ii < convRefs.length; ii++) {
			results[ii] = (AlgorithmFactory) bContext.getService(convRefs[ii]);
		}
		return results;
	}
	
	
	private Data[][] wrapInData(String[] testFilePaths, String format) {
		
		Data[][] results = new Data[testFilePaths.length][1];
		for (int ii = 0; ii < testFilePaths.length; ii++) {
			String filePath = testFilePaths[ii];
			
			results[ii] = 
				new Data[] {new BasicData(new File(filePath), format)};
		}
		
		return results;
	}
	
	
	private boolean areLossy(ServiceReference[] refs) {
		
		String lossiness = LOSSLESS;
        for (int i=0; i < refs.length; i++) {
            if (LOSSY.equals(refs[i].getProperty(CONVERSION))) {
                lossiness = LOSSY;
            }
        } 
        
        boolean result = lossiness.equals(LOSSY);
        return result;
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
	
	
}

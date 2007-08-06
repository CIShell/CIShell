package org.cishell.testing.convertertester.core.tester2.reportgen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.testing.convertertester.core.tester2.TestResult;
import org.cishell.testing.convertertester.core.tester2.filepassresults.ComparePhaseFailure;
import org.cishell.testing.convertertester.core.tester2.filepassresults.ConvertPhaseFailure;
import org.cishell.testing.convertertester.core.tester2.filepassresults.FilePassResult;
import org.cishell.testing.convertertester.core.tester2.filepassresults.FilePassSuccess;

public class SampleResultReportGenerator implements ReportGenerator {

	public static final String TEMP_FILE_PATH = "sample-result-report.txt";

	private int testSampleSize;
	private int filePassSampleSize;

	private File reportFile = null;

	public SampleResultReportGenerator(int testSampleSize, int filePassSampleSize) {
		this.testSampleSize = testSampleSize;
		this.filePassSampleSize = filePassSampleSize;
	}

	public void generateReport(TestResult[] testResults) {

		FileOutputStream reportOutStream = null;
		try {
			this.reportFile = new File(TEMP_FILE_PATH);
			reportOutStream = new FileOutputStream(reportFile);
			PrintStream report = new PrintStream(reportOutStream);

			int testStep = Math.max(1, testResults.length / this.testSampleSize);
			
			report.println("Sample Result Report");
			report.println("-----------------------------------------------");
			report.println("");
			for (int ii = 0; ii < testResults.length; ii += testStep) {
				TestResult selectedTestResult = testResults[ii];
				
				report.println("Test " + ii);
				
				if (selectedTestResult.allSucceeded()) {
					report.println("All Succeeded!");
				} else if (selectedTestResult.someSucceeded()) {
					report.println("Some Succeeded.");
				} else {
					report.println("All Failed.");
				}
				
				FilePassResult[] fprs = selectedTestResult.getFilePassResults();
				int filePassStep = Math.max(1, fprs.length / this.filePassSampleSize);
				
				for (int jj = 0; jj < fprs.length; jj += filePassStep) {
					report.println("File Pass " + jj);
					

					FilePassResult fpr = fprs[jj];
					AlgorithmFactory[] testConvs = fpr.getTestConverters();
					report.println("Test Converters involved...");
					for (int kk = 0; kk < testConvs.length; kk++) {
						AlgorithmFactory conv = testConvs[kk];
						//TODO: need to get real ids in here.
						report.println(conv.getClass());
					}
					report.println("Comparison Converters involved...");
					AlgorithmFactory[] compareConvs = fpr.getTestConverters();
					for (int kk = 0; kk < compareConvs.length; kk++) {
						AlgorithmFactory conv = compareConvs[kk];
						//TODO: need to get real ids in here.
						report.println(conv.getClass());
					}
					String type = fpr.getType();
					if (type.equals(FilePassResult.SUCCESS)) {
						FilePassSuccess fprSuccess = (FilePassSuccess) fpr;
						report.println("Success");
					} else if (type.equals(FilePassResult.CONVERT_FAILURE)) {
						ConvertPhaseFailure fprFailure = (ConvertPhaseFailure) fpr;
						report.println("Conversion Failure");
						report.println("Failed in " + fprFailure.getPhase());
						report.println("Failed on " + fprFailure.getFailedConverter());
						report.println(fprFailure.getExplanation());
					} else if (type.equals(FilePassResult.COMPARE_FAILURE)) {
						ComparePhaseFailure fprFailure = (ComparePhaseFailure) fpr;
						report.println("Graph Comparison Failure");
						report.println(fprFailure.getExplanation());
					}
				}
				
			}

		} catch (IOException e) {
			System.out.println("Unable to generate overview report.");
			e.printStackTrace();
			try {		
				if (reportOutStream != null) reportOutStream.close();
				} catch (IOException e2) {
					System.out.println("Unable to close overview report stream");
					e2.printStackTrace();
				}
		}
	}
	
	public File getReport() {
		return this.reportFile;
	}

}

package org.cishell.testing.convertertester.core.tester2.reportgen.alltests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.cishell.testing.convertertester.core.tester2.reportgen.ReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.AllTestsReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.TestReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllTestsResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.TestResult;
import org.osgi.service.log.LogService;

public class AllTestsReportGenerator implements ReportGenerator {

	public static final String TEMP_FILE_PATH = "all-tests-report.txt";

	private AllTestsReport allTestsReport;

	private TestReportSubGenerator testResultSubGen;
	
	private LogService log;
	
	public AllTestsReportGenerator(LogService log) {
		this.log = log;
		this.testResultSubGen = new TestReportSubGenerator();
	}

	public void generateReport(AllTestsResult atr) {
		FileOutputStream reportOutStream = null;
		try {
			File reportFile = new File(TEMP_FILE_PATH);
			reportOutStream = new FileOutputStream(reportFile);
			PrintStream report = new PrintStream(reportOutStream);
			
			report.println("ALL TESTS REPORT");
			report.println("-----------------------------------------------");
			report.println("");
			report.println("Summary...");
			report.println("  # Completely Passed Tests: " +
					atr.getNumTestsPassed());
			report.println("  # Partially Passed Tests : " + 
					atr.getNumTestsPartialPassed());
			report.println("  # Compleley Failed Tests : " + 
					atr.getNumTestsCompletelyFailed());
			report.println("  Total                    : " + 
					atr.getNumTests());
			report.println("");

			float percentCompletelyPassed = atr.getNumTestsPassed() / (atr.getNumTests());
			
			TestResult[] allTRs = atr.getAllTestResults();
			for (int ii = 0; ii < allTRs.length; ii++) {
				TestResult tr = allTRs[ii];
				tr.setName("Test " + ii);
			}
			
			TestResult[] passedTRs = atr.getPassedTestResults();
			report.println("Completely Passed Tests...");
			for (int ii = 0; ii < passedTRs.length; ii++) {
				TestResult passedTR = passedTRs[ii];
				
				report.println("  " + passedTR.getName() + " - " + 
						passedTR.getShortSummary());
			}
			report.println("");
			
			TestResult[] pPassedTRs = atr.getPartialPassedTestResults();
			report.println("Partially Passed Tests...");
			for (int ii = 0; ii < pPassedTRs.length; ii++) {
				TestResult pPassedTR = pPassedTRs[ii];
				report.println("  " + pPassedTR.getName() + " - " + 
						pPassedTR.getShortSummary());
			}
			
			report.println("");
			
			report.println("Completely Failed Tests...");
			TestResult[] failedTRs = atr.getFailedTestResults();
			for (int ii = 0; ii < failedTRs.length; ii++) {
				TestResult failedTR = failedTRs[ii];
	
				report.println("  " + failedTR.getName() + " - " + 
						failedTR.getShortSummary());
			}
			report.println("");
			
			List passedTRReports = new ArrayList();
			for (int ii = 0; ii < passedTRs.length; ii++) {
				TestResult passedTR = passedTRs[ii];
				
				this.testResultSubGen.generateSubreport(passedTR);
				TestReport testReport = testResultSubGen.getTestReport();
				
				passedTRReports.add(testReport);
			}
			
			List pPassedTRReports = new ArrayList();
			for (int ii = 0; ii < pPassedTRs.length; ii++) {
				TestResult pPassedTR = pPassedTRs[ii];
				
				this.testResultSubGen.generateSubreport(pPassedTR);
				TestReport testReport = testResultSubGen.getTestReport();
				
				pPassedTRReports.add(testReport);
			}
			
			List failedTRReports = new ArrayList();
			for (int ii = 0; ii < failedTRs.length; ii++) {
				TestResult failedTR = failedTRs[ii];
				
				this.testResultSubGen.generateSubreport(failedTR);
				TestReport testReport = testResultSubGen.getTestReport();
				
				failedTRReports.add(testReport);
			}
			
//			String summary = "%" + percentCompletelyPassed * 100 + " Completely Passed";
			String summary = "";
			this.allTestsReport = new AllTestsReport(reportFile,
				"All Test Reports",
				(TestReport[]) passedTRReports.toArray(new TestReport[0]),
				(TestReport[]) pPassedTRReports.toArray(new TestReport[0]),
				(TestReport[]) failedTRReports.toArray(new TestReport[0]),
				summary);
		} catch (IOException e) {
			System.out.println("Unable to generate all tests report.");
			e.printStackTrace();	
			closeStream(reportOutStream);
		} finally {
			closeStream(reportOutStream);
		}
	}
	
	public AllTestsReport getAllTestsReport() {
		return this.allTestsReport;
	}
	
	private void closeStream(FileOutputStream stream) {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (IOException e2) {
			System.out.println("Unable to close all tests report stream");
			e2.printStackTrace();
		}
	}


}

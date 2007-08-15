package org.cishell.testing.convertertester.core.tester2.reportgen.alltests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.cishell.testing.convertertester.core.converter.graph.ConverterPath;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.FilePassReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.TestReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.TestResult;
import org.osgi.framework.ServiceReference;

public class TestReportSubGenerator {

	private TestReport testReport;

	private FilePassSubGenerator filePassSubGen;
	
	public TestReportSubGenerator() {
		this.filePassSubGen = new FilePassSubGenerator();
	}

	public void generateSubreport(TestResult tr) {
		FileOutputStream reportOutStream = null;
		try {
			File reportFile = new File(tr.getName());
			reportOutStream = new FileOutputStream(reportFile);
			PrintStream report = new PrintStream(reportOutStream);
			
			report.println("Test Result Report");
			report.println("-----------------------------------------------");
			report.println("");
			
			report.println("Summary...");
			report.println("  # Successful File Passes: " + tr.getNumFilePassSuccesses());
			report.println("  # Failed File Passes    : " + tr.getNumFilePassFailures());
			report.println("  Total                   : " + tr.getNumFilePasses());
			
			float percentSuccessful = tr.getNumFilePassSuccesses() / tr.getNumFilePasses();
			
//			name them all
//			FilePassResult[] allFPs = tr.getFilePassResults();
//			for (int ii = 0; ii < allFPs.length; ii++) {
//				FilePassResult fp = allFPs[ii];			
//				fp.setName("Pass " + ii + " of " + tr.getName() + " . ");
//			}
			
			report.println("");
			report.println("---------------");
			report.println("");
			
			report.println("Test Converters...");
			ConverterPath testConvs = tr.getTestConverters();
			for (int ii = 0; ii < testConvs.size(); ii++) {
				ServiceReference ref = testConvs.getRef(ii);
				String name = ref.getProperty("service.pid").toString();
				report.println("  " + name);
			}
			report.println("");
			
			report.println("Comparison Converters...");
			ConverterPath compareConvs = tr.getComparisonConverters();
			for (int ii = 0; ii < compareConvs.size(); ii++) {
				ServiceReference ref = compareConvs.getRef(ii);
				String name = ref.getProperty("service.pid").toString();
				report.println("  " + name);
			}
			
			report.println("");
			report.println("---------------");
			report.println("");
			FilePassResult[] successfulFPs = tr.getFilePassSuccesses();
			report.println("Successful File Passes...");
			for (int ii = 0; ii < successfulFPs.length; ii++) {
				FilePassResult successfulFP = successfulFPs[ii];
				namePass("Successful", successfulFP, tr, ii);
				report.println("  " + successfulFP.getName() + successfulFP.getShortSummary());
			}
			report.println("");
			
			FilePassResult[] failedFPs = tr.getFilePassFailures();
			report.println("Failed File Passes...");
			for (int ii = 0; ii < failedFPs.length; ii++) {
				FilePassResult failedFP = failedFPs[ii];
				namePass("Failed", failedFP, tr, ii);
				report.println("  " + failedFP.getName() + failedFP.getShortSummary());
			}
			report.println("");
			
			List successfulFPReports = new ArrayList();
			for (int ii = 0; ii < successfulFPs.length; ii++) {
				FilePassResult successfulFP = successfulFPs[ii];
				
				filePassSubGen.generateSubreport(successfulFP);
				FilePassReport filePassReport = filePassSubGen.getFilePassReport();
				
				successfulFPReports.add(filePassReport);
			}
			
			List failedFPReports = new ArrayList();
			for (int ii = 0; ii < failedFPs.length; ii++) {
				FilePassResult failedFP = failedFPs[ii];
				
				filePassSubGen.generateSubreport(failedFP);
				FilePassReport filePassReport = filePassSubGen.getFilePassReport();
				
				failedFPReports.add(filePassReport);
			}
			
//			String summary = "%" + percentSuccessful + " Successful";
			String summary = "";
			this.testReport = new TestReport(reportFile, tr.getName(),
				(FilePassReport[]) successfulFPReports.toArray(new FilePassReport[0]),
				(FilePassReport[]) failedFPReports.toArray(new FilePassReport[0]),
				summary);
					
			
		} catch (IOException e) {
			System.out.println("Unable to generate a test report.");
			e.printStackTrace();	
			closeStream(reportOutStream);
		} finally {
			closeStream(reportOutStream);
		}
	}
	
	public TestReport getTestReport() {
		return this.testReport;
	}
	
	private void closeStream(FileOutputStream stream) {
		try {
			if (stream != null)
				stream.close();
		} catch (IOException e2) {
			System.out.println("Unable to close a test report stream");
			e2.printStackTrace();
		}
	}
	
	private void namePass(String prefix, FilePassResult fp, TestResult parent,
			int index) {
		fp.setName(prefix + " Pass " + index + " of " + fp);
	}
}

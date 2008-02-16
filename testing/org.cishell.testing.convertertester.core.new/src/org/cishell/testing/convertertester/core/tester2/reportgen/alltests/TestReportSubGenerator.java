package org.cishell.testing.convertertester.core.tester2.reportgen.alltests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.cishell.testing.convertertester.core.converter.graph.Converter;
import org.cishell.testing.convertertester.core.converter.graph.ConverterPath;
import org.cishell.testing.convertertester.core.tester2.reportgen.ReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.FilePassReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.TestReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.TestResult;
import org.osgi.service.log.LogService;

public class TestReportSubGenerator {

	private TestReport testReport;

	private FilePassSubGenerator filePassSubGen;
	
	private LogService log;
	
	public TestReportSubGenerator(LogService log) {
		this.log = log;
		
		this.filePassSubGen = new FilePassSubGenerator(this.log);
	}

	public void generateSubreport(TestResult tr) {
		FileOutputStream reportOutStream = null;
		try {
			File reportFile = new File(ReportGenerator.TEMP_DIR + tr.getName() + " for " + tr.getName());
			reportOutStream = new FileOutputStream(reportFile);
			PrintStream report = new PrintStream(reportOutStream);
			
			report.println("Test " + tr.getTestNum() + " Result Report");
			report.println("-----------------------------------------------");
			report.println("");
			
			report.println("Summary...");
			report.println("  # Successful File Passes: " + tr.getNumFilePassSuccesses());
			report.println("  # Failed File Passes    : " + tr.getNumFilePassFailures());
			report.println("  Total                   : " + tr.getNumFilePasses());
			
			float percentSuccessful;
			if (tr.getNumFilePasses() > 0) {
				percentSuccessful = tr.getNumFilePassSuccesses() / tr.getNumFilePasses();
			} else {
				percentSuccessful = 0;
			}
			
			report.println("");
			report.println("---------------");
			report.println("");
			
			report.println("Test Converters...");
			ConverterPath testConvs = tr.getTestConverters();
			for (int ii = 0; ii < testConvs.size(); ii++) {
				Converter conv = testConvs.get(ii);
				String shortName = conv.getShortName();
				report.println("  " + shortName);
			}
			report.println("");
			
			report.println("Comparison Converters...");
			ConverterPath compareConvs = tr.getComparisonConverters();
			for (int ii = 0; ii < compareConvs.size(); ii++) {
				Converter conv = compareConvs.get(ii);
				String shortName = conv.getShortName();
				report.println("  " + shortName);
			}
			
			report.println("");
			report.println("---------------");
			report.println("");
			FilePassResult[] successfulFPs = tr.getFilePassSuccesses();
			report.println("Successful File Passes...");
			report.println("");
			for (int ii = 0; ii < successfulFPs.length; ii++) {
				FilePassResult successfulFP = successfulFPs[ii];
				report.println(successfulFP.getNameWithPhaseExpln());
				filePassSubGen.writeReport(report, successfulFP);
			}
			report.println("");
			
			FilePassResult[] failedFPs = tr.getFilePassFailures();
			report.println("Failed File Passes...");
			report.println("");
			for (int ii = 0; ii < failedFPs.length; ii++) {
				FilePassResult failedFP = failedFPs[ii];
				report.println(failedFP.getNameWithPhaseExpln());
				filePassSubGen.writeReport(report, failedFP);
				report.println("");
			}
			report.println("");
			
			List successfulFPReports = new ArrayList();
			for (int ii = 0; ii < successfulFPs.length; ii++) {
				FilePassResult successfulFP = successfulFPs[ii];
				
				filePassSubGen.generateSubreport(tr, successfulFP);
				FilePassReport filePassReport = filePassSubGen.getFilePassReport();
				
				successfulFPReports.add(filePassReport);
			}
			
			List failedFPReports = new ArrayList();
			for (int ii = 0; ii < failedFPs.length; ii++) {
				FilePassResult failedFP = failedFPs[ii];
				
				filePassSubGen.generateSubreport(tr, failedFP);
				FilePassReport filePassReport = filePassSubGen.getFilePassReport();
				
				failedFPReports.add(filePassReport);
			}
			this.testReport = new TestReport(reportFile, tr.getNameWithSuccess(),
					(FilePassReport[]) successfulFPReports.toArray(new FilePassReport[successfulFPReports.size()]),
					(FilePassReport[]) failedFPReports.toArray(new FilePassReport[failedFPReports.size()]));
					
			
		} catch (IOException e) {
			this.log.log(LogService.LOG_ERROR, 
					"Unable to generate a test report.", e);
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
			this.log.log(LogService.LOG_ERROR,
					"Unable to close a test report stream", e2);
		}
	}
}

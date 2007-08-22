package org.cishell.testing.convertertester.core.tester2.reportgen.allconvs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.cishell.testing.convertertester.core.tester2.reportgen.ReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.ConvReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.TestReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.ConvResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.TestResult;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class ConvReportSubGenerator {

	private ConvReport convReport = null;
	
	private LogService log;
	
	public ConvReportSubGenerator(LogService log) {
		this.log = log;
	}
	
	public void generate(ConvResult cr) {

		FileOutputStream reportOutStream = null;
		try {
			ConvResult convResult = cr;
			ServiceReference conv = convResult.getRef();

			File reportFile = new File(ReportGenerator.TEMP_DIR + cr.getNameWithPackage());
			reportOutStream = new FileOutputStream(reportFile);

			PrintStream report = new PrintStream(reportOutStream);

			report.println("Converter Report");
			report.println("--------------------------------------");
			report.println("");
			report.println(cr.getNameWithPackage());
			report.println("");
			if (convResult.isTrusted()) {
				report.println("Trusted");
			} else {
				report.println("Not Trusted");

			}
			report.println("");
			
			report.println("# of files passed through :"
					+ convResult.getNumFilePasses());
			report.println("");
			report.println("% Passed                  : "
					+ convResult.getPercentPassed());
			report.println("% Chance of Flaw          : "
					+ convResult.getChanceOfFlaw());

			report.println("");

			report.println("Involved in the following tests...");
			TestResult[] involvedTests = cr.getTestsBySuccess();
			for (int ii = 0; ii < involvedTests.length; ii++) {
				TestResult tr = involvedTests[ii];
				report.println("  " + tr.getNameWithSuccess());
			}
			report.println("");
			
			String[] failureExps = cr.getUniqueFailureExplanations();
			if (failureExps.length > 0) {
				report.println("Unique Failure Explanations...");

				for (int ii = 0; ii < failureExps.length; ii++) {
					String failureExp = failureExps[ii];

					report.println("");
					report.println(failureExp);
					report.println("----------");

				}

			} else {
				report.println("Not involved in any file pass failures");
			}

			List testReportsList = new ArrayList();

			TestReport[] testReports = (TestReport[]) testReportsList
					.toArray(new TestReport[0]);
			
			this.convReport = new ConvReport(reportFile, new TestReport[0], cr
					.getNameNoPackageWithTrust());
			report.println("");
			report.flush();
			reportOutStream.close();
		} catch (IOException e) {
			this.log.log(LogService.LOG_ERROR, "Unable to generate a converter report.", e);
			try {
				if (reportOutStream != null)
					reportOutStream.close();
			} catch (IOException e2) {
				this.log.log(LogService.LOG_ERROR, "Unable to close a converter report", e);
			}
	}
	}
	
	public ConvReport getConvReport() {
		return this.convReport;
	}
}

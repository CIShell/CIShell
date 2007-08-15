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
import org.osgi.framework.ServiceReference;

public class ConvReportSubGenerator {

	private ConvReport convReport = null;

	public void generate(ConvResult cr) {

		FileOutputStream reportOutStream = null;
		try {
			ConvResult convResult = cr;
			ServiceReference conv = convResult.getRef();

			File reportFile = new File(ReportGenerator.TEMP_DIR + cr.getName());
			reportOutStream = new FileOutputStream(reportFile);

			PrintStream report = new PrintStream(reportOutStream);

			report.println("Converter Report");
			report.println("--------------------------------------");
			report.println("");
			report.println(conv.getProperty("service.pid"));
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
			System.out.println("Converter " + cr.getName()
					+ " is associate with " + testReports.length + " tests");
			
			String summary = "";
			this.convReport = new ConvReport(reportFile, new TestReport[0], cr
					.getName(), summary);
			report.println("");
			report.flush();

			reportOutStream.close();
		} catch (IOException e) {
			System.out.println("Unable to generate a converter report.");
			e.printStackTrace();
			try {
				if (reportOutStream != null)
					reportOutStream.close();
			} catch (IOException e2) {
				System.out.println("Unable to close a converter report" +
						" stream");
				e2.printStackTrace();
			}
	}
	}
	
	public ConvReport getConvReport() {
		return this.convReport;
	}
}

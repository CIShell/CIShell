package org.cishell.testing.convertertester.core.tester2.reportgen.allconvs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import org.cishell.testing.convertertester.core.converter.graph.Converter;
import org.cishell.testing.convertertester.core.tester2.reportgen.ReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.faultanalysis.ChanceAtFault;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.ConvReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.TestReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.ConvResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.TestResult;
import org.cishell.testing.convertertester.core.tester2.util.FormatUtil;
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
			Converter conv = convResult.getConverter();

			File reportFile = new File(ReportGenerator.TEMP_DIR + cr.getUniqueName());
			reportOutStream = new FileOutputStream(reportFile);

			PrintStream report = new PrintStream(reportOutStream);

			report.println("Converter Report");
			report.println("--------------------------------------");
			report.println("");
			report.println(cr.getUniqueName());
			report.println("");
			if (convResult.isTrusted()) {
				report.println("Trusted");
			} else {
				report.println("Not Trusted");

			}
			report.println("");
			
			if (cr.wasTested()) {
				report.println("# of files passed through :"
						+ convResult.getNumFilePasses());
				report.println("");
//				report.println("% Passed                  : "
//						+ FormatUtil.formatToPercent(convResult.getPercentPassed()));
				report.println("% Chance of Flaw          : "
						+ FormatUtil.formatToPercent(convResult.getChanceOfFlaw()));

				report.println("");

				report.println("Involved in the following tests...");
				TestResult[] involvedTests = cr.getTestsBySuccess();
				for (int ii = 0; ii < involvedTests.length; ii++) {
					TestResult tr = involvedTests[ii];
					report.println("  " + tr.getNameWithSuccess());
				}
				report.println("");

				ChanceAtFault[] failureExplns = cr
						.getUniqueExplnChanceAtFaults();
				Arrays.sort(failureExplns, cr.getCompareFaultsByLikelihood());
				
				if (failureExplns.length > 0) {
					report.println("Unique Failure Explanations " +
							"(sorted by likelihood)...");

					for (int ii = 0; ii < failureExplns.length; ii++) {
						ChanceAtFault failureExp = failureExplns[ii];
						FilePassResult filePass = failureExp.getFailedFilePass();
						
						if (filePass.failedWhileConverting()) {
							report.println("While converting " + 
									filePass.getOriginalFileShortLabel() + 
									"...");
						}
						
						String explanation = failureExp.getExplanation();
						float chanceAtFault = failureExp.getChanceAtFault();
						
						report.println("");
						report.println(explanation);
						report.println("");
						report.println("Chance this converter is responsible: "
								+ FormatUtil.formatToPercent(chanceAtFault));
						report.println("----------");

					}

				} else {
					report.println("Not involved in any file pass failures");
				}

			} else {
				report.println("Converter was not able to be tested.");
				report.println("");
				report.println("This is most likely because we were unable to create a");
				report.println("valid test path involving this converter. Valid test");
				report.println("paths are paths through the converter graph that start at");
				report.println("a file format, go through some converters, return back to");
				report.println("that same file format, and can then be somehow converted");
				report.println("to the in-memory graph comparison format (prefuse.graph)");
				report.println("It is also possible that all the file passes that would");
				report.println("eventually pass through this converter are failing before");
				report.println("they reach this converter.");
				report.println("");
				report.println("Consult the Annotated Graph Report for details on why this");
				report.println("may not be testable.");
			}
			
			this.convReport = new ConvReport(reportFile, new TestReport[0],
					cr.getShortNameWithCorrectness());
			
			report.println("");
			report.flush();
			reportOutStream.close();
		} catch (IOException e) {
			this.log.log(LogService.LOG_ERROR,
					"Unable to generate a converter report.", e);
			try {
				if (reportOutStream != null)
					reportOutStream.close();
			} catch (IOException e2) {
				this.log.log(LogService.LOG_ERROR,
						"Unable to close a converter report", e);
			}
	}
	}
	
	public ConvReport getConvReport() {
		return this.convReport;
	}
}

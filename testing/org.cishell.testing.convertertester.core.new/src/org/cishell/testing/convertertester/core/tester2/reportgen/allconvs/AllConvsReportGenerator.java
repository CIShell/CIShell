package org.cishell.testing.convertertester.core.tester2.reportgen.allconvs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cishell.testing.convertertester.core.tester2.reportgen.ReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.AllConvsReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.ConvReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllConvsResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllTestsResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.ConvResult;
import org.cishell.testing.convertertester.core.tester2.util.FormatUtil;
import org.osgi.service.log.LogService;

public class AllConvsReportGenerator implements ReportGenerator {

	public static final String TEMP_FILE_PATH = "All-Converters-Report2.txt";

	private ConvReportSubGenerator convSubGen;
	
	private AllConvsReport allConvsReport = null;
	
	private LogService log;
	
	public AllConvsReportGenerator(LogService log) {
		this.log = log;
		
		this.convSubGen = new ConvReportSubGenerator(this.log);
	}
	
	public void generateReport(AllTestsResult atr,
							   AllConvsResult acr,
							   File nwbConvGraph) {
		
		ConvResult[] convResults = acr.getConvResults();
		Arrays.sort(convResults, ConvResult.COMPARE_BY_CORRECTNESS);
		
		FileOutputStream reportOutStream = null;
		try {
			File reportFile = new File(TEMP_DIR + TEMP_FILE_PATH);
			reportOutStream = new FileOutputStream(reportFile);
			PrintStream report = new PrintStream(reportOutStream);
			
			report.println("All Converters Report");
			report.println("---------------------------------------------");
			report.println(""                                             );
			
			int numTested = 0;
			
			float passedPercentTotal = 0;
			for (int ii = 0; ii < convResults.length; ii++) {
				if (convResults[ii].wasTested()) {
					if (convResults[ii].wasTested()) {
						numTested++;
				passedPercentTotal += convResults[ii].getPercentPassed(); 
					}
				}
			}
			float avgPercentPassed = 0;
			if (numTested != 0) { 
				avgPercentPassed = 
					passedPercentTotal / (float) numTested;
			}	
			
			float chanceCorrectTotal = 0.0f;
			for (int ii = 0; ii < convResults.length; ii++) {
				if (convResults[ii].wasTested()) {	
					chanceCorrectTotal += convResults[ii].getChanceCorrect(); 
				}
			}
			
			float avgChanceCorrect = 0;
			if (numTested != 0) {
			avgChanceCorrect = chanceCorrectTotal / ((float) numTested);
			}
			
			List convReportsList = new ArrayList();

			List trustedConvs = new ArrayList();
			List nonTrustedConvs = new ArrayList();
			List nonTestedConvs = new ArrayList();

			for (int ii = 0; ii < convResults.length; ii++) {
				ConvResult cr = convResults[ii];

				if (cr.isTrusted()) {
					trustedConvs.add(cr);
				} else if (cr.wasTested()) {
					nonTrustedConvs.add(cr);
				} else {
					nonTestedConvs.add(cr);
				}
			}
			
			report.println("Summary...");
			report.println("  # of Trusted Converters                  : " + 
					trustedConvs.size());
			report.println("  # of Non-Trusted Converters              : " + 
					nonTrustedConvs.size());
			report.println("  # of Untested Converters                 :"  +
					nonTestedConvs.size());
			report.println("  Total # of Converters                    : " + 
					convResults.length);
			
			report.println("");
			
			report.println("  Average chance each converter is correct : " + 
					FormatUtil.formatToPercent(avgChanceCorrect));
			report.println("  Average % successful file passes         : " + 
					FormatUtil.formatToPercent(avgPercentPassed));

			
			report.println("");
			
			report.println("Trusted Converters...");
			for (int ii = 0; ii < trustedConvs.size(); ii++) {
				ConvResult cr = (ConvResult) trustedConvs.get(ii);
				
				if (cr.wasTested()) {
					//add this converters name to all convs report
					report.println("  " + cr.getShortName());
				}
			}
			report.println("");
			
			report.println("Non-Trusted Tested Converters...");
			for (int ii = 0; ii < nonTrustedConvs.size(); ii++) {
				ConvResult cr = (ConvResult) nonTrustedConvs.get(ii);
				
				if (cr.wasTested()) {
					//add this converters name to all convs report
					report.println("  " + cr.getShortName());
				}
			}
			
			report.println("");
			
			report.println("Untested Converters...");
			for (int ii = 0; ii < convResults.length; ii++) {
				ConvResult cr = convResults[ii];
				
				if (! cr.wasTested()) {
					report.println("  " + cr.getShortName());
				}
			}
			report.println("");
			
			for (int ii = 0; ii < convResults.length; ii++) {
				if (convResults[ii].wasTested()) {
					this.convSubGen.generate(convResults[ii]);
				ConvReport convReport = this.convSubGen.getConvReport();
				convReportsList.add(convReport);
				}
			}
			
			ConvReport[] convReports = 
				(ConvReport[]) convReportsList.toArray(new ConvReport[0]);
					
			this.allConvsReport = 
				new AllConvsReport(reportFile, convReports,
						"All Converters Report");
			
			report.flush();
			
			reportOutStream.close();
		} catch (IOException e) {
			
			this.log.log(LogService.LOG_ERROR, "Unable to generate all converters report.", e);
			try {		
				if (reportOutStream != null) reportOutStream.close();
				} catch (IOException e2) {
					this.log.log(LogService.LOG_ERROR, "Unable to generate all converters report.", e);
				}
		}
	}
	
	
	public AllConvsReport getAllConvsReport() {
		return this.allConvsReport;
	}

}

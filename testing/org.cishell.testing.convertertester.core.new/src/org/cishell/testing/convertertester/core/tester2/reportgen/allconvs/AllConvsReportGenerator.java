package org.cishell.testing.convertertester.core.tester2.reportgen.allconvs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.cishell.testing.convertertester.core.tester2.reportgen.ConvResultMaker;
import org.cishell.testing.convertertester.core.tester2.reportgen.ReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.AllConvsReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.ConvReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllTestsResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.ConvResult;
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
	
	public void generateReport(AllTestsResult atr) {
		
		ConvResultMaker convGen = new ConvResultMaker();
		ConvResult[] convResults = convGen.generate(atr);
		
		FileOutputStream reportOutStream = null;
		try {
			File reportFile = new File(TEMP_DIR + TEMP_FILE_PATH);
			reportOutStream = new FileOutputStream(reportFile);
			PrintStream report = new PrintStream(reportOutStream);
			
			report.println("All Converters Report");
			report.println("---------------------------------------------");
			report.println(""                                             );
			
			float passedPercentTotal = convResults[0].getPercentPassed();
			for (int ii = 1; ii < convResults.length; ii++) {
				passedPercentTotal += convResults[ii].getPercentPassed(); 
			}
			float avgPercentPassed = passedPercentTotal / convResults.length;
			float chanceCorrectTotal = convResults[0].getChanceCorrect();
			for (int ii = 1; ii < convResults.length; ii++) {
				chanceCorrectTotal += convResults[ii].getChanceCorrect(); 
			}

			float avgChanceCorrect = chanceCorrectTotal / convResults.length;

			List convReportsList = new ArrayList();

			List trustedConvs = new ArrayList();
			List nonTrustedConvs = new ArrayList();

			for (int ii = 0; ii < convResults.length; ii++) {
				ConvResult cr = convResults[ii];

				if (cr.isTrusted()) {
					trustedConvs.add(cr);
				} else {
					nonTrustedConvs.add(cr);
				}
			}
			
			report.println("Summary...");
			report.println("  # of Trusted Converters                  : " + 
					trustedConvs.size());
			report.println("  # of Non-Trusted Converters              : " + 
					nonTrustedConvs.size());
			report.println("  Total # of Converters                    : " + 
					convResults.length);
			
			report.println("");
			
			report.println("  Average chance each converter is correct : " + 
					avgChanceCorrect);
			report.println("  Average % successful file passes         : " + 
					avgPercentPassed);

			
			report.println("");
			
			report.println("Trusted Converters...");
			for (int ii = 0; ii < trustedConvs.size(); ii++) {
				ConvResult cr = (ConvResult) trustedConvs.get(ii);
				report.println("  " + cr.getNameNoPackage());
			}
			report.println("");
			
			report.println("Non-Trusted Converters...");
			for (int ii = 0; ii < nonTrustedConvs.size(); ii++) {
				ConvResult cr = (ConvResult) nonTrustedConvs.get(ii);
				report.println("  " + cr.getNameNoPackage());
			}
			report.println("");
			
			for (int ii = 0; ii < convResults.length; ii++) {
				this.convSubGen.generate(convResults[ii]);
				ConvReport convReport = this.convSubGen.getConvReport();
				convReportsList.add(convReport);
			}
			
//			String summary = "%" + avgChanceCorrect * 100 + " Correct";
			String summary = "";
			ConvReport[] convReports = 
				(ConvReport[]) convReportsList.toArray(new ConvReport[0]);
			this.allConvsReport = 
				new AllConvsReport(reportFile, convReports,
						"All Converters Report", summary);
			
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

package org.cishell.testing.convertertester.core.tester2.reportgen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.cishell.testing.convertertester.core.tester2.TestResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.ConvBasedResult;

/**
 * 
 * @author mwlinnem
 *
 */
public class OverviewReportGenerator implements ReportGenerator {

	public static final String TEMP_FILE_PATH = "overview-report.txt";
	
	private final ConvResultGenerator crGen = new ConvResultGenerator();
	private File reportFile = null;
	
	public void generateReport(TestResult[] testResults) {
		ConvBasedResult[] convResults = crGen.generate(testResults);
		
		int numTests = testResults.length;
		
		int numCompleteSuccesses = 0;
		int numPartialSuccesses = 0;
		int numCompleteFailures = 0;
		
		for (int ii = 0; ii < testResults.length; ii++) {
			TestResult tr = testResults[ii];
			if (tr.allSucceeded()) {
				numCompleteSuccesses++;
			} else if (tr.someSucceeded()) {
				numPartialSuccesses++;
			} else {
				numCompleteFailures++;
			}
		}
		
		int numConverters = convResults.length;
		
		int numTrusted = 0;
		
		for (int ii = 0; ii < convResults.length; ii++) {
			ConvBasedResult cbr = convResults[ii];
			
			if (cbr.isTrusted()) {
				numTrusted++;
			}
		}
		
		int numNotTrusted = numConverters - numTrusted;
		
		//write collected information to report
		
		FileOutputStream reportOutStream = null;
		try {
			this.reportFile = new File(TEMP_FILE_PATH);
			reportOutStream = new FileOutputStream(reportFile);
			PrintStream report = new PrintStream(reportOutStream);
			
			report.println("Converter Tester Overview Report");
			report.println("---------------------------------------------");
			report.println(""                                             );
			report.println("Tests..."                                     );
			report.println("  Complete Successes: " + numCompleteSuccesses);
			report.println("  Partial Successes : " + numPartialSuccesses );
			report.println("  Complete Failures : " + numCompleteFailures );
			report.println("  Total             : " + numTests            );
			report.println(""                                             );
			report.println("Converters..."                                );
			report.println("  Trusted    : " + numTrusted                 );
			report.println("  Not Trusted: " + numNotTrusted              );
			report.println("  Total      : " + numConverters              );
			report.println(""                                             );
			
			report.flush();
		
			reportOutStream.close();
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

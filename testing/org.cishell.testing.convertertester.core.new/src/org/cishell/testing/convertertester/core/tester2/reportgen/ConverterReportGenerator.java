package org.cishell.testing.convertertester.core.tester2.reportgen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.testing.convertertester.core.tester2.TestResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.ConvBasedResult;

public class ConverterReportGenerator implements ReportGenerator {

	public static final String TEMP_FILE_PATH = "converter-report.txt";
	
	private File reportFile = null;
	
	public void generateReport(TestResult[] allTestResults) {
		ConvResultGenerator convGen = new ConvResultGenerator();
		ConvBasedResult[] convResults = convGen.generate(allTestResults);
		
		FileOutputStream reportOutStream = null;
		try {
			this.reportFile = new File(TEMP_FILE_PATH);
			reportOutStream = new FileOutputStream(reportFile);
			PrintStream report = new PrintStream(reportOutStream);
			
			report.println("Converter-Based Report");
			report.println("---------------------------------------------");
			report.println(""                                             );
			
			for (int ii = 0; ii < convResults.length; ii++) {
				ConvBasedResult convResult = convResults[ii];
				AlgorithmFactory conv = convResult.getConverter();
				
				report.println(conv.getClass().toString());
				
				if (convResult.isTrusted()) {
					report.println("Trusted");
					report.println("% Passed        : " +
							convResult.getPercentPassed());
				} else {
					report.println("% Passed        : " +
							convResult.getPercentPassed());
					report.println("% Chance of Flaw: " + 
							convResult.getChanceOfFlaw());
				}
				
				report.println("");
			}
			
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

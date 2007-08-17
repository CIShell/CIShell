package org.cishell.testing.convertertester.core.tester2.reportgen.alltests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.cishell.testing.convertertester.core.tester2.reportgen.ReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.FilePassReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;

public class FilePassSubGenerator {
	
	private FilePassReport filePassReport;


	public void generateSubreport(FilePassResult fpr) {
		FileOutputStream reportOutStream = null;
		try {
			File reportFile = new File(ReportGenerator.TEMP_DIR + fpr.getName());
			reportOutStream = new FileOutputStream(reportFile);
			PrintStream report = new PrintStream(reportOutStream);
			
			report.println("File Pass Result Report");
			report.println("-----------------------------------------------");
			report.println("");
			
			boolean succeeded = fpr.succeeded();
			
			if (succeeded) {
				report.println("Succeeded");
			} else {
				report.println("Failed");
			}
			
			writeReport(report, fpr);
			
			String summary = "";
			this.filePassReport = new FilePassReport(reportFile, fpr.getName(),
					summary);
			
		} catch (IOException e) {
			System.out.println("Unable to generate file pass report.");
			e.printStackTrace();	
			closeStream(reportOutStream);
		} finally {
			closeStream(reportOutStream);
		}
		


		
	}
	
	public FilePassReport getFilePassReport() {
		return this.filePassReport;
	}
	
	public void writeReport(PrintStream report, FilePassResult fpr) {
		
		report.println("");
		report.println("File used   : " + fpr.getOriginalFileLabel());
		
		if (! fpr.getExplanation().trim().equals("")) {
			report.println("Explanation... \n" + fpr.getExplanation());
		}
		
		report.println("");
		
		report.flush();
	}
	
	private void closeStream(FileOutputStream stream) {
		try {
			if (stream != null)
				stream.close();
		} catch (IOException e2) {
			System.out.println("Unable to close file pass report stream");
			e2.printStackTrace();
		}
	}
}

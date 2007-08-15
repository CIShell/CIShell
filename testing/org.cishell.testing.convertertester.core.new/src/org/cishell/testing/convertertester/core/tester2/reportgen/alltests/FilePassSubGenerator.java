package org.cishell.testing.convertertester.core.tester2.reportgen.alltests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.cishell.testing.convertertester.core.tester2.reportgen.reports.FilePassReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.TestResult;

public class FilePassSubGenerator {
	
	private FilePassReport filePassReport;


	public void generateSubreport(FilePassResult fpr) {
		FileOutputStream reportOutStream = null;
		try {
			File reportFile = new File(fpr.getName());
			reportOutStream = new FileOutputStream(reportFile);
			PrintStream report = new PrintStream(reportOutStream);
			
			report.println("File Pass Result Report");
			report.println("-----------------------------------------------");
			report.println("");
			
			boolean succeeded = fpr.succeeded();
			
			String summary = null;
			if (succeeded) {
				summary = "Succeeded";
				report.println(summary);
			} else {
				summary = "Failed";
				report.println(summary);
			}
			
			report.println("");
			report.println("File used   : " + fpr.getOriginalFileLabel());
			
			if (! fpr.getExplanation().trim().equals("")) {
				report.println("Explanation :" + fpr.getExplanation());
			}
			
			report.println("");
			
			report.flush();
			
			
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

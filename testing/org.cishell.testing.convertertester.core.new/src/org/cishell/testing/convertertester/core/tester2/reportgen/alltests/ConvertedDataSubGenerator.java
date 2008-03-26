package org.cishell.testing.convertertester.core.tester2.reportgen.alltests;

import java.io.File;
import java.io.PrintStream;

import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.ConvertedDataReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.TestResult;
import org.osgi.service.log.LogService;

public class ConvertedDataSubGenerator {
private ConvertedDataReport convertedDataReport;
	
	private LogService log;
	
	public ConvertedDataSubGenerator(LogService log) {
		this.log = log;
	}
	
	public void generateSubreport(TestResult tr, FilePassResult fpr, Data[] convertedData) {
		if (convertedData.length == 0) {
			return;
		}

			Data firstData = convertedData[0];
			Object fileData = firstData.getData();
			if (fileData != null && fileData instanceof File) {
				this.convertedDataReport =
					new ConvertedDataReport((File) fileData,(String) firstData.getMetadata().get(DataProperty.LABEL) +
							" for " + fpr.getName() + " of " + tr.getName());
			} else {
				this.convertedDataReport = 
					new ConvertedDataReport((String) firstData.getMetadata().get(DataProperty.LABEL) +  
							" for " + fpr.getName() + " of " +  tr.getName());
			}
	}
	
	public ConvertedDataReport getReport() {
		return this.convertedDataReport;
	}
	
	public void writeReport(PrintStream report, FilePassResult fpr) {
		
		report.println("");
		report.println("File used   : " + fpr.getOriginalFileLabel());
		
		report.println("");
		if (! fpr.getExplanation().trim().equals("")) {
			
			if (fpr.failedWhileConverting()) {
				report.println("Failed at " + fpr.getFailedConverter());
			}
			
			report.println("Explanation... \r\n" + fpr.getExplanation());
		}
		
		report.println("");
		
		report.flush();
	}
}

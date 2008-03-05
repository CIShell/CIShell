package org.cishell.testing.convertertester.core.tester2.reportgen.alltests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.cishell.framework.data.Data;
import org.cishell.testing.convertertester.core.tester2.reportgen.ReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.ConvertedDataReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.FilePassReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.TestResult;
import org.osgi.service.log.LogService;

public class FilePassSubGenerator {
	
	private FilePassReport filePassReport;
	
	private ConvertedDataSubGenerator convDataSubGenerator;
	
	private LogService log;
	
	public FilePassSubGenerator(LogService log) {
		this.log = log;
		this.convDataSubGenerator = new ConvertedDataSubGenerator(log);
	}
	
	public void generateSubreport(TestResult tr, FilePassResult fpr) {
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
			
			Data[][] testData = fpr.getTestData();
			Data[][] origCompareData = fpr.getOrigCompareData();
			Data[][] resultCompareData = fpr.getResultCompareData();
			
			ConvertedDataReport[] testConvDataReports = genConvDataReports(tr, fpr, testData); 
			ConvertedDataReport[] origCompareConvDataReports = genConvDataReports(tr, fpr,origCompareData);
			ConvertedDataReport[] resultCompareConvDataReports = genConvDataReports(tr, fpr,resultCompareData);
			
			
			this.filePassReport = new FilePassReport(reportFile, fpr.getName() + "  for " + tr.getName(), 
					testConvDataReports,
					origCompareConvDataReports,
					resultCompareConvDataReports);
			
		} catch (IOException e) {
			this.log.log(LogService.LOG_ERROR, 
					"Unable to generate file pass report.", e);
			closeStream(reportOutStream);
		} finally {
			closeStream(reportOutStream);
		}
	}
	
	private ConvertedDataReport[] genConvDataReports(TestResult tr, FilePassResult fpr, Data[][] data) {
		if (data != null) {
			ConvertedDataReport[] testConvDataReports = new ConvertedDataReport[data.length];

			for (int ii = 0; ii < data.length; ii++) {
				Data[] datum = data[ii];
				convDataSubGenerator.generateSubreport(tr, fpr, datum);
				testConvDataReports[ii] =convDataSubGenerator.getReport();
			}
			
			return testConvDataReports;
		} else {
			return new ConvertedDataReport[0];
		}
	}
	
	public FilePassReport getFilePassReport() {
		return this.filePassReport;
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
	
	private void closeStream(FileOutputStream stream) {
		try {
			if (stream != null)
				stream.close();
		} catch (IOException e2) {
			this.log.log(LogService.LOG_ERROR, 
					"Unable to close file pass report stream", e2);
		}
	}
}

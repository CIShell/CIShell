package org.cishell.testing.convertertester.core.tester2.reportgen.reports;

import java.io.File;

public class FilePassReport implements Report {
	
	private File filePassReport;
	
	private String name; 
	
	private ConvertedDataReport[] testConvertedDataReports;
	private ConvertedDataReport[] origCompareConvertedDataReports;
	private ConvertedDataReport[] resultCompareConvertedDataReports;
	
	public FilePassReport (File filePassReport,
			String name,
			ConvertedDataReport[] testConvertedDataReports,
			ConvertedDataReport[] origCompareConvertedDataReports,
			ConvertedDataReport[] resultCompareConvertedDataReports) {
		this.filePassReport = filePassReport;
		this.name = name;
		this.testConvertedDataReports = testConvertedDataReports;
		this.origCompareConvertedDataReports = origCompareConvertedDataReports;
		this.resultCompareConvertedDataReports = resultCompareConvertedDataReports;
	}
	
	public File getFilePassReport() {
		return this.filePassReport;
	}
	
	public String getName() {
		return this.name;
	}
	
	public ConvertedDataReport[] getTestConvertedDataReports() {
		return this.testConvertedDataReports;
	}
	
	public ConvertedDataReport[] getOrigCompareConvertedDataReports() {
		return this.origCompareConvertedDataReports;
	}
	
	public ConvertedDataReport[] getResultCompareConvertedDataReports() {
		return this.resultCompareConvertedDataReports;
	}
	
}

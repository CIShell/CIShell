package org.cishell.testing.convertertester.core.tester2.reportgen.reports;

import java.io.File;

public class FilePassReport implements Report {
	
	private File filePassReport;
	
	private String name; 
	
	private ConvertedDataReport[] convertedDataReports;
	
	public FilePassReport (File filePassReport, String name, ConvertedDataReport[] convertedDataReports) {
		this.filePassReport = filePassReport;
		this.name = name;
		this.convertedDataReports = convertedDataReports;
	}
	
	public File getFilePassReport() {
		return this.filePassReport;
	}
	
	public String getName() {
		return this.name;
	}
	
	public ConvertedDataReport[] getConvertedDataReports() {
		return this.convertedDataReports;
	}
}

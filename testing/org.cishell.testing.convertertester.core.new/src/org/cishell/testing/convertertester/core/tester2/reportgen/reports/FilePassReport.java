package org.cishell.testing.convertertester.core.tester2.reportgen.reports;

import java.io.File;

public class FilePassReport implements Report {
	
	private File filePassReport;
	
	private String name; 
	private String summary;
	
	public FilePassReport (File filePassReport, String name, String summary) {
		this.filePassReport = filePassReport;
		this.name = name;
		this.summary = summary;
	}
	
	public File getFilePassReport() {
		return this.filePassReport;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getShortSummary() {
		return this.summary;
	}
}

package org.cishell.testing.convertertester.core.tester2.reportgen.reports;

import java.io.File;

public class FilePassReport implements Report {
	
	private File filePassReport;
	
	private String name; 
	
	public FilePassReport (File filePassReport, String name) {
		this.filePassReport = filePassReport;
		this.name = name;
	}
	
	public File getFilePassReport() {
		return this.filePassReport;
	}
	
	public String getName() {
		return this.name;
	}
}

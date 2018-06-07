package org.cishell.testing.convertertester.core.tester2.reportgen.reports;

import java.io.File;

public class ConvertedDataReport implements Report {

	private String name;
	private File report;
	
	public ConvertedDataReport (File report, String name) {
		this.report = report;
		this.name = name;
	}
	
	public ConvertedDataReport(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public File getReport() {
		return report;
	}

}

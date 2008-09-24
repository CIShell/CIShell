package org.cishell.testing.convertertester.core.tester2.reportgen.reports;

import java.io.File;

public class AllErrorReport implements Report {

	private File file;
	private String name;
	
	public AllErrorReport(File file, String name) {
		this.file = file;
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	
	public File getReportFile() {
		return this.file;
	}
}

package org.cishell.testing.convertertester.core.tester2.reportgen.reports;

import java.io.File;


public class ReadMeReport implements Report {

	private File file;
	private String name;
	
	public ReadMeReport(File file, String name) {
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

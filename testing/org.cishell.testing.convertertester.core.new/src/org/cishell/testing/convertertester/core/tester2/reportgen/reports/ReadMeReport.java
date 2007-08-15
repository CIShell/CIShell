package org.cishell.testing.convertertester.core.tester2.reportgen.reports;

import java.io.File;


public class ReadMeReport implements Report {

	private File file;
	private String name;
	private String summary;
	
	public ReadMeReport(File file, String name, String summary) {
		this.file = file;
		this.name = name;
		this.summary = summary;
	}
	public String getName() {
		return this.name;
	}

	public String getShortSummary() {
		return this.summary;
	}
	
	public File getReportFile() {
		return this.file;
	}

}

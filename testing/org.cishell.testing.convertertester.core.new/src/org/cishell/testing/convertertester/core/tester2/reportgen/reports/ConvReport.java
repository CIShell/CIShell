package org.cishell.testing.convertertester.core.tester2.reportgen.reports;

import java.io.File;

public class ConvReport implements Report {

	private File report;
	private TestReport[] trs;
	private String name;
	private String summary;
	
	public ConvReport (File report, TestReport[] trs, String name, String summary) {
		this.report = report;
		this.trs = trs;
		this.name = name;
		this.summary = summary;
	}
	public String getName() {
		return this.name;
	}
	
	public File getReport() {
		return this.report;
	}
	
	public TestReport[] getTestReports() {
		return this.trs;
	}
	
	public String getShortSummary() {
		return this.summary;
	}
}

package org.cishell.testing.convertertester.core.tester2.reportgen.reports;

import java.io.File;

public class ConvReport implements Report {

	private File report;
	private TestReport[] trs;
	private String name;
	
	public ConvReport (File report, TestReport[] trs, String name) {
		this.report = report;
		this.trs = trs;
		this.name = name;
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
}

package org.cishell.testing.convertertester.core.tester2.reportgen.reports;

import java.io.File;


public class TestReport implements Report{

	private File testReport;
	private FilePassReport[] successfulFilePassReports;
	private FilePassReport[] failedFilePassReports;
	
	private String name;
	private String summary;
	
	public TestReport(File testReport, String name, FilePassReport[] successfulFilePassReports,
			FilePassReport[] failedFilePassReports, String summary) {
		this.testReport     = testReport;
		this.name = name;
		this.successfulFilePassReports = successfulFilePassReports;
		this.failedFilePassReports = failedFilePassReports;
		this.summary = summary;
	}
	
	public File getTestReport() {
		return this.testReport;
	}
	
	public FilePassReport[] getSuccessfulFilePassReports() {
		return this.successfulFilePassReports;
	}
	
	public FilePassReport[] getFailedFilePassReports() {
		return this.failedFilePassReports;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getShortSummary() {
		return this.summary;
	}
}

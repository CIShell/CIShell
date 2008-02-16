package org.cishell.testing.convertertester.core.tester2.reportgen.reports;

import java.io.File;


public class TestReport implements Report{

	private File testReport;
	private FilePassReport[] successfulFilePassReports;
	private FilePassReport[] failedFilePassReports;
	private String name;
	
	public TestReport(File testReport, String name, FilePassReport[] successfulFilePassReports,
			FilePassReport[] failedFilePassReports) {
		this.testReport     = testReport;
		this.name = name;
		this.successfulFilePassReports = successfulFilePassReports;
		this.failedFilePassReports = failedFilePassReports;
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
}

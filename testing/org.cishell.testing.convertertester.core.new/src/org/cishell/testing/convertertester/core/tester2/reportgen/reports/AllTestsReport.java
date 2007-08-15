package org.cishell.testing.convertertester.core.tester2.reportgen.reports;

import java.io.File;


public class AllTestsReport implements Report {

	private File allTestsReport;
	private TestReport[] successfulTestReports;
	private TestReport[] partialSuccessTestReports;
	private TestReport[] failedTestReports;
	
	private String name;
	private String summary;
	
	public AllTestsReport(File allTestsReport, String name,
			TestReport[] successfulTestReports,
			TestReport[] partialSuccessTestReports,
			TestReport[] failedTestReports,
			String summary) {
		this.allTestsReport = allTestsReport;
		this.name = name;
		this.successfulTestReports = successfulTestReports;
		this.partialSuccessTestReports = partialSuccessTestReports;
		this.failedTestReports = failedTestReports;
		this.summary = summary;
	}
	
	public File getAllTestsReport() {
		return this.allTestsReport;
	}
	
	public TestReport[] getSuccessfulTestReports() {
		return this.successfulTestReports;
	}
	
	public TestReport[] getPartialSuccessTestReports() {
		return this.partialSuccessTestReports;
	}
	
	public TestReport[] getFailedTestReports() {
		return this.failedTestReports;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getShortSummary() {
		return this.summary;
	}
}

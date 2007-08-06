package org.cishell.testing.convertertester.core.tester2.reportgen;

import org.cishell.testing.convertertester.core.tester2.TestResult;


public interface ReportGenerator {

	public void generateReport(TestResult[] allTestResults);
}

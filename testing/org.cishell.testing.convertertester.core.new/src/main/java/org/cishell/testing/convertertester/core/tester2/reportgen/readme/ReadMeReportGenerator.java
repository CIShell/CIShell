package org.cishell.testing.convertertester.core.tester2.reportgen.readme;

import java.io.File;

import org.cishell.testing.convertertester.core.tester2.TestFileKeeper;
import org.cishell.testing.convertertester.core.tester2.reportgen.ReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.ReadMeReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllConvsResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllTestsResult;
import org.osgi.service.log.LogService;

public class ReadMeReportGenerator implements ReportGenerator {

	private LogService log;
	private ReadMeReport readme;
	
	public ReadMeReportGenerator(LogService log) {
		this.log = log;
	}
	/**
	 * Instead of actually generating the report in-line, we just
	 * load a file with the report text inside it, and return it as is.
	 */
	public void generateReport(AllTestsResult atr,
			                   AllConvsResult acr,
			                   File nwbConvGraph) {
		String readmePath = TestFileKeeper.DEFAULT_ROOT_DIR +
			"ReportREADME.txt";
		File readmeFile = new File(readmePath);

		this.readme = new ReadMeReport(readmeFile, "README");
	}
	
	public ReadMeReport getReadMe() {
		return this.readme;
	}

}

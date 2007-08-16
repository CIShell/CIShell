package org.cishell.testing.convertertester.core.tester2.reportgen.readme;

import java.io.File;

import org.cishell.testing.convertertester.core.tester2.TestFileKeeper;
import org.cishell.testing.convertertester.core.tester2.reportgen.ReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.ReadMeReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.Report;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllTestsResult;

public class ReadMeReportGenerator implements ReportGenerator {

	private ReadMeReport readme;
	
	public void generateReport(AllTestsResult atr) {
		String readmePath = TestFileKeeper.DEFAULT_ROOT_DIR + "ReportREADME.txt";
		System.out.println("ReadMe path is: " + readmePath);
		File readmeFile = new File(readmePath);

		this.readme = new ReadMeReport(readmeFile, "README", "");
	}
	
	public ReadMeReport getReadMe() {
		return this.readme;
	}

}

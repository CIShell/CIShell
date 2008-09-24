package org.cishell.testing.convertertester.core.tester2.reportgen;

import java.io.File;

import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllConvsResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllTestsResult;


public interface ReportGenerator {

	public static final String FS = File.separator;
	public static final String TEMP_DIR = "tmp" + FS; 
	
	public void generateReport(AllTestsResult atr,
							   AllConvsResult acr,
							   File nwbConvGraph);
}

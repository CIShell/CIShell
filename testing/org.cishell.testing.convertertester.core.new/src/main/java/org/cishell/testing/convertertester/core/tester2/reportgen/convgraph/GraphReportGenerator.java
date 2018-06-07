package org.cishell.testing.convertertester.core.tester2.reportgen.convgraph;

import java.io.File;

import org.cishell.testing.convertertester.core.tester2.reportgen.ReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllConvsResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllTestsResult;
import org.osgi.service.log.LogService;

public class GraphReportGenerator implements ReportGenerator {

	private LogService log;
	private File nwbConvGraph;
	
	public GraphReportGenerator(LogService log) {
		this.log = log;
	}
	
	public void generateReport(AllTestsResult atr,
							   AllConvsResult acr,
							   File nwbConvGraph) {
		this.nwbConvGraph = nwbConvGraph;
	}
	
	
	public File getGraphReport() {
		return this.nwbConvGraph;
	}
}

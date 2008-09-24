package org.cishell.testing.convertertester.core.tester2.reportgen.reports;

import java.io.File;

public class AllConvsReport implements Report {

	private File report;
	private ConvReport[] convReports;
	private String name;
	
	public AllConvsReport (File report, ConvReport[] convReports, String name) {
		this.report = report;
		this.convReports = convReports;
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	
	public File getReport() {
		return this.report;
	}
	
	public ConvReport[] getConverterReports() {
		return this.convReports;
	}
}

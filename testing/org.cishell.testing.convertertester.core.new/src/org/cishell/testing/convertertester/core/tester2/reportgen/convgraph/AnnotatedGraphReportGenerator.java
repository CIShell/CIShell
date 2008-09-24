package org.cishell.testing.convertertester.core.tester2.reportgen.convgraph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.cishell.testing.convertertester.core.tester2.ConvResultMaker;
import org.cishell.testing.convertertester.core.tester2.reportgen.ReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllConvsResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllTestsResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.ConvResult;
import org.cishell.testing.convertertester.core.tester2.util.FormatUtil;
import org.osgi.service.log.LogService;

public class AnnotatedGraphReportGenerator implements ReportGenerator {

	private ConvResultMaker convGen = new ConvResultMaker();
	private LogService log;
	
	private File annotatedNWBGraph = new File(TEMP_DIR + "annotated-converter-graph.nwb");
	
	private String NODE_LINE = "^\\d+? \".*?\"$";
	
	public AnnotatedGraphReportGenerator(LogService log) {
		this.log = log;
	}
	
	public void generateReport(AllTestsResult atr,
							   AllConvsResult acr,
							   File nwbConvGraph) {
		ConvResult[] convs = acr.getConvResults();
		
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			reader = new BufferedReader( new FileReader(nwbConvGraph));
			writer = new BufferedWriter( new FileWriter(this.annotatedNWBGraph));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				
				if (line.startsWith("id*int")) {
					writer.write(line + " isTrusted*int chanceCorrect*float isConverter*int \r\n");
				} else if (line.matches(NODE_LINE)) {
					String[] parts = line.split(" ");
					String rawConvName = parts[1];
					//raw names are now short names
					String convName = rawConvName.replaceAll("\"", "");
					
					boolean wroteAttributes = false;
					for (int ii = 0; ii < convs.length ; ii++) {
						ConvResult cr = convs[ii];
						if (cr.getShortName().equals(convName)) {
							int trusted;
							
							if (cr.isTrusted()) {
								trusted = 1;
							} else {
								trusted = 0;
							}
							
							writer.write(line + " " + trusted 
								+ " " + 
								FormatUtil.formatToPercent(cr.getChanceCorrect())
								+ " 1 " + "\r\n");
							wroteAttributes = true;
							break;
						}
					}
					
					if (! wroteAttributes) {
						writer.write(line + " 1 100.0 0" + "\r\n");
					}
					
				} else {
					writer.write(line + "\r\n");
				}
			}
			
		} catch (IOException e) {
			this.log.log(LogService.LOG_ERROR,
					"Unable to generate Graph Report.", e);
			try {		
				if (reader != null) reader.close();
				} catch (IOException e2) {
					this.log.log(LogService.LOG_ERROR,
							"Unable to close graph report stream", e);
				}
		} finally {
			try {
			if (reader != null) {
				reader.close();
			} 
			
			if (writer != null) {
				writer.close();
			}
			} catch (IOException e) {
				this.log.log(LogService.LOG_ERROR, 
						"Unable to close either graph report reader or " +
						"writer.", e);
				e.printStackTrace();
			}
		}
	}
	
	
	public File getGraphReport() {
		return this.annotatedNWBGraph;
	}
}

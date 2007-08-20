package org.cishell.testing.convertertester.core.tester2.reportgen.convgraph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.cishell.testing.convertertester.core.tester2.reportgen.ConvResultMaker;
import org.cishell.testing.convertertester.core.tester2.reportgen.ReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllTestsResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.ConvResult;
import org.osgi.service.log.LogService;

public class GraphReportGenerator implements ReportGenerator {

	private File nwbGraph;
	private ConvResultMaker convGen = new ConvResultMaker();
	private LogService log;
	
	private File annotatedNWBGraph = new File(TEMP_DIR + "annotated-converter-graph.nwb");
	
	private String NODE_LINE = "^\\d+? \".*?\"$";
	
	public GraphReportGenerator(File nwbGraph, LogService log) {
		this.log = log;
		this.nwbGraph = nwbGraph;
	}
	
	public void generateReport(AllTestsResult atr) {
		ConvResult[] convs = convGen.generate(atr);
		
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			reader = new BufferedReader( new FileReader(this.nwbGraph));
			writer = new BufferedWriter( new FileWriter(this.annotatedNWBGraph));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				
				if (line.startsWith("id*int")) {
					writer.write(line + " isTrusted*int chanceCorrect*float isConverter*int \n");
				} else if (line.matches(NODE_LINE)) {
					String[] parts = line.split(" ");
					String rawConvName = parts[1];
					String convName = rawConvName.replaceAll("\"", "");
					
					boolean wroteAttributes = false;
					for (int ii = 0; ii < convs.length ; ii++) {
						ConvResult cr = convs[ii];
						if (cr.getName().equals(convName)) {
							int trusted;
							
							if (cr.isTrusted()) {
								trusted = 1;
							} else {
								trusted = 0;
							}
							
							writer.write(line + " " + trusted + " " + 
									cr.getChanceCorrect() + " 1 " + "\n");
							wroteAttributes = true;
							break;
						}
					}
					
					if (! wroteAttributes) {
						writer.write(line + " 1 1.0 0" + "\n");
					}
					
				} else {
					writer.write(line + "\n");
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

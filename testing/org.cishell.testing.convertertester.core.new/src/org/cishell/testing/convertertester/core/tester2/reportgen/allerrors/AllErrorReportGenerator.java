package org.cishell.testing.convertertester.core.tester2.reportgen.allerrors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cishell.testing.convertertester.core.converter.graph.Converter;
import org.cishell.testing.convertertester.core.tester2.reportgen.ReportGenerator;
import org.cishell.testing.convertertester.core.tester2.reportgen.allerrors.analyzer.DefaultErrorSourceAnalyzer;
import org.cishell.testing.convertertester.core.tester2.reportgen.allerrors.analyzer.ErrorSource;
import org.cishell.testing.convertertester.core.tester2.reportgen.allerrors.analyzer.ErrorSourceAnalyzer;
import org.cishell.testing.convertertester.core.tester2.reportgen.faultanalysis.ChanceAtFault;
import org.cishell.testing.convertertester.core.tester2.reportgen.reports.AllErrorReport;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllConvsResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllTestsResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.ConvResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.TestResult;
import org.cishell.testing.convertertester.core.tester2.util.FormatUtil;
import org.osgi.service.log.LogService;

public class AllErrorReportGenerator implements ReportGenerator {
	public static final String TEMP_FILE_PATH = "All-Errors-Report.txt";

	private LogService log;
	
	private AllErrorReport allErrorsReport;
	private ErrorSourceAnalyzer errSourceAnalyzer = 
		new DefaultErrorSourceAnalyzer();
	
	public AllErrorReportGenerator(LogService log) {
		this.log = log;
	}
	
	public void generateReport(AllTestsResult atr,
							   AllConvsResult acr,
							   File nwbConvGraph) {
		
		Map explnToCafs = getChanceAtFaultsByExpln(acr);
		
		FileOutputStream reportOutStream = null;
		try {
			File reportFile = 
				new File(ReportGenerator.TEMP_DIR + TEMP_FILE_PATH);
			reportOutStream = new FileOutputStream(reportFile);
			PrintStream report = new PrintStream(reportOutStream);
			
			report.println("All Errors Report");
			report.println("---------------------------------------------");
			report.println(""                                             );
			
			Set explnSet = explnToCafs.keySet();
			String[] explns = (String[]) explnSet.toArray(new String[0]);
			Arrays.sort(explns);
			
			for (int ii = 0; ii < explns.length; ii++) {
				
				report.println("------------");
				report.println("Error " + ii);
				
				String expln = explns[ii];
				
				report.println(expln);
				report.println("");
				
				List CafsWithSameExpln = (List) explnToCafs.get(expln);
				Map testToPassesToCafs = 
					categorizeByTestAndPass(CafsWithSameExpln);
				
				ErrorSource[] errSources = 
					this.errSourceAnalyzer.analyze(testToPassesToCafs);
				
				report.println("--Error Source Analysis Summary--");
				
				for (int jj = 0; jj < errSources.length; jj++) {
					ErrorSource errSource = errSources[jj];
					
					report.println("");
					
					ChanceAtFault[] overallCafs = errSource.getCulprits();
					
					if (overallCafs.length > 0) {
					report.println(errSource.getComment());
					
					Arrays.sort(overallCafs,
							ChanceAtFault.COMPARE_BY_LIKELIHOOD);
					
					for (int kk = 0; kk < overallCafs.length; kk++) {
						ChanceAtFault caf = overallCafs[kk];
						
						report.println(caf.getConverter() + " (%" +
									FormatUtil.
										formatToPercent(
												caf.getChanceAtFault()) + 
									" Chance At Fault)");
					}
					
					report.println("");
					
					} else {
						report.println("There is no converter common to all "  +
								"failed file passes with this error.");
						report.println("This most likely means that there is " +
								"more than one source that returns this error" +
								" message.");
						report.println("");
					}
				}
				report.println("--All Error Sources--");
				
				Set tests = testToPassesToCafs.keySet();
				
				Iterator testIter = tests.iterator();
				while (testIter.hasNext()) {
					TestResult test = (TestResult) testIter.next();
					
					report.println(test.getName());
					
					Map passToCafs
						= (Map) testToPassesToCafs.get(test);
					
					Set passes = passToCafs.keySet();
					Iterator passIter = passes.iterator();
					while (passIter.hasNext()) {
						FilePassResult pass = (FilePassResult) passIter.next();
						
						report.println("  " + pass.getName());
						report.println("  " + pass.getOriginalFileShortLabel());
						
						List cafs = (List) passToCafs.get(pass);
						
						Converter[] involvedConvs = pass.getConvertersInvolved();
						for (int jj = 0; jj < involvedConvs.length; jj++) {
							Converter involvedConv = involvedConvs[jj];
							
							ChanceAtFault associatedCaf = 
								getCafAssociatedWithProvidedConverter(
										cafs, involvedConv);
							
							if (associatedCaf.getChanceAtFault() > 0.0f) {
							report.println("    " + 
									involvedConv.getShortName() + " (%" +
									FormatUtil.formatToPercent(associatedCaf.getChanceAtFault()) + 
									" Chance At Fault)");
							} else {
								report.println("    " + 
										involvedConv.getShortName());
							}
						}
					}
				}
			}
			report.flush();
			
			reportOutStream.close();
			
			this.allErrorsReport = new AllErrorReport(reportFile,
					"All Errors Report");
			
		} catch (IOException e) {
			
			this.log.log(LogService.LOG_ERROR, "Unable to generate all converters report.", e);
			try {		
				if (reportOutStream != null) reportOutStream.close();
				} catch (IOException e2) {
					this.log.log(LogService.LOG_ERROR, "Unable to generate all converters report.", e);
				}
		}
	}
	
	//expln = explanation
	private Map getChanceAtFaultsByExpln(AllConvsResult acr) {
		Map explnToCafs = new HashMap();
		
		ConvResult[] crs = acr.getConvResults();
		for (int ii = 0; ii < crs.length; ii++) {
			ConvResult cr = crs[ii];
			
			ChanceAtFault[] cafs = cr.getAllChanceAtFaults();
			for (int jj = 0; jj < cafs.length; jj++) {
				ChanceAtFault caf = cafs[jj];
				
				String expln = caf.getExplanation();
				
				if(explnToCafs.get(expln) != null) {
					((List)explnToCafs.get(expln)).add(caf);
				}
				else {
					List cafsWithSameExpln = new ArrayList();
					cafsWithSameExpln.add(caf);
					explnToCafs.put(expln, cafsWithSameExpln);
				}
			}
		}
		
		return explnToCafs;
	}
	
	//Map<test, Map<pass, List<cafs>>>
	private Map categorizeByTestAndPass(List cafList) {
		Map testToPassesToCafs = new HashMap();
		for (int ii = 0; ii < cafList.size(); ii++) {
			ChanceAtFault caf = (ChanceAtFault) cafList.get(ii);	
			FilePassResult pass = caf.getFailedFilePass();
			TestResult test = pass.getParent();
			
			Map passToCafs;
			List cafs;
			
			passToCafs = (Map) testToPassesToCafs.get(test);
			
			if(passToCafs == null) {
				passToCafs = new HashMap();
				
				cafs = new ArrayList();
			} else {	
			cafs = (List) passToCafs.get(pass);
			
			if (cafs == null) {
				cafs = new ArrayList();
			}
			}
			
			cafs.add(caf);
			passToCafs.put(pass, cafs);
			testToPassesToCafs.put(test, passToCafs);
		}
		
		return testToPassesToCafs;
	}
	
	public AllErrorReport getAllErrorsReport() {
		return this.allErrorsReport;
	}
	
	public ChanceAtFault getCafAssociatedWithProvidedConverter(
			List cafs, Converter conv) {
		Iterator cafIter = cafs.iterator();
		while (cafIter.hasNext()) {
			ChanceAtFault caf = (ChanceAtFault) cafIter.next();
			
			if (caf.getConverter() == conv) {
				return caf;
			}
		}
		
		return null;
	}
}

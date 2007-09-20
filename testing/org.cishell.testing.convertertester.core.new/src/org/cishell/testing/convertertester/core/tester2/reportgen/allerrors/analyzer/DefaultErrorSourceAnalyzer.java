package org.cishell.testing.convertertester.core.tester2.reportgen.allerrors.analyzer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cishell.testing.convertertester.core.tester2.reportgen.faultanalysis.ChanceAtFault;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.TestResult;

public class DefaultErrorSourceAnalyzer implements ErrorSourceAnalyzer {

	public ErrorSource[] analyze(Map testToPassesToCafs) {
		
		PotentialCulpritList potentialCulprits = new PotentialCulpritList();
		
		Set tests = testToPassesToCafs.keySet();
		
		Iterator testIter = tests.iterator();
		while (testIter.hasNext()) {
			TestResult test = (TestResult) testIter.next();
			
			Map passToCafs
				= (Map) testToPassesToCafs.get(test);
			
			Set passes = passToCafs.keySet();
			Iterator passIter = passes.iterator();
			while (passIter.hasNext()) {
				FilePassResult pass = (FilePassResult) passIter.next();
				
				List cafList = (List) passToCafs.get(pass);
				ChanceAtFault[] cafs = 
					(ChanceAtFault[]) cafList.toArray(new ChanceAtFault[0]);
				
				potentialCulprits.merge(cafs);
				}
			}
		
		ErrorSource onlyErrorSource = new ErrorSource(
				"Assuming the error has only one source, the following " +
				"converters are most likely to be that source.",
				potentialCulprits.getCulprits());
		
		return new ErrorSource[] {onlyErrorSource};
	}
	
	private class PotentialCulpritList {
		private List pcs;
				
		public void merge(ChanceAtFault[] otherPCs) {
			if (this.pcs != null) {
				//set potential culprits to be intersection of
				//new potential culprits and old.
				List newPCs = new ArrayList();

				for (int ii = 0; ii < this.pcs.size(); ii++) {
					ChanceAtFault pc = (ChanceAtFault) this.pcs.get(ii);

					for (int jj = 0; jj < otherPCs.length; jj++) {
						ChanceAtFault otherPC = otherPCs[jj];

						if (pc.getConverter() == otherPC.getConverter()
								&& otherPC.getChanceAtFault() > 0.0f) {

							ChanceAtFault newPC = new ChanceAtFault(pc
									.getFailedFilePass(), pc.getConverter(), pc
									.getChanceAtFault()
									+ otherPC.getChanceAtFault());

							newPCs.add(pc);

							break;
						}
					}
				}

				this.pcs = newPCs;

			} else {
				//add all chance at faults > 0.0f to potential culprit list
				List newPCs = new ArrayList();
				for (int ii = 0; ii < otherPCs.length; ii++) {
					ChanceAtFault pc = (ChanceAtFault) otherPCs[ii];
					
					if (pc.getChanceAtFault() > 0.0f) {
						newPCs.add(pc);
					}
				}
				
				this.pcs = newPCs;
				}
			}
		
		public ChanceAtFault[] getCulprits() {
			//normalize chance at faults
			float totalChanceAtFaults = 0.0f;
			for (int ii = 0; ii <  pcs.size(); ii++) {
				ChanceAtFault pc = (ChanceAtFault) pcs.get(ii);
				
				totalChanceAtFaults += pc.getChanceAtFault();
			}
			
			ChanceAtFault[] uniqueCafs = 
				removeDuplicateConverters(this.pcs);
			
			List uniqueNormalizedCafs = new ArrayList();
			for (int ii = 0; ii < uniqueCafs.length; ii++) {
				ChanceAtFault pc = uniqueCafs[ii];
				
				ChanceAtFault normPC = new ChanceAtFault(
						pc.getFailedFilePass(),
						pc.getConverter(),
						pc.getChanceAtFault() / totalChanceAtFaults);
				
				uniqueNormalizedCafs.add(normPC);
			}
			

			
			return (ChanceAtFault[]) 
				uniqueNormalizedCafs.toArray(new ChanceAtFault[0]);
		}
	}
	
	private ChanceAtFault[] removeDuplicateConverters(List cafs) {
		List newCafs = new ArrayList();
		List newCafConvs = new ArrayList();
		for (int ii = 0; ii < cafs.size(); ii++) {
			ChanceAtFault caf  = (ChanceAtFault) cafs.get(ii);
			if (! newCafConvs.contains(caf.getConverter())) {
				newCafs.add(caf);
				newCafConvs.add(caf.getConverter());
			}
		}
		
		return (ChanceAtFault[]) newCafs.toArray(new ChanceAtFault[0]);
	}
}

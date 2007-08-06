package org.cishell.testing.convertertester.core.tester2.reportgen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.testing.convertertester.core.tester2.TestResult;
import org.cishell.testing.convertertester.core.tester2.filepassresults.ComparePhaseFailure;
import org.cishell.testing.convertertester.core.tester2.filepassresults.ConvertPhaseFailure;
import org.cishell.testing.convertertester.core.tester2.filepassresults.FilePassResult;
import org.cishell.testing.convertertester.core.tester2.filepassresults.FilePassSuccess;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.ConvBasedResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.ConvFilePassFailure;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.ConvFilePassSuccess;

/**
 * 
 * @author mwlinnem
 *
 * 
 */
public class ConvResultGenerator {
	
	/**
	 * Takes an array of ConverterTester results that are organized by test, 
	 * and returns an array of ConverterTester results organized by converter.
	 * This should make it easier for report generators to display
	 * data about how correct or incorrect each converter is, as oppose to 
	 * which tests failed or succeeded.
	 * @param trs The results of testing the converters, organized by test
	 * @return the results of testing the converters, organized by converter
	 */
	public ConvBasedResult[] generate(TestResult[] trs) {
		//maps convert testers to their test result data.
		Map resultHolder = new HashMap();
		 
		resultHolder =  markTrusted(trs, resultHolder);
		resultHolder = createConverterResults(trs, resultHolder);
		 
		ConvBasedResult[] results = extractResults(resultHolder);
		return results;
	}
	
	/**
	 * Looks for tests where each file pass succeeded. We will assume that
	 * each converter involved in a test where all file passes succeeded is
	 * very likely to be correct. If one of these converters is involved
	 * in a file pass that fails at some later point, we will assume
	 * that that converter was not at fault, but was fed erroneous data
	 * by previous converters.
	 * @param trs the converter tester results organized by test
	 * @param rh the result holder, which keeps track of the converter results.
	 * @return the result holder, which is side-effected with trust info.
	 */
	private Map markTrusted( TestResult[] trs,  Map rh) {
		for (int ii = 0; ii < trs.length; ii++) {
			TestResult tr = trs[ii];
			FilePassResult[] fprs = tr.getFilePassResults();
			
			//check if all file passes were successes
			boolean trusted = true;
			for (int jj = 0; jj < fprs.length; jj++) {
				FilePassResult fpr = fprs[jj];
				if (! passed(fpr)) {
					//not all were successes
					trusted = false;
					break;
				}
			} 
			
			if (trusted && fprs.length > 0) {
				//mark all converters involved as trusted.
				FilePassResult anyFpr = fprs[0];
				AlgorithmFactory[] allConvs = anyFpr.getAllConverters();
				for (int kk = 0; kk < allConvs.length; kk++) {
					ConvBasedResult ctr = getResult(rh, allConvs[kk]);
					ctr.setTrusted(true);
				}
			}
		}
		
		return rh;
	}
	
	/**
	 * Associate each converter with the file passes that involved it,
	 * also keeping track of whether the file pass succeeded or not. If the 
	 * file pass did not succeed, record what chance each converter had
	 * of being at fault.
	 * @param trs the converter tester results organized by test
     * @param rh the result holder, which keeps track of the converter
     * results, which now holds info on which converters are trusted.
	 * @return the result holder, which is side-effected with 
	 * success/failure info
	 */
	private Map createConverterResults( TestResult[] trs, Map rh) {
		for (int ii = 0; ii < trs.length; ii++) {
			TestResult tr = trs[ii];
			FilePassResult[] fprs = tr.getFilePassResults();

			for (int jj = 0; jj < fprs.length; jj++) {
				FilePassResult fpr = fprs[jj];
				if (passed(fpr)) {
					FilePassSuccess fprSuccess = (FilePassSuccess) fpr;
					createPassResult(fprSuccess, rh);
				} else if (failedOnConverterPhase(fpr)) {
					ConvertPhaseFailure fprFailure = (ConvertPhaseFailure) fpr;
					createPassResult(fprFailure, rh);
				} else if (failedOnGraphComparePhase(fpr)) {
					ComparePhaseFailure fprFailure = (ComparePhaseFailure) fpr;
					createPassResult(fprFailure, rh);
				}
			}
		}
		
		return rh;
	}
	
	/**
	 * Simply gets the converter results out of the result holder.
     * @param rh the result holder, which keeps track of the converter
     * results, which now holds all the trust and success/failure info
	 * @return the converter results
	 */
	private ConvBasedResult[] extractResults(Map rh) {
		Collection values = rh.values();
		ConvBasedResult[] results = 
			(ConvBasedResult[]) values.toArray(new ConvBasedResult[0]);
		return results;
	}
	
	private void createPassResult(FilePassSuccess fprSuccess,  Map rh) {
		AlgorithmFactory[] allConvs = fprSuccess.getAllConverters();
		for (int kk = 0; kk < allConvs.length; kk++) {
			ConvBasedResult ctr = getResult(rh,allConvs[kk]);
			ctr.addPass(new ConvFilePassSuccess(fprSuccess));
		}
	}
	
	private void createPassResult(ConvertPhaseFailure fprFailure,  Map rh) {
		AlgorithmFactory[] testConvAlgs = fprFailure.getTestConverters();
		AlgorithmFactory[] compareConvAlgs = fprFailure
				.getComparisonConverters();
		
		AlgorithmFactory failedConv = fprFailure.getFailedConverter();
		
		List possiblyResponsible = new ArrayList();		
		List involvedButNotResponsible = new ArrayList();
		
		for (int kk = 0; kk < testConvAlgs.length; kk++) {
			AlgorithmFactory testConvAlg = testConvAlgs[kk];
			
			
			ConvBasedResult ctr = getResult(rh, testConvAlg);
			involvedButNotResponsible.add(ctr);
			if (! ctr.isTrusted()) {
				possiblyResponsible.add(ctr);
			
			}
			
			if (fprFailure.getPhase().equals(ConvertPhaseFailure.TEST_PHASE) &&
					failedConv == testConvAlg) {
				//reached where the converters broke
			 break;	
			}
		}
		
		if (fprFailure.getPhase().equals(ConvertPhaseFailure.COMPARISON_PHASE)) {
			for (int kk = 0; kk < compareConvAlgs.length; kk++) {
				AlgorithmFactory compareConvAlg = testConvAlgs[kk];
				
				ConvBasedResult ctr = getResult(rh, compareConvAlg);
				if (ctr.isTrusted()) {
					involvedButNotResponsible.add(ctr);
					
				} else {
					possiblyResponsible.add(ctr);
				}
				
				if (failedConv == compareConvAlg) {
					//reached where the converters broke
				 break;	
				}
			}
		} 
		
		float chanceEachResponsible = 1.0f / possiblyResponsible.size();
		
		Iterator iter = possiblyResponsible.iterator();
		while (iter.hasNext()) {
			ConvBasedResult ctr = (ConvBasedResult) iter.next();
			ctr.addPass(new ConvFilePassFailure(fprFailure, chanceEachResponsible));
		}
		
		Iterator iter2 = involvedButNotResponsible.iterator();
		while (iter.hasNext()) {
			ConvBasedResult ctr = (ConvBasedResult) iter2.next();
			//TODO: May want to give these a slight chance of being responsible.
			ctr.addPass(new ConvFilePassFailure(fprFailure, 0.0f));
		}
		
	}
	
	private void createPassResult(ComparePhaseFailure fprFailure,  Map rh) {
		AlgorithmFactory[] allConvs = fprFailure
		.getTestConverters();

		
		//all are possibly responsible.
		
		float chanceEachResponsible = 1.0f / allConvs.length;
		for (int kk = 0; kk < allConvs.length; kk++) {
			ConvBasedResult ctr = getResult(rh, allConvs[kk]);
			ctr.addPass(new ConvFilePassFailure(fprFailure, chanceEachResponsible));
		}
	}
	
	private ConvBasedResult getResult(Map rh, AlgorithmFactory conv) {
		ConvBasedResult newResult;
		
		Object currentTestResult = rh.get(conv);
		
		//check if we have recorded a converter result for this converter yet.
		if (currentTestResult == null) {
			//we have not yet created a converter result. Make a new one.
			newResult = new ConvBasedResult(conv);
			rh.put(conv, newResult);
		} else {
			//We have created a converter result. Return it.
			newResult = (ConvBasedResult) currentTestResult;
		}
		
		return newResult;
	}

	private boolean passed(FilePassResult fpr) {
		return fpr instanceof FilePassSuccess;
	}
	
	private boolean failedOnConverterPhase(FilePassResult fpr) {
		return fpr instanceof ConvertPhaseFailure;
	}
	
	private boolean failedOnGraphComparePhase(FilePassResult fpr) {
		return fpr instanceof ComparePhaseFailure;
	}
}

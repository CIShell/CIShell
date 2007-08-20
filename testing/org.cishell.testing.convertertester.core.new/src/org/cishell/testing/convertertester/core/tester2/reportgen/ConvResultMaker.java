package org.cishell.testing.convertertester.core.tester2.reportgen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cishell.testing.convertertester.core.converter.graph.ConverterPath;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllTestsResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.ConvResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.TestResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.converter.ConvFilePassFailure;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.converter.ConvFilePassSuccess;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass.ComparePhaseFailure;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass.ConvertPhaseFailure;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass.FilePassSuccess;
import org.osgi.framework.ServiceReference;

/**
 * 
 * @author mwlinnem
 *
 * 
 */
public class ConvResultMaker {
	
	/**
	 * Takes an array of ConverterTester results that are organized by test, 
	 * and returns an array of ConverterTester results organized by converter.
	 * This should make it easier for report generators to display
	 * data about how correct or incorrect each converter is, as oppose to 
	 * which tests failed or succeeded.
	 * @param trs The results of testing the converters, organized by test
	 * @return the results of testing the converters, organized by converter
	 */
	public ConvResult[] generate(AllTestsResult atr) {
		TestResult[] trs = atr.getTestResults();
		
		//maps convert testers to their test result data.
		Map resultHolder = new HashMap();
		 
		resultHolder = addTests(trs, resultHolder);
		resultHolder = markTrusted(trs, resultHolder);
		resultHolder = createConverterResults(trs, resultHolder);
		 
		ConvResult[] results = extractResults(resultHolder);
		return results;
	}
	
	/**
	 * 
	 * For each test result, go through each of the converters involved
	 * in the tests, letting each converter know which tests they are
	 * involved in.
	 * 
	 * @param trs the test results
	 * @param rh the result holder, without converters knowing which tests
	 * they are used in
	 * @return the result holder, with converters that know which tests
	 * they are used in
	 */
	private Map addTests(TestResult[] trs, Map rh) {
		for (int ii = 0; ii < trs.length; ii++) {
			TestResult tr = trs[ii];
			
			ConverterPath convPath = tr.getAllConverters();
			
			for (int jj = 0; jj < convPath.size(); jj++) {
				ConvResult ctr = getResult(rh, convPath.getRef(jj));
				ctr.addTest(tr);
			}
		}
		
		return rh;
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
				ConverterPath allConvs = tr.getTestConverters();
				for (int kk = 0; kk < allConvs.size(); kk++) {
					ConvResult ctr = getResult(rh, allConvs.getRef(kk));
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
	private ConvResult[] extractResults(Map rh) {
		Collection values = rh.values();
		ConvResult[] results = 
			(ConvResult[]) values.toArray(new ConvResult[0]);
		return results;
	}
	
	private void createPassResult(FilePassSuccess fprSuccess,  Map rh) {
		ConverterPath allConvs = 
			fprSuccess.getParent().getAllConverters();
		
		for (int kk = 0; kk < allConvs.size(); kk++) {
			ConvResult ctr = getResult(rh,allConvs.getRef(kk));
			ctr.addPass(fprSuccess);
		}
	}
	
	private void createPassResult(ConvertPhaseFailure fprFailure,  Map rh) {
		TestResult parent = fprFailure.getParent();
		
		ConverterPath testConvs = parent.getTestConverters();
		
		ConverterPath compareConvs = parent.getComparisonConverters();
		
		String failedConvName = fprFailure.getFailedConverter();
		
		List possiblyResponsible = new ArrayList();		
		List involvedButNotResponsible = new ArrayList();
		
		for (int kk = 0; kk < testConvs.size(); kk++) {
			ServiceReference testConvRef = testConvs.getRef(kk);
			
			
			ConvResult ctr = getResult(rh, testConvRef);
			
			involvedButNotResponsible.add(ctr);
			if (! ctr.isTrusted()) {
				possiblyResponsible.add(ctr);
			
			}
			
			String currentConvName = (String) testConvRef.getProperty("service.pid");
			
			if (fprFailure.getPhase().equals(ConvertPhaseFailure.TEST_PHASE) &&
					failedConvName.equals(currentConvName)) {
				//reached where the converters broke
			 break;	
			}
		}
		
		if (fprFailure.getPhase().equals(ConvertPhaseFailure.COMPARISON_PHASE)) {
			for (int kk = 0; kk < compareConvs.size(); kk++) {
				ServiceReference compareConvRef = testConvs.getRef(kk);
				
				ConvResult ctr = getResult(rh, compareConvRef);
				if (ctr.isTrusted()) {
					involvedButNotResponsible.add(ctr);
					
				} else {
					possiblyResponsible.add(ctr);
				}
				
				String currentConvName = (String) compareConvRef.getProperty("service.pid");
				if (failedConvName.equals(currentConvName)) {
					//reached where the converters broke
					System.out.println("Reached the end in compare phase");
				 break;	
				}
			}
		} 
		
		float chanceEachResponsible = 1.0f / possiblyResponsible.size();
		
		Iterator iter = possiblyResponsible.iterator();
		while (iter.hasNext()) {
			ConvResult ctr = (ConvResult) iter.next();
			ctr.addPass(fprFailure, chanceEachResponsible);
		}
		
		Iterator iter2 = involvedButNotResponsible.iterator();
		while (iter.hasNext()) {
			ConvResult ctr = (ConvResult) iter2.next();
			//TODO: May want to give these a slight chance of being responsible.
			ctr.addPass(fprFailure, 0.0f);
		}
		
	}
	
	private void createPassResult(ComparePhaseFailure fprFailure,  Map rh) {
		ConverterPath allConvs = fprFailure
		.getParent().getTestConverters();
		
		List trustedConvs    = new ArrayList();
		List nonTrustedConvs = new ArrayList();
		for (int ii = 0; ii < allConvs.size(); ii++) {
			ConvResult ctr = getResult(rh, allConvs.getRef(ii));
			
			if (! ctr.isTrusted()) {
				nonTrustedConvs.add(ctr);
			} else {
				trustedConvs.add(ctr);
			}
		}
		
		float chanceEachResponsible = 1.0f / nonTrustedConvs.size();
		for (int ii = 0; ii < nonTrustedConvs.size(); ii++) {
			ConvResult ctr = (ConvResult) nonTrustedConvs.get(ii);
				ctr.addPass(fprFailure, chanceEachResponsible);
		}
		
		for (int ii = 0; ii < trustedConvs.size(); ii++) {
			ConvResult ctr = (ConvResult) trustedConvs.get(ii);
				ctr.addPass(fprFailure, 0.0f);
		}
	}
	
	private ConvResult getResult(Map rh, ServiceReference conv) {
		ConvResult newResult;
		
		Object currentTestResult = rh.get(conv);
		
		//check if we have recorded a converter result for this converter yet.
		if (currentTestResult == null) {
			//we have not yet created a converter result. Make a new one.
			newResult = new ConvResult(conv);
			rh.put(conv, newResult);
		} else {
			//We have created a converter result. Return it.
			newResult = (ConvResult) currentTestResult;
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

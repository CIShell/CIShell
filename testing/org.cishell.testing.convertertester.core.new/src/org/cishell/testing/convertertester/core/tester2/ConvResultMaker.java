package org.cishell.testing.convertertester.core.tester2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cishell.testing.convertertester.core.converter.graph.Converter;
import org.cishell.testing.convertertester.core.converter.graph.ConverterPath;
import org.cishell.testing.convertertester.core.tester2.reportgen.faultanalysis.ChanceAtFault;
import org.cishell.testing.convertertester.core.tester2.reportgen.faultanalysis.ChanceAtFaultHeuristic;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllConvsResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.AllTestsResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.ConvResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.TestResult;

public class ConvResultMaker {
	
	//must succeed at least x times in the file pass
	private static int MINIMUM_SUCCESSES_TO_BE_TRUSTED = 3;
	//must be tested by at least x% of file passes in test 
	private static float MINIMUM_PERCENT_FILE_PASSES_REACHED_TO_BE_TRUSTED = .8f;
	
	public static AllConvsResult generate(AllTestsResult atr,
			Converter[] allConvs, ChanceAtFaultHeuristic faultHeuristic) {
		
		Converter[] trustedConvs  = determineWhichAreTrusted(atr); 
		
		Map convToTestsToPasses   = associateConvsWithTestsAndPasses(atr,
												allConvs, trustedConvs,
												faultHeuristic);
		ConvResult[] convResults  = generateConvResults(convToTestsToPasses,
												allConvs, trustedConvs);
		
//		addFaultInfo(convResults, trustedConvs, faultHeuristic);
		
		return new AllConvsResult(convResults);
		
	}
	
	/**
	 * Looks for tests where each file pass succeeded. We will assume that
	 * each converter involved in a test where all file passes succeeded is
	 * very likely to be correct. If one of these converters is involved
	 * in a file pass that fails at some later point, we will assume
	 * that that converter was not at fault, but was fed erroneous data
	 * by previous converters.
	 * @param atr the test results, which contain info on failure
	 * or success of each test.
	 * @return an array of the converters that are trusted
	 */
	private static Converter[] determineWhichAreTrusted(AllTestsResult atr) {
			TestResult[] trs =  atr.getTestResults();
			
			Set trustedConverters = new HashSet();
			for (int ii = 0; ii < trs.length; ii++) {
				TestResult tr = trs[ii];
				FilePassResult[] fprs = tr.getFilePassResults();
				
				
				//determine which converters always succeeded.
				
				int FAILED = -1;
				int NEVER_TESTED = 0; // array entries are 0 by default
				
				int[] trusted = new int[tr.getAllConverters().length];
				for (int jj = 0; jj < fprs.length; jj++) {
					FilePassResult fpr = fprs[jj];
					
					if (fpr.succeeded()) {
						for (int kk = 0; kk < trusted.length; kk++) {
							if (trusted[kk] != FAILED) {
								trusted[kk] += 1;
							}
						}
					} else if (fpr.failedWhileComparingGraphs()) {
						for (int kk = 0; kk < trusted.length; kk++) {
							trusted[kk] = FAILED;
						}
					} else if (fpr.failedWhileConverting()) {
						Converter[] convsInvolved = fpr.getConvertersInvolved();
						for (int kk = 0; kk < convsInvolved.length; kk++) {
							trusted[kk] = FAILED;
						}
					}
					
				}
				
				Converter[] allConvs = tr.getAllConverters();
				ConverterPath testConvs = tr.getTestConverters();
				if (fprs.length > 0) {
					//mark trusted converters.
						
					for (int kk = 0; kk < testConvs.size(); kk++) {
						Converter c = testConvs.get(kk);
						float percentFilePassesThisConvParticipatedIn = 
							trusted[kk] / (float) fprs.length;
						if (trusted[kk] >= MINIMUM_SUCCESSES_TO_BE_TRUSTED &&
								percentFilePassesThisConvParticipatedIn >=
						MINIMUM_PERCENT_FILE_PASSES_REACHED_TO_BE_TRUSTED) {
						trustedConverters.add(c);
						}
					}
				}
			}
			
			return (Converter[]) trustedConverters.toArray(new Converter[0]);
		}
	
	
	private static Map associateConvsWithTestsAndPasses(AllTestsResult atr,
			Converter[] allConvs, Converter[] trustedConvs, 
			ChanceAtFaultHeuristic faultHeuristic) {
		TestResult[] trs = atr.getTestResults();
		
		//Map<Converter, Map<TestResult, List<FilePassResult>>>
		Map convToTestsToPasses = new HashMap();
		initialize(convToTestsToPasses, allConvs);
		
		//for each test...
		for (int ii = 0; ii < trs.length; ii++) {
			TestResult tr = trs[ii];
			
			FilePassResult[] fps = tr.getFilePassResults();
			//for each file pass...
			for (int jj = 0; jj < fps.length; jj++) {
				FilePassResult fp = fps[jj];
				
				Converter[] convsInvolved = fp.getConvertersInvolved();
				
				/*
				 * associate the file pass with the chance that each converter
				 * involved is at fault.
				 */
				
				if (! fp.succeeded()) {
					ChanceAtFault[] chanceAtFaults  = faultHeuristic.determine(fp,
							convsInvolved, trustedConvs);
					for (int kk = 0; kk < chanceAtFaults.length; kk++) {
						ChanceAtFault caf = chanceAtFaults[kk];
						fp.addChanceAtFault(caf);
					}
				}
				
				//for each converter involved...
				for (int kk = 0; kk < convsInvolved.length; kk++) {
					Converter conv = convsInvolved[kk];
					
					/*
					 * associate the converter with the tests and 
					 * file passes that involve it.
					 * 
					 * (side-effects convToTestsToPasses)
					 */
					addPassResult(convToTestsToPasses, conv,
							fp);
					
				}
			}
		}
		
		return convToTestsToPasses;
	}
	
	private static ConvResult[] generateConvResults(Map convToTestsToPasses,
			Converter[] allConvs, Converter[] trustedConvs) {
		
		ConvResult[] convResults = makeAndFillWithAssociatedTestsAndPasses(
				convToTestsToPasses, allConvs, trustedConvs);
		
//		addFaultInfo(convResults, convToTestsToPasses, allConvs, trustedConvs,
//				faultHeuristic);
		
		return convResults;
	}
		
		private static ConvResult[] makeAndFillWithAssociatedTestsAndPasses(
			Map convToTestsToPasses, Converter[] allConvs,
			Converter[] trustedConvs) {
		List convResults = new ArrayList();

		List trustedConvList = Arrays.asList(trustedConvs);
		// for every converter we know of...
		for (int ii = 0; ii < allConvs.length; ii++) {
			Converter conv = allConvs[ii];
			// get the associated test to passes map
			Map testToPasses = (Map) convToTestsToPasses.get(conv);

			/*
			 * create converter result objects which contain information about
			 * which tests and file passes it is involved with, and whether it
			 * is trusted.
			 */

			boolean isTrusted = trustedConvList.contains(conv);

			ConvResult convResult = new ConvResult(conv, isTrusted,
					testToPasses);

			convResults.add(convResult);
		}
		// return the converter result objects
		return (ConvResult[]) convResults.toArray(new ConvResult[0]);
	}
	
	private static void addPassResult(Map convToTestsToPasses, Converter conv,
			FilePassResult pass) {
		
		TestResult test = pass.getParent();
		
		Map testToPasses = (Map) convToTestsToPasses.get(conv);
		List passes;
		
		//if we already associated this test with the converter...
		if (testToPasses != null) {
			passes = (List) testToPasses.get(test);
			if (passes == null) {
				passes = new ArrayList();
			}
		} else { //otherwise...
			testToPasses = new HashMap();
			passes = new ArrayList();
		}
		
		passes.add(pass);
		testToPasses.put(test, passes);
		convToTestsToPasses.put(conv, testToPasses);
	}
	
	/**
	 * Ensures that each converter is represented in the map, whether or not
	 * there are any test associated with it.
	 * @param convToTestsToPasses The map we side-effect
	 * @param allConvs an array of all the converters we know of
	 */
	private static void initialize(Map convToTestsToPasses, Converter[] allConvs) {
		for (int ii = 0; ii < allConvs.length; ii++) {
			Converter conv = allConvs[ii];
			
			convToTestsToPasses.put(conv, new HashMap());
		}
	}
	
//	private static addFaultInfo(AllTestsResult atr, ConvResult[] crs,
//			Converter[] trustedConvs, ChanceAtFaultHeuristic faultHeuristic) {
//		
//		Map convToChanceAtFaults = new HashMap();
//		
//		FilePassResult[] failFPRs = atr.getFailedFilePasses();
//		for (int ii = 0; ii < failFPRs.length; ii++) {
//			FilePassResult failFPR = failFPRs[ii];
//		
//			ChanceAtFault[] chanceAtFaultsForEachConv = getFaultInfo(failFPR,
//					trustedConvs, trustedConvs);
//			
//			for (int jj = 0; jj < chanceAtFaultsForEachConv.length; jj++) {
//				ChanceAtFault chanceAtFaultForAConv = 
//					chanceAtFaultsForEachConv[jj];
//				
//				Converter conv = chanceAtFaultForAConv.getConverter();
//				
//				addEntryToList(convToChanceAtFaults, conv,
//						chanceAtFaultForAConv);
//				
//			}
//		}
//		
//		for (int ii = 0; ii < crs.length; ii++) {
//			ConvResult cr = crs[ii];
//			
//			Converter c = cr.getConverter();
//			List chanceAtFaults = convToChanceAtFaults.get(c);
//			
//			if (chanceAtFaults != null) {
//				
//				Iterator faultIter = chanceAtFaults.iterator();
//				while (faultIter.hasNext()) {
//					ChanceAtFault caf = (ChanceAtFault) faultIter.next();
//					
//					cr.
//				}
//			}
//		}
//		
//		
////		List convResults = new ArrayList();
////		
////		List trustedConvList = Arrays.asList(trustedConvs);
////		//for every converter we know of...
////		for (int ii = 0; ii < allConvs.length; ii++) {
////			Converter conv = allConvs[ii];
////			//get the associated test to passes map
////			Map testToPasses = (Map) convToTestsToPasses.get(conv);
////			
////			/*
////			 * create converter result objects which contain 
////			 * information about which tests and file passes
////			 * it is involved with, and whether it is trusted.
////			 */
////			
////			boolean isTrusted = trustedConvList.contains(conv);
////			
////			ConvResult convResult = new ConvResult(conv, isTrusted,
////					testToPasses);
////			
////			convResults.add(convResult);
////			
////			Set tests = testToPasses.keySet();
////			Iterator testIter = tests.iterator();
////			//for each test...
////			while (testIter.hasNext()) {
////				TestResult tr = (TestResult) testIter.next();
////				List passes = (List) testToPasses.get(tr);
////				Iterator passIter = passes.iterator();
////				//for each involved pass...
////				while (passIter.hasNext()) {
////					FilePassResult fpr = (FilePassResult) passIter.next();
////					
////					if (! fpr.succeeded()) {
////						addFailInfo(fpr, trustedConvs, faultHeuristic);
////					}
////				}
////			}
////		}
//	}
	
//	private static ChanceAtFault[] getFaultInfo(FilePassResult failFP, Converter[] trustedConverters,
//			ChanceAtFaultHeuristic faultHeuristic) {
//		Converter[] convsInvolvedInFailure = failFP.getConvertersInvolved();
//		ChanceAtFault[] chanceAtFaults = faultHeuristic.determine(failFP,
//				convsInvolvedInFailure, trustedConverters);
//		return chanceAtFaults;
//	}
	
	/**
	 * For maps that associate an object to a list of objects,
	 * add the given value to the list associated with the key.
	 * 
	 * Side-effects the map
	 * 
	 * @param map
	 * @param key
	 * @param value
	 */
	private static void addEntryToList(Map map, Object key, Object value) {
		List oldEntries = (List) map.get(key);
		
		if (oldEntries == null) {
			//no entries so far
			oldEntries = new ArrayList();
		}
		
		oldEntries.add(value);
		
		map.put(key, value);
	}
}

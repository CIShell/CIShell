package org.cishell.testing.convertertester.core.tester2.reportgen.results;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.testing.convertertester.core.converter.graph.Converter;
import org.cishell.testing.convertertester.core.tester2.reportgen.faultanalysis.ChanceAtFault;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass.PassPhase;

public abstract class FilePassResult {
	
	
	public static final Comparator COMPARE_BY_SUCCESS = new CompareBySuccess();
	
	public static final String DEFAULT_NAME = "Default File Pass Name";
	
	private Data[] originalData;
	private String explanation;
	private PassPhase lastReachedPhase;
	private Converter failedConverter;
	
	private Data[][] testData;
	private Data[][] origCompareData;
	private Data[][] resultCompareData;
		
	//variables not set by constructor
	
	private TestResult parent;
	private int index = -1;
	
	
	//variables that are cached 
	
	private Converter[] convertersInvolvedWith;
	
	/*
	 * will be empty for non-failed file passes, and file passes
	 * that have not yet gone through the FaultChanceInfoMaker
	 */
	private List chanceAtFaults;
	
	public FilePassResult(Data[] originalData, String explanation,
			PassPhase lastReachedPhase, Converter failedConverter, Data[][] testData, Data[][] origCompareData, Data[][] resultCompareData) {
		this.originalData = originalData;
		this.explanation = explanation;
		this.lastReachedPhase = lastReachedPhase;
		this.failedConverter = failedConverter;
		
		this.testData = testData;
		this.origCompareData = origCompareData;
		this.resultCompareData = resultCompareData;
		
		chanceAtFaults = new ArrayList();
	}
	
	public boolean succeeded() {
		boolean result = lastReachedPhase == PassPhase.SUCCEEDED_PHASE;
		return result;
	}
	
	public boolean failedWhileConverting() {
		
		boolean result = 
		(this.getLastReachedPhase().equals(PassPhase.TEST_CONV_PHASE) ||
		this.getLastReachedPhase().equals(PassPhase.COMPARE_CONV_ORIG_PHASE) ||
		this.getLastReachedPhase().equals(PassPhase.COMPARE_CONV_RESULT_PHASE));
		
		return result;
	}
	
	public boolean failedWhileComparingGraphs() {
		
		boolean result = 
			(this.getLastReachedPhase().equals(PassPhase.GRAPH_COMPARE_PHASE));
		
		return result;
	}

	public PassPhase getLastReachedPhase() {
		return this.lastReachedPhase;
	}
	
	public String getExplanation() {
		return this.explanation;
	}
	
	/**
	 * Will return null if either this file pass did not fail,
	 * or it did not fail due to a converter error (failed during
	 * graph comparison)
	 * @return the failed converter, or null
	 */
	public Converter getFailedConverter() {
		return this.failedConverter;
	}
	
	public Data[] getOriginalData() {
		return this.originalData;
	}
	
	public Data[][] getTestData() {
		return this.testData;
	}
	
	public Data[][] getOrigCompareData() {
		return this.origCompareData;
	}
	
	public Data[][] getResultCompareData() {
		return this.resultCompareData;
	}
	
	public String getOriginalFileLabel() {
		return (String) getOriginalData()[0].getMetadata().get(DataProperty.LABEL);
	}
	
	public String getOriginalFileShortLabel() {
		String label = getOriginalFileLabel();
		
		int lastSeparatorIndex = label.lastIndexOf(File.separator);
		
		String shortLabel = label.substring(lastSeparatorIndex + 1);
		return shortLabel;
	}
	
	public String getFileFormat() {
		return originalData[0].getFormat();
	}
	
	//methods dealing with cached variables
	
	public Converter[] getConvertersInvolved() {
		if (this.convertersInvolvedWith != null) {
			return this.convertersInvolvedWith;
		} else {
			this.convertersInvolvedWith =
				this.getParent().getConvertersThrough(
						this.getFailedConverter(),
						this.getLastReachedPhase());
			return this.convertersInvolvedWith;
		}
	}
	
	//methods dealing with side-effected variables
	
	public String getName() {
		if (index != -1) {
			return "Pass " + this.index;
		} else {
			return "Unindexed Pass (Error)";
		}
	}
	
	public String getNameWithPhaseExpln() {
		return getName() + " - " + getPhaseExplanation();
	}
	
	public String getPhaseExplanation() {
		if (lastReachedPhase.equals(
				PassPhase.TEST_CONV_PHASE)) {
			return "Failed in test converters";
		} else if (lastReachedPhase.equals(
				PassPhase.COMPARE_CONV_ORIG_PHASE)) {
			return "Failed in comparison converters " + 
					"(using original file)";
		} else if (lastReachedPhase.equals(
				PassPhase.COMPARE_CONV_RESULT_PHASE)) {
			return "Failed in comparison converters " + 
					"(using result file)";
		} else if (lastReachedPhase.equals(
				PassPhase.GRAPH_COMPARE_PHASE)) {
			return "Failed while comparing original" +
					"and result graphs";
		} else if (lastReachedPhase.equals(
				PassPhase.SUCCEEDED_PHASE)) {
			return "Succeeded";
		} else {
			return "(Error) Illegal Unknown Phase";
		}
	}
	
//	public void setName(String name) {
//		this.name = name;
//	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public void setParentTest(TestResult parent) {
		if (this.parent == null) {
			this.parent = parent;
		} else {
			throw new IllegalArgumentException("setParent should only be " +
			"called once");
		}
	}
	
	public TestResult getParent() {
		return this.parent;
	}
	
	/**
	 * Will have zero entries if this pass succeeded, or if
	 * this pass has not yet been through the FaultChanceInfoMaker
	 * @return
	 */
	public ChanceAtFault[] getChanceAtFaults() {
		return (ChanceAtFault[]) 
			this.chanceAtFaults.toArray(new ChanceAtFault[0]);
	}
	
	/**
	 * Get the chance at fault for a converter involved in this file pass
	 * @param c The involved converter
	 * @return The chance at fault corresponding to the provided converter,
	 * or null if that converter was not involved in this file pass.
	 */
	public ChanceAtFault getChanceAtFaultFor(Converter c) {
		Iterator iter = this.chanceAtFaults.iterator();
		while (iter.hasNext()) {
			ChanceAtFault chanceAtFault = (ChanceAtFault) iter.next();
			
			if (chanceAtFault.getConverter() == c) {
				return chanceAtFault;
			}
		}
		
		return null;
	}
	
	public void addChanceAtFault(ChanceAtFault chanceAtFault) {
		this.chanceAtFaults.add(chanceAtFault);
	}
	
	
private static class CompareBySuccess implements Comparator {
		
		/**
		 * Compare first by success, where
		 * completely successful > partially successful > complet failure,
		 * and then alphabetize (the natural order of test results) 
		 * for cases where they both have the same success type.
		 */
		public int compare(Object o1, Object o2) {
			if (o1 instanceof TestResult && o2 instanceof TestResult) {
				TestResult tr1 = (TestResult) o1;
				TestResult tr2 = (TestResult) o2;
				
				int success1 = getSuccessRating(tr1);
				int success2 = getSuccessRating(tr2);
				
				if (success1 != success2) {
					return success2 - success1;
				} else {
					return tr1.compareTo(tr2);
				}
				
			} else {
				throw new IllegalArgumentException("Can only " +
						"compare test results");
			}
			
		}
		
		private int getSuccessRating(TestResult tr) {
			if (tr.allSucceeded()) {
				return 2;
			} else if (tr.someSucceeded()) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}

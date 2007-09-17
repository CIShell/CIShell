package org.cishell.testing.convertertester.core.tester2.reportgen.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cishell.testing.convertertester.core.converter.graph.Converter;
import org.cishell.testing.convertertester.core.tester2.reportgen.faultanalysis.ChanceAtFault;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass.FilePassFailure;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass.FilePassSuccess;

public class ConvResult {
	
	//original instance variables
	
	private Converter conv;
	private boolean isTrusted;
	private Map involvedTestsAndPasses;
	
	//derived instance variables
	
	private List involvedTests;
	private List involvedPasses;
	private List succeededInvolvedPasses;
	private List failedInvolvedPasses;
	private float percentPassed;
	private List chanceAtFaults;
	private List uniqueExplnChanceAtFaults;
	private float chanceCorrect;
	
	public ConvResult(Converter conv, boolean isTrusted,
			Map involvedTestsAndPasses) {
		this.conv = conv;
		this.isTrusted = isTrusted;
		this.involvedTestsAndPasses = involvedTestsAndPasses;
		
		
		initializeDerivedVariables();
	}
	
	private void initializeDerivedVariables() {
		initializeInvolvedTests();
		initializeInvolvedPasses();
		initializeSucceededAndFailedInvolvedPasses();
		initializePercentPassed();
		initializeChanceAtFaults();
		initializeChanceCorrect();
	}
	
	private void initializeInvolvedTests() {
		Set involvedTestSet = involvedTestsAndPasses.keySet();
		this.involvedTests = new ArrayList(involvedTestSet);
	}
	
	private void initializeInvolvedPasses() {
		this.involvedPasses = new ArrayList();
		
		Iterator iter = involvedTests.iterator();
		while (iter.hasNext()) {
			TestResult tr = (TestResult) iter.next();
			
			List passes = (List) involvedTestsAndPasses.get(tr);
			
			involvedPasses.addAll(passes);
		}
	}
	
	private void initializeSucceededAndFailedInvolvedPasses() {
		this.succeededInvolvedPasses = new ArrayList();
		this.failedInvolvedPasses = new ArrayList();
		
		Iterator iter = this.involvedPasses.iterator();
		while (iter.hasNext()) {
			FilePassResult fpr = (FilePassResult) iter.next();
			
			if (fpr.succeeded()) {
				this.succeededInvolvedPasses.add(fpr);
			} else {
				this.failedInvolvedPasses.add(fpr);
			}
		}
	}
	
	private void initializePercentPassed() {
		int succeededPasses = this.succeededInvolvedPasses.size();
		int totalPasses = this.involvedPasses.size();
		
		if (totalPasses > 0) {
			this.percentPassed = ((float) succeededPasses) / ((float) totalPasses);
		} else {
			//solves division by zero issue.
			this.percentPassed = 0;
		}
	}
	
	private void initializeChanceAtFaults() {
		this.chanceAtFaults = new ArrayList();
		this.uniqueExplnChanceAtFaults = new ArrayList();
		
		Iterator iter = this.failedInvolvedPasses.iterator();
		while (iter.hasNext()) {
			FilePassFailure failFP = (FilePassFailure) iter.next();
			
			ChanceAtFault chanceAtFault = 
				failFP.getChanceAtFaultFor(this.conv);
			
			//add to list for all involved chance at faults
			this.chanceAtFaults.add(chanceAtFault);
			
			/*
			 * potentially add to list for chance at faults with unique
			 *  explanations.
			 */
			
			String explanation = chanceAtFault.getExplanation();
			
			boolean unique = 
				! containsChanceAtFaultWithExpln(
						this.uniqueExplnChanceAtFaults, explanation);
			
			if (unique) {
				this.uniqueExplnChanceAtFaults.add(chanceAtFault);
			}
		}
	}
	
	private void initializeChanceCorrect() {
		float chanceCorrectSoFar = 1.0f;
		
		Iterator iter = this.uniqueExplnChanceAtFaults.iterator();
		while (iter.hasNext()) {
			ChanceAtFault uniqueExplnCAF = (ChanceAtFault) iter.next();
			
			float chanceCorrectForThisError = 
				uniqueExplnCAF.getChanceNotAtFault();
			
			chanceCorrectSoFar *= chanceCorrectForThisError;
			
		}
		
		float finalChanceCorrect = chanceCorrectSoFar;
		
		this.chanceCorrect = finalChanceCorrect;
	}
	
	
	
	public Converter getConverter() {
		return this.conv;
	}
	
	public boolean isTrusted() {
		return this.isTrusted;
	}

	public int getNumFilePasses() {
		return involvedPasses.size();
	}
	
	public String getUniqueName() {
		return this.conv.getUniqueName();
	}
	
	public String getShortName() {
		return this.conv.getShortName();
	}
	
	public String getShortNameWithStatus() {
		String nameNoPackageWithTrust =  " - " + getShortName() ;
		if (! wasTested()) {
			nameNoPackageWithTrust = "Not Tested" + nameNoPackageWithTrust;
		} else if (isTrusted()) {
			nameNoPackageWithTrust = "Trusted" + nameNoPackageWithTrust;
		} else {
			nameNoPackageWithTrust = "Not Trusted" + nameNoPackageWithTrust;
		}
		
		return nameNoPackageWithTrust;
	}
	
	public boolean wasTested() {
		return this.involvedPasses.size() > 0;
	}
	
	public FilePassResult[] getFilePasses() {

		return (FilePassResult[]) 
			this.involvedPasses.toArray(new FilePassResult[0]);
	}
	
	public FilePassSuccess[] getSuccessFilePasses() {
		return (FilePassSuccess[]) 
			this.succeededInvolvedPasses.toArray(new FilePassSuccess[0]);
	}
	
	public FilePassFailure[] getFailFilePasses() {
		return (FilePassFailure[]) 
			this.failedInvolvedPasses.toArray(new FilePassFailure[0]);
	}

	
	public TestResult[] getTests() {
		Collections.sort(this.involvedTests);
		return (TestResult[]) this.involvedTests.toArray(new TestResult[0]);
	}
	
	public TestResult[] getTestsBySuccess() {
		Collections.sort(this.involvedTests, TestResult.getCompareBySuccess());
		return (TestResult[]) this.involvedTests.toArray(new TestResult[0]);
	}
	
	public ChanceAtFault[] getAllChanceAtFaults() {
		return (ChanceAtFault[])
			this.chanceAtFaults.toArray(new ChanceAtFault[0]);
	}
	
	public ChanceAtFault[] getUniqueExplnChanceAtFaults() {
		return (ChanceAtFault[])
			this.uniqueExplnChanceAtFaults.toArray(new ChanceAtFault[0]);
	}
	
	public float getPercentPassed() {
		return this.percentPassed;
	}
	
	public float getChanceCorrect() {
		return this.chanceCorrect;
	}
	
	public float getChanceOfFlaw() {
		return 1.0f - this.chanceCorrect; 
	}
	
	public Comparator getCompareFaultsByLikelihood() {
		return ChanceAtFault.COMPARE_BY_LIKELIHOOD;
	}
	
	private boolean containsChanceAtFaultWithExpln(List chanceAtFaultList,
			String explanation) {
		Iterator iter = chanceAtFaultList.iterator();
		while (iter.hasNext()) {
			ChanceAtFault caf = (ChanceAtFault) iter.next();
			
			if (explanation.equals(caf.getExplanation())) {
				return true;
			}
		}
		
		return false;
	}
}

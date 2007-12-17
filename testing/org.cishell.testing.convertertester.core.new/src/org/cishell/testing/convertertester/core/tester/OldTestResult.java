package org.cishell.testing.convertertester.core.tester;

public abstract class OldTestResult {
	public abstract String getType();
	public abstract String[] getTestConverterNames();
	public abstract String[] getComparisonConverterNames();
	
	public boolean hasExplanation() {return false;}
	public boolean hasFailedConverterName() {return false;}
	public boolean hasOriginalGraphName() {return false;}
	public boolean hasResultingGraphNames() {return false;}
	
	public String getExplanation() {return "";}
	public String getFailedConverterName() {return "";}
	public String getOriginalGraphName() {return "";}
	public String[] getResultGraphNames() {return new String[0];}
}

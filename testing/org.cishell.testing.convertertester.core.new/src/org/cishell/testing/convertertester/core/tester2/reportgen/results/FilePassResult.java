package org.cishell.testing.convertertester.core.tester2.reportgen.results;

import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;

public abstract class FilePassResult {
	
	public static final String SUCCESS = "success";
	public static final String CONVERT_FAILURE = "convert failure";
	public static final String COMPARE_FAILURE = "compare failure";
	
	public static final String DEFAULT_NAME = "Default File Pass Name";
	
	private Data[] originalData;
	private String name = DEFAULT_NAME;
	
	private TestResult parent;
	
	public FilePassResult(Data[] originalData) {
		this.originalData = originalData;
	}
	
	public abstract boolean succeeded();
	public abstract String getType();
	public abstract String getExplanation();
	public abstract String getFullExplanation();
	
	public String getOriginalFileLabel() {
		return (String) getOriginalData()[0].getMetaData().get(DataProperty.LABEL);
	}
	
	public String getFormat() {
		return originalData[0].getFormat();
	}
	
	public Data[] getOriginalData() {
		return this.originalData;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getShortSummary() {
		return getType();
	}
	
	public void setParentTest(TestResult parent) {
		this.parent = parent;
	}
	
	public TestResult getParent() {
		return this.parent;
	}
}

package org.cishell.testing.convertertester.core.tester2;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.data.Data;
import org.cishell.testing.convertertester.core.converter.graph.ConverterPath;
import org.cishell.testing.convertertester.core.tester2.graphcomparison.NewGraphComparer;

public class TestConfigData {

	private NewGraphComparer comparer;
	private ConverterPath testConverters;
	private ConverterPath comparisonConverters;
	private CIShellContext context;
	private Data[][] testFileData;
	
	public TestConfigData(NewGraphComparer comparer,
			ConverterPath testConverters,
			ConverterPath comparisonConverters,
			CIShellContext context,
			Data[][] testFileData) {
		this.comparer = comparer;
		this.testConverters = testConverters;
		this.comparisonConverters = comparisonConverters;
		this.context = context;
		this.testFileData = testFileData;
	}

	public NewGraphComparer getComparer() {
		return this.comparer;
	}

	public ConverterPath getComparisonConverters() {
		return this.comparisonConverters;
	}

	public CIShellContext getContext() {
		return this.context;
	}

	public ConverterPath getTestConverters() {
		return this.testConverters;
	}

	public Data[][] getTestFileData() {
		return this.testFileData;
	}

}

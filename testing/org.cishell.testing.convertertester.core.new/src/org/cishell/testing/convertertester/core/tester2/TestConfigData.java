package org.cishell.testing.convertertester.core.tester2;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.cishell.testing.convertertester.core.tester2.graphcomparison.NewGraphComparer;

public class TestConfigData {

	private NewGraphComparer comparer;
	private AlgorithmFactory[] testConverters;
	private AlgorithmFactory[] comparisonConverters;
	private CIShellContext context;
	private Data[][] testFileData;
	
	public TestConfigData(NewGraphComparer comparer,
			AlgorithmFactory[] testConverters,
			AlgorithmFactory[] comparisonConverters,
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

	public AlgorithmFactory[] getComparisonConverters() {
		return this.comparisonConverters;
	}

	public CIShellContext getContext() {
		return this.context;
	}

	public AlgorithmFactory[] getTestConverters() {
		return this.testConverters;
	}

	public Data[][] getTestFileData() {
		return this.testFileData;
	}

}

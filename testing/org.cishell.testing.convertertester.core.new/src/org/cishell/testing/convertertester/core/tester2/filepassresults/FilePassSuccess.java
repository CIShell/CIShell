package org.cishell.testing.convertertester.core.tester2.filepassresults;

import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;

public class FilePassSuccess extends FilePassResult {

	public FilePassSuccess(
			Data[] originalData,
			AlgorithmFactory[] testConverters,
			AlgorithmFactory[] comparisonConverters) {
		super(originalData, testConverters, comparisonConverters);
	}

	public String getType() {
		return FilePassResult.SUCCESS;
	}
}

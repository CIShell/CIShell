package org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass;

import org.cishell.framework.data.Data;
import org.cishell.testing.convertertester.core.converter.graph.Converter;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;

public class FilePassFailure extends FilePassResult {

	public FilePassFailure(Data[] originalData, String explanation,
			PassPhase phaseReached, Converter failedConverter, Data[][] testData, Data[][] origCompareData, Data[][] resultCompareData) {
		super(originalData, explanation, phaseReached,
				failedConverter, testData, origCompareData, resultCompareData);
	}
}

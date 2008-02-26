package org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass;

import org.cishell.framework.data.Data;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;

public class FilePassSuccess extends FilePassResult {

	public FilePassSuccess(Data[] originalData, String explanation, Data[][] allData) {
		super(originalData, explanation, PassPhase.SUCCEEDED_PHASE, null, allData);
	}
}

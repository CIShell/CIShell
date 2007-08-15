package org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass;

import org.cishell.framework.data.Data;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;

public abstract class FilePassFailure extends FilePassResult {
	
	public FilePassFailure(Data[] originalData) {
		super(originalData);
	}
}

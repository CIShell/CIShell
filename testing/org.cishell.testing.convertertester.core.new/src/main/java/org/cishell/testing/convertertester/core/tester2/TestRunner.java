package org.cishell.testing.convertertester.core.tester2;

import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;

public interface TestRunner {
	public FilePassResult[] runTest(TestConfigData testData);
}

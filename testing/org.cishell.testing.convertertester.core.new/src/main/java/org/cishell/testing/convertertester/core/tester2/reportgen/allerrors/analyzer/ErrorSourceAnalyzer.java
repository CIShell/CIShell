package org.cishell.testing.convertertester.core.tester2.reportgen.allerrors.analyzer;

import java.util.Map;

public interface ErrorSourceAnalyzer {
	public ErrorSource[] analyze(Map testToPassesToCafs);
}

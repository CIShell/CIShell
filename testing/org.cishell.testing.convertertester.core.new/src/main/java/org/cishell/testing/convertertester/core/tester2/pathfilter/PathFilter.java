package org.cishell.testing.convertertester.core.tester2.pathfilter;

import org.cishell.testing.convertertester.core.converter.graph.ConverterPath;

public interface PathFilter {
	public ConverterPath[] filter (ConverterPath[] testPaths);
}

package org.cishell.testing.convertertester.algorithm.pathfilter;

import java.util.ArrayList;
import java.util.List;

import org.cishell.testing.convertertester.core.converter.graph.ConverterPath;
import org.cishell.testing.convertertester.core.tester2.pathfilter.PathFilter;

public class ConvAndHopFilter implements PathFilter {

	private String mustHaveConvName;
	private int maxNumHops;
	
	public ConvAndHopFilter(String mustHaveConvName, int maxNumHops) {
		this.mustHaveConvName = mustHaveConvName;
		this.maxNumHops = maxNumHops;
	}
	
	public ConverterPath[] filter(ConverterPath[] testPaths) {
		if (testPaths != null) {
			List filteredTestPaths = new ArrayList();
			for (int ii = 0; ii < testPaths.length; ii++) {
				ConverterPath testPath = testPaths[ii];
				
				if (testPath.containsConverterNamed(this.mustHaveConvName) &&
						testPath.size() <= this.maxNumHops) {
					filteredTestPaths.add(testPath);
				}
			}
		
		return (ConverterPath[]) 
			filteredTestPaths.toArray(new ConverterPath[0]);
		
		} else {
			return new ConverterPath[0];
		}
	}

}

package org.cishell.testing.convertertester.algorithm;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.data.Data;
import org.cishell.testing.convertertester.core.tester.ConverterTester;
import org.osgi.framework.BundleContext;

public class ConverterTesterAlgorithm implements Algorithm {
    Data[] data;
    Dictionary parameters;
    CIShellContext context;
    ConverterTester tester;
    
    public ConverterTesterAlgorithm(Data[] data, Dictionary parameters,
    		CIShellContext csContext, BundleContext bContext ) {
        this.data = data;
        this.parameters = parameters;
        this.context = csContext;
        
        this.tester = new ConverterTester(bContext, csContext);
    }

    public Data[] execute() {
    	return null;
    }
}
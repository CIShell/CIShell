package org.cishell.tests.alg1;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;

public class Alg1Factory implements AlgorithmFactory {
    public Algorithm createAlgorithm(Data[] dm, Dictionary parameters, CIShellContext context) {
        return new Alg(context, parameters);
    }
}

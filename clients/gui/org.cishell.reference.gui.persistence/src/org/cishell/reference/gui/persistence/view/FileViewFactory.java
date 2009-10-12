package org.cishell.reference.gui.persistence.view;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;


public class FileViewFactory implements AlgorithmFactory {
	public Algorithm createAlgorithm(Data[] data,
									 Dictionary parameters,
									 CIShellContext ciShellContext) {
        return new FileView(data, parameters, ciShellContext);
    }
}
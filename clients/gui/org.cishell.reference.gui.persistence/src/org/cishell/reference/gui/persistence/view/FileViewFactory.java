package org.cishell.reference.gui.persistence.view;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.metatype.MetaTypeProvider;
//import org.osgi.service.metatype.MetaTypeService;


public class FileViewFactory implements AlgorithmFactory {

	public Algorithm createAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
        return new FileView(data, parameters, context);
    }
}
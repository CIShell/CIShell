/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 22, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.gui.menumanager.menu;

import java.util.Dictionary;

import org.cishell.app.service.datamanager.DataManagerService;
import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.Converter;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;


public class AlgorithmWrapper implements Algorithm, AlgorithmProperty {
    protected ServiceReference ref;
    protected BundleContext bContext;
    protected CIShellContext ciContext;
    protected Data[] data;
    protected Converter[][] converters;
    protected Dictionary parameters;
    
    public AlgorithmWrapper(ServiceReference ref, BundleContext bContext, 
            CIShellContext ciContext, Data[] data, Converter[][] converters,
            Dictionary parameters) {
        this.ref = ref;
        this.bContext = bContext;
        this.ciContext = ciContext;
        this.data = data;
        this.converters = converters;
        this.parameters = parameters;
    }

    /**
     * @see org.cishell.framework.algorithm.Algorithm#execute()
     */
    public Data[] execute() {
        try {
            for (int i=0; i < data.length; i++) {
                if (converters[i] != null) {
                    data[i] = converters[i][0].convert(data[i]);
                }
            }
            
            AlgorithmFactory factory = (AlgorithmFactory) bContext.getService(ref);
            Algorithm alg = factory.createAlgorithm(data, parameters, ciContext);
            Data[] outData = alg.execute();
            
            if (outData != null) {
                DataManagerService dataManager = (DataManagerService) 
                    bContext.getService(bContext.getServiceReference(
                            DataManagerService.class.getName()));
                
                for (int i=0; i < outData.length; i++) {
                    dataManager.setSelectedData(outData);
                }
            }
            
            return outData;
        } catch (Throwable e) {
            GUIBuilderService guiBuilder = (GUIBuilderService) 
                ciContext.getService(GUIBuilderService.class.getName());
            guiBuilder.showError("Error!", 
                    "The Algorithm: \""+ref.getProperty(AlgorithmProperty.LABEL)+
                    "\" had an error while executing.", e);
            
            return new Data[0];
        }
    }
}

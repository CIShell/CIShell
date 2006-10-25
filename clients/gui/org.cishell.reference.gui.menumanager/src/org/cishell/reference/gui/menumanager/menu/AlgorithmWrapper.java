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

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import org.cishell.app.service.datamanager.DataManagerService;
import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.service.conversion.Converter;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;


public class AlgorithmWrapper implements Algorithm, AlgorithmProperty {
    protected ServiceReference ref;
    protected BundleContext bContext;
    protected CIShellContext ciContext;
    protected Data[] originalData;
    protected Data[] data;
    protected Converter[][] converters;
    protected Dictionary parameters;
    
    public AlgorithmWrapper(ServiceReference ref, BundleContext bContext,
            CIShellContext ciContext, Data[] originalData, Data[] data,
            Converter[][] converters, Dictionary parameters) {
        this.ref = ref;
        this.bContext = bContext;
        this.ciContext = ciContext;
        this.originalData = originalData;
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
                    converters[i] = null;
                }
            }
            
            AlgorithmFactory factory = (AlgorithmFactory) bContext.getService(ref);
            Algorithm alg = factory.createAlgorithm(data, parameters, ciContext);
            
            LogService logger = getLogService();
            if (logger != null) {
        		logger.log(LogService.LOG_INFO, "");
        		logger.log(LogService.LOG_INFO, "Input Parameters Used:");
            	for (Enumeration e = this.parameters.keys();
            		 e.hasMoreElements();) {
            		String key = (String)e.nextElement();
            		logger.log(LogService.LOG_INFO, key + ": " + this.parameters.get(key));
            	}
            }
            
            Data[] outData = alg.execute();
            
            if (outData != null) {
                DataManagerService dataManager = (DataManagerService) 
                    bContext.getService(bContext.getServiceReference(
                            DataManagerService.class.getName()));
                
                doParentage(outData);
                
                List goodData = new ArrayList();
                for (int i=0; i < outData.length; i++) {
                    if (outData[i] != null) {
                        goodData.add(outData[i]);
                    }
                }
                
                outData = (Data[]) goodData.toArray(new Data[0]);
                
                if (outData.length != 0) {
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
    
    //only does anything if parentage=default so far...
    protected void doParentage(Data[] outData) {
        //make sure the parent set is the original Data and not the
        //converted data...
        if (outData != null && data != null && originalData != null 
                && originalData.length == data.length) {
            for (int i=0; i < outData.length; i++) {
                if (outData[i] != null) {
                    Object parent = outData[i].getMetaData().get(DataProperty.PARENT);
                    
                    if (parent != null) {
                        for (int j=0; j < data.length; i++) {
                            if (parent == data[j]) {
                                outData[i].getMetaData().put(DataProperty.PARENT, 
                                        originalData[j]);
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        //check and act on parentage settings
        String parentage = (String)ref.getProperty("parentage");
        if (parentage != null) {
            parentage = parentage.trim();
            if (parentage.equalsIgnoreCase("default")) {
                if (originalData != null && originalData.length > 0 && originalData[0] != null) {
                    
                    for (int i=0; i < outData.length; i++) {
                        //if they don't have a parent set already then we set one
                        if (outData[i] != null && 
                                outData[i].getMetaData().get(DataProperty.PARENT) == null) {
                            outData[i].getMetaData().put(DataProperty.PARENT, originalData[0]);
                        }
                    }
                }
            }
        }
    }
    
	private LogService getLogService() {
		ServiceReference serviceReference = bContext.getServiceReference(DataManagerService.class.getName());
		LogService log = null;
		
		if (serviceReference != null) {
			log = (LogService) bContext.getService(
				bContext.getServiceReference(LogService.class.getName()));
		}
		
		return log;
	}
}

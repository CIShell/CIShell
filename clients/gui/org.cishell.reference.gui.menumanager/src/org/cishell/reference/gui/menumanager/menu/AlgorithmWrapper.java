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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.cishell.app.service.datamanager.DataManagerService;
import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.algorithm.DataValidator;
import org.cishell.framework.algorithm.ProgressMonitor;
import org.cishell.framework.algorithm.ProgressTrackable;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.service.conversion.Converter;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;


public class AlgorithmWrapper implements Algorithm, AlgorithmProperty, ProgressTrackable {
    protected ServiceReference ref;
    protected BundleContext bContext;
    protected CIShellContext ciContext;
    protected Data[] originalData;
    protected Data[] data;
    protected Converter[][] converters;
    protected Dictionary parameters;
    protected Map idToLabelMap;
    protected MetaTypeProvider provider;
    protected ProgressMonitor progressMonitor;
    protected Algorithm algorithm;
    
    public AlgorithmWrapper(ServiceReference ref, BundleContext bContext,
            CIShellContext ciContext, Data[] originalData, Data[] data,
            Converter[][] converters) {
        this.ref = ref;
        this.bContext = bContext;
        this.ciContext = ciContext;
        this.originalData = originalData;
        this.data = data;
        this.converters = converters;

        this.idToLabelMap = new HashMap();
        this.progressMonitor = null;
        
        
    }

    /**
     * @see org.cishell.framework.algorithm.Algorithm#execute()
     */
    public Data[] execute() {
        try {
            for (int i=0; i < data.length; i++) {
                if (converters[i] != null) {
                    data[i] = converters[i][0].convert(data[i]);
    
                    if (data[i] == null && i < (data.length - 1)) {
                    	Exception e = 
                    		new Exception("The converter " + 
                    				converters[i].getClass().getName() +
                    				" returned a null result where data was expected.");
                    	throw e;
                    }
                    converters[i] = null;
                }
            }
            
            GUIBuilderService builder = (GUIBuilderService)
            ciContext.getService(GUIBuilderService.class.getName());
        
            AlgorithmFactory factory = (AlgorithmFactory) bContext.getService(ref);
            
            if (factory instanceof DataValidator) {
            	String validation = ((DataValidator) factory).validate(data);
            	
            	if (validation != null && validation.length() > 0) {
            		String label = (String) ref.getProperty(LABEL);
            		if (label == null) {
            			label = "Algorithm";
            		}
            		
            		builder.showError("Invalid Data", "The data given to \""+label+"\" is incompatible for this reason: "+validation , (String) null);
            		return null;
            	}
            }
            
            this.provider = factory.createParameters(data);
            String pid = (String)ref.getProperty(Constants.SERVICE_PID);
            
            this.parameters = new Hashtable();
            if (provider != null) {
                this.parameters = builder.createGUIandWait(pid, provider);
            }
            
            if(this.parameters == null) {
            	return new Data[0];
            }
            
            algorithm = factory.createAlgorithm(data, parameters, ciContext);
            
            printParameters();
            
            if (progressMonitor != null && algorithm instanceof ProgressTrackable) {
            	((ProgressTrackable)algorithm).setProgressMonitor(progressMonitor);
            }
            
            Data[] outData = algorithm.execute();
            
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
    
    protected void printParameters() {
        LogService logger = getLogService();
        setupIdToLabelMap();
        
        if (logger != null) {
            if (!this.parameters.isEmpty()) {
            	//adjust to log all input parameters in one block
            	StringBuffer inputParams = new StringBuffer("\n"+"Input Parameters:");
                
                for (Enumeration e = this.parameters.keys(); e
                        .hasMoreElements();) {
                    String key = (String) e.nextElement();
                    Object value = this.parameters.get(key);
                    
                    key = (String) idToLabelMap.get(key);
                    inputParams.append("\n"+key+": "+value);                   
                    
                }
                logger.log(LogService.LOG_INFO, inputParams.toString());
            }
        }
    }
    
    protected void setupIdToLabelMap() {
        if (provider != null) {
            ObjectClassDefinition ocd = null;
            try {
                String pid = (String) ref.getProperty(Constants.SERVICE_PID);
                ocd = provider.getObjectClassDefinition(pid, null);
                
                if (ocd != null) {
                    AttributeDefinition[] attr = 
                        ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);
                    
                    for (int i=0; i < attr.length; i++) {
                        String id = attr[i].getID();
                        String label = attr[i].getName();
                        
                        idToLabelMap.put(id, label);
                    }
                }
            } catch (IllegalArgumentException e) {}
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

	public ProgressMonitor getProgressMonitor() {
		if (algorithm instanceof ProgressTrackable) {
			return progressMonitor;
		}
		else {
			return null;
		}
	}

	public void setProgressMonitor(ProgressMonitor monitor) {
		progressMonitor = monitor;
	}
}

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

import java.io.IOException;
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
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.algorithm.DataValidator;
import org.cishell.framework.algorithm.ParameterMutator;
import org.cishell.framework.algorithm.ProgressMonitor;
import org.cishell.framework.algorithm.ProgressTrackable;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.framework.userprefs.UserPrefsProperty;
import org.cishell.reference.gui.menumanager.Activator;
import org.cishell.reference.gui.menumanager.menu.metatypewrapper.ParamMetaTypeProvider;
import org.cishell.reference.service.metatype.BasicMetaTypeProvider;
import org.cishell.service.conversion.ConversionException;
import org.cishell.service.conversion.Converter;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;


public class AlgorithmWrapper implements Algorithm, AlgorithmProperty, ProgressTrackable {
    protected ServiceReference ref;
    protected BundleContext bContext;
    protected CIShellContext ciContext;
    protected Data[] originalData;
    protected Data[] data;
    protected Converter[][] converters;
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
        this.progressMonitor = null;           
    }

    /**
     * @see org.cishell.framework.algorithm.Algorithm#execute()
     */
    public Data[] execute() {
    	AlgorithmFactory factory = (AlgorithmFactory) bContext.getService(ref);
    	String pid = (String)ref.getProperty(Constants.SERVICE_PID);
    	
    	// convert input data to the correct format
        boolean conversionSuccessful = tryConvertingDataToRequiredFormat(data, converters);
        if (!conversionSuccessful) return null;
        boolean inputIsValid = testDataValidityIfPossible(factory, data);
        if (!inputIsValid) return null;
        
        // create algorithm parameters        
        String metatype_pid = getMetaTypeID(ref);
        
        MetaTypeProvider provider = getPossiblyMutatedMetaTypeProvider(metatype_pid, pid, factory);
        Dictionary parameters = getUserEnteredParameters(metatype_pid, provider);
        
        // check to see if the user cancelled the operation
        if(parameters == null) return null;
        
        printParameters(metatype_pid, provider, parameters);
        
        // create the algorithm
        algorithm = factory.createAlgorithm(data, parameters, ciContext);
        trackAlgorithmIfPossible(algorithm);
        
        // execute the algorithm
        Data[] outData = tryExecutingAlgorithm(algorithm);
        if (outData == null) return null;
        
        // process and return the algorithm's output
        doParentage(outData);
        outData = removeNullData(outData);
        addDataToDataManager(outData);

        return outData;
    }
    
    protected Data[] removeNullData(Data[] outData) {
        if (outData != null) {            
            List goodData = new ArrayList();
            for (int i=0; i < outData.length; i++) {
                if (outData[i] != null) {
                    goodData.add(outData[i]);
                }
            }
            
            outData = (Data[]) goodData.toArray(new Data[0]);
        }
        
        return outData;
    }
    
    protected void addDataToDataManager(Data[] outData) {
        if (outData != null) {
            DataManagerService dataManager = (DataManagerService) 
                bContext.getService(bContext.getServiceReference(
                        DataManagerService.class.getName()));
                        
            if (outData.length != 0) {
                dataManager.setSelectedData(outData);
            }
        }
    }
    
    protected Data[] tryExecutingAlgorithm(Algorithm algorithm) {
        Data[] outData = null;
        try {
        	outData = algorithm.execute();
        } catch (AlgorithmExecutionException e) {
            log(LogService.LOG_ERROR,
        		"The Algorithm: \""+ref.getProperty(AlgorithmProperty.LABEL)+
                "\" had an error while executing: "+e.getMessage());
        } catch (RuntimeException e) {
            GUIBuilderService builder = (GUIBuilderService)
            ciContext.getService(GUIBuilderService.class.getName());
            
        	builder.showError("Error!", "An unexpected exception occurred while "
        			+"executing \""+ref.getProperty(AlgorithmProperty.LABEL)+".\"", e);
        }
        
        return outData;
    }
    
    protected boolean tryConvertingDataToRequiredFormat(Data[] data, Converter[][] converters) {
        for (int i=0; i < data.length; i++) {
            if (converters[i] != null) {
            	try {
            		data[i] = converters[i][0].convert(data[i]);
            	} catch (ConversionException e) {
            		log(LogService.LOG_ERROR,"The conversion of data to give" +
            				" the algorithm failed for this reason: "+e.getMessage(), e);
            		return false;
            	}

                if (data[i] == null && i < (data.length - 1)) {
                	log(LogService.LOG_ERROR, "The converter: " + 
                				converters[i].getClass().getName() +
                				" returned a null result where data was expected when" +
                				" converting the data to give the algorithm.");
                	return false;
                }
                converters[i] = null;
            }
        }
        
        return true;
    }
    
    protected boolean testDataValidityIfPossible(AlgorithmFactory factory, Data[] data) {
        if (factory instanceof DataValidator) {
        	String validation = ((DataValidator) factory).validate(data);
        	
        	if (validation != null && validation.length() > 0) {
        		String label = (String) ref.getProperty(LABEL);
        		if (label == null) {
        			label = "Algorithm";
        		}
        		
        		log(LogService.LOG_ERROR,"INVALID DATA: The data given to \""+label+"\" is incompatible for this reason: "+validation);
        		return false;
        	}
        }
        
        return true;
    }
    
    protected String getMetaTypeID(ServiceReference ref) {
    	String pid = (String)ref.getProperty(Constants.SERVICE_PID);
        String metatype_pid = (String) ref.getProperty(PARAMETERS_PID);
        
        if (metatype_pid == null) {
        	metatype_pid = pid;
        }
        
        return metatype_pid;
    }
    
    protected MetaTypeProvider getPossiblyMutatedMetaTypeProvider(String metatype_pid, String pid, AlgorithmFactory factory) {
    	MetaTypeProvider provider = null;
    	
        MetaTypeService metaTypeService = (MetaTypeService) Activator.getService(MetaTypeService.class.getName());
        if (metaTypeService != null) {
        	provider = metaTypeService.getMetaTypeInformation(ref.getBundle());            	
        }

        if (factory instanceof ParameterMutator && provider != null) {
        	try {
        		ObjectClassDefinition ocd = provider.getObjectClassDefinition(metatype_pid, null);
        		
        		ocd = ((ParameterMutator) factory).mutateParameters(data, ocd);
            	
            	if (ocd != null) {
            		provider = new BasicMetaTypeProvider(ocd);
            	}
        	} catch (IllegalArgumentException e) {
        		 log(LogService.LOG_DEBUG, pid+" has an invalid metatype id: "+metatype_pid);
        	}
        }
        
        if (provider != null) {
        	provider = wrapProvider(ref, provider);
        }
        
        return provider;
    }
    
    protected void trackAlgorithmIfPossible(Algorithm algorithm) {
        if (progressMonitor != null && algorithm instanceof ProgressTrackable) {
        	((ProgressTrackable)algorithm).setProgressMonitor(progressMonitor);
        }
    }
    
    protected Dictionary getUserEnteredParameters(String metatype_pid, MetaTypeProvider provider) {
    	Dictionary parameters = new Hashtable();
        if (provider != null) {
            GUIBuilderService builder = (GUIBuilderService)
            ciContext.getService(GUIBuilderService.class.getName());
            
            parameters = builder.createGUIandWait(metatype_pid, provider);
        }
        
    	return parameters;
    }
        
	// wrap the provider to provide special functionality, such as overriding default values of attributes through
	// preferences.
	protected MetaTypeProvider wrapProvider(ServiceReference algRef, MetaTypeProvider unwrappedProvider) {
		ConfigurationAdmin ca = getConfigurationAdmin();
		
		if (ca != null && hasParamDefaultPreferences(algRef)) {
			String standardServicePID = (String) algRef.getProperty(Constants.SERVICE_PID);
			String paramOverrideConfPID = standardServicePID + UserPrefsProperty.PARAM_PREFS_CONF_SUFFIX;
			try {
				Configuration defaultParamValueOverrider = ca.getConfiguration(paramOverrideConfPID, null);
				Dictionary defaultParamOverriderDict = defaultParamValueOverrider.getProperties();
				MetaTypeProvider wrappedProvider = new ParamMetaTypeProvider(unwrappedProvider,
						defaultParamOverriderDict);
				return wrappedProvider;
			} catch (IOException e) {
				return unwrappedProvider;
			}
		} else {
		}

		return unwrappedProvider;
	}
	
	protected boolean hasParamDefaultPreferences(ServiceReference algRef) {
		String prefsToPublish = (String) algRef.getProperty(UserPrefsProperty.PREFS_PUBLISHED_KEY);
		if (prefsToPublish == null) {
			return true;
		}

		return prefsToPublish.contains(UserPrefsProperty.PUBLISH_PARAM_DEFAULT_PREFS_VALUE);
	}
    
    protected void log(int logLevel, String message) {
    	LogService log = (LogService) ciContext.getService(LogService.class.getName());
    	if (log != null) {
    		log.log(logLevel, message);
    	} else {
    		System.out.println(message);
    	}
    }

    protected void log(int logLevel, String message, Throwable exception) {
    	LogService log = (LogService) ciContext.getService(LogService.class.getName());
    	if (log != null) {
    		log.log(logLevel, message, exception);
    	} else {
    		System.out.println(message);
    		exception.printStackTrace();
    	}
    }    
    
    protected void printParameters(String metatype_pid, MetaTypeProvider provider, Dictionary parameters) {
        LogService logger = getLogService();
        Map idToLabelMap = setupIdToLabelMap(metatype_pid, provider);
        
        if (logger != null && !parameters.isEmpty()) {
        	//adjust to log all input parameters in one block
        	StringBuffer inputParams = new StringBuffer("\n"+"Input Parameters:");
            
            for (Enumeration e = parameters.keys(); e
                    .hasMoreElements();) {
                String key = (String) e.nextElement();
                Object value = parameters.get(key);
                
                key = (String) idToLabelMap.get(key);
                inputParams.append("\n"+key+": "+value);                   
                
            }
            logger.log(LogService.LOG_INFO, inputParams.toString());
        }
    }
    
    protected Map setupIdToLabelMap(String metatype_pid, MetaTypeProvider provider) {
    	Map idToLabelMap = new HashMap();
        if (provider != null) {
            ObjectClassDefinition ocd = null;
            try {
                ocd = provider.getObjectClassDefinition(metatype_pid, null);
                
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
        
        return idToLabelMap;
    }
    
    //only does anything if parentage=default so far...
    protected void doParentage(Data[] outData) {
        //make sure the parent set is the original Data and not the
        //converted data...
        if (outData != null && data != null && originalData != null 
                && originalData.length == data.length) {
            for (int i=0; i < outData.length; i++) {
                if (outData[i] != null) {
                    Object parent = outData[i].getMetadata().get(DataProperty.PARENT);
                    
                    if (parent != null) {
                        for (int j=0; j < data.length; i++) {
                            if (parent == data[j]) {
                                outData[i].getMetadata().put(DataProperty.PARENT, 
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
                                outData[i].getMetadata().get(DataProperty.PARENT) == null) {
                            outData[i].getMetadata().put(DataProperty.PARENT, originalData[0]);
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
	
	private ConfigurationAdmin getConfigurationAdmin() {
		ServiceReference serviceReference = bContext.getServiceReference(ConfigurationAdmin.class.getName());
		ConfigurationAdmin ca = null;
		
		if (serviceReference != null) {
			ca = (ConfigurationAdmin) bContext.getService(
				bContext.getServiceReference(ConfigurationAdmin.class.getName()));
		}
		
		return ca;
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

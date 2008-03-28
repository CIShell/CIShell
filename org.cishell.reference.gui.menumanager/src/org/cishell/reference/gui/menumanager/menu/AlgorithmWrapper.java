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
	protected ServiceReference algFactoryRef;
	protected BundleContext bContext;
	protected CIShellContext ciContext;
	protected Data[] originalData;
	protected Data[] convertableData;
	protected Converter[][] converters;
	protected ProgressMonitor progressMonitor;

	protected ConfigurationAdmin ca;
	protected GUIBuilderService builder;
	protected DataManagerService dataManager;

	// ConfigurationAdmin may be null
	public AlgorithmWrapper(ServiceReference ref, CIShellContext ciContext, BundleContext bContext,
			Data[] originalData, Data[] data, Converter[][] converters, ConfigurationAdmin ca) {
		this.algFactoryRef = ref;
		this.bContext = bContext;
		this.ciContext = ciContext;
		this.originalData = originalData;
		this.convertableData = data;
		this.converters = converters;

		this.ca = ca;
		this.progressMonitor = null;
		this.builder = (GUIBuilderService) ciContext.getService(GUIBuilderService.class.getName());
		this.dataManager = (DataManagerService) bContext.getService(bContext
				.getServiceReference(DataManagerService.class.getName()));
	}

	/**
	 * @see org.cishell.framework.algorithm.Algorithm#execute()
	 */
	public Data[] execute() {
		try {
			// prepare to run the algorithm

			Data[] data = convertDataToRequiredFormat(this.convertableData, this.converters);
			AlgorithmFactory algFactory = (AlgorithmFactory) bContext.getService(algFactoryRef);
			boolean inputIsValid = testDataValidityIfPossible(algFactory, data);
			if (!inputIsValid) return null;

			// create algorithm parameters

			MetaTypeProvider parameterSetupInfo = obtainParameterSetupInfo(algFactory, data);
			Dictionary parameters = createParameters(parameterSetupInfo);
			printParameters(parameters, parameterSetupInfo);

			// create the algorithm

			Algorithm algorithm = algFactory.createAlgorithm(data, parameters, ciContext);
			trackAlgorithmProgressIfPossible(algorithm);

			// execute the algorithm

			Data[] rawOutData = algorithm.execute();

			// return the algorithm's output

			Data[] processedOutData = processOutData(rawOutData);
			return processedOutData;

		} catch (Throwable e) {
			// show any errors to the user

			showGenericExecutionError(e);
			return new Data[0];
		}
	}

	// should return null if algorithm is not progress trackable
	public ProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	public void setProgressMonitor(ProgressMonitor monitor) {
		progressMonitor = monitor;
	}

	protected Data[] convertDataToRequiredFormat(Data[] providedData, Converter[][] converters) throws Exception {
		// convert data into the in_data format of the algorithm we are trying to run
		Data[] readyData = new Data[providedData.length];

		for (int i = 0; i < providedData.length; i++) {
			if (converters[i] != null) {
				// WARNING: arbitrarily chooses first converter out of a list of possible converters
				readyData[i] = converters[i][0].convert(providedData[i]);

				if (readyData[i] == null && i < (readyData.length - 1)) {
					Exception e = new Exception("The converter " + converters[i].getClass().getName()
							+ " returned a null result where data was expected.");
					throw e;
				}
				converters[i] = null;
			}
		}

		return readyData;
	}

	protected boolean testDataValidityIfPossible(AlgorithmFactory algFactory, Data[] dataInQuestion) {
		if (algFactory instanceof DataValidator) {
			String validation = ((DataValidator) algFactory).validate(dataInQuestion);

			if (validation != null && validation.length() > 0) {
				String label = (String) algFactoryRef.getProperty(LABEL);
				if (label == null) {
					label = "Algorithm";
				}

				builder.showError("Invalid Data", "The data given to \"" + label + "\" is incompatible for this reason: "
						+ validation, (String) null);
				return false;
			}
			return true;
			} else {
				//counts as valid if there is no validator available.
				return true;
			}
	}

	protected MetaTypeProvider obtainParameterSetupInfo(AlgorithmFactory algFactory, Data[] data) {

		MetaTypeProvider provider = null;

		// first, get the standard parameter setup info for the algorithm factory.

		MetaTypeService metaTypeService = (MetaTypeService) Activator.getService(MetaTypeService.class.getName());
		if (metaTypeService != null) {
			provider = metaTypeService.getMetaTypeInformation(algFactoryRef.getBundle());
		}

		// if the algorithm factory wants to mutate the parameter setup info, allow it to.
		if (algFactory instanceof ParameterMutator && provider != null) {
			String parameterPID = determineParameterPID(algFactoryRef, provider);
			try {
				ObjectClassDefinition ocd = provider.getObjectClassDefinition(parameterPID, null);

				ocd = ((ParameterMutator) algFactory).mutateParameters(data, ocd);

				if (ocd != null) {
					provider = new BasicMetaTypeProvider(ocd);
				}
			} catch (IllegalArgumentException e) {
				LogService logger = getLogService();
				logger.log(LogService.LOG_DEBUG, algFactoryRef.getProperty(Constants.SERVICE_PID)
						+ " has an invalid metatype parameter id: " + parameterPID);
			}
		}

		// wrap the parameter setup info so that default parameter values
		// specified in the user preference service are filled in.

		if (provider != null) {
			provider = wrapProvider(this.algFactoryRef, provider);
		}

		return provider;
	}

	protected Dictionary createParameters(MetaTypeProvider parameterSetupInfo) {
		// ask the user to specify the values for algorithm's parameters
		String parameterPID = determineParameterPID(algFactoryRef, parameterSetupInfo);
		Dictionary parameters = new Hashtable();
		if (parameterSetupInfo != null) {
			parameters = builder.createGUIandWait(parameterPID, parameterSetupInfo);
		}
		return parameters;
	}

	protected void printParameters(Dictionary parameters, MetaTypeProvider parameterSetupInfo) {
		LogService logger = getLogService();
		Map idToLabelMap = createIdToLabelMap(parameterSetupInfo);

		if (logger != null) {
			if (parameters.isEmpty()) {
				// adjust to log all input parameters in one block
				StringBuffer inputParams = new StringBuffer("\n" + "Input Parameters:");

				for (Enumeration e = parameters.keys(); e.hasMoreElements();) {
					String key = (String) e.nextElement();
					Object value = parameters.get(key);

					key = (String) idToLabelMap.get(key);
					inputParams.append("\n" + key + ": " + value);

				}
				logger.log(LogService.LOG_INFO, inputParams.toString());
			}
		}
	}

	protected void trackAlgorithmProgressIfPossible(Algorithm algorithm) {
		if (algorithm instanceof ProgressTrackable) {
			((ProgressTrackable) algorithm).setProgressMonitor(progressMonitor);
		}
	}

	protected Data[] processOutData(Data[] rawOutData) {
		if (rawOutData != null) {
			doParentage(rawOutData);
			List goodData = new ArrayList();
			for (int i = 0; i < rawOutData.length; i++) {
				if (rawOutData[i] != null) {
					goodData.add(rawOutData[i]);
				}
			}

			Data[] processedOutData = (Data[]) goodData.toArray(new Data[goodData.size()]);
			if (rawOutData.length != 0) {
				dataManager.setSelectedData(rawOutData);
			}

			return processedOutData;
		} else {
			return null;
		}
	}

	// wrap the provider to provide special functionality, such as overriding default values of attributes through
	// preferences.
	protected MetaTypeProvider wrapProvider(ServiceReference algRef, MetaTypeProvider unwrappedProvider) {
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

	protected String determineParameterPID(ServiceReference ref, MetaTypeProvider provider) {
		String overridePID = (String) ref.getProperty(AlgorithmProperty.PARAMETERS_PID);
		if (overridePID != null) {
			return overridePID;
		} else {
			return (String) ref.getProperty(Constants.SERVICE_PID);
		}
	}

	protected Map createIdToLabelMap(MetaTypeProvider parameterSetupInfo) {
		Map idToLabelMap = new HashMap();
		if (parameterSetupInfo != null) {
			ObjectClassDefinition ocd = null;
			try {
				String parameterPID = determineParameterPID(algFactoryRef, parameterSetupInfo);
				ocd = parameterSetupInfo.getObjectClassDefinition(parameterPID, null);

				if (ocd != null) {
					AttributeDefinition[] attr = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);

					for (int i = 0; i < attr.length; i++) {
						String id = attr[i].getID();
						String label = attr[i].getName();

						idToLabelMap.put(id, label);
					}
				}
			} catch (IllegalArgumentException e) {
			}
		}

		return idToLabelMap;
	}

	// only does anything if parentage=default so far...
	protected void doParentage(Data[] outData) {
		// make sure the parent set is the original Data and not the
		// converted data...
		if (outData != null && convertableData != null && originalData != null
				&& originalData.length == convertableData.length) {
			for (int i = 0; i < outData.length; i++) {
				if (outData[i] != null) {
					Object parent = outData[i].getMetadata().get(DataProperty.PARENT);

					if (parent != null) {
						for (int j = 0; j < convertableData.length; i++) {
							if (parent == convertableData[j]) {
								outData[i].getMetadata().put(DataProperty.PARENT, originalData[j]);
								break;
							}
						}
					}
				}
			}
		}

		// check and act on parentage settings
		String parentage = (String) algFactoryRef.getProperty("parentage");
		if (parentage != null) {
			parentage = parentage.trim();
			if (parentage.equalsIgnoreCase("default")) {
				if (originalData != null && originalData.length > 0 && originalData[0] != null) {

					for (int i = 0; i < outData.length; i++) {
						// if they don't have a parent set already then we set one
						if (outData[i] != null && outData[i].getMetadata().get(DataProperty.PARENT) == null) {
							outData[i].getMetadata().put(DataProperty.PARENT, originalData[0]);
						}
					}
				}
			}
		}
	}

	protected LogService getLogService() {
		ServiceReference serviceReference = bContext.getServiceReference(DataManagerService.class.getName());
		LogService log = null;

		if (serviceReference != null) {
			log = (LogService) bContext.getService(bContext.getServiceReference(LogService.class.getName()));
		}

		return log;
	}

	protected boolean hasParamDefaultPreferences(ServiceReference algRef) {
		String prefsToPublish = (String) algRef.getProperty(UserPrefsProperty.PREFS_PUBLISHED_KEY);
		if (prefsToPublish == null) {
			return true;
		}

		if (prefsToPublish.contains(UserPrefsProperty.PUBLISH_PARAM_DEFAULT_PREFS_VALUE)) {
			return true;
		} else {
			return false;
		}
	}

	protected void showGenericExecutionError(Throwable e) {
		builder.showError("Error!", "The Algorithm: \"" + algFactoryRef.getProperty(AlgorithmProperty.LABEL)
				+ "\" had an error while executing.", e);
	}
}

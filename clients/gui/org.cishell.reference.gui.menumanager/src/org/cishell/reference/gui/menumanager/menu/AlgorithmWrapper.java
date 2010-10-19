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
import org.cishell.framework.algorithm.AlgorithmCanceledException;
import org.cishell.framework.algorithm.AlgorithmCreationCanceledException;
import org.cishell.framework.algorithm.AlgorithmCreationFailedException;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.algorithm.AllParametersMutatedOutException;
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
	protected ServiceReference serviceReference;
	protected BundleContext bundleContext;
	protected CIShellContext ciShellContext;
	protected Data[] originalData;
	protected Data[] data;
	protected Converter[][] converters;
	protected ProgressMonitor progressMonitor;
	protected Algorithm algorithm;

	public AlgorithmWrapper(
			ServiceReference serviceReference,
			BundleContext bundleContext,
			CIShellContext ciShellContext,
			Data[] originalData,
			Data[] data,
			Converter[][] converters) {
		this.serviceReference = serviceReference;
		this.bundleContext = bundleContext;
		this.ciShellContext = ciShellContext;
		this.originalData = originalData;
		this.data = data;
		this.converters = converters;
		this.progressMonitor = null;
	}

	/**
	 * @see org.cishell.framework.algorithm.Algorithm#execute()
	 */
	public Data[] execute() {
		try {
			AlgorithmFactory factory = getAlgorithmFactory(bundleContext, serviceReference);

			if (factory == null) {
				return null;
			}

			String pid = (String) serviceReference.getProperty(Constants.SERVICE_PID);

			// Convert input data to the correct format.
			boolean conversionSuccessful = tryConvertingDataToRequiredFormat(data, converters);

			if (!conversionSuccessful) {
				return null;
			}
			
			boolean inputIsValid = testDataValidityIfPossible(factory, data);

			if (!inputIsValid) {
				return null;
			}

			// Create algorithm parameters.
			String metatypePID = getMetaTypeID(serviceReference);

			// TODO: Refactor this.
			MetaTypeProvider provider = null;

			try {
				provider = getPossiblyMutatedMetaTypeProvider(metatypePID, pid, factory);
			} catch (AlgorithmCreationFailedException e) {
				String format =
					"An error occurred when creating the algorithm \"%s\" with the data you " +
					"provided.  (Reason: %s)";
				String logMessage = String.format(
					format,
					serviceReference.getProperty(AlgorithmProperty.LABEL),
					e.getMessage());
				log(LogService.LOG_WARNING, logMessage, e);

				return null;
			}
			
			Dictionary<String, Object> parameters =
				getUserEnteredParameters(metatypePID, provider);

			// Check to see if the user canceled the operation.
			if (parameters == null) {
				return null;
			}

			printParameters(metatypePID, provider, parameters);

			// Create the algorithm.
			algorithm = createAlgorithm(factory, data, parameters, ciShellContext);
			
			if (algorithm == null) {
				return null;
			}
			
			trackAlgorithmIfPossible(algorithm);

			// Execute the algorithm.
			Data[] outData = tryExecutingAlgorithm(algorithm);
			
			if (outData == null)
				return null;

			// Process and return the algorithm's output.
			doParentage(outData);
			outData = removeNullData(outData);
			addDataToDataManager(outData);

			return outData;
		}
		catch (Exception e) {
			GUIBuilderService builder = (GUIBuilderService)ciShellContext.getService
				(GUIBuilderService.class.getName());
			
			String errorMessage = "An error occurred while preparing to run "
				+ "the algorithm \"" + serviceReference.getProperty(AlgorithmProperty.LABEL)
				+ ".\"";
			
			builder.showError("Error!", errorMessage, e);
			log(LogService.LOG_ERROR, errorMessage, e);
		}
		
		return null;
	}

	protected AlgorithmFactory getAlgorithmFactory(
			BundleContext bundleContext, ServiceReference serviceReference) {
		AlgorithmFactory algorithmFactory =
			(AlgorithmFactory) bundleContext.getService(serviceReference);

		if (algorithmFactory == null) {
			String errorMessage =
				"Could not create AlgorithmFactory for the algorithm "
				+ "\"" + serviceReference.getProperty(AlgorithmProperty.LABEL) + "\".";
			String details = "The algorithm's pid was \""
				+ serviceReference.getProperty(Constants.SERVICE_PID)
				+ "\" (potentially useful for debugging purposes).";
			GUIBuilderService builder =
				(GUIBuilderService) ciShellContext.getService(GUIBuilderService.class.getName());
			builder.showError("Error!", errorMessage, details);
			this.logger(LogService.LOG_ERROR, errorMessage);
		}

		return algorithmFactory;
	}

	protected Algorithm createAlgorithm(
			AlgorithmFactory factory,
			Data[] data,
			Dictionary<String, Object> parameters,
			CIShellContext ciContext) {
		final String algorithmName =
			(String) serviceReference.getProperty(AlgorithmProperty.LABEL);
		// TODO: Call on algorithm invocation service here.
		try {
			return factory.createAlgorithm(data, parameters, ciContext);
		} catch (AlgorithmCreationCanceledException e) {
			String logMessage = String.format(
				"The algorithm \"%s\" was canceled by the user.",
				algorithmName,
				e.getMessage());
			log(LogService.LOG_WARNING, logMessage, e);

			return null;
		} catch (AlgorithmCreationFailedException e) {
			String format = "An error occurred when creating algorithm \"%s\".  (Reason: %s)";
			String errorMessage = String.format(format, algorithmName, e.getMessage());
			GUIBuilderService builder =
				(GUIBuilderService) ciContext.getService(GUIBuilderService.class.getName());
			builder.showError("Error!", errorMessage, e);
			log(LogService.LOG_ERROR, errorMessage, e);

			return null;
		} catch (Exception e) {
			String errorMessage = String.format(
				"Unexpected error occurred while creating algorithm \"%s\".", algorithmName);
			GUIBuilderService builder =
				(GUIBuilderService) ciContext.getService(GUIBuilderService.class.getName());
			// TODO: This is where uncaught exceptions are displayed.
			builder.showError("Error!", errorMessage, e);
			log(LogService.LOG_ERROR, errorMessage, e);

			return null;
		}
	}

	protected Data[] removeNullData(Data[] outData) {
		if (outData != null) {
			List<Data> goodData = new ArrayList<Data>();

			for (Data data : outData) {
				if (data != null) {
					goodData.add(data);
				}
			}

			outData = (Data[]) goodData.toArray(new Data[0]);
		}

		return outData;
	}

	protected void addDataToDataManager(Data[] outData) {
		if (outData != null) {
			DataManagerService dataManager = (DataManagerService)
				bundleContext.getService(
						bundleContext.getServiceReference(
								DataManagerService.class.getName()));

			if (outData.length != 0) {
				for (int ii = 0; ii < outData.length; ii++) {
					dataManager.addData(outData[ii]);
				}
				
				Data[] dataToSelect = new Data[] { outData[0] };
				dataManager.setSelectedData(dataToSelect);
			}
		}
	}

	protected Data[] tryExecutingAlgorithm(Algorithm algorithm) {
		Data[] outData = null;
		final String algorithmName =
			(String) serviceReference.getProperty(AlgorithmProperty.LABEL);

		try {
			outData = algorithm.execute();
		} catch (AlgorithmCanceledException e) {
			String logMessage = String.format(
				"The algorithm: \"%s\" was canceled by the user.",
				algorithmName,
				e.getMessage());
			log(LogService.LOG_WARNING, logMessage, e);
		} catch (AlgorithmExecutionException e) {
			String logMessage = String.format(
				"The algorithm: \"%s\" had an error while executing: %s",
				algorithmName,
				e.getMessage());
			log(LogService.LOG_ERROR, logMessage, e);
		} catch (RuntimeException e) {
			GUIBuilderService builder =
				(GUIBuilderService) ciShellContext.getService(GUIBuilderService.class.getName());
			String errorMessage = String.format(
				"An unxpected error occurred while executing the algorithm \"%s\".",
				algorithmName);
			builder.showError("Error!", errorMessage, e);
		}

		return outData;
	}

	protected boolean tryConvertingDataToRequiredFormat(
			Data[] data, Converter[][] converters) {
		for (int i = 0; i < data.length; i++) {
			if (converters[i] != null) {
				try {
					data[i] = converters[i][0].convert(data[i]);
				} catch (ConversionException e) {
					log(LogService.LOG_ERROR,
						"Error: Unable to convert data for use by the "
						+ "algorithm:\n    " + e.getMessage(), e);
					return false;
				}

				if (data[i] == null && i < (data.length - 1)) {
					logger(LogService.LOG_ERROR, "The converter: "
							+ converters[i].getClass().getName()
							+ " returned a null result where data was "
							+ "expected when converting the data to give "
							+ "the algorithm.");
					return false;
				}
				converters[i] = null;
			}
		}

		return true;
	}

	protected boolean testDataValidityIfPossible(AlgorithmFactory factory,
												 Data[] data) {
		if (factory instanceof DataValidator) {
			String validation = ((DataValidator) factory).validate(data);

			if (validation != null && validation.length() > 0) {
				String label = (String) serviceReference.getProperty(LABEL);
				if (label == null) {
					label = "Algorithm";
				}

				logger(LogService.LOG_ERROR,
						"INVALID DATA: The data given to \"" + label
						+ "\" is incompatible for this reason: " + validation);
				return false;
			}
		}

		return true;
	}

	protected String getMetaTypeID(ServiceReference ref) {
		String pid = (String) ref.getProperty(Constants.SERVICE_PID);
		String metatype_pid = (String) ref.getProperty(PARAMETERS_PID);

		if (metatype_pid == null) {
			metatype_pid = pid;
		}

		return metatype_pid;
	}

	protected MetaTypeProvider getPossiblyMutatedMetaTypeProvider(
			String metatypePID, String pid,	AlgorithmFactory factory)
			throws AlgorithmCreationFailedException {
		MetaTypeProvider provider = null;
		MetaTypeService metaTypeService = (MetaTypeService)
			Activator.getService(MetaTypeService.class.getName());
		if (metaTypeService != null) {
			provider = metaTypeService.getMetaTypeInformation(serviceReference.getBundle());
		}

		if ((factory instanceof ParameterMutator) && (provider != null)) {
			try {
				ObjectClassDefinition objectClassDefinition =
					provider.getObjectClassDefinition(metatypePID, null);

				if (objectClassDefinition == null) {
					logNullOCDWarning(pid, metatypePID);
				}

				try {
					objectClassDefinition =
						((ParameterMutator) factory).mutateParameters(data, objectClassDefinition);

					if (objectClassDefinition != null) {
						provider = new BasicMetaTypeProvider(objectClassDefinition);
					}
				} catch (AllParametersMutatedOutException e) {
					provider = null;
				}
			} catch (IllegalArgumentException e) {
				log(LogService.LOG_DEBUG, pid + " has an invalid metatype id: " + metatypePID, e);
			}
		}

		if (provider != null) {
			provider = wrapProvider(serviceReference, provider);
		}

		return provider;
	}

	protected void trackAlgorithmIfPossible(Algorithm algorithm) {
		if (progressMonitor != null && algorithm instanceof ProgressTrackable) {
			((ProgressTrackable) algorithm).setProgressMonitor(progressMonitor);
		}
	}

	protected Dictionary<String, Object> getUserEnteredParameters(
			String metatypePID, MetaTypeProvider provider) {
		Dictionary<String, Object> parameters = new Hashtable<String, Object>();

		if (provider != null) {
			GUIBuilderService builder =
				(GUIBuilderService) ciShellContext.getService(GUIBuilderService.class.getName());

			// TODO: Make builder.createGUIAndWait return a Dictionary<String, Object>.
			parameters = builder.createGUIandWait(metatypePID, provider);
		}

		return parameters;
	}

	/* Wrap the provider to provide special functionality, such as overriding
	 * default values of attributes through preferences.
	 */
	protected MetaTypeProvider wrapProvider(
			ServiceReference algRef, MetaTypeProvider unwrappedProvider) {
		ConfigurationAdmin ca = getConfigurationAdmin();

		if (ca != null && hasParamDefaultPreferences(algRef)) {
			String standardServicePID =
				(String) algRef.getProperty(Constants.SERVICE_PID);
			String paramOverrideConfPID =
				standardServicePID + UserPrefsProperty.PARAM_PREFS_CONF_SUFFIX;
			try {
				Configuration defaultParamValueOverrider =
					ca.getConfiguration(paramOverrideConfPID, null);
				Dictionary defaultParamOverriderDict =
					defaultParamValueOverrider.getProperties();
				MetaTypeProvider wrappedProvider =
					new ParamMetaTypeProvider(unwrappedProvider,
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
		String prefsToPublish =
			(String) algRef.getProperty(UserPrefsProperty.PREFS_PUBLISHED_KEY);
		if (prefsToPublish == null) {
			return false;
		}

		return prefsToPublish.contains(
				UserPrefsProperty.PUBLISH_PARAM_DEFAULT_PREFS_VALUE);
	}

	protected void logger(int logLevel, String message) {
		LogService log =
			(LogService) ciShellContext.getService(LogService.class.getName());
		if (log != null) {
			log.log(logLevel, message);
		} else {
			System.out.println(message);
		}
	}

	protected void log(int logLevel, String message, Throwable exception) {
		LogService log =
			(LogService) ciShellContext.getService(LogService.class.getName());
		if (log != null) {
			log.log(logLevel, message, exception);
		} else {
			System.out.println(message);
			exception.printStackTrace();
		}
	}

	protected void printParameters(String metatype_pid,
								   MetaTypeProvider provider,
								   Dictionary parameters) {
		LogService logger = getLogService();
		Map idToLabelMap = setupIdToLabelMap(metatype_pid, provider);

		if (logger != null && !parameters.isEmpty()) {
			// adjust to log all input parameters in one block
			StringBuffer inputParams =
				new StringBuffer("\n" + "Input Parameters:");

			for (Enumeration e = parameters.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				Object value = parameters.get(key);

				key = (String) idToLabelMap.get(key);
				inputParams.append("\n" + key + ": " + value);

			}
			logger.log(LogService.LOG_INFO, inputParams.toString());
		}
	}

	protected Map setupIdToLabelMap(String metatype_pid,
									MetaTypeProvider provider) {
		Map idToLabelMap = new HashMap();
		if (provider != null) {
			ObjectClassDefinition ocd = null;
			try {
				ocd = provider.getObjectClassDefinition(metatype_pid, null);

				if (ocd != null) {
					AttributeDefinition[] attr =
						ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);

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
		if (outData != null
				&& data != null
				&& originalData != null
				&& originalData.length == data.length) {
			for (int i = 0; i < outData.length; i++) {
				if (outData[i] != null) {
					Object parent =
						outData[i].getMetadata().get(DataProperty.PARENT);

					if (parent != null) {
						for (int j = 0; j < data.length; j++) {
							if (parent == data[j]) {
								outData[i].getMetadata().put(
										DataProperty.PARENT, originalData[j]);
								break;
							}
						}
					}
				}
			}
		}

		// Check and act on parentage settings
		String parentage = (String) serviceReference.getProperty("parentage");
		if (parentage != null) {
			parentage = parentage.trim();
			if (parentage.equalsIgnoreCase("default")) {
				if (originalData != null
						&& originalData.length > 0
						&& originalData[0] != null) {

					for (int i = 0; i < outData.length; i++) {
						// If they don't have a parent set already then we set one
						if (outData[i] != null
								&& outData[i].getMetadata().get(DataProperty.PARENT) == null) {
							outData[i].getMetadata().put(
									DataProperty.PARENT, originalData[0]);
						}
					}
				}
			}
		}
	}

	private LogService getLogService() {
		ServiceReference serviceReference =
			bundleContext.getServiceReference(DataManagerService.class.getName());
		LogService log = null;

		if (serviceReference != null) {
			log = (LogService) bundleContext.getService(
					bundleContext.getServiceReference(LogService.class.getName()));
		}

		return log;
	}

	private ConfigurationAdmin getConfigurationAdmin() {
		ServiceReference serviceReference =
			bundleContext.getServiceReference(ConfigurationAdmin.class.getName());
		ConfigurationAdmin ca = null;

		if (serviceReference != null) {
			ca = (ConfigurationAdmin) bundleContext.getService(
					bundleContext.getServiceReference(
							ConfigurationAdmin.class.getName()));
		}

		return ca;
	}

	private void logNullOCDWarning(String pid, String metatype_pid) {
		this.logger(LogService.LOG_WARNING,
			"Warning: could not get object class definition '" + metatype_pid
			+ "' from the algorithm '" + pid + "'");
	}

	public ProgressMonitor getProgressMonitor() {
		if (algorithm instanceof ProgressTrackable) {
			return progressMonitor;
		} else {
			return null;
		}
	}

	public void setProgressMonitor(ProgressMonitor monitor) {
		progressMonitor = monitor;
	}
}

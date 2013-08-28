package org.cishell.reference.gui.workflow.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmCreationCanceledException;
import org.cishell.framework.algorithm.AlgorithmCreationFailedException;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.algorithm.ProgressMonitor;
import org.cishell.framework.algorithm.ProgressTrackable;
import org.cishell.framework.data.Data;
import org.cishell.reference.gui.workflow.Activator;
import org.cishell.reference.gui.workflow.Utilities.Constant;
import org.cishell.service.conversion.ConversionException;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class AlgorithmWorkflowItem implements WorkflowItem {
	private String name;
	private Long internalId;
	@XStreamOmitField
	private ServiceReference serviceReference;
	private Dictionary<String, Object> parameters;
	private Dictionary<String, String> nameToId;
	@XStreamOmitField
	private Data[] inputData;
	@XStreamOmitField
	protected Converter[][] converters;
	@XStreamOmitField
	private Workflow workflow;
	private String pid;

	public AlgorithmWorkflowItem(String name, Long id, String pid) {
		this.name = name;
		this.internalId = id;
		System.out.println("id =" + id);
		nameToId = new Hashtable<String, String>();
		this.pid = pid;
		System.out.println("\npid =" + pid);

	}

	@Override
	public String getType() {
		return Constant.AlgorithmItem;
	}

	public void add(String name, String id) {
		this.nameToId.put(name, id);
	}

	public Dictionary<String, String> getNameToId() {
		return nameToId;
	}

	public void setNameToId(Dictionary<String, String> nameToId) {
		this.nameToId = nameToId;
	}

	public void setInternalId(Long internalId) {
		this.internalId = internalId;
	}

	public void addParameter(String name, Object obj) {
		String id = this.nameToId.get(name);
		parameters.put(id, obj);
	}

	public Object getParameterValue(String name) {
		String id = this.nameToId.get(name);
		return parameters.get(id);
	}

	@Override
	public Object[] run() {

		for (Enumeration e = parameters.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			Object value = parameters.get(key);
		}

		AlgorithmFactory factory = (AlgorithmFactory) Activator.getContext()
				.getService(serviceReference);

		Algorithm algo = factory.createAlgorithm(inputData, parameters,
				Activator.getCiShellContext());
		if (algo instanceof ProgressTrackable) {
			ProgressTrackable pt = (ProgressTrackable) algo;
			pt.setProgressMonitor(new AlgorithmProgressMonitor());
		}
		try {

			Data[] data = algo.execute();
			return data;
		} catch (AlgorithmExecutionException e) {
			String logMessage = String.format(
					"The algorithm \"%s\" failed to execute", name,
					e.getMessage());
			log(LogService.LOG_WARNING, logMessage, e);
			return null;
		}

		catch (AlgorithmCreationCanceledException e) {
			String logMessage = String.format(
					"The algorithm \"%s\" was canceled by the user.", name,
					e.getMessage());
			log(LogService.LOG_WARNING, logMessage, e);

			return null;
		} catch (AlgorithmCreationFailedException e) {
			String format = "An error occurred when creating algorithm \"%s\".  (Reason: %s)";
			String errorMessage = String.format(format, name, e.getMessage());
			GUIBuilderService builder = (GUIBuilderService) Activator
					.getCiShellContext().getService(
							GUIBuilderService.class.getName());
			builder.showError("Error!", errorMessage, e);
			log(LogService.LOG_ERROR, errorMessage, e);
			return null;
		} catch (RuntimeException e) {
			GUIBuilderService builder = (GUIBuilderService) Activator
					.getCiShellContext().getService(
							GUIBuilderService.class.getName());
			String errorMessage = String
					.format("An unxpected error occurred while executing the algorithm \"%s\".",
							e.getMessage());
			builder.showError("Error!", errorMessage, e);
			return null;
		}
	}

	public void dataSelected(Data[] selectedData) {
		String inDataString = (String) this.serviceReference
				.getProperty(AlgorithmProperty.IN_DATA);
		String[] inData = separateInData(inDataString);

		if ((inData.length == 1)
				&& inData[0].equalsIgnoreCase(AlgorithmProperty.NULL_DATA)) {
			this.inputData = new Data[0];
		} else if (selectedData == null) {
			this.inputData = null;
		} else {
			DataConversionService converter = (DataConversionService) Activator
					.getCiShellContext().getService(
							DataConversionService.class.getName());

			List<Data> dataSet = new ArrayList<Data>(
					Arrays.asList(selectedData));
			this.inputData = new Data[inData.length];
			this.converters = new Converter[inData.length][];
			for (int ii = 0; ii < inData.length; ii++) {
				for (int jj = 0; jj < dataSet.size(); jj++) {
					Data datum = (Data) dataSet.get(jj);
					if (datum != null) {
						if (isAssignableFrom(inData[ii], datum)) {
							dataSet.remove(jj);
							this.inputData[ii] = datum;
							this.converters[ii] = null;
						} else {
							Converter[] conversion = converter.findConverters(
									datum, inData[ii]);

							if (conversion.length > 0) {
								dataSet.remove(jj);
								this.inputData[ii] = datum;
								this.converters[ii] = conversion;
							} else {
								System.out.println("converter notfound");

							}
						}
					}
				}

				// If there isn't a converter for one of the inputs then this
				// data isn't useful.
				if (this.inputData[ii] == null) {
					this.inputData = null;
					break;
				}
			}
		}
	}

	private boolean isAssignableFrom(String type, Data datum) {
		Object data = datum.getData();
		boolean assignable = false;

		if ((type != null) && type.equalsIgnoreCase(datum.getFormat())) {
			assignable = true;
		} else if (data != null) {
			try {
				Class<?> clazz = Class.forName(type, false, data.getClass()
						.getClassLoader());

				if (clazz != null && clazz.isInstance(data)) {
					assignable = true;
				}
			} catch (ClassNotFoundException e) { /* Ignore. */
			}
		}

		return assignable;
	}

	private String[] separateInData(String inDataString) {
		String[] inData = ("" + inDataString).split(",");

		for (int ii = 0; ii < inData.length; ii++) {
			inData[ii] = inData[ii].trim();
		}

		return inData;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Long getIternalId() {
		return internalId;
	}

	public Dictionary<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Dictionary<String, Object> parameters) {
		this.parameters = parameters;
	}

	public Data[] getInputData() {
		return inputData;
	}

	public void setInputData(Data[] data) {
		setServiceReferenceFromBundle();
		// this.inputData = data;
		try {
			dataSelected(data);
			tryConvertingDataToRequiredFormat();
		} catch (RuntimeException e) {
			String logMessage = String
					.format("Error: Unable to convert data for use by the algorithm:%n    %s",
							e.getMessage());
			log(LogService.LOG_ERROR, logMessage, e);
			e.printStackTrace();
		}
	}

	public Long getInternalId() {
		return internalId;
	}

	protected void log(int logLevel, String message, Throwable exception) {
		LogService logger = (LogService) Activator.getCiShellContext()
				.getService(LogService.class.getName());

		if (logger != null) {
			logger.log(this.serviceReference, logLevel, message, exception);
		} else {
			System.out.println(message);
			exception.printStackTrace();
		}
	}

	protected boolean tryConvertingDataToRequiredFormat() {
		for (int i = 0; i < inputData.length; i++) {
			if (converters[i] != null) {
				try {
					inputData[i] = converters[i][0].convert(inputData[i]);
				} catch (ConversionException e) {
					String logMessage = String
							.format("Error: Unable to convert data for use by the algorithm:%n    %s",
									e.getMessage());
					log(LogService.LOG_ERROR, logMessage, e);
					e.printStackTrace();

					return false;
				}

				if (inputData[i] == null && i < (inputData.length - 1)) {
					log(LogService.LOG_ERROR, "The converter: "
							+ converters[i].getClass().getName()
							+ " returned a null result where data was "
							+ "expected when converting the data to give "
							+ "the algorithm.", new Throwable());
					return false;
				}
				converters[i] = null;
			}
		}

		return true;
	}

	private class AlgorithmProgressMonitor implements ProgressMonitor {
		private double totalWorkUnits;

		public void describeWork(String currentWork) {
		}

		public void done() {
		}

		public boolean isCanceled() {
			return false;
		}

		public boolean isPaused() {
			return false;
		}

		public void setCanceled(boolean value) {
		}

		public void setPaused(boolean value) {
		}

		public void start(int capabilities, int totalWorkUnits) {
		}

		public void start(int capabilities, double totalWorkUnits) {
			if ((capabilities & ProgressMonitor.CANCELLABLE) > 0) {
			}

			if ((capabilities & ProgressMonitor.PAUSEABLE) > 0) {
			}

			if ((capabilities & ProgressMonitor.WORK_TRACKABLE) > 0) {

			}

			this.totalWorkUnits = totalWorkUnits;
		}

		public void worked(final int work) {
			worked((double) work);
		}

		public void worked(final double work) {
		}
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	public void setServiceReferenceFromBundle() {
		ServiceReference[] serviceReferences = null;
		try {
			serviceReferences = Activator.getContext().getAllServiceReferences(
					AlgorithmFactory.class.getName(), null);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < serviceReferences.length; i++) {
			String pid = (String) serviceReferences[i]
					.getProperty(Constants.SERVICE_PID);
			if (pid.compareTo(this.pid) == 0) {
				this.serviceReference = serviceReferences[i];
				break;
			}
		}
	}

}

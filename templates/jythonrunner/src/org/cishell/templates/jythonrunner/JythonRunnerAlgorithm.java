package org.cishell.templates.jythonrunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.osgi.framework.Bundle;
import org.osgi.service.log.LogService;
import org.python.core.PyFile;
import org.python.core.PyJavaInstance;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

/**
 * 
 * @author mwlinnem
 * 
 */

//TODO:refactor me into multiple classes
//TODO:general cleanup, renaming, etc...

public class JythonRunnerAlgorithm implements Algorithm {
    private Data[] data;
    private Dictionary parameters;
    private Dictionary properties;
    
	private LogService logger;
    
    private URL script;
	
	public static final String SCRIPT_ARGUMENT_PREFIX = "arg";
	public static final String SCRIPT_RESULT_PREFIX = "result";
	
	public JythonRunnerAlgorithm(Data[] data, Dictionary parameters,
			CIShellContext context, Dictionary properties, Bundle myBundle) {
		this.data = data;
		this.parameters = parameters;
		this.properties = properties;
		
		String scriptPath = (String) properties.get(
				JythonFileProperty.SCRIPT_PATH_KEY);
		script = myBundle.getResource(scriptPath);
		
		this.logger = (LogService) context.getService(
				LogService.class.getName());
	}

    public Data[] execute() {		
    	
    	PythonInterpreter interp = initializeInterpreter(
    			new PythonInterpreter(), data, parameters);
    	
    	List rawResults = runScript(interp, script);
    	
    	Data[] results  = formatRawResults(rawResults, data, properties);
    	  	
    	return results;
    }
    
    
    private PythonInterpreter initializeInterpreter(PythonInterpreter interp,
    		Data[] data, Dictionary parameters) {
    	interp = passUserProvidedArguments(interp, parameters);
    	interp = passCIShellProvidedArguments(interp, data);
    	interp = initializeLogging(interp);
    	return interp;
    }
    
    /**
     * Executes the script and extracts the raw results.
     * @param interp The initialized python interpreter
     * @param script The jython script itself, which the interpreter will run
     * @return A list of objects that the script returned.
     */
    private List runScript(PythonInterpreter interp, URL script) {
			interp = executeFile(interp, script);
			List rawResults = getRawResults(interp); 
			return rawResults;
    }
    
    private Data[] formatRawResults(List rawResults, Data[] inputData,
    		Dictionary properties) {
    	List dataResults = convertToData(rawResults);
		List dataResultsWithMetaData = addMetaData(dataResults, inputData,
				properties);
		Data[] resultsArray = convertToArray(dataResultsWithMetaData);
		return resultsArray;
    }
    
    
    
    private PythonInterpreter executeFile(PythonInterpreter interp,
    		URL script) {
    	try {
    		interp.execfile(script.openStream());
    	} catch (IOException e) {
    		logger.log(LogService.LOG_ERROR, "Unable to open jython script " +
    				script.toString() + ".", e);
			e.printStackTrace();		
    	}
    	return interp;
    }
    
    private PythonInterpreter passUserProvidedArguments(
    		PythonInterpreter interp, Dictionary parameters) {
    	 
    	Enumeration enumer = parameters.keys();
    	while (enumer.hasMoreElements()) {
    		String key   = (String) enumer.nextElement();
    		Object value = parameters.get(key);
    		String argName = key;
    		
    		interp = passArgument(value, argName, interp);
    	}
    	
    	return interp;
    }
    
    private PythonInterpreter passCIShellProvidedArguments(
    		PythonInterpreter interp, Data[] data) {
    	for (int ii = 0; ii < this.data.length; ii++) {
			Data argData = this.data[ii];
			Object arg = argData.getData();
			String argName = SCRIPT_ARGUMENT_PREFIX + String.valueOf(ii);
			
			interp = passArgument(arg, argName, interp);
			}	
	
    	return interp;
    }
    
    private PythonInterpreter initializeLogging(PythonInterpreter interp) {
    	interp.setErr(System.err);
    	interp.setOut(System.out);
    	return interp;
    }
    
    /**
     * Gets Java versions of all the results from the script.
     * @param interp a python interpreter that holds results (presumably
     * after having executed a script)
     * @return A list of objects, where each object is a result from
     * the interpreters environment.
     */
    private List getRawResults(PythonInterpreter interp) {
    	List results = new ArrayList();
    	
    	/*
    	 * gets the values held in result variables, from 
    	 * "result0" counting upward, until we reach a result 
    	 * variable which is not defined.
    	 */
    	int ii = 0;
    	String resultName = SCRIPT_RESULT_PREFIX + ii;
    	
    	while (variableIsDefined(interp, resultName)) {
    		
    		results.add(interp.get(resultName, Object.class));

    		ii++;
    		resultName = SCRIPT_RESULT_PREFIX + ii;
    	}
    	
    	return results;
    }
    
    /**
     * adds metadata obtained from the algorithm's .properties files
     * that specify information about what the script returns.
     * @param data a list of data objects, in the order
     * they were returned.
     * @param inputData the data passed from CIShell to this algorithm
     * @param properties information about the script, such as
     * the labels, types, and parents of all the returned data.
     * @return a list of data objects in the order they were provided,
     * now containing the appropriate metadata obtained from the 
     * .properties file.
     */
    private List addMetaData(List data, Data[] inputData, Dictionary properties) {
    	List results = new ArrayList();
    	for (int ii = 0; ii < data.size(); ii++) {
    		Data result = ((Data) data.get(ii));
    		Dictionary metadataHolder = result.getMetaData();
    		
    		String dataLabel = getResultLabel(properties, ii);
    		metadataHolder.put(DataProperty.LABEL, dataLabel);
    		
    		String dataType = getResultType(properties, ii);
    		metadataHolder.put(DataProperty.TYPE, dataType);
    		
    		Data dataParent = getResultParent(properties, ii, inputData); 
    		if (dataParent != null) {
    		metadataHolder.put(DataProperty.PARENT, dataParent);
    		} //it's okay to not have a parent, little Timmy.
    		
    		results.add(result);
    	}
		return results;
    }
    
    public String getResultLabel(Dictionary props, int numResult) {
		String labelKey = JythonFileProperty.RESULT_PREFIX + numResult
				+ JythonFileProperty.LABEL_SUFFIX;
		Object labelValue = props.get(labelKey);
		
		String labelValueString;
		if (!(labelValue == null)) {
			labelValueString = (String) labelValue;
		} else {
			labelValueString = "Data";
			logger.log(LogService.LOG_WARNING, "Label of data returned from "
					+ "jython script not specified in .properties file. "
					+ "Assigning label to '" + labelValueString + "'.");
		}
		return labelValueString;
	}
    
    public String getResultType(Dictionary props, int numResult) {
    	String typeKey = JythonFileProperty.RESULT_PREFIX + 
    		numResult + JythonFileProperty.TYPE_SUFFIX;
		Object typeValue = props.get(typeKey);
		
		String typeValueString;
		if (! (typeValue == null)) {
			typeValueString = (String) typeValue;
			checkType(typeValueString);

		} else {
			typeValueString = DataProperty.OTHER_TYPE;
			logger.log(LogService.LOG_WARNING, "Type of data returned from " + 
					"jython script not specified in .properties file. " + 
					"Assigning type to '" + typeValueString + "'.");
		}
		return typeValueString;
    }
    
    /**
     * Looks to see whether the result has a parent specified in the 
     * algorithm's .properties file. If it does, return the parent data.
     * Otherwise return null.
     * @param props information about the script, such as
     * the labels, types, and parents of all the returned data.
     * @param numResult specifies which result's information we need to look 
     * up
     * @param inputData the data CIShell passed this algorithm. 
     * @return either the parent of the result data specified by numResults,
     * or null, if there is no parent specified.
     */
    public Data getResultParent(Dictionary props, int numResult, Data[] inputData) {
		String childKey = JythonFileProperty.RESULT_PREFIX + numResult
		+ JythonFileProperty.PARENT_SUFFIX;
		Object parentName = props.get(childKey);
		
		Data parent;
		if (! (parentName == null)) {
			//TODO: more validation on parentName
			char parentDataIndexChar = getLastChar((String) parentName);
			int parentDataIndex = Character.digit(parentDataIndexChar, 10);
			if (parentDataIndex < inputData.length) {
				parent = inputData[parentDataIndex];
			} else {
				logger.log(LogService.LOG_WARNING, ".properties file " + 
						"tried to assign result" + numResult + "the " +
						"parent arg"+ parentDataIndex + ", which has an " +
						"index greater than any arg provided. Cannot " + 
						"assign result" + numResult + " a parent.");
				parent = null;
			}
		} else {
			//it's okay not to specify a parent.
			parent = null;
		}
		return parent;
    }
      
    private PythonInterpreter passArgument(Object arg, String argName,
    		PythonInterpreter interp) {
    	if (! (arg instanceof File)) {
			interp.set(argName,
					new PyJavaInstance(arg));
		} else {
			try {
			File fileArg = (File) arg;
			InputStream fileStream = fileArg.toURL().openStream();
			interp.set(argName,
					new PyFile(fileStream));
			} catch (IOException e) {
				logger.log(LogService.LOG_ERROR, "Problem opening file" +
						" provided as an argument to jython script.", e);
				e.printStackTrace();
			}	
		}
    	
    	return interp;
    }
    
    /**
     * Takes java objects and wraps them in our CIShell data objects
     * so that they can be returned from the algorithm.
     * @param rawResults a list of java objects.
     * @return a list of data objects.
     */
    private List convertToData(List rawResults) {
    	List results = new ArrayList();
    	for (int ii = 0; ii < rawResults.size(); ii++) {
    	Object rawResult = rawResults.get(ii);
    	String resultClassName = rawResult.getClass().getName();
    	BasicData data = new BasicData(rawResult, resultClassName);
    	results.add(data);
    	}
    	return results;
    }
    private boolean variableIsDefined(PythonInterpreter interp,
    		String variableName) {
    	String predicate = "vars().has_key('" + variableName + "') or " +
    		               "globals().has_key('" + variableName + "')";
    	boolean result = evalPredicate(interp, predicate);
    	return result;
    }
    private boolean evalPredicate(PythonInterpreter interp, String predicate) {
    	PyObject pyResult = interp.eval(predicate);
    	Boolean resultObj = (Boolean) pyResult.__tojava__(Boolean.class);
    	boolean result = resultObj.booleanValue();
    	return result;
    }
    
    private Data[] convertToArray(List dataList) {
    	Data[] dataArray = new Data[dataList.size()];
    	for (int ii = 0; ii < dataArray.length; ii++) {
    		dataArray[ii] = (Data) dataList.get(ii);
    	}
    	return dataArray;
    }
    
    private void checkType(String ts) {
    	if (! (ts.equals(DataProperty.MATRIX_TYPE) || 
    		   ts.equals(DataProperty.NETWORK_TYPE) ||
    		   ts.equals(DataProperty.TEXT_TYPE) ||
    		   ts.equals(DataProperty.OTHER_TYPE) ||
    		   ts.equals(DataProperty.TEXT_TYPE))) {
    		logger.log(LogService.LOG_WARNING, "JythonRunnerAlgorithm: " +
    				"Assigning return data an unsupported data type " + 
    				ts +". Either the type is invalid or " +
    				"JythonRunnerAlgorithm has not be updated to reflect " +
    				"types introduced in newer versions");
    	}
    }
    
    public char getLastChar(String s) {
    	if (s.length() > 0) {
    		return s.charAt(s.length() - 1);
    	} else {
    		throw new IndexOutOfBoundsException("Cannot get the last " +
    				"character of an empy string");
    	}
    }
}
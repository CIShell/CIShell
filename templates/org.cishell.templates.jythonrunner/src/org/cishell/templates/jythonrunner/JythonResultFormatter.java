package org.cishell.templates.jythonrunner;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.osgi.service.log.LogService;

/**
 * 
 * @author mwlinnem
 *
 */
public class JythonResultFormatter {

	public static final String DEFAULT_LABEL_VALUE = "Data";
	
	protected LogService logger;
	
	public JythonResultFormatter(LogService logger) {
		this.logger = logger;
	}
	
	public Data[] formatRawResults(List rawResults, Data[] inputData,
    		Dictionary properties) {
    	List dataResults;
    	
    	dataResults = convertToData(rawResults, properties);
		dataResults = addMetaData(dataResults, inputData,
				properties);
		
		Data[] resultsArray = convertToArray(dataResults);
		return resultsArray;
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
    protected List addMetaData(List data, Data[] inputData, Dictionary properties) {
    	List results = new ArrayList();
    	for (int ii = 0; ii < data.size(); ii++) {
    		Data result = ((Data) data.get(ii));
    		Dictionary metadataHolder = result.getMetadata();
    		
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
    
    protected String getResultLabel(Dictionary props, int numResult) {
		String labelKey = JythonFileProperty.RESULT_PREFIX + numResult
				+ JythonFileProperty.LABEL_SUFFIX;
		Object labelValue = props.get(labelKey);
		
		String labelValueString;
		if (!(labelValue == null)) {
			labelValueString = (String) labelValue;
		} else {
			labelValueString = DEFAULT_LABEL_VALUE;
			logger.log(LogService.LOG_WARNING, "Label of data returned from "
					+ "jython script not specified in .properties file. "
					+ "Assigning label to '" + labelValueString + "'.");
		}
		return labelValueString;
	}
    
    protected String getResultType(Dictionary props, int numResult) {
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
    protected Data getResultParent(Dictionary props, int numResult, Data[] inputData) {
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
    
    /**
     * Takes java objects and wraps them in our CIShell data objects
     * so that they can be returned from the algorithm.
     * @param rawResults a list of java objects.
     * @return a list of data objects.
     */
    protected List convertToData(List rawResults, Dictionary props) {
    	List results = new ArrayList();
    	String outData = (String) props.get(AlgorithmProperty.OUT_DATA);
    	
    	if (outData.trim().equalsIgnoreCase(AlgorithmProperty.NULL_DATA)) {
    		return results;
    	}
    	 
    	String[] formats = outData.split(",");
    	
    	int numFinalResults;
    	if (formats.length > rawResults.size()) {
    		numFinalResults = rawResults.size();
    		logger.log(LogService.LOG_WARNING, "More out_data formats " +
    				"provided than an actual data returned. Ignoring extra " +
    				"formats.");
    	} else if (rawResults.size() > formats.length) {
    		numFinalResults = formats.length;
    		logger.log(LogService.LOG_WARNING, "More data returned than " +
    				"out_data formats provided. Ignoring additional data " +
    				"returned");
    	} else {
    		//both are the same length.
    		numFinalResults = rawResults.size();
    	}
    	
    	for (int ii = 0; ii < numFinalResults; ii++) {
    	Object rawResult = rawResults.get(ii);
    	BasicData data = new BasicData(rawResult, formats[ii]);
    	results.add(data);
    	}
    	return results;
    }
    
    protected char getLastChar(String s) {
    	if (s.length() > 0) {
    		return s.charAt(s.length() - 1);
    	} else {
    		throw new IndexOutOfBoundsException("Cannot get the last " +
    				"character of an empty string");
    	}
    }
    
    protected Data[] convertToArray(List dataList) {
    	Data[] dataArray = new Data[dataList.size()];
    	for (int ii = 0; ii < dataArray.length; ii++) {
    		dataArray[ii] = (Data) dataList.get(ii);
    	}
    	return dataArray;
    }
    
    protected void checkType(String ts) {
    	if (! (ts.equals(DataProperty.MATRIX_TYPE) || 
    		   ts.equals(DataProperty.NETWORK_TYPE) ||
    		   ts.equals(DataProperty.TEXT_TYPE) ||
    		   ts.equals(DataProperty.OTHER_TYPE))) {
    		logger.log(LogService.LOG_WARNING, "JythonRunnerAlgorithm: " +
    				"Assigning return data an unsupported data type " + 
    				ts +". Either the type is invalid or " +
    				"JythonRunnerAlgorithm has not be updated to reflect " +
    				"types introduced in newer versions");
    	}
    }
}

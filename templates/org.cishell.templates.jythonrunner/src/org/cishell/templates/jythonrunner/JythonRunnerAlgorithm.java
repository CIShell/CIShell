package org.cishell.templates.jythonrunner;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.data.Data;
import org.osgi.framework.Bundle;
import org.osgi.service.log.LogService;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

/**
 * 
 * @author mwlinnem
 * 
 */

public class JythonRunnerAlgorithm implements Algorithm {
    private Data[] data;
    private Dictionary parameters;
    private Dictionary properties;
    
	private LogService logger;
    
    private URL script;
    
    private JythonInterpreterInitializer interpInitializer;
    private JythonResultFormatter resultFormatter;
	
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
		
		this.interpInitializer = new JythonInterpreterInitializer(logger);
		this.resultFormatter = new JythonResultFormatter(logger);
	}

    public Data[] execute() {		
    	
    	PythonInterpreter interp = interpInitializer.initializeInterpreter(
    			new PythonInterpreter(), data, parameters);
    	
    	List rawResults = runScript(interp, script);
    	
    	Data[] results  = resultFormatter.formatRawResults(rawResults,
    			data, properties);
    	  	
    	return results;
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
    
    /**
     * Gets Java versions of all the results from the script.
     * @param interp a python interpreter that holds results (presumably
     * after having executed a script)
     * @return A list of objects, where each object is a result from
     * the interpreters environment.
     */
    protected List getRawResults(PythonInterpreter interp) {
    	List results = new ArrayList();
    	
    	/*
    	 * gets the values held in result variables, from 
    	 * "result0" counting upward, until we reach a result 
    	 * variable which is not defined.
    	 */
    	int ii = 0;
    	String resultName = JythonFileProperty.RESULT_PREFIX + ii;
    	
    	while (variableIsDefined(interp, resultName)) {
    		
    		results.add(interp.get(resultName, Object.class));

    		ii++;
    		resultName = JythonFileProperty.RESULT_PREFIX + ii;
    	}
    	
    	return results;
    }
    
    protected boolean variableIsDefined(PythonInterpreter interp,
    		String variableName) {
    	String predicate = "vars().has_key('" + variableName + "') or " +
    		               "globals().has_key('" + variableName + "')";
    	boolean result = evalPredicate(interp, predicate);
    	return result;
    }
    
    protected boolean evalPredicate(PythonInterpreter interp, String predicate) {
    	PyObject pyResult = interp.eval(predicate);
    	Boolean resultObj = (Boolean) pyResult.__tojava__(Boolean.class);
    	boolean result = resultObj.booleanValue();
    	return result;
    }
}
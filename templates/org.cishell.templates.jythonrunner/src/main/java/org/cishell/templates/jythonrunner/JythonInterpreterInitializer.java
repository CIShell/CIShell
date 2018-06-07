package org.cishell.templates.jythonrunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Enumeration;

import org.cishell.framework.data.Data;
import org.osgi.service.log.LogService;
import org.python.core.PyFile;
import org.python.core.PyJavaInstance;
import org.python.util.PythonInterpreter;

/**
 * 
 * @author mwlinnem
 *
 */
public class JythonInterpreterInitializer {

	protected LogService logger;
	
	public JythonInterpreterInitializer(LogService logger) {
		this.logger = logger;
	}
	
	public PythonInterpreter initializeInterpreter(PythonInterpreter interp,
    		Data[] data, Dictionary parameters) {
    	interp = passUserProvidedArguments(interp, parameters);
    	interp = passCIShellProvidedArguments(interp, data);
    	interp = initializeLogging(interp);
    	return interp;
    }
	
	protected PythonInterpreter passUserProvidedArguments(
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
    
    protected PythonInterpreter passCIShellProvidedArguments(
    		PythonInterpreter interp, Data[] data) {
    	for (int ii = 0; ii < data.length; ii++) {
			Data argData = data[ii];
			Object arg = argData.getData();
			String argName = JythonFileProperty.ARGUMENT_PREFIX + ii;
			
			interp = passArgument(arg, argName, interp);
			}	
	
    	return interp;
    }
    
    protected PythonInterpreter initializeLogging(PythonInterpreter interp) {
    	interp.setErr(System.err);
    	interp.setOut(System.out);
    	return interp;
    }
    

       
    protected PythonInterpreter passArgument(Object arg, String argName,
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
    

    

}

package org.cishell.templates.staticexecutable;

import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Properties;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.ProgressMonitor;
import org.cishell.framework.algorithm.ProgressTrackable;
import org.cishell.framework.data.Data;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

public class StaticExecutableAlgorithm implements Algorithm, ProgressTrackable {
   
	
	Data[] data;
    Dictionary parameters;
    CIShellContext context;
    LogService logger;
    
    private BundleContext bContext;
    private String algName;
    
    private ProgressMonitor monitor;
    
    public StaticExecutableAlgorithm(Data[] data, Dictionary parameters, CIShellContext context, BundleContext bContext, String algName) 
    	{
        this.data = data;
        this.parameters = parameters;
        this.context = context;
        this.bContext = bContext;
        this.algName = algName;
        
        logger = (LogService)context.getService(LogService.class.getName());
    }

    public Data[] execute() throws AlgorithmExecutionException {
        try {
        	//prepare to run the static executable
        	
            Properties serviceProps = getProperties("/"+algName+"/service.properties");
            Properties configProps = getProperties("/"+algName+"/config.properties");
            
            serviceProps.putAll(configProps);
            serviceProps.put("Algorithm-Directory", algName);
            
            StaticExecutableRunner runner = 
                new StaticExecutableRunner(bContext, context, serviceProps, parameters, data, monitor, algName);
            
            //run it!
            return runner.execute();
   
        } catch (IOException e) {
            throw new AlgorithmExecutionException(e.getMessage(), e);
        }
    }
    
    private Properties getProperties(String entry) throws IOException {
        URL url = bContext.getBundle().getEntry(entry);
        Properties props = null;
        
        if (url != null) {
            props = new Properties();
            props.load(url.openStream());
        }
        return props;
    }

	public ProgressMonitor getProgressMonitor() {
		return this.monitor;
	}

	public void setProgressMonitor(ProgressMonitor monitor) {
		this.monitor = monitor;
	}
}
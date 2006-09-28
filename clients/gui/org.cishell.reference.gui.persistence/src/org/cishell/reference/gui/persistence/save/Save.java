package org.cishell.reference.gui.persistence.save;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.log.LogService;

public class Save implements Algorithm {
    Data[] data;
    Dictionary parameters;
    CIShellContext context;
    
    final Shell parentShell;
    
    DataConversionService conversionManager;
    LogService logService;
    
    public Save(Data[] data, Dictionary parameters, CIShellContext context) {
        this.data = data;
        this.parameters = parameters;
        this.context = context;
        
        parentShell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();

        conversionManager = (DataConversionService) context.getService(
        		DataConversionService.class.getName());
        
        logService = (LogService)context.getService(LogService.class.getName());
    }

    public Data[] execute() {
    	//This only checks the first Data in the array
    	final Converter[] converters = conversionManager.findConverters(data[0], "file-ext:*");
    	/*
    	for (int i = 0; i < converters.length; ++i) {
    		Dictionary dict = converters[i].getProperties();
    		Object inDataObj  = dict.get(AlgorithmProperty.IN_DATA);
    		Object outDataObj = dict.get(AlgorithmProperty.OUT_DATA);
    	}
    	*/
    	if (converters.length < 1) {
    		logService.log(LogService.LOG_ERROR, "No valid converters found!");
    	}
    	else {
    		if (!parentShell.isDisposed()) {
    			parentShell.getDisplay().syncExec(new Runnable() {
    				public void run() {
    					//Shell shell = new Shell(parentShell);
    					SaveDataChooser sdc = new SaveDataChooser(data[0],
    		    			                     			parentShell, converters,
    		    			                     			"title",
    		    			                     			Save.class.getName(),
    		    			                     			context);
    					sdc.createContent(parentShell);
    					sdc.open();
    				}});
    		}
    	}
        return null;
    }
}
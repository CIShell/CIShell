/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 16, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.gui.menumanager.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cishell.app.service.datamanager.DataManagerListener;
import org.cishell.app.service.datamanager.DataManagerService;
import org.cishell.app.service.scheduler.SchedulerService;
import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.eclipse.jface.action.Action;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;


public class AlgorithmAction extends Action implements AlgorithmProperty, DataManagerListener {
    protected CIShellContext ciContext;
    protected BundleContext bContext;
    protected ServiceReference ref;
    protected Data[] data;
    protected Data[] originalData;
    protected Converter[][] converters;
    
    public AlgorithmAction(
    		ServiceReference ref, BundleContext bContext, CIShellContext ciContext) {
    	this((String)ref.getProperty(LABEL), ref, bContext, ciContext);
    }
    
    public AlgorithmAction(
    		String label, ServiceReference ref, BundleContext bContext, CIShellContext ciContext) {
        this.ref = ref;
        this.ciContext = ciContext;
        this.bContext = bContext;
        
        setText(label);
        setToolTipText((String)ref.getProperty(AlgorithmProperty.DESCRIPTION));
        
        DataManagerService dataManager = (DataManagerService) 
            bContext.getService(bContext.getServiceReference(
                    DataManagerService.class.getName()));
        
        dataManager.addDataManagerListener(this);
        dataSelected(dataManager.getSelectedData());
    }

    public void run() {
        try {
            printAlgorithmInformation(ref, ciContext);
           
            Algorithm algorithm = new AlgorithmWrapper(ref, bContext, ciContext, originalData, data, converters);
            SchedulerService scheduler = (SchedulerService) getService(SchedulerService.class);
            
            scheduler.schedule(algorithm, ref);
        } catch (Throwable exception) {
        	// Just in case an uncaught exception occurs. Eclipse will swallow errors thrown here.
            exception.printStackTrace();
        }
    }
    
    private void printAlgorithmInformation(ServiceReference ref, CIShellContext ciContext) {
        // Adjust to log the whole acknowledgement in one block.
        LogService logger = (LogService) ciContext.getService(LogService.class.getName());
        StringBuffer acknowledgement = new StringBuffer();
        String label = (String)ref.getProperty(LABEL);

        if (label != null) {
        	acknowledgement.append("..........\n" + label + " was selected.\n");
        }

        String authors = (String)ref.getProperty(AUTHORS);

        if (authors != null) {
        	acknowledgement.append("Author(s): " + authors + "\n");
        }

        String implementers = (String)ref.getProperty(IMPLEMENTERS);

        if (implementers != null) {
        	acknowledgement.append("Implementer(s): " + implementers + "\n");
        }

        String integrators = (String)ref.getProperty(INTEGRATORS);

        if (integrators != null) {
            acknowledgement.append("Integrator(s): " + integrators + "\n");
        }

        String reference = (String)ref.getProperty(REFERENCE);
        String reference_url = (String)ref.getProperty(REFERENCE_URL);            

        if ((reference != null) && (reference_url != null)) {
            acknowledgement.append(
            	"Reference: " + reference + " (" + reference_url + ")\n");
        } else if ((reference != null) && (reference_url == null)) {
        	acknowledgement.append("Reference: " + reference + "\n");
        }

        String docu = (String)ref.getProperty(DOCUMENTATION_URL);

        if (docu != null) {
        	acknowledgement.append("Documentation: " + docu + "\n");
        }

        if (acknowledgement.length() > 1) {
        	logger.log(LogService.LOG_INFO, acknowledgement.toString());
        }
    }
    
    private String[] separateInData(String inDataString) {
		String[] inData = ("" + inDataString).split(",");

        for (int ii = 0; ii < inData.length; ii++) {
        	inData[ii] = inData[ii].trim();
        }

		return inData;
	}
    
    public void dataSelected(Data[] selectedData) {        
        String inDataString = (String)ref.getProperty(IN_DATA);
        String[] inData = separateInData(inDataString);
        
        if ((inData.length == 1 && inData[0].equalsIgnoreCase(NULL_DATA))) {
            data = new Data[0];
        } else if (selectedData == null) {
            data = null;
        } else {
            DataConversionService converter = (DataConversionService)
                ciContext.getService(DataConversionService.class.getName());
            
            List dataSet = new ArrayList(Arrays.asList(selectedData));
            data = new Data[inData.length];
            converters = new Converter[inData.length][];
            
            for (int i=0; i < inData.length; i++) {
                for (int j=0; j < dataSet.size(); j++) {
                    Data datum = (Data) dataSet.get(j);
                    
                    if (datum != null) {
                        if (isAssignableFrom(inData[i], datum)) {
                            dataSet.remove(j);
                            data[i] = datum;
                            converters[i] = null;
                        } else {
                            Converter[] conversion = converter.findConverters(datum, inData[i]);
                            
                            if (conversion.length > 0) {
                                dataSet.remove(j);
                                data[i] = datum;
                                converters[i] = conversion;
                            }
                        }
                    }
                }
               
                //if there isn't a converter for one of the inputs
                //then this data isn't useful
                if (data[i] == null) {
                    data = null; 
                    break;
                }
            }
        }
        
        if (data != null) {
            originalData = (Data[]) data.clone();
        } else {
            originalData = null;
        }
        
        setEnabled(data != null);
    }
    
    private boolean isAssignableFrom(String type, Data datum) {
        Object data = datum.getData();
        boolean assignable = false;
        
        if (type != null && type.equalsIgnoreCase(datum.getFormat())) {
            assignable = true;
        } else if (data != null) {
            try {
                Class c = Class.forName(type, false, data.getClass().getClassLoader());
                
                if (c != null && c.isInstance(data)) {
                    assignable = true;
                } 
            } catch (ClassNotFoundException e) { /*ignore*/ }
        }
        
        return assignable;
    }
    
    public void dataAdded(Data data, String label) {}
    public void dataLabelChanged(Data data, String label) {}
    public void dataRemoved(Data data) {}
    
    private Object getService(Class clas) {
    	ServiceReference ref = bContext.getServiceReference(clas.getName());
    	if (ref != null) {
    		return bContext.getService(ref);
    	}
    	
    	return null;
    }
    
    public ServiceReference getServiceReference(){
    	return ref;
    }
}
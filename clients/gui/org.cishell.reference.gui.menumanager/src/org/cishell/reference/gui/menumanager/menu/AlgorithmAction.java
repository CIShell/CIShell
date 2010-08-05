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
    protected CIShellContext ciShellContext;
    protected BundleContext bundleContext;
    protected ServiceReference serviceReference;
    protected Data[] data;
    protected Data[] originalData;
    protected Converter[][] converters;
    
    public AlgorithmAction(
    		ServiceReference serviceReference,
    		BundleContext bundleContext,
    		CIShellContext ciShellContext) {
    	this(
    		(String) serviceReference.getProperty(LABEL),
    		serviceReference,
    		bundleContext,
    		ciShellContext);
    }
    
    public AlgorithmAction(
    		String label,
    		ServiceReference serviceReference,
    		BundleContext bundleContext,
    		CIShellContext ciShellContext) {
        this.serviceReference = serviceReference;
        this.ciShellContext = ciShellContext;
        this.bundleContext = bundleContext;
        
        setText(label);
        setToolTipText((String)serviceReference.getProperty(AlgorithmProperty.DESCRIPTION));
        
        DataManagerService dataManager = (DataManagerService) 
            bundleContext.getService(bundleContext.getServiceReference(
                    DataManagerService.class.getName()));
        
        dataManager.addDataManagerListener(this);
        dataSelected(dataManager.getSelectedData());
    }

    public void run() {
        try {
            printAlgorithmInformation(this.serviceReference, this.ciShellContext);

            Algorithm algorithm = new AlgorithmWrapper(
            	this.serviceReference,
            	this.bundleContext,
            	this.ciShellContext,
            	this.originalData,
            	this.data,
            	this.converters);
            SchedulerService scheduler = (SchedulerService) getService(SchedulerService.class);
            
            scheduler.schedule(algorithm, this.serviceReference);
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
        String inDataString = (String) this.serviceReference.getProperty(IN_DATA);
        String[] inData = separateInData(inDataString);
        
        if ((inData.length == 1) && inData[0].equalsIgnoreCase(NULL_DATA)) {
            this.data = new Data[0];
        } else if (selectedData == null) {
            this.data = null;
        } else {
            DataConversionService converter =
            	(DataConversionService) this.ciShellContext.getService(
            		DataConversionService.class.getName());
            
            List<Data> dataSet = new ArrayList<Data>(Arrays.asList(selectedData));
            this.data = new Data[inData.length];
            this.converters = new Converter[inData.length][];
            
            for (int ii = 0; ii < inData.length; ii++) {
                for (int jj = 0; jj < dataSet.size(); jj++) {
                    Data datum = (Data) dataSet.get(jj);
                    
                    if (datum != null) {
                        if (isAssignableFrom(inData[ii], datum)) {
                            dataSet.remove(jj);
                            this.data[ii] = datum;
                            this.converters[ii] = null;
                        } else {
                            Converter[] conversion = converter.findConverters(datum, inData[ii]);
                            
                            if (conversion.length > 0) {
                                dataSet.remove(jj);
                                this.data[ii] = datum;
                                this.converters[ii] = conversion;
                            }
                        }
                    }
                }
               
                // If there isn't a converter for one of the inputs then this data isn't useful.
                if (this.data[ii] == null) {
                    this.data = null;

                    break;
                }
            }
        }
        
        if (this.data != null) {
            this.originalData = (Data[]) this.data.clone();
        } else {
            this.originalData = null;
        }
        
        setEnabled(this.data != null);
    }
    
    private boolean isAssignableFrom(String type, Data datum) {
        Object data = datum.getData();
        boolean assignable = false;
        
        if ((type != null) && type.equalsIgnoreCase(datum.getFormat())) {
            assignable = true;
        } else if (data != null) {
            try {
                Class<?> clazz = Class.forName(type, false, data.getClass().getClassLoader());
                
                if (clazz != null && clazz.isInstance(data)) {
                    assignable = true;
                } 
            } catch (ClassNotFoundException e) { /* Ignore. */ }
        }
        
        return assignable;
    }
    
    public void dataAdded(Data data, String label) {}
    public void dataLabelChanged(Data data, String label) {}
    public void dataRemoved(Data data) {}
    
    private Object getService(Class<?> clazz) {
    	ServiceReference serviceReference = bundleContext.getServiceReference(clazz.getName());

    	if (serviceReference != null) {
    		return bundleContext.getService(serviceReference);
    	}
    	
    	return null;
    }
    
    public ServiceReference getServiceReference(){
    	return this.serviceReference;
    }
}
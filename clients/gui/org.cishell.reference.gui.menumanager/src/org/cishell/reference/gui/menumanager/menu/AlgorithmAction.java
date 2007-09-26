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
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.eclipse.jface.action.Action;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class AlgorithmAction extends Action implements AlgorithmProperty, DataManagerListener {
    protected CIShellContext ciContext;
    protected BundleContext bContext;
    protected ServiceReference ref;
    protected Data[] data;
    protected Data[] originalData;
    protected Converter[][] converters;
    
    public AlgorithmAction(ServiceReference ref, BundleContext bContext, CIShellContext ciContext) {
        this.ref = ref;
        this.ciContext = ciContext;
        this.bContext = bContext;
        
        setText((String)ref.getProperty(LABEL));
        setToolTipText((String)ref.getProperty(AlgorithmProperty.DESCRIPTION));
        
        DataManagerService dataManager = (DataManagerService) 
            bContext.getService(bContext.getServiceReference(
                    DataManagerService.class.getName()));
        
        dataManager.addDataManagerListener(this);
        dataSelected(dataManager.getSelectedData());
        
    }
    
    public AlgorithmAction(String label, ServiceReference ref, BundleContext bContext, CIShellContext ciContext) {
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
        //hmm... should probably change this.. maybe use the scheduler...
        new Thread("Menu Item Runner") {
            public void run() {
                runTask();
            }}.start();
    }
    
    public void runTask() {
        try {
            //save the current data
            Data[] data = this.data;
            Converter[][] converters = this.converters;

            SchedulerService scheduler = (SchedulerService) 
                bContext.getService(bContext.getServiceReference(
                        SchedulerService.class.getName()));
            

            printAlgorithmInformation();
            
            
            
            
           scheduler.schedule(new AlgorithmWrapper(ref, bContext, ciContext, originalData, data, converters), ref);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    private void printAlgorithmInformation() {
        //adjust to log the whole acknowledgement in one block
        LogService logger = (LogService) ciContext.getService(LogService.class.getName());
        StringBuffer acknowledgement = new StringBuffer();
        String label = (String)ref.getProperty("label");
        if (label != null){
        	acknowledgement.append("..........\n"+
            					label+" was selected.\n");
        }
        String authors = (String)ref.getProperty("authors");
        if (authors != null)
        	acknowledgement.append("Author(s): "+authors+"\n"); 
        String implementers = (String)ref.getProperty("implementers");
        if (implementers != null)
        	acknowledgement.append("Implementer(s): "+implementers+"\n");    
        String integrators = (String)ref.getProperty("integrators");
        if (integrators != null)
            acknowledgement.append("Integrator(s): "+integrators+"\n");
        String reference = (String)ref.getProperty("reference");
        String reference_url = (String)ref.getProperty("reference_url");            
        if (reference != null && reference_url != null )
            acknowledgement.append("Reference: "+reference+
                    " ("+reference_url+")\n"); 
        else if (reference != null && reference_url == null )
        	acknowledgement.append("Reference: "+reference+"\n");                     
        String docu = (String)ref.getProperty("docu");
        if (docu != null)
        	acknowledgement.append("Docu: "+docu+"\n");
        if(acknowledgement.length()>1)
        	logger.log(LogService.LOG_INFO, acknowledgement.toString());    
        
    }
    
    public void dataSelected(Data[] selectedData) {        
        String inDataString = (String)ref.getProperty(IN_DATA);
        String[] inData = ("" + inDataString).split(",");
        
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
                        if (isAsignableFrom(inData[i], datum)) {
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
     
        
        setEnabled(data != null); //&& isValid());
    }
    
    //This method will be disabled until we can find a better solution
    //for extra validation beyond input/output checking
/*    private boolean isValid() {
        String valid = null;
        String[] classes = (String[]) ref.getProperty(Constants.OBJECTCLASS);
        
        if (classes != null && data != null) {
            for (int i=0; i < classes.length; i++) {
                if (classes[i].equals(DataValidator.class.getName())) {
                    DataValidator validator = (DataValidator) bContext.getService(ref);
                                  
                    //FIXME: Could cause concurrency problems...
                    for (int j=0; j < data.length; j++) {
                        if (converters[j] != null && converters[j].length > 0) {
                            //does not work for large inputs...
                            data[j] = converters[j][0].convert(data[j]);
                            converters[j] = null;
                        }
                    }
                    
                    valid = validator.validate(data);
                }
            }
        }
        
        return valid == null || valid.length() == 0;
    }
*/    
    private boolean isAsignableFrom(String type, Data datum) {
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
    
    public ServiceReference getServiceReference(){
    	return ref;
    }
    
   
}
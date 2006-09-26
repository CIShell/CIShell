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
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.cishell.app.service.datamanager.DataManagerListener;
import org.cishell.app.service.datamanager.DataManagerService;
import org.cishell.app.service.scheduler.SchedulerService;
import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.eclipse.jface.action.Action;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.metatype.MetaTypeProvider;


public class AlgorithmAction extends Action implements AlgorithmProperty, DataManagerListener {
    protected CIShellContext ciContext;
    protected BundleContext bContext;
    protected ServiceReference ref;
    protected Data[] data;
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
            GUIBuilderService builder = (GUIBuilderService)
                ciContext.getService(GUIBuilderService.class.getName());
            
            AlgorithmFactory factory = (AlgorithmFactory) bContext.getService(ref);
            MetaTypeProvider provider = factory.createParameters(null);
            String pid = (String)ref.getProperty(Constants.SERVICE_PID); 
            
            Dictionary params = new Hashtable();
            if (provider != null) {
                params = builder.createGUIandWait(pid, provider);
            }
            
            if (params != null) {
                scheduler.schedule(new AlgorithmWrapper(ref, bContext, ciContext, data, converters, params), ref);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
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
        setEnabled(data != null);
    }
    
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
}
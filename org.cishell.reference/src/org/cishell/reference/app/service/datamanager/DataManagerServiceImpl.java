/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 15, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.app.service.datamanager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.cishell.app.service.datamanager.DataManagerListener;
import org.cishell.app.service.datamanager.DataManagerService;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;


public class DataManagerServiceImpl implements DataManagerService {
    private Map modelToLabelMap;
    private Map labelToModelMap; 
    private Map substringToNumberMap;
    private Set models;
    private Set selectedModels;
    
    private Set listeners;
    
    /**
     * Creates a new BasicModelManager Object.
     */
    public DataManagerServiceImpl() {
        modelToLabelMap = new HashMap();
        labelToModelMap = new HashMap();
        substringToNumberMap = new HashMap();
        models = new HashSet();
        listeners = new HashSet();
    }

    /**
     * @see edu.iu.iv.core.ModelManager#addData(java.lang.Object)
     */
    public void addData(Data model) {
        String label = (String)model.getMetaData().get(DataProperty.LABEL);
        String type = (String)model.getMetaData().get(DataProperty.TYPE);
        
        if(type == null){
            type = DataProperty.OTHER_TYPE;
            model.getMetaData().put(DataProperty.TYPE, type);
        }
        
        //generate label if needed
        if(label == null || label.equals("")){
            StackTraceElement[] stack = new Throwable().getStackTrace();                
            
            if (stack.length > 2) {
                String className = stack[2].getClassName();
                int lastDot = className.lastIndexOf(".");
                
                if (className.length() > lastDot) {
                    lastDot++;
                    className = className.substring(lastDot);
                    
                    if (className.endsWith("Algorithm")) {
                        className = className.substring(0,className.lastIndexOf("Algorithm"));
                    }
                    
                    if (className.endsWith("Factory")) {
                        className = className.substring(0,className.lastIndexOf("Factory"));
                    }
                }           
                label = className;  
            } else {
                label = "Unknown";
            }
            
            label = label + "." + type;           
        }
        
        addModel(model, label);
        
        for (Iterator iter=listeners.iterator(); iter.hasNext();) {
            ((DataManagerListener) iter.next()).dataAdded(model, label);
        }
    }

    private void addModel(Data model, String label) {
        label = findUniqueLabel(label);
        model.getMetaData().put(DataProperty.LABEL, label);
        //set the model to be unsaved initially
        model.getMetaData().put(DataProperty.MODIFIED, new Boolean(true));
                
        modelToLabelMap.put(model, label);
        labelToModelMap.put(label, model);      
        models.add(model);
    }
    
    private String findUniqueLabel(String label) {
        int lastIndex = label.length() - 1;
        boolean foundNumber = false;
        
        while (lastIndex > 0 && Character.isDigit(label.charAt(lastIndex))) {
            foundNumber = true;
            lastIndex--;
        }
        
        String newLabel = label.substring(0,lastIndex + 1);
        if (newLabel.charAt(newLabel.length()-1) != ' ') {
            newLabel = newLabel + ".";
        }
        
        
        Integer oldNumber = (Integer) substringToNumberMap.get(newLabel);
        if (oldNumber != null) {
            oldNumber = new Integer(oldNumber.intValue() + 1);
        } else {
            oldNumber = new Integer(1);
        }
        
        substringToNumberMap.put(newLabel,oldNumber);
        
        if (foundNumber) {
            int oldNum = oldNumber.intValue();
            int number = Integer.parseInt(label.substring(lastIndex+1));
            
            if (number < oldNum && getModelForLabel(newLabel + number) == null) {
                return newLabel + number;
            }
        } 
        
        newLabel = newLabel + oldNumber;
        
        return newLabel;
    }


    public void removeData(Data model) {
        String label = getLabel(model);
        
        labelToModelMap.remove(label);
        modelToLabelMap.remove(model);
        models.remove(model);
        
        for (Iterator iter=listeners.iterator(); iter.hasNext();) {
            ((DataManagerListener) iter.next()).dataRemoved(model);
        }
    }

    public Data[] getSelectedData() {
        if (selectedModels == null) {
            selectedModels = new HashSet();
        }
        
        return (Data[]) selectedModels.toArray(new Data[]{});
    }

    public void setSelectedData(Data[] models) {
        selectedModels = new HashSet(Arrays.asList(models));
        
        for (Iterator iter=listeners.iterator(); iter.hasNext();) {
            ((DataManagerListener) iter.next()).dataSelected(models);
        }
    }
    
    private Data getModelForLabel(String label){
        return (Data)labelToModelMap.get(label);
    }
    
    public String getLabel(Data model){
        return (String)modelToLabelMap.get(model);
    }
    
    public synchronized void setLabel(Data model, String label) {
        label = findUniqueLabel(label);
        
        modelToLabelMap.put(model, label);
        labelToModelMap.put(label, model);  
        
        for (Iterator iter=listeners.iterator(); iter.hasNext();) {
            ((DataManagerListener) iter.next()).dataLabelChanged(model, label);
        }
    }

    public Data[] getAllData() {
        return (Data[]) models.toArray(new Data[]{});
    }

    public void addDataManagerListener(DataManagerListener listener) {
        listeners.add(listener);
    }

    public void removeDataManagerListener(DataManagerListener listener) {
        listeners.remove(listener);
    }
}

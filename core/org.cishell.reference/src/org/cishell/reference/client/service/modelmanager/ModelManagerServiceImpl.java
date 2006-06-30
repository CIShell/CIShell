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
package org.cishell.reference.client.service.modelmanager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.cishell.client.service.modelmanager.ModelManagerListener;
import org.cishell.client.service.modelmanager.ModelManagerService;
import org.cishell.framework.datamodel.DataModel;
import org.cishell.framework.datamodel.DataModelProperty;
import org.cishell.framework.datamodel.DataModelType;


public class ModelManagerServiceImpl implements ModelManagerService {
    private Map modelToLabelMap;
    private Map labelToModelMap; 
    private Map substringToNumberMap;
    private Set models;
    private Set selectedModels;
    
    private Set listeners;
    
    /**
     * Creates a new BasicModelManager Object.
     */
    public ModelManagerServiceImpl() {
        modelToLabelMap = new HashMap();
        labelToModelMap = new HashMap();
        substringToNumberMap = new HashMap();
        models = new HashSet();
        listeners = new HashSet();
    }

    /**
     * @see edu.iu.iv.core.ModelManager#addModel(java.lang.Object)
     */
    public void addModel(DataModel model) {
        String label = (String)model.getProperties().get(DataModelProperty.LABEL);
        String type = (String)model.getProperties().get(DataModelProperty.TYPE);
        
        if(type == null){
            type = DataModelType.OTHER;
            model.getProperties().put(DataModelProperty.TYPE, type);
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
                    
                    if (className.endsWith("Plugin")) {
                        className = className.substring(0,className.lastIndexOf("Plugin"));
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
            ((ModelManagerListener) iter.next()).modelAdded(model);
        }
    }

    private void addModel(DataModel model, String label) {
        label = findUniqueLabel(label);
        model.getProperties().put(DataModelProperty.LABEL, label);
        //set the model to be unsaved initially
        model.getProperties().put(DataModelProperty.MODIFIED, new Boolean(true));
                
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


    public void removeModel(DataModel model) {
        String label = getLabelForModel(model);
        
        labelToModelMap.remove(label);
        modelToLabelMap.remove(model);
        models.remove(model);
        
        for (Iterator iter=listeners.iterator(); iter.hasNext();) {
            ((ModelManagerListener) iter.next()).modelRemoved(model);
        }
    }

    public DataModel[] getSelectedModels() {
        if (selectedModels == null) {
            selectedModels = new HashSet();
        }
        
        return (DataModel[]) selectedModels.toArray(new DataModel[]{});
    }

    public void setSelectedModels(DataModel[] models) {
        selectedModels = new HashSet(Arrays.asList(models));
        
        for (Iterator iter=listeners.iterator(); iter.hasNext();) {
            ((ModelManagerListener) iter.next()).modelsSelected(models);
        }
    }
    
    private DataModel getModelForLabel(String label){
        return (DataModel)labelToModelMap.get(label);
    }
    
    private String getLabelForModel(DataModel model){
        return (String)modelToLabelMap.get(model);
    }

    public DataModel[] getAllModels() {
        return (DataModel[]) models.toArray(new DataModel[]{});
    }

    public void addModelManagerListener(ModelManagerListener listener) {
        listeners.add(listener);
    }

    public void removeModelManagerListener(ModelManagerListener listener) {
        listeners.remove(listener);
    }
}

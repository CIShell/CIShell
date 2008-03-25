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
    private Map labelToNumOccurrences;
    private Set models;
    private Set selectedModels;
    
    private Set listeners;
    
    /**
     * Creates a new BasicModelManager Object.
     */
    public DataManagerServiceImpl() {
        modelToLabelMap = new HashMap();
        labelToModelMap = new HashMap();
        labelToNumOccurrences = new HashMap();
        models = new HashSet();
        listeners = new HashSet();
    }

    /**
     * @see edu.iu.iv.core.ModelManager#addData(java.lang.Object)
     */
    public void addData(Data model) {
    	if(model == null){
    		return;
    	}
        String label = (String)model.getMetadata().get(DataProperty.LABEL);
        String type = (String)model.getMetadata().get(DataProperty.TYPE);
        
        if(type == null){
            type = DataProperty.OTHER_TYPE;
            model.getMetadata().put(DataProperty.TYPE, type);
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
        model.getMetadata().put(DataProperty.LABEL, label);
        //set the model to be unsaved initially
        model.getMetadata().put(DataProperty.MODIFIED, new Boolean(true));
                
        modelToLabelMap.put(model, label);
        labelToModelMap.put(label, model);      
        models.add(model);
    }
    
    /**
     * Ensures that the label is unique by comparing it to the labels
     * currently in the data manager. If the label is unique, simply
     * return it. Otherwise add a numeric suffix indicating which
     * occurrence of the label it is (Starting at 2, going up).
     * @param label The label we are examining for uniqueness
     * @return A unique version of the provided label.
     */
    private String findUniqueLabel(String label) {
        
    	Integer numOccurences = (Integer) labelToNumOccurrences.get(label);
    	
    	if (numOccurences == null) {
    		//the label is unique
    		labelToNumOccurrences.put(label, new Integer(1));
    		return label;
    		
    	} else {
    		//the label is not unique
    		int numOccurrencesVal = numOccurences.intValue();
    		
    		int newNumOccurrencesVal = numOccurrencesVal + 1;
    		
    		String newLabel = label + "." + newNumOccurrencesVal;
    		
    		/*
    		 * In the rare case that someone sneaky sticks in a label that is
    		 *  identical to our newly generated 'unique' label, keep 
    		 *  incrementing the value of the suffix until it makes the new 
    		 *  label unique.
    		 */
    		while (getModelForLabel(newLabel) != null) {
    			newNumOccurrencesVal++;
    			newLabel = label + "." + newNumOccurrencesVal;
    		}
    		
    		/*
    		 * remember how many occurrences of the original label we have.
    		 */
    		labelToNumOccurrences.put(label,
    				new Integer(newNumOccurrencesVal));
    		
    		/*
    		 * also, remember that we now have a new label which might be
    		 * duplicated. For example, if we had whatever.xml and 
    		 * whatever.xml.2, if someone tried to add a new label
    		 * whatever.xml we would return whatever.xml.3, BUT if 
    		 * someone tried to add whatever.xml.2, we would return
    		 * whatever.xml.2.2 (the second version of the second version
    		 * of whatever.xml). Maybe not the best way to do this, but
    		 * it makes sense.
    		 */
    		labelToNumOccurrences.put(newLabel, new Integer(1));
    		
    		return newLabel;
    	}
    	
//    	int lastIndex = label.length() - 1;
//        boolean foundNumber = false;
//        
//        //set last index to the index before any trailing digits
//        while (lastIndex > 0 && Character.isDigit(label.charAt(lastIndex))) {
//            foundNumber = true;
//            lastIndex--;
//        }
//        
//        //set newLabel to the old label, minus the numbers at the end
//        String newLabel = label.substring(0,lastIndex + 1);
//        
//        //if the last character in the new label is blank, add a dot.
//        if (newLabel.charAt(newLabel.length()-1) != ' ') {
//            newLabel = newLabel + ".";
//        }
//        Integer newNumber;
//        Integer oldNumber = (Integer) substringToNumberMap.get(newLabel);
//        
//        if (oldNumber != null) {
//            newNumber = new Integer(oldNumber.intValue() + 1);
//        } else {
//            newNumber = new Integer(1);
//        }
//        
//        substringToNumberMap.put(newLabel,newNumber);
//        
//        if (foundNumber) {
//            int newNumVal = newNumber.intValue();
//            int numOnLabel = Integer.parseInt(label.substring(lastIndex+1));
//            
//            if (numOnLabel < newNumVal && getModelForLabel(newLabel + numOnLabel) == null) {
//                return newLabel + numOnLabel;
//            }
//        } 
//        
//        newLabel = newLabel + newNumber;
//        
//        return newLabel;
    }


    public void removeData(Data model) {
        String label = getLabel(model);
        
        labelToModelMap.remove(label);
        modelToLabelMap.remove(model);
        labelToNumOccurrences.remove(label);
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

    public void setSelectedData(Data[] inModels) {
        selectedModels = new HashSet(Arrays.asList(inModels));
        
        for (int i=0; i < inModels.length; i++) {
        	if (!this.models.contains(inModels[i])) {
        		addData(inModels[i]);
        	}
        }
        
        for (Iterator iter=listeners.iterator(); iter.hasNext();) {
            ((DataManagerListener) iter.next()).dataSelected(inModels);
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

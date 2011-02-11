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
import java.util.Map;
import java.util.Set;

import org.cishell.app.service.datamanager.DataManagerListener;
import org.cishell.app.service.datamanager.DataManagerService;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;


public class DataManagerServiceImpl implements DataManagerService {
    private Map<Data, String> datumToLabel = new HashMap<Data, String>();
    private Map<String, Data> labelToDatum = new HashMap<String, Data>(); 
    private Map<String, Integer> labelToOccurrenceCount = new HashMap<String, Integer>();
    private Set<Data> data = new HashSet<Data>();
    private Set<Data> selectedData = new HashSet<Data>();
    private Set<DataManagerListener> listeners = new HashSet<DataManagerListener>();

    public void addData(Data datum) {
    	if (datum == null) {
    		return;
    	}

        String label = (String) datum.getMetadata().get(DataProperty.LABEL);
        String type = (String) datum.getMetadata().get(DataProperty.TYPE);
        
        if (type == null) {
            type = DataProperty.OTHER_TYPE;
            datum.getMetadata().put(DataProperty.TYPE, type);
        }
        
        // Generate label if needed.
        if ((label == null) || "".equals(label)) {
            label = generateDefaultLabel(type);  
        }
        
        addModel(datum, label);

        for (DataManagerListener listener : this.listeners) {
            listener.dataAdded(datum, label);
        }
    }

    private void addModel(Data datum, String label) {
        label = findUniqueLabel(label);
        datum.getMetadata().put(DataProperty.LABEL, label);
        // Set the model to be unsaved initially.
        datum.getMetadata().put(DataProperty.MODIFIED, new Boolean(true));
                
        this.datumToLabel.put(datum, label);
        this.labelToDatum.put(label, datum);      
        this.data.add(datum);
    }

    private String generateDefaultLabel(String dataType) {
    	String label;
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
        
        return String.format("%s.%s", label, dataType);
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
        
    	Integer occurenceCount = this.labelToOccurrenceCount.get(label);
    	
    	if (occurenceCount == null) {
    		//the label is unique
    		this.labelToOccurrenceCount.put(label, new Integer(1));

    		return label;
    		
    	} else {
    		// The label is not unique.
    		int numOccurrencesVal = occurenceCount.intValue();
    		
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
    		this.labelToOccurrenceCount.put(label, new Integer(newNumOccurrencesVal));
    		
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
    		this.labelToOccurrenceCount.put(newLabel, new Integer(1));
    		
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


    public void removeData(Data datum) {
        String label = getLabel(datum);
        
        this.labelToDatum.remove(label);
        this.datumToLabel.remove(datum);
        this.labelToOccurrenceCount.remove(label);
        this.data.remove(datum);

        for (DataManagerListener listener : this.listeners) {
            listener.dataRemoved(datum);
        }
    }

    public Data[] getSelectedData() {
        return this.selectedData.toArray(new Data[0]);
    }

    public void setSelectedData(Data[] data) {
        this.selectedData.clear();
        this.selectedData.addAll(Arrays.asList(data));
        
        for (int ii = 0; ii < data.length; ii++) {
        	if (!this.data.contains(data[ii])) {
        		addData(data[ii]);
        	}
        }

        for (DataManagerListener listener : this.listeners) {
            listener.dataSelected(data);
        }
    }
    
    private Data getModelForLabel(String label) {
        return this.labelToDatum.get(label);
    }
    
    public String getLabel(Data datum) {
        return this.datumToLabel.get(datum);
    }
    
    public synchronized void setLabel(Data datum, String label) {
        String uniqueLabel = findUniqueLabel(label);
        this.datumToLabel.put(datum, uniqueLabel);
        this.labelToDatum.put(uniqueLabel, datum);  

        for (DataManagerListener listener : this.listeners) {
            listener.dataLabelChanged(datum, label);
        }
    }

    public Data[] getAllData() {
        return this.data.toArray(new Data[0]);
    }

    public void addDataManagerListener(DataManagerListener listener) {
        this.listeners.add(listener);
    }

    public void removeDataManagerListener(DataManagerListener listener) {
        this.listeners.remove(listener);
    }
}

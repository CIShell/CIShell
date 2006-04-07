/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Jan 12, 2005 at Indiana University.
 */
package edu.iu.iv.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.iu.iv.core.ModelManager;
import edu.iu.iv.core.datamodels.DataModel;
import edu.iu.iv.core.datamodels.DataModelProperty;
import edu.iu.iv.core.datamodels.DataModelType;

/**
 * 
 * @author Team IVC 
 */
//Created by: Bruce Herr
public class BasicModelManager implements ModelManager {
	private Map modelToLabelMap;
	private Map labelToModelMap; 
	private Map substringToNumberMap;
	private Set models;
	private Set selectedModels;
	
	/**
	 * Creates a new BasicModelManager Object.
	 */
	public BasicModelManager() {
		modelToLabelMap = new HashMap();
		labelToModelMap = new HashMap();
		substringToNumberMap = new HashMap();
		models = new HashSet();
	}

	/**
	 * @see edu.iu.iv.core.ModelManager#addModel(java.lang.Object)
	 */
	public void addModel(DataModel model) {
	    String label = (String)model.getProperties().getPropertyValue(DataModelProperty.LABEL);
	    DataModelType type = (DataModelType)model.getProperties().getPropertyValue(DataModelProperty.TYPE);
	    
	    if(type == null){
	        type = DataModelType.OTHER;
	        model.getProperties().setPropertyValue(DataModelProperty.TYPE, type);
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
			
			label = label + "." + type.getName();			
	    }
	    
	    addModel(model, label);
	}

	private void addModel(DataModel model, String label) {
		label = findUniqueLabel(label);
		model.getProperties().setPropertyValue(DataModelProperty.LABEL, label);
		//set the model to be unsaved initially
		model.getProperties().setPropertyValue(DataModelProperty.MODIFIED, new Boolean(true));
				
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


	/**
	 * @see edu.iu.iv.core.ModelManager#removeModel(java.lang.Object)
	 */
	public void removeModel(DataModel model) {
		String label = getLabelForModel(model);
		
		labelToModelMap.remove(label);
		modelToLabelMap.remove(model);
		models.remove(model);
	}

	/**
	 * @see edu.iu.iv.core.ModelManager#getSelectedModels()
	 */
	public Set getSelectedModels() {
		if (selectedModels == null) {
			selectedModels = new HashSet();
		}
		
		return selectedModels;
	}

	/**
	 * @see edu.iu.iv.core.ModelManager#setSelectedModels(java.util.List)
	 */
	public void setSelectedModels(Set models) {
		selectedModels = models;
	}
	
	
	private DataModel getModelForLabel(String label){
	    return (DataModel)labelToModelMap.get(label);
	}
	
	private String getLabelForModel(DataModel model){
	    return (String)modelToLabelMap.get(model);
	}

	/**
	 * @see edu.iu.iv.core.ModelManager#getModels()
	 */
    public Set getModels() {
        return models;
    }
    
}

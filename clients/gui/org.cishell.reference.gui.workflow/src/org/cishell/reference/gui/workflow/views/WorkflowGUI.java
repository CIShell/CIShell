/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Feb 19, 2005 at Indiana University.
 */
package org.cishell.reference.gui.workflow.views;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cishell.framework.data.DataProperty;
import org.cishell.reference.gui.workflow.Utilities.Constant;
import org.cishell.reference.gui.workflow.model.Workflow;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * DataModelGUIItem is a wrapper of a DataModel which is used by the
 * DataModelTreeView to hold the items in the TreeView. It adds to the
 * DataModel the notion of having parent and children DataModelTreeItems
 * and keeps track of this information for usage by the TreeView.
 *
 * @author Team IVC
 */
public class WorkflowGUI implements WorkflowTreeItem {
	
    private Image algorithmIcon;
    private Image parametersIcon;
    private Image workflowIcon;
    private Image workflow_mgrIcon;
    private Image unknownIcon;
    private Workflow workflow;
    
    
	private Map<String, Image> typeToImage = new HashMap<String, Image>();
    //for root
    private Collection<WorkflowGUI> children = new ArrayList<WorkflowGUI>();
    private Collection<AlgorithmItemGUI> workFlowItemChildren = new ArrayList<AlgorithmItemGUI>();

    private String label;
    private WorkflowGUI parent;


    /**
     * Creates a new DataModelGUIItem object.
     *
     * @param model the DataModel this DataModelGUIItem is using
     * @param parent the parent DataModelGUIItem of this DataModelGUIItem
     * @param brandPluginID 
     */
    
    public WorkflowGUI(Workflow workflow, WorkflowGUI parent,int type, String brandPluginID) {
    	if(workflow !=null)
    	    label = workflow.getName();
    	this.workflow = workflow;
         algorithmIcon     = getImage("algorithm.png", brandPluginID);
         parametersIcon    = getImage("parameters.png", brandPluginID);
         workflowIcon      = getImage("workflow.png", brandPluginID);
         workflow_mgrIcon  = getImage("workflow_mgr.png", brandPluginID);
         unknownIcon       = getImage("unknown.png", brandPluginID);
         registerImage(DataProperty.MODEL_TYPE, algorithmIcon);
         registerImage(DataProperty.TABLE_TYPE, parametersIcon);
         registerImage(DataProperty.TREE_TYPE, workflowIcon);
         registerImage(DataProperty.OTHER_TYPE, workflow_mgrIcon);
         registerImage(DataProperty.OTHER_TYPE, unknownIcon);
    }
    

    public String getLabel() {
		return label;
	}

	public void setAlgorithmLabel(String algorithmLabel) {
		this.label = algorithmLabel;
	}

 
    /**
     * Returns the parent DataModelGUIItem of this DataModelGUIItem, or
     * null if this is the root item.
     *
     * @return the parent DataModelGUIItem of this DataModelGUIItem, or
     * null if this is the root item
     */
    public WorkflowTreeItem getParent() {
        return parent;
    }

    /**
     * Adds the given DataModelGUIItem as a child of this DataModelGUIItem
     *
     * @param item the new child of this DataModelGUIItem
     */
    public void addChild(WorkflowGUI item) {
        this.children.add(item);
    }

    /**
     * Returns an array of all of the children of this DataModelGUIItem
     *
     * @return an array of all of the children of this DataModelGUIItem
     */
    public Object[] getChildren() {
        return this.workFlowItemChildren.toArray();
    }
    
    public Object[] getRootsChildren() {
        return this.children.toArray();
    }
    
    public void addChild(AlgorithmItemGUI item) {
        this.workFlowItemChildren.add(item);
    }
    /**
     * Removes the given DataModelGUIItem from the collection of children
     * of this DataModelGUIItem.
     *
     * @param item the child of this DataModelGUIItem to remove
     */
    public void removeChild(WorkflowGUI item) {
        this.children.remove(item);
    }
    
    
    public Image getIcon() {
        return this.workflowIcon;
    }
    
    
    public void registerImage(String type, Image image) {
        this.typeToImage.put(type, image);
    }

    public static Image getImage(String name, String brandPluginID) {
        if (Platform.isRunning()) {
        	String imageLocation =
        		String.format("%sicons%s%s", File.separator, File.separator, name);
            ImageDescriptor imageDescriptor =
            	AbstractUIPlugin.imageDescriptorFromPlugin(brandPluginID, imageLocation);

            if (imageDescriptor != null) {
            	return imageDescriptor.createImage(); 
            } else {
            	String errorMessage = String.format(
            		"Could not find the icon '%s' in '%s'. Using the default image instead.",
            		imageLocation,
            		brandPluginID);
            	System.err.println(errorMessage);

            	return getDefaultImage();
            }
   
        } else {
        	String format =
        		"Could not obtain the image '%s' in '%s', since the platform was not " +
        		"running (?). Using the default image instead.";
        	String errorMessage = String.format(format, name, brandPluginID);
        	System.err.println(errorMessage);

        	return getDefaultImage();
        }            
    }
    
    private static final String DEFAULT_IMAGE_LOCATION = File.separator + "unknown.png";
    
    private static Image getDefaultImage() {
    	ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(null, DEFAULT_IMAGE_LOCATION);
    	return imageDescriptor.createImage();
    }

	@Override
	public String getType() {
		return Constant.Workflow;
	}
	
	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public void setLabel(String label) {
        this.label = label;		
	}

	@Override
	public void removeAllChildren() {
		workFlowItemChildren.clear();
	}


	@Override
	public boolean hasChild(WorkflowTreeItem wfTreeItem) {
		return workFlowItemChildren.contains(wfTreeItem);
	}


	@Override
	public void removeChild(WorkflowTreeItem wfTreeItem) {
		workFlowItemChildren.remove(wfTreeItem);
		
	}


	@Override
	public void addChild(WorkflowTreeItem wfTreeItem) {
		if(wfTreeItem instanceof AlgorithmItemGUI )
			this.workFlowItemChildren.add((AlgorithmItemGUI)wfTreeItem);
		
	}
	
}

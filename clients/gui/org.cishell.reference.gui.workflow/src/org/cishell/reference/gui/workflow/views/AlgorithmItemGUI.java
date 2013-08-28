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
import org.cishell.reference.gui.workflow.model.WorkflowItem;
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
public class AlgorithmItemGUI implements WorkflowTreeItem {
	private String brandPluginID;
	
    //images for the defined types
//    private Image matrixIcon;
    private Image treeIcon;
    private Image networkIcon;
    private Image unknownIcon;
    private Image textIcon;
    private Image plotIcon;
    private Image tableIcon;
    private Image databaseIcon;
    private Image rasterImageIcon;
    private Image vectorImageIcon;
    private Image modelIcon;
    private Image rIcon;
    
    private Map<String, Image> typeToImage = new HashMap<String, Image>();
    private Collection<WorkflowTreeItem> children = new ArrayList<WorkflowTreeItem>();
    //private Algorithm algorithm;
    private WorkflowItem wfItem;
    private String label;
    private WorkflowTreeItem parent;


    /**
     * Creates a new AlgorithmItemGUI object.
     *
     * @param wfItem the DataModel this AlgorithmItemGUI is using
     * @param parent the parent WorkflowTreeItem of this DataModelGUIItem
     */
    public AlgorithmItemGUI(WorkflowItem wfItem, WorkflowTreeItem parent) {
    	
        this.wfItem = wfItem;
        label =     wfItem.getName();
        this.parent = parent;
    	
        this.brandPluginID = "org.cishell.reference.gui.workflow";
//        matrixIcon      = getImage("matrix.png", this.brandPluginID);
        modelIcon        = getImage("model.jpg", this.brandPluginID);
        registerImage(DataProperty.OTHER_TYPE, unknownIcon);
        
        /********************************************
         * This is a temporary work around solution.
         * Since many algs claims the output data type is MATRIX_TYPE,
         * but in fact it should be TABLE_TYPE. 
         * Should associate MATRIX_TYPE with matrixIcon and clean up
         * related algs.
         * ******************************************/      
        registerImage(DataProperty.MODEL_TYPE, modelIcon);
    }    

    public String getLabel() {
		return label;
	}

	public void setAlgorithmLabel(String algorithmLabel) {
		this.label = algorithmLabel;
	}

 
    /**
     * Returns the parent WorkflowTreeItem of this AlgorithmItemGUI, or
     * null if this is the root item.
     *
     * @return the parent WorkflowTreeItem of this AlgorithmItemGUI, or
     * null if this is the root item
     */
    public WorkflowTreeItem getParent() {
        return parent;
    }

    /**
     * Adds the given WorkflowTreeItem as a child of this AlgorithmItemGUI
     *
     * @param item the new child of this AlgorithmItemGUI
     */
    public void addChild(WorkflowTreeItem item) {
        this.children.add(item);
    }

    /**
     * Returns an array of all of the children of this AlgorithmItemGUI
     *
     * @return an array of all of the children of this AlgorithmItemGUI
     */
    public Object[] getChildren() {
        return this.children.toArray();
    }

    /**
     * Removes the given AlgorithmItemGUI from the collection of children
     * of this AlgorithmItemGUI.
     *
     * @param item the child of this AlgorithmItemGUI to remove
     */
    public void removeChild(AlgorithmItemGUI item) {
        this.children.remove(item);
    }
    
    
    public Image getIcon() {
     
        return this.modelIcon;
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
    
    public static Image getDefaultImage() {
    	String thisPluginID = "org.cishell.reference.gui.workflow"; //TODO: don't hardcode this
    	ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
    			thisPluginID, DEFAULT_IMAGE_LOCATION);
    	return imageDescriptor.createImage();
    }

	@Override
	public String getType() {
		return Constant.AlgorithmUIItem;
	}

	@Override
	public void removeAllChildren() {
		children.clear();
	}

	@Override
	public boolean hasChild(WorkflowTreeItem wfTreeItem) {
		return children.contains(wfTreeItem);
	}

	@Override
	public void removeChild(WorkflowTreeItem wfTreeItem) {
		children.remove(wfTreeItem);
		
	}

	public WorkflowItem getWfItem() {
		return wfItem;
	}

	@Override
	public void setLabel(String label) {
      this.label = label;		
	}

	
}

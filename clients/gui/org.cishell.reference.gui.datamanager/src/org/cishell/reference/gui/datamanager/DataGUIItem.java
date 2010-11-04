/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Feb 19, 2005 at Indiana University.
 */
package org.cishell.reference.gui.datamanager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
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
public class DataGUIItem {
    
	private String brandPluginID;
	
    //images for the defined types
    private Image matrixIcon;
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
    
    private Map typeToImageMapping;
    
    private List children;
    private Data data;
    private DataGUIItem parent;


    /**
     * Creates a new DataModelGUIItem object.
     *
     * @param model the DataModel this DataModelGUIItem is using
     * @param parent the parent DataModelGUIItem of this DataModelGUIItem
     */
    public DataGUIItem(Data data, DataGUIItem parent, String brandPluginID) {
        this.data = data;
        this.parent = parent;
        children = new ArrayList();
        
        this.brandPluginID = brandPluginID;
        matrixIcon      = getImage("matrix.png", this.brandPluginID);
        treeIcon        = getImage("tree.png", this.brandPluginID);
        networkIcon     = getImage("network.png", this.brandPluginID);
        unknownIcon     = getImage("unknown.png", this.brandPluginID);
        textIcon 	    = getImage("text.png", this.brandPluginID);
        plotIcon 	    = getImage("plot.png", this.brandPluginID);
        tableIcon 	    = getImage("table.png", this.brandPluginID);
        databaseIcon    = getImage("database.jpg", this.brandPluginID);
        rasterImageIcon = getImage("raster_image.jpg", this.brandPluginID);
        vectorImageIcon = getImage("vector_image.jpg", this.brandPluginID);
        modelIcon       = getImage("model.jpg", this.brandPluginID);
        rIcon = getImage("r.png", this.brandPluginID);

        typeToImageMapping = new HashMap();
        registerImage(DataProperty.OTHER_TYPE, unknownIcon);
        
        /********************************************
         * This is a temporary work around solution.
         * Since many algs claims the output data type is MATRIX_TYPE,
         * but in fact it should be TABLE_TYPE. 
         * Should associate MATRIX_TYPE with matrixIcon and clean up
         * related algs.
         * ******************************************/      
        registerImage(DataProperty.MATRIX_TYPE, tableIcon);
        registerImage(DataProperty.NETWORK_TYPE, networkIcon);
        registerImage(DataProperty.TREE_TYPE, treeIcon);
        registerImage(DataProperty.TEXT_TYPE, textIcon);
        registerImage(DataProperty.PLOT_TYPE, plotIcon);
        registerImage(DataProperty.TABLE_TYPE, tableIcon);
        registerImage(DataProperty.DATABASE_TYPE, databaseIcon);
        registerImage(DataProperty.RASTER_IMAGE_TYPE, rasterImageIcon);
        registerImage(DataProperty.VECTOR_IMAGE_TYPE, vectorImageIcon);
        registerImage(DataProperty.MODEL_TYPE, modelIcon);
        registerImage(DataProperty.R_INSTANCE_TYPE, rIcon);
    }

    /**
     * Returns the DataModel used by this DataModelGUIItem
     *
     * @return the DataModel used by this DataModelGUIItem
     */
    public Data getModel() {
        return data;
    }   

    /**
     * Returns the parent DataModelGUIItem of this DataModelGUIItem, or
     * null if this is the root item.
     *
     * @return the parent DataModelGUIItem of this DataModelGUIItem, or
     * null if this is the root item
     */
    public DataGUIItem getParent() {
        return parent;
    }

    /**
     * Adds the given DataModelGUIItem as a child of this DataModelGUIItem
     *
     * @param item the new child of this DataModelGUIItem
     */
    public void addChild(DataGUIItem item) {
        children.add(item);
    }

    /**
     * Returns an array of all of the children of this DataModelGUIItem
     *
     * @return an array of all of the children of this DataModelGUIItem
     */
    public Object[] getChildren() {
        return children.toArray();
    }

    /**
     * Removes the given DataModelGUIItem from the collection of children
     * of this DataModelGUIItem.
     *
     * @param item the child of this DataModelGUIItem to remove
     */
    public void removeChild(DataGUIItem item) {
        children.remove(item);
    }
    
    /**
     * Returns the icon associated with this DataModel for display in IVC.
     * 
     * @return the icon associated with this DataModel for display in IVC
     */
    public Image getIcon(){
        Image icon = (Image)typeToImageMapping.get(data.getMetadata().get(DataProperty.TYPE));
        if(icon == null) icon = unknownIcon;
        return icon;
    }
    
    public void registerImage(String type, Image image){
        typeToImageMapping.put(type, image);
    }
    
   
    
    public static Image getImage(String name, String brandPluginID){
        if(Platform.isRunning()) {
        	String imageLocation =  File.separator + "icons" + File.separator + name;
            ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
            		brandPluginID, 
            	    imageLocation);
            if (imageDescriptor != null) {
            	return imageDescriptor.createImage(); 
            } else {
            	System.err.println("Could not find the icon " +
            			"'" + imageLocation + "'" +
            			"in" +
            			"'" + brandPluginID + "'." +
            			"Using the default image instead.");
            	return getDefaultImage();
            }
   
        }
        else {
        	System.err.println("Could not obtain the image " +
        			"'" + name + "'" +
        			"in" +
        			"'" + brandPluginID + "'" +
        			", since the platform was not running (?)." +
        			"Using the default image instead.");
        	return getDefaultImage();
        }            
    }
    
    private static final String DEFAULT_IMAGE_LOCATION = File.separator + "unknown.png";
    
    private static Image getDefaultImage() {
    	String thisPluginID = "org.cishell.reference.gui.datamanager"; //TODO: don't hardcode this
    	ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
    			thisPluginID, DEFAULT_IMAGE_LOCATION);
    	return imageDescriptor.createImage();
    }
}

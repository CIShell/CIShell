/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Feb 19, 2005 at Indiana University.
 */
package edu.iu.iv.datamodelview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;

import edu.iu.iv.core.datamodels.DataModel;
import edu.iu.iv.core.datamodels.DataModelProperty;
import edu.iu.iv.core.datamodels.DataModelType;
import edu.iu.iv.gui.IVCImageLoader;


/**
 * DataModelGUIItem is a wrapper of a DataModel which is used by the
 * DataModelTreeView to hold the items in the TreeView. It adds to the
 * DataModel the notion of having parent and children DataModelTreeItems
 * and keeps track of this information for usage by the TreeView.
 *
 * @author Team IVC
 */
public class DataModelGUIItem {
    
    //images for the defined types
    private static final Image matrixIcon = IVCImageLoader.createImage("table.png");
    private static final Image treeIcon = IVCImageLoader.createImage("tree.png");
    private static final Image networkIcon = IVCImageLoader.createImage("network.png");
    private static final Image unknownIcon= IVCImageLoader.createImage("unknown.png");
    
    private static Map typeToImageMapping;
    
    static {
        typeToImageMapping = new HashMap();
        registerImage(DataModelType.OTHER, unknownIcon);
        registerImage(DataModelType.MATRIX, matrixIcon);
        registerImage(DataModelType.NETWORK, networkIcon);
        registerImage(DataModelType.TREE, treeIcon);
    }
    
    private List children;
    private DataModel model;
    private DataModelGUIItem parent;


    /**
     * Creates a new DataModelGUIItem object.
     *
     * @param model the DataModel this DataModelGUIItem is using
     * @param parent the parent DataModelGUIItem of this DataModelGUIItem
     */
    public DataModelGUIItem(DataModel model, DataModelGUIItem parent) {
        this.model = model;
        this.parent = parent;
        children = new ArrayList();
    }

    /**
     * Returns the DataModel used by this DataModelGUIItem
     *
     * @return the DataModel used by this DataModelGUIItem
     */
    public DataModel getModel() {
        return model;
    }   

    /**
     * Returns the parent DataModelGUIItem of this DataModelGUIItem, or
     * null if this is the root item.
     *
     * @return the parent DataModelGUIItem of this DataModelGUIItem, or
     * null if this is the root item
     */
    public DataModelGUIItem getParent() {
        return parent;
    }

    /**
     * Adds the given DataModelGUIItem as a child of this DataModelGUIItem
     *
     * @param item the new child of this DataModelGUIItem
     */
    public void addChild(DataModelGUIItem item) {
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
    public void removeChild(DataModelGUIItem item) {
        children.remove(item);
    }
    
    /**
     * Returns the icon associated with this DataModel for display in IVC.
     * 
     * @return the icon associated with this DataModel for display in IVC
     */
    public Image getIcon(){
        Image icon = (Image)typeToImageMapping.get((DataModelType)model.getProperties().getPropertyValue(DataModelProperty.TYPE));
        if(icon == null) icon = unknownIcon;
        return icon;
    }
    
    public static void registerImage(DataModelType type, Image image){
        typeToImageMapping.put(type, image);
    }    
}

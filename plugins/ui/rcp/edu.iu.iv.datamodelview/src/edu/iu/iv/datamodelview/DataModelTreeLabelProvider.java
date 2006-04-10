/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Feb 19, 2005 at Indiana University.
 */
package edu.iu.iv.datamodelview;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import edu.iu.iv.core.datamodels.DataModel;
import edu.iu.iv.core.datamodels.DataModelProperty;


/**
 * LabelProvider for the DataModelTreeView used to represent DataModels in IVC.
 *
 * @author Team IVC
 */
public class DataModelTreeLabelProvider extends LabelProvider {
    
    /**
     * Returns the Image associated with the given element that should be
     * displayed in the Tree.
     *
     * @param element the element in the DataModelTreeView for which to
     * return the associated Image.
     *
     * @return the Image associated with the given element that should be
     * displayed in the Tree
     */
    public Image getImage(Object element) {
        if (element instanceof DataModelGUIItem) {
            return ((DataModelGUIItem) element).getIcon();            
        }

        return null;
    }

    /**
     * Returns the text to display for the given DataModelTreeView element. This
     * is the label of the DataModel which the element represents.
     *
     * @param element the element in the DataModelTreeView to find the text
     * to display.
     *
     * @return the text to display for the given DataModelTreeView element
     */
    public String getText(Object element) {
        if (element instanceof DataModelGUIItem) {
            DataModel model = ((DataModelGUIItem) element).getModel();
            String label = (String) model.getProperties().getPropertyValue(DataModelProperty.LABEL);
            Boolean modified = (Boolean)model.getProperties().getPropertyValue(DataModelProperty.MODIFIED);
            if(modified != null && modified.booleanValue()){
                label = ">" + label;
            }

            return label;
        }

        return null;
    }
}

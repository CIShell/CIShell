/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Feb 19, 2005 at Indiana University.
 */
package org.cishell.reference.gui.datamanager;

import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


/**
 * LabelProvider for the DataModelTreeView used to represent DataModels in IVC.
 *
 * @author Team IVC
 */
public class DataTreeLabelProvider extends LabelProvider {
    
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
        if (element instanceof DataGUIItem) {
            return ((DataGUIItem) element).getIcon();            
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
        if (element instanceof DataGUIItem) {
            Data model = ((DataGUIItem) element).getModel();
            String label = (String) model.getMetadata().get(DataProperty.LABEL);
           // Boolean modified = (Boolean)model.getMetadata().get(DataProperty.MODIFIED);
           /* if(modified != null && modified.booleanValue()){
                label = ">" + label;
            }*/

            return label;
        }

        return null;
    }
}

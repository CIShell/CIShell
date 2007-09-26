/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Feb 19, 2005 at Indiana University.
 */
package org.cishell.reference.gui.datamanager;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * ContentProvider for the DataModel TreeView.  This class is used to form
 * the structure of the tree that is displayed based on the relationships between
 * the DataModelTreeItems within it.
 *
 * @author Team IVC
 */
public class DataTreeContentProvider implements ITreeContentProvider {
    private static final Object[] EMPTY_ARRAY = new Object[0];

    /**
     * Returns the children of the given TreeView element.  This parentElement
     * should be a DataModelGUIItem
     *
     * @param parentElement the TreeView element to find the children of
     *
     * @return Returns the children of the given TreeView element
     */
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof DataGUIItem) {
            return ((DataGUIItem) parentElement).getChildren();
        }

        return EMPTY_ARRAY;
    }

    /**
     * Gets the parent of the given TreeView element. This element should be
     * a DataModelGUIItem
     *
     * @param element the element to find the parent of
     *
     * @return the parent of the given TreeView element
     */
    public Object getParent(Object element) {
        if (element instanceof DataGUIItem) {
            return ((DataGUIItem) element).getParent();
        }

        return null;
    }

    /**
     * Determines whether or not the given TreeView element has any children.
     *
     * @param element the TreeView element to find the children for.
     *
     * @return true if the given element has children, false if not.
     */
    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

    /**
     * Returns an array of the elements to display in the TreeViewer
     *
     * @param inputElement the root element of the TreeViewer
     *
     * @return an array of the elements to display in the TreeViewer
     */
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    /**
     * Does nothing.
     */
    public void dispose() {}

    /**
     * Does nothing. 
     * 
     * This method could be used to notify this content provider that 
     * the given viewer's input has been switched to a different element.
     *
     * @param viewer the viewer
     * @param oldInput the old input element, or <code>null</code> if the viewer
     *   did not previously have an input
     * @param newInput the new input element, or <code>null</code> if the viewer
     *   does not have an input
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
}

/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Feb 19, 2005 at Indiana University.
 */
package edu.iu.iv.datamodelview;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.part.ViewPart;

import edu.iu.iv.common.property.PropertyMap;
import edu.iu.iv.core.AddModelListener;
import edu.iu.iv.core.IVC;
import edu.iu.iv.core.ModelManager;
import edu.iu.iv.core.datamodels.DataModel;
import edu.iu.iv.core.datamodels.DataModelProperty;


/**
 * DataModelTreeView provides a Tree representation of the DataModels loaded into
 * IVC.  This hierarchical relationship is based upon the parent-child relationships
 * of those DataModels, as specified by their DataModelProperties.
 * 
 * This View is an AddModelListener, so it gets notified of changes in the
 * ModelManager used by IVC.  It uses a jface TreeViewer with a SWT Tree
 * control to display the View.
 *
 * @author Team IVC
 */
public class DataModelTreeView extends ViewPart implements AddModelListener, IStartup {
    public static final String ID_VIEW = "edu.iu.iv.datamodelview.DataModelTreeView";
    public static DataModelTreeView defaultView;
    private TreeViewer viewer;
    private TreeEditor editor;
    private Text newEditor;

    //flag to notify if a tree item is currently being updated so there
    //isnt a conflict among various listeners
    private boolean updatingTreeItem;
    private Tree tree;
    private Menu menu;
    private Map modelToDataModelGUIItemMap;
    private DataModelGUIItem rootItem;
    private ModelManager manager;
    private DiscardListener discardListener;

    //boolean to tell whether refresh has been run at the beginning
    private static boolean ranOnce = false;
    
    /**
     * Creates a new DataModelTreeView object.
     */
    public DataModelTreeView() {
        if(defaultView == null)
            defaultView = this;
        updatingTreeItem = false;
        modelToDataModelGUIItemMap = new HashMap();
        manager = IVC.getInstance().getModelManager();
    }

    /**
     * Returns the default instance of DataModelTreeView
     *
     * @return the default instance of DataModelTreeView
     */
    public static DataModelTreeView getDefault() {
        if (!ranOnce) {
            //Waits half a second then notifies all of the 
            //plugins of its starting status of no selections
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    ranOnce = true;
                    defaultView.refresh();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {}
                    
                    defaultView.refresh();
                }});
        }
        
        return defaultView;
    }

    /**
     * Returns the context Menu used by this DataModelTreeView
     *
     * @return the context Menu used by this DataModelTreeView
     */
    public Menu getContextMenu() {
        return menu;
    }
    
    /**
     * Returns the Listener used by the discard section of the context menu,
     * allowing possible overriding of this functionality by plugins.  The core
     * core persistence plugin will override this listener so that data models
     * are saved before discarding upon the users request.
     * 
     * @return the Listener used by the discard section of the context menu
     */
    public Listener getDiscardListener(){
        return discardListener;
    }
    
    public void remove(DataModel model){
        DataModelGUIItem item = (DataModelGUIItem)modelToDataModelGUIItemMap.get(model);
	    DataModelGUIItem parent = item.getParent();
		
        if (parent != null) {
            parent.removeChild(item);
            //add this nodes children to its parent
            Object[] children = item.getChildren();
            for(int i = 0; i < children.length; i++){
                parent.addChild((DataModelGUIItem)children[i]);
            }
        }
		                
		modelToDataModelGUIItemMap.remove(item.getModel());
		Set selection = IVC.getInstance().getModelManager().getSelectedModels();
		selection.remove(model);
		IVC.getInstance().getModelManager().setSelectedModels(selection);
		viewer.refresh();
		viewer.expandAll();
    }
    
    /**
     * Refreshes this DataModelTreeView, causing it to reset its selection
     * based on the currently selected models in IVC's model manager and
     * refresh the tree based on the models that are currently in existence.   
     */
    public void refresh(){
        Set selection = manager.getSelectedModels();
        DataModel[] items = new DataModel[selection.size()];
        Iterator iterator = selection.iterator();
        int i = 0;
        while(iterator.hasNext()){
            Object next = iterator.next();            
            items[i++] = (DataModel)next;            
        }
        
        getSite().getSelectionProvider().setSelection(new StructuredSelection(items));
        viewer.refresh();        
    }

    /**
     * Creates the control for this View.  This View uses a TreeViewer control with
     * a Tree control to show the Data Models in IVC.
     *
     * @param parent the parent Composite of this View, in which the control for this
     * View is to be made
     */
    public void createPartControl(Composite parent) {
        //create the TreeViewer
        viewer = new TreeViewer(parent);
        viewer.setContentProvider(new DataModelTreeContentProvider());
        viewer.setLabelProvider(new DataModelTreeLabelProvider());
        rootItem = new DataModelGUIItem(null, null);
        viewer.setInput(rootItem);
        viewer.expandAll();
        
        //grab the tree and add the appropriate listeners
        tree = viewer.getTree();
        tree.addSelectionListener(new DatamodelSelectionListener());
        tree.addMouseListener(new ContextMenuListener());

        //setup the context menu for the tree
        menu = new Menu(tree);
        menu.setVisible(false);

        MenuItem saveItem = new MenuItem(menu, SWT.PUSH);
        saveItem.setText("Save");
        saveItem.setEnabled(false);

        MenuItem renameItem = new MenuItem(menu, SWT.PUSH);
        renameItem.setText("Rename");
        renameItem.addListener(SWT.Selection,
            new Listener() {
                public void handleEvent(Event event) {
                    handleInput();
                }
            });

        MenuItem discardItem = new MenuItem(menu, SWT.PUSH);
        discardItem.setText("Discard");
        discardListener = new DiscardListener();
        discardItem.addListener(SWT.Selection, discardListener);
        tree.setMenu(menu);

        //allow cells to be edited on double click or when pressing enter on them
        editor = new TreeEditor(tree);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;
        editor.minimumWidth = 50;
        TreeItemEditorListener editorListener = new TreeItemEditorListener(editor);
        tree.addMouseListener(editorListener);
        tree.addKeyListener(editorListener);

        //listen to IVC for models being added by plugins
        IVC.getInstance().addAddModelListener(this);
                
        getSite().setSelectionProvider(new DataModelSelectionProvider());
        
    }

    /**
     * Sets the Focus of this View, for DataModelTreeView, this gives the TreeViewer
     * focus.
     */
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    /**
     * Notifies this DataModelTreeView that a model has been added to IVC
     * (see AddModelListener).  This method handles the job of adding this new
     * model to the gui and updating the selection appropriately.
     *
     * @param model the DataModel that was added to IVC and needs to be added to the
     * TreeView
     */
    public void addModel(final DataModel model) {
        //get the data from the model needed to setup the DataModelGUIItem
        PropertyMap modelProperties = model.getProperties();
        
        DataModel parent = (DataModel) modelProperties.getPropertyValue(DataModelProperty.PARENT);       
        DataModelGUIItem parentItem;        
        if (parent == null) {
            //if it has no parent, it is a child of the root
            parentItem = rootItem;
        } else {
            //otherwise find the associated DataModelGUIItem for the parent
            parentItem = (DataModelGUIItem) modelToDataModelGUIItemMap.get(parent);
        }

        //create the new DataModelGUIItem
        final DataModelGUIItem item = new DataModelGUIItem(model, parentItem);
        //notify the parent DataModelGUIItem of its new child
        parentItem.addChild(item);
        //keep a reference to the new model in the model->TreeItem mapping so that
        //it can be used in the future if it has a child
        modelToDataModelGUIItemMap.put(model, item);

        //update the ModelManager with the new selection
        Set selection = new HashSet();
        selection.add(model);
        manager.setSelectedModels(selection);
        
        Display.getDefault().syncExec(new Runnable(){
            public void run(){
                if(!tree.isDisposed()){
	                //update the TreeView
	                viewer.refresh();	                
	                //context menu may need to have options enabled/disabled based on the new selection
	                updateContextMenu(model);
	                //update the global selection
	                viewer.expandToLevel(item, 0);
	                selectItem(item, tree.getItems());
	                getSite().getSelectionProvider().setSelection(new StructuredSelection(new DataModel[]{model}));
	                setFocus();
                }
            }
        });
    }

    private void selectItem(DataModelGUIItem item, TreeItem[] items){
        for(int i = 0; i < items.length; i++){
            TreeItem treeItem = items[i];
            if(treeItem.getData() == item){
                treeItem.getParent().setSelection(new TreeItem[]{treeItem});
                return;
            }
            selectItem(item, treeItem.getItems());
        }
    }
    
    /*
     * enables/disables save item in the context menu based on whether or not
     * their is an available Persister for the given model
     */
    private void updateContextMenu(DataModel model) {
        //reset the enablement of the save context menu appropriately
        boolean saveEnabled = (model.getData() != null) &&
            !(IVC.getInstance().getPersistenceRegistry()
                 .getSupportingPersisters(model.getData()).isEmpty());
        menu.getItem(0).setEnabled(saveEnabled);
    }

    /*
     * allows for direct text editing of the label of a DataModelGUIItem by
     * using a Text control associated with a TreeEditor. This allows for renaming
     * of data models without a pop-up dialog or anything
     */
    private void handleInput() {
        // Clean up any previous editor control
        Control oldEditor = editor.getEditor();

        if (oldEditor != null) {
            oldEditor.dispose();
        }

        // Identify the selected row, only allow input if there is a single selected row
        TreeItem[] selection = tree.getSelection();

        if (selection.length != 1) {
            return;
        }

        final TreeItem item = selection[0];

        if (item == null) {
            return;
        }

        // The control that will be the editor must be a child of the Table
        newEditor = new Text(tree, SWT.NONE);
        newEditor.setText(item.getText());
        newEditor.addFocusListener(new FocusAdapter() {
                public void focusLost(FocusEvent e) {
                    if (!updatingTreeItem) {
                        updateText(newEditor.getText(), item);
                    }
                }
            });

        // ENTER ESC
        newEditor.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                    if ((e.character == SWT.CR) && !updatingTreeItem) {
                        updateText(newEditor.getText(), item);
                    } else if (e.keyCode == SWT.ESC) {
                        newEditor.dispose();
                    }
                }
            });
        newEditor.selectAll();
        newEditor.setFocus();
        editor.setEditor(newEditor, item);
    }

    /*
     * updates the actual Label property of the DataModel that is being
     * edited by the TreeEditor for renaming - only if the new name is valid though
     */
    private void updateText(String newLabel, TreeItem item) {
        updatingTreeItem = true;

        if(newLabel.startsWith(">"))
            newLabel = newLabel.substring(1);
        
        if (isValid(newLabel)) {
            editor.getItem().setText(newLabel);

            DataModelGUIItem treeItem = (DataModelGUIItem) item.getData();
            DataModel model = treeItem.getModel();
            model.getProperties().setPropertyValue(DataModelProperty.LABEL, newLabel);
            viewer.refresh();
            newEditor.dispose();
        } else {
            String message = "Invalid data model name. The following characters" +
                " are not allowed:\n\n" + "`~!@#$%^&*()+=[{]}\\|;:'\",<>/?";
            IVC.showWarning("Invalid Name", message, "");
            handleInput();
        }

        updatingTreeItem = false;
    }

    //not valid chars: `~!@#$%^&*()+=[{]}\|;:'",<>/?
    private boolean isValid(String label) {
        StringTokenizer st = new StringTokenizer(" " + label + " ",
                "`~!@#$%^&*()+=[{]}\\|;:'\",<>/?");

        return st.countTokens() == 1;
    }

    /*
     * Listens for selection in the GUI of data models by selecting their
     * item in the tree and updates the ModelManager appropriately     
     */
    private class DatamodelSelectionListener extends SelectionAdapter {
        public void widgetSelected(SelectionEvent e) {
            Tree tree = (Tree) e.getSource();
            TreeItem[] selection = tree.getSelection();
            Set models = new HashSet();
            DataModel[] modelArray = new DataModel[selection.length];

            for (int i = 0; i < selection.length; i++) {
                DataModel model = ((DataModelGUIItem) selection[i].getData()).getModel();
                updateContextMenu(model);
                models.add(model);
                modelArray[i] = model;
            }

            manager.setSelectedModels(models);
            getSite().getSelectionProvider().setSelection(new StructuredSelection(modelArray));
        }
    }

    /*
     * Listens for right-clicks on TreeItems and opens the context menu when
     * needed. 
     */
    private class ContextMenuListener extends MouseAdapter {
        public void mouseUp(MouseEvent event) {
            if (event.button == 3) {
                TreeItem item = tree.getItem(new Point(event.x, event.y));

                if (item != null) {
                    tree.getMenu().setVisible(true);
                } else {
                    tree.getMenu().setVisible(false);
                }
            }
        }
    }

    /*
     * Listens for double clicks or Enter presses on a TreeItem to cause that
     * item to become editable to rename it. 
     */
    private class TreeItemEditorListener extends MouseAdapter
        implements KeyListener {
        //private TreeEditor editor;

        public TreeItemEditorListener(TreeEditor editor) {
            //this.editor = editor;
        }

        public void keyReleased(KeyEvent e) {
            if ((e.keyCode == SWT.CR) && !updatingTreeItem) {
                handleInput();
            }
        }

        public void mouseDoubleClick(MouseEvent e) {
            handleInput();
        }

        public void keyPressed(KeyEvent e) {
        }
    }

    private class DiscardListener implements Listener {
        public void handleEvent(Event event) {
            TreeItem[] selection = DataModelTreeView.this.tree.getSelection();                       

            for (int i = 0; i < selection.length; i++) {
                DataModelGUIItem item = (DataModelGUIItem) selection[i].getData();
                DataModelGUIItem parent = item.getParent();

                if (parent != null) {
                    parent.removeChild(item);
                }
                
                modelToDataModelGUIItemMap.remove(item.getModel());
                manager.removeModel(item.getModel());
            }

            manager.setSelectedModels(new HashSet());
            viewer.refresh();            
        }                
    }

    public void earlyStartup() {
        
    }
    
    private class DataModelSelectionProvider implements ISelectionProvider {
        
        private Set listeners = new HashSet();
        private ISelection selection;

        public void addSelectionChangedListener(ISelectionChangedListener listener) {
            listeners.add(listener);
        }

        public ISelection getSelection() {
            return selection;
        }

        public void removeSelectionChangedListener(ISelectionChangedListener listener) {
            listeners.remove(listener);
        }

        private TreeItem getTreeItem(DataModel model, TreeItem[] items){
            TreeItem result = null;
            int i = 0;
            while(i < items.length && result == null){
                DataModelGUIItem data = ((DataModelGUIItem)items[i].getData());
                if(data != null){ //not sure why this happens..                    
                    DataModel item = data.getModel();
	                
	                if(item == model)
	                    result = items[i];
	                else {                    
	                    //check  the children recursively
	                    result = getTreeItem(model, items[i].getItems());
	                }
                }
                else {                    
                    //check  the children recursively
                    result = getTreeItem(model, items[i].getItems());
                }                                
                
                i++;
            }
            
            return result;
        }
        
        public void setSelection(ISelection selection) {
            if(selection != this.selection){
                this.selection = selection;
                viewer.refresh(true);
                
                if (selection != null && selection instanceof IStructuredSelection) {
                    IStructuredSelection ss = (IStructuredSelection) selection;                
                    Iterator iterator = ss.iterator();
                    TreeItem[] newTreeSelection = new TreeItem[ss.size()];
                    int i = 0;
                    while(iterator.hasNext()){
                        Object next = iterator.next();
                        if(next instanceof DataModel){
                            TreeItem result = getTreeItem((DataModel)next, tree.getItems());
                            newTreeSelection[i] = result;
                            viewer.expandToLevel(modelToDataModelGUIItemMap.get(next), 0);
                        }                    
                        i++;
                    }                    
                    
                    tree.setSelection(newTreeSelection);
                }
                
	            Iterator listenerIterator = listeners.iterator();
	            while(listenerIterator.hasNext()){
	                ISelectionChangedListener listener = (ISelectionChangedListener)listenerIterator.next();
	                SelectionChangedEvent event = new SelectionChangedEvent(this, selection);
	                listener.selectionChanged(event);
	            }
            }
        }
        
    }
}

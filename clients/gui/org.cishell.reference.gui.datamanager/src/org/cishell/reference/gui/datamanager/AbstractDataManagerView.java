/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 21, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.gui.datamanager;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.cishell.app.service.datamanager.DataManagerListener;
import org.cishell.app.service.datamanager.DataManagerService;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.reference.gui.workspace.CIShellApplication;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuManager;
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
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public abstract class AbstractDataManagerView extends ViewPart implements
		DataManagerListener, BundleListener {
	private String brandPluginID;

	private DataManagerService manager;

	private TreeViewer viewer;

	private TreeEditor editor;

	private Text newEditor;

	private DataGUIItem rootItem;

	// flag to notify if a tree item is currently being updated so there
	// isnt a conflict among various listeners
	private boolean updatingTreeItem;

	private Tree tree;

	private Menu menu;

	private Map dataToDataGUIItemMap;
	
	private AlgorithmFactory saveFactory;
	private AlgorithmFactory viewFactory;
	private AlgorithmFactory viewWithFactory;
	

	private DiscardListener discardListener;

	private SaveListener saveListener;
	private ViewListener viewListener;
	private ViewWithListener viewWithListener;

	public AbstractDataManagerView(String brandPluginID) {
		manager = Activator.getDataManagerService();
		this.brandPluginID = brandPluginID;
		dataToDataGUIItemMap = new HashMap();

		if (manager == null) {
			LogService log = Activator.getLogService();
			if (log != null) {
				log.log(LogService.LOG_ERROR, "Data Manager Service unavailable!");
			}
		}
	}

	private String getItemID(ServiceReference ref) {
    	return ref.getProperty("PID:" + Constants.SERVICE_PID) + "-SID:" + 
                                ref.getProperty(Constants.SERVICE_ID);
    }
    
	
	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {

		// Label label = new Label(parent, SWT.NONE);
		// label.setText("Data Manager");
		viewer = new TreeViewer(parent);
		viewer.setContentProvider(new DataTreeContentProvider());
		viewer.setLabelProvider(new DataTreeLabelProvider());

		rootItem = new DataGUIItem(null, null, this.brandPluginID);
		viewer.setInput(rootItem);
		viewer.expandAll();

		// grab the tree and add the appropriate listeners
		tree = viewer.getTree();
		tree.addSelectionListener(new DatamodelSelectionListener());
		tree.addMouseListener(new ContextMenuListener());

		// setup the context menu for the tree
		menu = new Menu(tree);
		menu.setVisible(false);

		MenuItem saveItem = new MenuItem(menu, SWT.PUSH);
		saveItem.setText("Save");
		saveListener = new SaveListener();
		saveItem.addListener(SWT.Selection, saveListener);

		MenuItem viewItem = new MenuItem(menu, SWT.PUSH);
		viewItem.setText("View");
		viewListener = new ViewListener();
		viewItem.addListener(SWT.Selection, viewListener);
			
		MenuItem viewWithItem = new MenuItem(menu, SWT.PUSH);			
		viewWithItem.setText("View With...");
		viewWithListener = new ViewWithListener();
		viewWithItem.addListener(SWT.Selection, viewWithListener);		
		
		MenuItem renameItem = new MenuItem(menu, SWT.PUSH);
		renameItem.setText("Rename");
		renameItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				handleInput();
			}
		});

		MenuItem discardItem = new MenuItem(menu, SWT.PUSH);
		discardItem.setText("Discard");
		discardListener = new DiscardListener();
		discardItem.addListener(SWT.Selection, discardListener);
		tree.setMenu(menu);

		// allow cells to be edited on double click or when pressing enter on
		// them
		editor = new TreeEditor(tree);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
		
		// listen to OSGi for models being added by plugins
		if (manager != null) {
			manager.addDataManagerListener(this);
		}
		else {
			Activator.getBundleContext().addBundleListener(this);
			manager = Activator.getDataManagerService();
			if (manager != null) {
				manager.addDataManagerListener(this);
			}
		}

		getSite().setSelectionProvider(new DataModelSelectionProvider());
	}
	
	public void bundleChanged(BundleEvent event) {
		if (event.getType() == BundleEvent.STARTED) {
			manager = Activator.getDataManagerService();
			if (manager != null) {
				manager.addDataManagerListener(this);				
			}
		}
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void dataAdded(final Data newData, String label) {
		
		//get the new data's parent GUI Item (either root or another data item)
		DataGUIItem parentItem = getParent(newData);
		
		// wrap the new data in a DataGUIItem
		final DataGUIItem newItem = new DataGUIItem(newData, parentItem,
				this.brandPluginID);
		
		// notify the parent DataModelGUIItem of its new child
		parentItem.addChild(newItem);
		
		// keep a reference to the new model in the model->TreeItem mapping so
		// that
		// it can be used in the future if it has a child
		dataToDataGUIItemMap.put(newData, newItem);
		
		// update the ModelManager with the new selection
		final Set selection = new HashSet();
		selection.add(newData);

		guiRun(new Runnable() {
			public void run() {
				if (!tree.isDisposed()) {
					// update the TreeView
					viewer.refresh();
					// context menu may need to have options enabled/disabled
					// based on the new selection
					updateContextMenu(newData);
					// update the global selection
					viewer.expandToLevel(newItem, 0);
					manager.setSelectedData((Data[]) selection.toArray(new Data[0]));
				}
			}
		});
	}
	
	private DataGUIItem getParent(Data data) {
		Dictionary modelDictionary = data.getMetaData();
		
		Data parent = (Data) modelDictionary.get(DataProperty.PARENT);
		DataGUIItem parentItem;
		if (parent == null) {
			// if it has no parent, it is a child of the root
			parentItem = rootItem;
		} else {
			// otherwise find the associated DataModelGUIItem for the parent
			parentItem = (DataGUIItem) dataToDataGUIItemMap.get(parent);
            
			//The parent may not be in the GUI. If its not, then use root item
            if (parentItem == null) {
                parentItem = rootItem;
            }
		}
		
		return parentItem;
	}
	
	private void guiRun(Runnable run) {
		if (Thread.currentThread() == Display.getDefault().getThread()) {
			run.run();
		} else {
			Display.getDefault().syncExec(run);
		}
	}

	public void dataLabelChanged(Data data, String label) {
		if (data != null && label != null) {
			TreeItem[] treeItems = tree.getItems();
			for (int i = 0; i < treeItems.length; ++i) {
				if (((DataGUIItem)treeItems[i].getData()).getModel() == data) {
					updateText(label, treeItems[i]);
					break;
				}
			}
		}
	}

	public void dataRemoved(Data data) {
		TreeItem[] treeItems = tree.getItems();
		for (int i = 0; i < treeItems.length; ++i) {
			if (((DataGUIItem)treeItems[i].getData()).getModel() == data) {
				tree.clear(tree.indexOf(treeItems[i]), false);
			}
		}
	}

	public void dataSelected(final Data[] data) {
		if (data != null) {
			//setFocus();
			guiRun(new Runnable() {
				public void run() {
					Set itemSet = new HashSet();
					for (int i = 0; i < data.length; ++i) {
						TreeItem[] treeItems = tree.getItems();
						for (int j = 0; j < treeItems.length; ++j) {
							if (treeItems[j].getData() == data[i]) {
								itemSet.add(treeItems[j]);
								break;
							}
						}
					}
					tree.setSelection((TreeItem[]) itemSet
							.toArray(new TreeItem[0]));
					getSite().getSelectionProvider().setSelection(
							new StructuredSelection(data));
				}
			});
		}
	}

	/*
	 * enables/disables save item in the context menu based on whether or not
	 * their is an available Persister for the given model
	 */
	private void updateContextMenu(Data model) {
		saveFactory = enableMenuItemCheck(saveFactory,
				"org.cishell.reference.gui.persistence.save.Save", model, 0);
		viewFactory = enableMenuItemCheck(viewFactory,
				"org.cishell.reference.gui.persistence.view.FileView", model, 1);
		viewWithFactory = enableMenuItemCheck(viewWithFactory,
				"org.cishell.reference.gui.persistence.viewwith.FileViewWith", model, 2);
	}
	
	private AlgorithmFactory enableMenuItemCheck(
			AlgorithmFactory algorithmFactory, String service, Data model,
			int menuNdx) {
		boolean validSaveFactory = false;
		if (algorithmFactory == null) {
			algorithmFactory = Activator.getService(service);
			if (algorithmFactory != null) {
				validSaveFactory = true;
			} 
		} else {
			validSaveFactory = true;
		}

		boolean enabled = false;
		
		if (validSaveFactory) {
			Algorithm algorithm = algorithmFactory.createAlgorithm(
					new Data[] { model }, new Hashtable(), Activator
							.getCIShellContext());
			if (algorithm != null) {
				enabled = true;
			}
		}
		menu.getItem(menuNdx).setEnabled(enabled);
		return algorithmFactory;
	}

	/*
	 * Listens for selection in the GUI of data models by selecting their item
	 * in the tree and updates the ModelManager appropriately
	 */
	private class DatamodelSelectionListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			Tree tree = (Tree) e.getSource();
			TreeItem[] selection = tree.getSelection();
			Set models = new HashSet();
			Data[] modelArray = new Data[selection.length];

			for (int i = 0; i < selection.length; i++) {
				Data model = ((DataGUIItem) selection[i].getData()).getModel();
				updateContextMenu(model);
				models.add(model);
				modelArray[i] = model;
			}

			manager.setSelectedData(modelArray);
		}
	}

	/*
	 * allows for direct text editing of the label of a DataModelGUIItem by
	 * using a Text control associated with a TreeEditor. This allows for
	 * renaming of data models without a pop-up dialog or anything
	 */
	private void handleInput() {
		// Clean up any previous editor control
		Control oldEditor = editor.getEditor();

		if (oldEditor != null) {
			oldEditor.dispose();
		}

		// Identify the selected row, only allow input if there is a single
		// selected row
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
					//updateText(newEditor.getText(), item);
					manager.setLabel(((DataGUIItem)item.getData()).getModel(), newEditor.getText());
					// FELIX.  This is not > stupidness.
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
	 * updates the actual Label property of the DataModel that is being edited
	 * by the TreeEditor for renaming - only if the new name is valid though
	 */
	private void updateText(String newLabel, TreeItem item) {
		updatingTreeItem = true;
     
		if (newLabel.startsWith(">"))
			newLabel = newLabel.substring(1);
		
	
		editor.getItem().setText(newLabel);

		DataGUIItem treeItem = (DataGUIItem) item.getData();
		Data model = treeItem.getModel();
		model.getMetaData().put(DataProperty.LABEL, newLabel);
		viewer.refresh();
		newEditor.dispose();
	
		updatingTreeItem = false;
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


	private class SaveListener implements Listener {
		public void handleEvent(Event event) {
			if (saveFactory != null) {
				Data data[] = AbstractDataManagerView.this.manager
						.getSelectedData();
				Algorithm algorithm = saveFactory
						.createAlgorithm(data, new Hashtable(), Activator
								.getCIShellContext());
				algorithm.execute();
			}
		}
	}

	private class ViewListener implements Listener {
		public void handleEvent(Event event) {
			if (viewFactory != null) {
				Data data[] = AbstractDataManagerView.this.manager
						.getSelectedData();
				Algorithm algorithm = viewFactory
						.createAlgorithm(data, new Hashtable(), Activator
								.getCIShellContext());
				algorithm.execute();
			}
		}
	}

	private class ViewWithListener implements Listener {
		public void handleEvent(Event event) {
			IMenuManager topLevelMenu = CIShellApplication.getMenuManager();
			IMenuManager fileMenu = topLevelMenu.findMenuUsingPath("File");
			BundleContext bContext = Activator.getBundleContext();
			
			try {
				ServiceReference[] ref = bContext.getAllServiceReferences(AlgorithmFactory.class.getName(), 
						"(service.pid=org.cishell.reference.gui.persistence.viewwith.FileViewWith)");
				
				if (ref != null && ref.length > 0) {
					ActionContributionItem action = (ActionContributionItem)fileMenu.find(getItemID(ref[0]));
					action.getAction().run();
				}
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class DiscardListener implements Listener {
		public void handleEvent(Event event) {
			TreeItem[] selection = AbstractDataManagerView.this.tree
					.getSelection();

			for (int i = 0; i < selection.length; i++) {
				DataGUIItem item = (DataGUIItem) selection[i].getData();
				DataGUIItem parent = item.getParent();

				if (parent != null) {
					parent.removeChild(item);
				}

				dataToDataGUIItemMap.remove(item.getModel());
				manager.removeData(item.getModel());
			}

			manager.setSelectedData(new Data[0]);
			viewer.refresh();
		}
	}


	private class DataModelSelectionProvider implements ISelectionProvider {

		private Set listeners = new HashSet();

		private ISelection selection;

		public void addSelectionChangedListener(
				ISelectionChangedListener listener) {
			listeners.add(listener);
		}

		public ISelection getSelection() {
			return selection;
		}

		public void removeSelectionChangedListener(
				ISelectionChangedListener listener) {
			listeners.remove(listener);
		}

		private TreeItem getTreeItem(Data model, TreeItem[] items) {
			TreeItem result = null;
			int i = 0;
			while (i < items.length && result == null) {
				DataGUIItem data = ((DataGUIItem) items[i].getData());
				if (data != null) { // not sure why this happens..
					Data item = data.getModel();

					if (item == model)
						result = items[i];
					else {
						// check the children recursively
						result = getTreeItem(model, items[i].getItems());
					}
				} else {
					// check the children recursively
					result = getTreeItem(model, items[i].getItems());
				}

				i++;
			}

			return result;
		}

		public void setSelection(ISelection selection) {
			if (selection != this.selection) {
				this.selection = selection;
				viewer.refresh(true);

				if (selection != null
						&& selection instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection) selection;
					Iterator iterator = ss.iterator();
					TreeItem[] newTreeSelection = new TreeItem[ss.size()];
					int i = 0;
					while (iterator.hasNext()) {
						Object next = iterator.next();
						if (next instanceof Data) {
							TreeItem result = getTreeItem((Data) next, tree
									.getItems());
							newTreeSelection[i] = result;
							viewer.expandToLevel(
									dataToDataGUIItemMap.get(next), 0);
						}
						i++;
					}

					tree.setSelection(newTreeSelection);
				}

				Iterator listenerIterator = listeners.iterator();
				while (listenerIterator.hasNext()) {
					ISelectionChangedListener listener = (ISelectionChangedListener) listenerIterator
							.next();
					SelectionChangedEvent event = new SelectionChangedEvent(
							this, selection);
					listener.selectionChanged(event);
				}
			}
		}

	}
}

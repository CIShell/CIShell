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
import org.cishell.framework.algorithm.AlgorithmCanceledException;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
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

public abstract class AbstractDataManagerView
		extends ViewPart implements BundleListener, DataManagerListener {
	private String brandPluginID;
	private DataManagerService manager;
	private TreeViewer viewer;
	private TreeEditor editor;
	private Text newEditor;
	private DataGUIItem rootItem;
	/*
	 * Flag to notify if a tree item is currently being updated so there isnt a conflict among
	 *  various listeners.
	 */
	private boolean updatingTreeItem;
	private Tree tree;
	private Menu menu;
	private Map<Data, DataGUIItem> dataToDataGUIItemMap;
	private AlgorithmFactory saveFactory;
	private AlgorithmFactory viewFactory;
	private AlgorithmFactory viewWithFactory;
	private DiscardListener discardListener;
	private SaveListener saveListener;
	private ViewListener viewListener;
	private ViewWithListener viewWithListener;
	private LogService logger;

	public AbstractDataManagerView(String brandPluginID) {
		this.brandPluginID = brandPluginID;
		this.dataToDataGUIItemMap = new HashMap<Data, DataGUIItem>();
		this.manager = Activator.getDataManagerService();
		this.logger = Activator.getLogService();
		
		if (this.manager == null) {			
			if (this.logger != null) {
				this.logger.log(LogService.LOG_ERROR, "Data Manager Service unavailable!");
			}			
		}
	}

	private String getItemID(ServiceReference serviceReference) {
    	return serviceReference.getProperty(
    		"PID:" +
    		Constants.SERVICE_PID) +
    		"-SID:" +
    		serviceReference.getProperty(Constants.SERVICE_ID);
    }
    
	
	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		this.viewer = new TreeViewer(parent);
		this.viewer.setContentProvider(new DataTreeContentProvider());
		this.viewer.setLabelProvider(new DataTreeLabelProvider());

		this.rootItem = new DataGUIItem(null, null, this.brandPluginID);
		this.viewer.setInput(this.rootItem);
		this.viewer.expandAll();

		// Grab the tree and add the appropriate listeners.
		this.tree = this.viewer.getTree();
		this.tree.addSelectionListener(new DatamodelSelectionListener());
		this.tree.addMouseListener(new ContextMenuListener());

		// Setup the context menu for the tree.
		this.menu = new Menu(tree);
		this.menu.setVisible(false);

		MenuItem saveItem = new MenuItem(this.menu, SWT.PUSH);
		saveItem.setText("Save");
		this.saveListener = new SaveListener();
		saveItem.addListener(SWT.Selection, this.saveListener);

		MenuItem viewItem = new MenuItem(this.menu, SWT.PUSH);
		viewItem.setText("View");
		this.viewListener = new ViewListener();
		viewItem.addListener(SWT.Selection, this.viewListener);
			
		MenuItem viewWithItem = new MenuItem(this.menu, SWT.PUSH);			
		viewWithItem.setText("View With...");
		this.viewWithListener = new ViewWithListener();
		viewWithItem.addListener(SWT.Selection, this.viewWithListener);		
		
		MenuItem renameItem = new MenuItem(this.menu, SWT.PUSH);
		renameItem.setText("Rename");
		renameItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				handleInput();
			}
		});

		MenuItem discardItem = new MenuItem(this.menu, SWT.PUSH);
		discardItem.setText("Discard");
		this.discardListener = new DiscardListener();
		discardItem.addListener(SWT.Selection, this.discardListener);
		this.tree.setMenu(this.menu);

		// Allow cells to be edited on double click or when pressing enter on them.
		this.editor = new TreeEditor(this.tree);
		this.editor.horizontalAlignment = SWT.LEFT;
		this.editor.grabHorizontal = true;
		this.editor.minimumWidth = 50;
		
		// Listen to OSGi for models being added by plugins.
		if (this.manager != null) {
			this.manager.addDataManagerListener(this);
		} else {
			Activator.getBundleContext().addBundleListener(this);
			this.manager = Activator.getDataManagerService();

			if (this.manager != null) {
				this.manager.addDataManagerListener(this);
			}
		}

		getSite().setSelectionProvider(new DataModelSelectionProvider());
	}
	
	public void bundleChanged(BundleEvent event) {
		if (event.getType() == BundleEvent.STARTED) {
			this.manager = Activator.getDataManagerService();

			if (this.manager != null) {
				this.manager.addDataManagerListener(this);				
			}
		}
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		this.viewer.getControl().setFocus();
	}

	public void dataAdded(final Data newData, String label) {
		
		// Get the new data's parent GUI Item (either root or another data item).
		DataGUIItem parentItem = getParent(newData);
		
		// Wrap the new data in a DataGUIItem.
		final DataGUIItem newItem = new DataGUIItem(newData, parentItem, this.brandPluginID);
		
		// Notify the parent DataModelGUIItem of its new child.
		parentItem.addChild(newItem);
		
		/* Keep a reference to the new model in the model->TreeItem mapping so that it can be used
		 *  in the future if it has a child.
		 */
		this.dataToDataGUIItemMap.put(newData, newItem);
		
		// update the ModelManager with the new selection
		final Set<Data> selection = new HashSet<Data>();
		selection.add(newData);

		guiRun(new Runnable() {
			public void run() {
				if (!tree.isDisposed()) {
					// update the TreeView
					AbstractDataManagerView.this.viewer.refresh();
					// context menu may need to have options enabled/disabled
					// based on the new selection
					updateContextMenu(newData);
					// update the global selection
					AbstractDataManagerView.this.viewer.expandToLevel(newItem, 0);
					manager.setSelectedData((Data[]) selection.toArray(new Data[0]));
				}
			}
		});
	}
	
	private DataGUIItem getParent(Data data) {
		Dictionary<String, Object> modelDictionary = data.getMetadata();
		
		Data parent = (Data) modelDictionary.get(DataProperty.PARENT);
		DataGUIItem parentItem;

		if (parent == null) {
			// If it has no parent, it is a child of the root.
			parentItem = this.rootItem;
		} else {
			// Otherwise find the associated DataModelGUIItem for the parent.
			parentItem = this.dataToDataGUIItemMap.get(parent);
            
			// The parent may not be in the GUI. If its not, then use root item.
            if (parentItem == null) {
                parentItem = this.rootItem;
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
			TreeItem[] treeItems = this.tree.getItems();
			for (int i = 0; i < treeItems.length; ++i) {
				if (((DataGUIItem)treeItems[i].getData()).getModel() == data) {
					updateText(label, treeItems[i]);
					break;
				}
			}
		}
	}

	public void dataRemoved(Data data) {
		TreeItem[] treeItems = this.tree.getItems();
		for (int i = 0; i < treeItems.length; ++i) {
			if (((DataGUIItem)treeItems[i].getData()).getModel() == data) {
				this.tree.clear(this.tree.indexOf(treeItems[i]), false);
			}
		}
	}

	public void dataSelected(final Data[] data) {
		if (data != null) {
			guiRun(new Runnable() {
				public void run() {
					// TODO: Abstract this?
					Set<TreeItem> itemSet = new HashSet<TreeItem>();

					for (int i = 0; i < data.length; ++i) {
						TreeItem[] treeItems = tree.getItems();
						for (int j = 0; j < treeItems.length; ++j) {
							if (treeItems[j].getData() == data[i]) {
								itemSet.add(treeItems[j]);
								break;
							}
						}
					}

					tree.setSelection(itemSet.toArray(new TreeItem[0]));
					getSite().getSelectionProvider().setSelection(new StructuredSelection(data));
				}
			});
		}
	}

	/*
	 * Enables/disables save item in the context menu based on whether or not
	 * their is an available Persister for the given model
	 */
	private void updateContextMenu(Data model) {
//			throws AlgorithmCreationCanceledException, AlgorithmCreationFailedException {
		this.saveFactory = enableMenuItemCheck(
			this.saveFactory, "org.cishell.reference.gui.persistence.save.Save", model, 0);
		this.viewFactory = enableMenuItemCheck(
			this.viewFactory, "org.cishell.reference.gui.persistence.view.FileView", model, 1);
		this.viewWithFactory = enableMenuItemCheck(
			this.viewWithFactory,
			"org.cishell.reference.gui.persistence.viewwith.FileViewWith",
			model,
			2);
	}
	
	private AlgorithmFactory enableMenuItemCheck(
			AlgorithmFactory algorithmFactory, String service, Data model, int menuIndex) {
//			throws AlgorithmCreationCanceledException, AlgorithmCreationFailedException {
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
				new Data[] { model },
				new Hashtable<String, Object>(),
				Activator.getCIShellContext());

			if (algorithm != null) {
				enabled = true;
			}
		}

		this.menu.getItem(menuIndex).setEnabled(enabled);

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
			Set<Data> models = new HashSet<Data>();
			Data[] modelArray = new Data[selection.length];

			for (int i = 0; i < selection.length; i++) {
				Data model = ((DataGUIItem) selection[i].getData()).getModel();
				updateContextMenu(model);
				models.add(model);
				modelArray[i] = model;
			}

			AbstractDataManagerView.this.manager.setSelectedData(modelArray);
		}
	}

	/*
	 * allows for direct text editing of the label of a DataModelGUIItem by
	 * using a Text control associated with a TreeEditor. This allows for
	 * renaming of data models without a pop-up dialog or anything
	 */
	private void handleInput() {
		// Clean up any previous editor control
		Control oldEditor = this.editor.getEditor();

		if (oldEditor != null) {
			oldEditor.dispose();
		}

		// Identify the selected row, only allow input if there is a single
		// selected row
		TreeItem[] selection = this.tree.getSelection();

		if (selection.length != 1) {
			return;
		}

		final TreeItem item = selection[0];

		if (item == null) {
			return;
		}

		// The control that will be the editor must be a child of the Table
		this.newEditor = new Text(this.tree, SWT.NONE);
		this.newEditor.setText(item.getText());
		this.newEditor.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				if (!updatingTreeItem) {
					//updateText(newEditor.getText(), item);
					AbstractDataManagerView.this.manager.setLabel(
						((DataGUIItem) item.getData()).getModel(),
						AbstractDataManagerView.this.newEditor.getText());
					// FELIX.  This is not > stupidness.
				}
			}
		});

		// ENTER ESC
		this.newEditor.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if ((e.character == SWT.CR) && !AbstractDataManagerView.this.updatingTreeItem) {
					updateText(AbstractDataManagerView.this.newEditor.getText(), item);
				} else if (e.keyCode == SWT.ESC) {
					AbstractDataManagerView.this.newEditor.dispose();
				}
			}
		});
		this.newEditor.selectAll();
		this.newEditor.setFocus();
		this.editor.setEditor(this.newEditor, item);
	}

	/*
	 * updates the actual Label property of the DataModel that is being edited
	 * by the TreeEditor for renaming - only if the new name is valid though
	 */
	private void updateText(String newLabel, TreeItem item) {
		this.updatingTreeItem = true;
     
		if (newLabel.startsWith(">"))
			newLabel = newLabel.substring(1);
		
	
		this.editor.getItem().setText(newLabel);

		DataGUIItem treeItem = (DataGUIItem) item.getData();
		Data model = treeItem.getModel();
		model.getMetadata().put(DataProperty.LABEL, newLabel);
		viewer.refresh();
		this.newEditor.dispose();
	
		updatingTreeItem = false;
	}

	/*
	 * Listens for right-clicks on TreeItems and opens the context menu when
	 * needed.
	 */
	private class ContextMenuListener extends MouseAdapter {
		public void mouseUp(MouseEvent event) {
			if (event.button == 3) {
				TreeItem item =
					AbstractDataManagerView.this.tree.getItem(new Point(event.x, event.y));

				if (item != null) {
					AbstractDataManagerView.this.tree.getMenu().setVisible(true);
				} else {
					AbstractDataManagerView.this.tree.getMenu().setVisible(false);
				}
			}
		}
	}


	private class SaveListener implements Listener {
		public void handleEvent(Event event) {
			if (AbstractDataManagerView.this.saveFactory != null) {
				Data data[] = AbstractDataManagerView.this.manager.getSelectedData();
				Algorithm algorithm = AbstractDataManagerView.this.saveFactory.createAlgorithm(
					data, new Hashtable<String, Object>(), Activator.getCIShellContext());

				try {
					algorithm.execute();
				} catch (AlgorithmExecutionException e)  {
					if (AbstractDataManagerView.this.logger != null) {
						AbstractDataManagerView.this.logger.log(
							LogService.LOG_ERROR, e.getMessage(), e);
						e.printStackTrace();
					} else {
						AbstractDataManagerView.this.logger = Activator.getLogService();

						if (AbstractDataManagerView.this.logger != null) {
							AbstractDataManagerView.this.logger.log(
								LogService.LOG_ERROR,
								"org.cishell.framework.algorithm.AlgorithmExecutionException",
								e);
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	private class ViewListener implements Listener {
		public void handleEvent(Event event) {
			if (AbstractDataManagerView.this.viewFactory != null) {
				Data data[] = AbstractDataManagerView.this.manager.getSelectedData();
				Algorithm algorithm = AbstractDataManagerView.this.viewFactory.createAlgorithm(
					data, new Hashtable<String, Object>(), Activator.getCIShellContext());

				try {
					algorithm.execute();
				} catch (AlgorithmExecutionException e)  {
					if (AbstractDataManagerView.this.logger != null) {
						AbstractDataManagerView.this.logger.log(
							LogService.LOG_ERROR, e.getMessage(), e);
					} else {
						AbstractDataManagerView.this.logger = Activator.getLogService();

						// TODO: Find occurrences of this crap happening, and make it a method.
						if (AbstractDataManagerView.this.logger != null) {
							AbstractDataManagerView.this.logger.log(
								LogService.LOG_ERROR, e.getMessage(), e);
						}
					}

					e.printStackTrace();
				}
			}
		}
	}

	private class ViewWithListener implements Listener {
		public void handleEvent(Event event) {
			IMenuManager topLevelMenu = CIShellApplication.getMenuManager();
			IMenuManager fileMenu = topLevelMenu.findMenuUsingPath("File");
			BundleContext bundleContext = Activator.getBundleContext();
			
			try {
				ServiceReference[] serviceReference = bundleContext.getAllServiceReferences(
					AlgorithmFactory.class.getName(), 
					"(service.pid=org.cishell.reference.gui.persistence.viewwith.FileViewWith)");
				
				if ((serviceReference != null) && (serviceReference.length > 0)) {
					ActionContributionItem action =
						(ActionContributionItem) fileMenu.find(getItemID(serviceReference[0]));
					action.getAction().run();
				}
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class DiscardListener implements Listener {
		public void handleEvent(Event event) {
			TreeItem[] selections = AbstractDataManagerView.this.tree.getSelection();

			for (TreeItem selection : selections) {
//			for (int i = 0; i < selections.length; i++) {
//				DataGUIItem item = (DataGUIItem) selections[i].getData();
				DataGUIItem item = (DataGUIItem) selection.getData();
				DataGUIItem parent = item.getParent();

				if (parent != null) {
					parent.removeChild(item);
				}

				AbstractDataManagerView.this.dataToDataGUIItemMap.remove(item.getModel());
				AbstractDataManagerView.this.manager.removeData(item.getModel());
			}

			AbstractDataManagerView.this.manager.setSelectedData(new Data[0]);
			AbstractDataManagerView.this.viewer.refresh();
		}
	}


	private class DataModelSelectionProvider implements ISelectionProvider {
		private Set<ISelectionChangedListener> listeners =
			new HashSet<ISelectionChangedListener>();
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

		private TreeItem getTreeItem(Data model, TreeItem[] items) {
			TreeItem result = null;
			int i = 0;

			while ((i < items.length) && (result == null)) {
				DataGUIItem data = ((DataGUIItem) items[i].getData());

				// TODO: Not sure why this happens...
				if (data != null) {
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
				AbstractDataManagerView.this.viewer.refresh(true);

				if ((selection != null) && (selection instanceof IStructuredSelection)) {
					IStructuredSelection selections = (IStructuredSelection) selection;
					Iterator<?> iterator = selections.iterator();
					TreeItem[] newTreeSelection = new TreeItem[selections.size()];
					int i = 0;

					while (iterator.hasNext()) {
						Object next = iterator.next();

						if (next instanceof Data) {
							TreeItem result = getTreeItem(
								(Data) next, AbstractDataManagerView.this.tree.getItems());
							newTreeSelection[i] = result;
							AbstractDataManagerView.this.viewer.expandToLevel(
								AbstractDataManagerView.this.dataToDataGUIItemMap.get(next), 0);
						}

						i++;
					}

					AbstractDataManagerView.this.tree.setSelection(newTreeSelection);
				}

				Iterator<ISelectionChangedListener> listenerIterator = listeners.iterator();

				while (listenerIterator.hasNext()) {
					ISelectionChangedListener listener = listenerIterator.next();
					SelectionChangedEvent event = new SelectionChangedEvent(this, selection);
					listener.selectionChanged(event);
				}
			}
		}
	}
}

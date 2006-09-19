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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.cishell.app.service.datamanager.DataManagerListener;
import org.cishell.app.service.datamanager.DataManagerService;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
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
import org.eclipse.ui.part.ViewPart;
import org.osgi.service.log.LogService;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public abstract class AbstractDataManagerView extends ViewPart implements
		DataManagerListener {
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

	private DiscardListener discardListener;

	public AbstractDataManagerView(String brandPluginID) {
		manager = Activator.getDataManagerService();
		this.brandPluginID = brandPluginID;
		dataToDataGUIItemMap = new HashMap();

		if (manager == null) {
			Activator.getLogService().log(LogService.LOG_ERROR,
					"Data Manager Service unavailable!");
		}
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
		saveItem.setEnabled(false);

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
		TreeItemEditorListener editorListener = new TreeItemEditorListener(
				editor);
		tree.addMouseListener(editorListener);
		tree.addKeyListener(editorListener);

		// listen to IVC for models being added by plugins
		manager.addDataManagerListener(this);

		getSite().setSelectionProvider(new DataModelSelectionProvider());
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void dataAdded(final Data data, String label) {
		// get the data from the model needed to setup the DataModelGUIItem
		Dictionary modelDictionary = data.getMetaData();

		Data parent = (Data) modelDictionary.get(DataProperty.PARENT);
		DataGUIItem parentItem;
		if (parent == null) {
			// if it has no parent, it is a child of the root
			parentItem = rootItem;
		} else {
			// otherwise find the associated DataModelGUIItem for the parent
			parentItem = (DataGUIItem) dataToDataGUIItemMap.get(parent);
		}

		// create the new DataModelGUIItem
		final DataGUIItem item = new DataGUIItem(data, parentItem,
				this.brandPluginID);
		// notify the parent DataModelGUIItem of its new child
		parentItem.addChild(item);
		// keep a reference to the new model in the model->TreeItem mapping so
		// that
		// it can be used in the future if it has a child
		dataToDataGUIItemMap.put(data, item);

		// update the ModelManager with the new selection
		Set selection = new HashSet();
		selection.add(data);
		manager.setSelectedData((Data[]) selection.toArray(new Data[0]));
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (!tree.isDisposed()) {
					// update the TreeView
					viewer.refresh();
					// context menu may need to have options enabled/disabled
					// based on the new selection
					updateContextMenu(data);
					// update the global selection
					viewer.expandToLevel(item, 0);
					selectItem(item, tree.getItems());
					getSite().getSelectionProvider().setSelection(
							new StructuredSelection(new Data[] { data }));
					setFocus();
				}
			}
		});
	}

	private void selectItem(DataGUIItem item, TreeItem[] items) {
		for (int i = 0; i < items.length; i++) {
			TreeItem treeItem = items[i];
			if (treeItem.getData() == item) {
				manager.setSelectedData(new Data[] { item.getModel() });
				treeItem.getParent().setSelection(new TreeItem[] { treeItem });
				return;
			}
			selectItem(item, treeItem.getItems());
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
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					Set itemSet = new HashSet();
					for (int i = 0; i < data.length; ++i) {

						TreeItem[] treeItems = tree.getItems();
						for (int j = 0; j < treeItems.length; ++j) {
							if (treeItems[i].getData() == data[i]) {
								itemSet.add(treeItems[i]);
								break;
							}
						}
					}
					tree.setSelection((TreeItem[]) itemSet
							.toArray(new TreeItem[0]));
				}
			});
		}
	}

	/*
	 * enables/disables save item in the context menu based on whether or not
	 * their is an available Persister for the given model
	 */
	private void updateContextMenu(Data model) {
		// reset the enablement of the save context menu appropriately
		// boolean saveEnabled = (model.getData() != null) &&
		// !(IVC.getInstance().getPersistenceRegistry()
		// .getSupportingPersisters(model.getData()).isEmpty());
		boolean saveEnabled = false;
		menu.getItem(0).setEnabled(saveEnabled);
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
			getSite().getSelectionProvider().setSelection(
					new StructuredSelection(modelArray));
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

		if (isValid(newLabel)) {
			editor.getItem().setText(newLabel);

			DataGUIItem treeItem = (DataGUIItem) item.getData();
			Data model = treeItem.getModel();
			model.getMetaData().put(DataProperty.LABEL, newLabel);
			viewer.refresh();
			newEditor.dispose();
		} else {
			String message = "Invalid data model name. The following characters"
					+ " are not allowed:\n\n"
					+ "`~!@#$%^&*()+=[{]}\\|;:'\",<>/?";
			Activator.getLogService().log(LogService.LOG_WARNING, message);
			handleInput();
		}

		updatingTreeItem = false;
	}

	// not valid chars: `~!@#$%^&*()+=[{]}\|;:'",<>/?
	private boolean isValid(String label) {
		StringTokenizer st = new StringTokenizer(" " + label + " ",
				"`~!@#$%^&*()+=[{]}\\|;:'\",<>/?");

		return st.countTokens() == 1;
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

	/*
	 * Listens for double clicks or Enter presses on a TreeItem to cause that
	 * item to become editable to rename it.
	 */
	private class TreeItemEditorListener extends MouseAdapter implements
			KeyListener {
		// private TreeEditor editor;

		public TreeItemEditorListener(TreeEditor editor) {
			// this.editor = editor;
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

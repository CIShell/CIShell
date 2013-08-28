/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 21, 2006 at Indiana University.
 * Changed on Dec 19, 2006 at Indiana University
 * 
 * Contributors:
 * 	   Weixia(Bonnie) Huang, Bruce Herr, Ben Markines
 *     School of Library and Information Science, Indiana University 
 * ***************************************************************************/
package org.cishell.reference.gui.workflow.views;

import java.util.Calendar;
import java.util.Dictionary;

import org.cishell.app.service.scheduler.SchedulerListener;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmCreationFailedException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;
import org.cishell.reference.gui.menumanager.menu.AlgorithmWrapper;
import org.cishell.reference.gui.workflow.Utilities.Constant;
import org.cishell.reference.gui.workflow.Utilities.Utils;
import org.cishell.reference.gui.workflow.controller.WorkflowMaker;
import org.cishell.reference.gui.workflow.controller.WorkflowManager;
import org.cishell.reference.gui.workflow.model.AlgorithmWorkflowItem;
import org.cishell.reference.gui.workflow.model.SchedulerContentModel;
import org.cishell.reference.gui.workflow.model.Workflow;
import org.cishell.reference.gui.workflow.model.NormalWorkflow;
import org.cishell.reference.gui.workflow.views.WorkflowGUI;
import org.cishell.reference.gui.workflow.views.DataTreeContentProvider;
import org.cishell.reference.gui.workflow.views.DataTreeLabelProvider;
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.ServiceReference;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;

import org.cishell.reference.gui.workflow.Activator;
import org.osgi.framework.Constants;

/**
 * Creates and maintains the overall GUI for the workflow. Controls the table
 * and controls (moving, removing, etc.).
 * 
 */
public class WorkflowView extends ViewPart implements SchedulerListener {
	private static WorkflowView workFlowView;
	public static final String ID_VIEW = "org.cishell.reference.gui.workflow.views.WorkflowView";
	private TreeViewer viewer;
	private WorkflowGUI rootItem, currentWorkFlowItem;
	private Tree tree;
	private Menu menu;
	private Menu whiteSpacemenu;
	private SaveListener saveListener;
	private LoadListener loadListener;
	private RunListener runListener;
	private DeleteListener deleteListener;
	private WorkflowMode mode;
	private TreeEditor editor;
	private Text newEditor;
	private boolean updatingTreeItem;
	private WorkflowTreeItem currentParentItem;
	
	public WorkflowView() {
		workFlowView = this;		
		
	}

	/**
	 * Get the current workflow view
	 * 
	 * @return The workflow view
	 */
	public static WorkflowView getDefault() {
		return workFlowView;
	}

	/**
	 * Creates buttons, table, and registers listeners
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 * @param parent
	 *            The SWT parent
	 */
	@Override
	public void createPartControl(Composite parent) {

		
		this.viewer = new TreeViewer(parent);
		this.viewer.setContentProvider(new DataTreeContentProvider());
		this.viewer.setLabelProvider(new DataTreeLabelProvider());

		this.rootItem = new WorkflowGUI(null, null, 2);
		this.viewer.setInput(this.rootItem);
		this.viewer.expandAll();
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

		MenuItem runItem = new MenuItem(this.menu, SWT.PUSH);
		runItem.setText("Run");
		this.runListener = new RunListener();
		runItem.addListener(SWT.Selection, runListener);

		MenuItem deleteItem = new MenuItem(this.menu, SWT.PUSH);
		deleteItem.setText("Delete");
		this.deleteListener = new DeleteListener();
		deleteItem.addListener(SWT.Selection, this.deleteListener);

		MenuItem changeItem = new MenuItem(this.menu, SWT.PUSH);
		changeItem.setText("Change");
		changeItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				handleInput();
			}
		});

		this.editor = new TreeEditor(this.tree);
		this.editor.horizontalAlignment = SWT.LEFT;
		this.editor.grabHorizontal = true;
		this.editor.minimumWidth = 50;

		// create white spacemennu
		this.whiteSpacemenu = new Menu(tree);
		this.whiteSpacemenu.setVisible(false);

		MenuItem newItem = new MenuItem(this.whiteSpacemenu, SWT.PUSH);
		newItem.setText("New Workflow");
		
		MenuItem loadItem = new MenuItem(this.whiteSpacemenu, SWT.PUSH);
		loadItem.setText("Load");
		this.loadListener = new LoadListener();
		loadItem.addListener(SWT.Selection, this.loadListener);
        
		guiRun(new Runnable() {
			public void run() {
				try {
					IWorkbenchPage page = WorkflowView.this.getSite().getPage();

					page.showView("org.cishell.reference.gui.datamanager.DataManagerView");
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

				}
			
		});
        
				newItem.addListener(SWT.Selection, new NewWorkflow());

		addNewWorkflow("Workflow ");
		SchedulerContentModel.getInstance().register(this);
	}

	protected String getMetaTypeID(ServiceReference ref) {
		String pid = (String) ref.getProperty(Constants.SERVICE_PID);
		String metatype_pid = (String) ref
				.getProperty(AlgorithmProperty.PARAMETERS_PID);
		// String metatype_pid ="parameters_pid2;
		if (metatype_pid == null) {
			metatype_pid = pid;
		}

		return metatype_pid;
	}



	@Override
	public void algorithmScheduled(Algorithm algorithm, Calendar time) {
	}

	@Override
	public void algorithmRescheduled(Algorithm algorithm, Calendar time) {
	}

	@Override
	public void algorithmUnscheduled(Algorithm algorithm) {
	}

	@Override
	public void algorithmStarted(Algorithm algorithm) {
	}

	public void addNewWorkflow(String name) {
		Workflow workfFlow = WorkflowManager.getInstance().createWorkflow(name,
				Constant.NormalWorkflow);
		final WorkflowGUI dataItem = new WorkflowGUI(workfFlow,
				this.currentWorkFlowItem, 1);
		this.currentWorkFlowItem = dataItem;
		this.currentParentItem = dataItem;
		this.rootItem.addChild(dataItem);
		refresh(dataItem);
	}

	@Override
	public void algorithmFinished(Algorithm algorithm, Data[] createdData) {
		if (mode == WorkflowMode.RUNNING)
			return;
		Dictionary<String, Object> parameters = null;
		if (algorithm instanceof AlgorithmWrapper) {

			AlgorithmWrapper algo = (AlgorithmWrapper) algorithm;
			parameters = algo.getParameters();
		}
		// get service reference
		ServiceReference serviceReference = Activator.getSchedulerService()
				.getServiceReference(algorithm);
		String algorithmLabel = "";
		if (serviceReference != null) {
			algorithmLabel = (String) serviceReference
					.getProperty(AlgorithmProperty.LABEL);
		}
		AlgorithmFactory factory = (AlgorithmFactory) Activator.getContext()
				.getService(serviceReference);

		String pid = (String) serviceReference
				.getProperty(Constants.SERVICE_PID);

		AlgorithmWorkflowItem wfi = new AlgorithmWorkflowItem(algorithmLabel,
				((NormalWorkflow) currentWorkFlowItem.getWorkflow())
						.getUniqueInternalId(), pid);
		wfi.setParameters(parameters);
		wfi.setWorkflow(currentWorkFlowItem.getWorkflow());
		currentWorkFlowItem.getWorkflow().add(wfi);
		
		final AlgorithmItemGUI dataItem = new AlgorithmItemGUI(wfi,
				this.currentParentItem);
		this.currentParentItem.addChild(dataItem);
		// System.out.println("current Parent Item !!!!!!!!!!"+
		this.currentParentItem = dataItem;
		refresh(dataItem);
		// Create algorithm parameters.
		String metatypePID = getMetaTypeID(serviceReference);
		// get the input parameters
		MetaTypeProvider provider = null;

		try {
			provider = getPossiblyMutatedMetaTypeProvider(metatypePID, pid,
					factory, serviceReference);
		} catch (AlgorithmCreationFailedException e) {
			String format = "An error occurred when creating the algorithm \"%s\" with the data you "
					+ "provided.  (Reason: %s)";
			String logMessage = String.format(format,
					serviceReference.getProperty(AlgorithmProperty.LABEL),
					e.getMessage());
			// log(LogService.LOG_ERROR, logMessage, e);

			return;
		} catch (Exception e) {
			return;
		}

		if (parameters == null || parameters.isEmpty())
			return;

		final GeneralTreeItem paramItem = new GeneralTreeItem("Parameters",
				Constant.Label, dataItem, Utils.getImage("matrix.png",
						"org.cishell.reference.gui.workflow"));
		dataItem.addChild(paramItem);
		ObjectClassDefinition obj = provider.getObjectClassDefinition(
				metatypePID, null);
		if (obj != null) {
			AttributeDefinition[] attr = obj
					.getAttributeDefinitions(ObjectClassDefinition.ALL);

			for (int i = 0; i < attr.length; i++) {
				String id = attr[i].getID();
				String name = attr[i].getName();
				// add this into the hashmap of Algorithm Item
				wfi.add(name, id);
				Object valueRaw = parameters.get(id);
				String value = "";
				if (valueRaw != null) {
					value = valueRaw.toString();
				}
				// System.out.println( "id=" +id +"name="+ name +"\n");
				GeneralTreeItem paramName = new GeneralTreeItem(name,
						Constant.ParameterName, paramItem, Utils.getImage(
								"matrix.png",
								"org.cishell.reference.gui.workflow"));
				paramItem.addChildren(paramName);
				GeneralTreeItem paramValue = new GeneralTreeItem(value,
						Constant.ParameterValue, paramName, Utils.getImage(
								"matrix.png",
								"org.cishell.reference.gui.workflow"));
				paramName.addChildren(paramValue);
			}
		}
        
		refresh(paramItem);
	}
	
	private void refresh(final WorkflowTreeItem item)
	{
		guiRun(new Runnable() {
			public void run() {
				if (!tree.isDisposed()) {
					// update the TreeView
					WorkflowView.this.viewer.refresh();
					// update the global selection
					WorkflowView.this.viewer.expandToLevel(item, 0);
				}
			}
		});
		
	}

	@Override
	public void algorithmError(Algorithm algorithm, Throwable error) {
	}

	@Override
	public void schedulerRunStateChanged(boolean isRunning) {	}

	@Override
	public void schedulerCleared() {
	}

	private void guiRun(Runnable run) {
		if (Thread.currentThread() == Display.getDefault().getThread()) {
			run.run();
		} else {
			Display.getDefault().syncExec(run);
		}
	}

	public WorkflowMode getMode() {
		return mode;
	}

	protected MetaTypeProvider getPossiblyMutatedMetaTypeProvider(
			String metatypePID, String pid, AlgorithmFactory factory,
			ServiceReference serviceRef)
			throws AlgorithmCreationFailedException {
		MetaTypeProvider provider = null;
		MetaTypeService metaTypeService = (MetaTypeService) Activator
				.getService(MetaTypeService.class.getName());
		if (metaTypeService != null) {
			provider = metaTypeService.getMetaTypeInformation(serviceRef
					.getBundle());
		}
		return provider;
	}

	/*
	 * Listens for right-clicks on TreeItems and opens the context menu when
	 * needed.
	 */
	private class ContextMenuListener extends MouseAdapter {
		public void mouseUp(MouseEvent event) {
			if (event.button == 3) {

				TreeItem item = WorkflowView.this.tree.getItem(new Point(
						event.x, event.y));

				if (item != null) {
					WorkflowView.this.menu.setVisible(true);
					WorkflowView.this.whiteSpacemenu.setVisible(false);
				} else {
					WorkflowView.this.menu.setVisible(false);
					WorkflowView.this.whiteSpacemenu.setVisible(true);
				}
			}
		}
	}

	public boolean isRootItem(WorkflowGUI wfg) {
		if (this.rootItem.equals(wfg))
			return true;
		return false;
	}
   
	public void UpdateUI(){
		ManageView mview = new ManageView();
		mview.updateUI(this.tree, this.viewer);
	}
     
	public void addWorflowtoUI(Workflow wf){
		ManageView mview = new ManageView();
		mview.addworkflow(this.rootItem,wf);
		this.viewer.refresh();

	}

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

		this.newEditor = new Text(this.tree, SWT.NONE);
		this.newEditor.setText(item.getText());
		this.newEditor.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				if (!updatingTreeItem) {
					updateText(newEditor.getText(), item);
				}
			}
		});
		// ENTER ESC
		this.newEditor.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if ((e.character == SWT.CR)
						&& !WorkflowView.this.updatingTreeItem) {
					updateText(WorkflowView.this.newEditor.getText(), item);
				} else if (e.keyCode == SWT.ESC) {
					WorkflowView.this.newEditor.dispose();
				}
			}
		});
		this.newEditor.selectAll();
		this.newEditor.setFocus();
		this.editor.setEditor(this.newEditor, item);
	}

	private void updateText(String newLabel, TreeItem item) {
		this.updatingTreeItem = true;

		if (newLabel.startsWith(">"))
			newLabel = newLabel.substring(1);

		this.editor.getItem().setText(newLabel);
		WorkflowTreeItem wfTreeItem = (WorkflowTreeItem) item.getData();
		if (wfTreeItem.getType() == Constant.ParameterValue) {
           try{
			String paramName = wfTreeItem.getParent().getLabel();
			WorkflowTreeItem alfoITem = wfTreeItem.getParent().getParent()
					.getParent();

			System.out.println(" !!!!Type of the Object" + alfoITem.getType());
			AlgorithmWorkflowItem wfg = (AlgorithmWorkflowItem) ((AlgorithmItemGUI) alfoITem)
					.getWfItem();
			Object obj = wfg.getParameterValue(paramName);
			if (obj != null) {
				// As reflecion does not work it is a work around
				if (obj instanceof String) {
					obj = newLabel;
				} else if (obj instanceof Integer) {
					obj = Integer.parseInt(newLabel);
				} else if (obj instanceof java.lang.Boolean) {
					obj = Boolean.parseBoolean(newLabel);
				} else if (obj instanceof java.lang.Float) {
					obj = Float.parseFloat(newLabel);
				} else if (obj instanceof java.lang.Double) {
					obj = Double.parseDouble(newLabel);
				} else if (obj instanceof java.lang.Long) {
					obj = Long.parseLong(newLabel);
				} else if (obj instanceof java.lang.Short) {
					obj = Short.parseShort(newLabel);
				}
			} else {
				obj = newLabel;
			}
			wfg.addParameter(paramName, obj);
			System.out.println("parameter is" + obj);
			wfTreeItem.setLabel(newLabel);
           }
           catch(Exception e)
           {
        	   viewer.refresh();
       		this.newEditor.dispose();
       		updatingTreeItem = false;
           }
		} else if (wfTreeItem.getType() == Constant.Workflow) {
			  wfTreeItem.setLabel(newLabel);
			((WorkflowGUI) wfTreeItem).getWorkflow().setName(newLabel);
		}
		viewer.refresh();
		this.newEditor.dispose();
		updatingTreeItem = false;
	}

	private class DatamodelSelectionListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
		}
	}

	private class SaveListener implements Listener {
		public void handleEvent(Event event) {
			TreeItem[] items = WorkflowView.this.tree.getSelection();
			if (items.length != 1)
				return;
			WorkflowTreeItem itm = (WorkflowTreeItem) items[0].getData();
			String type = itm.getType();
			if (type == Constant.Workflow) {
				WorkflowGUI wfGUI = (WorkflowGUI) itm;
				System.out.println("Save " + wfGUI.getLabel() + " Type: "
						+ type);
			
			WorkflowMaker savedState = new WorkflowMaker();
			savedState.save(wfGUI.getWorkflow());
			}
		}
	}

	private class LoadListener implements Listener {

		@Override
		public void handleEvent(Event event) {
			WorkflowMaker loadState = new WorkflowMaker();
			loadState.load();

		}

	}

	private class DeleteListener implements Listener {

		@Override
		public void handleEvent(Event arg0) {
			try {
				TreeItem[] items = WorkflowView.this.tree.getSelection();
				if (items.length != 1)
					return;
				WorkflowTreeItem itm = (WorkflowTreeItem) items[0].getData();
				String type = itm.getType();
				if (type == Constant.Workflow) {
					WorkflowGUI wfGUI = (WorkflowGUI) itm;
					System.out.println("Delete " + wfGUI.getLabel() + " Type:"
							+ type);
					WorkflowManager.getInstance().removeWorkflow(
							wfGUI.getWorkflow());// model
					itm.removeAllChildren();// GUI
					rootItem.removeChild(wfGUI);// GUI
					WorkflowView.this.viewer.refresh();
					if (WorkflowView.this.rootItem.getRootsChildren().length == 0 || WorkflowView.this.currentWorkFlowItem == wfGUI) {
						WorkflowView.this.addNewWorkflow("New Workflow");
					}
				} else if (type == Constant.AlgorithmUIItem) {
					AlgorithmItemGUI aiGUI = (AlgorithmItemGUI) itm;
					System.out.println("Delete " + aiGUI.getLabel() + " Type:"
							+ type);
					AlgorithmWorkflowItem wfItem = (AlgorithmWorkflowItem) aiGUI
							.getWfItem();
					Workflow wf = wfItem.getWorkflow();

					WorkflowTreeItem parent = itm.getParent();// GUI
					itm.removeAllChildren();
					parent.removeChild(itm);
					//rootItem.removeChild(aiGUI);
					WorkflowView.this.viewer.refresh();
					wf.remove(wfItem);// model
					if (parent.getChildren().length == 0 ||WorkflowView.this.currentParentItem == aiGUI) {
						WorkflowView.this.currentParentItem = parent;
					}

				} else {
					System.out.println("Cant Delete GeneralItem");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private class RunListener implements Listener {
		public void handleEvent(Event event) {
			TreeItem[] items = WorkflowView.this.tree.getSelection();
			if (items.length != 1)
				return;
			WorkflowTreeItem itm = (WorkflowTreeItem) items[0].getData();
			String type = itm.getType();
			if (type == Constant.Workflow) {
				WorkflowView.this.mode = WorkflowMode.RUNNING;
				((WorkflowGUI) itm).getWorkflow().run();
				WorkflowView.this.mode = WorkflowMode.STOPPED;
			}
		}
	}

	private class NewWorkflow implements Listener {
		public void handleEvent(Event event) {
			WorkflowView.this.addNewWorkflow("New Workflow");

		}
	}

	@Override
	public void setFocus() {		
	}
}

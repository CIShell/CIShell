package org.cishell.reference.gui.workflow.views;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.cishell.reference.gui.workflow.Utilities.Constant;
import org.cishell.reference.gui.workflow.Utilities.Utils;
import org.cishell.reference.gui.workflow.controller.WorkflowManager;
import org.cishell.reference.gui.workflow.model.AlgorithmWorkflowItem;
import org.cishell.reference.gui.workflow.model.NormalWorkflow;
import org.cishell.reference.gui.workflow.model.Workflow;
import org.cishell.reference.gui.workflow.model.WorkflowItem;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;

public class ManageView {

	public void updateUI(Tree tree, TreeViewer viewer) {
		System.out.print("Inside update ui");

		tree.clearAll(true);

		WorkflowTreeItem rootItem = new WorkflowGUI(null, null, 2);
		viewer.setInput(rootItem);
		viewer.expandAll();

		LinkedHashMap<Long, Workflow> map = WorkflowManager.getInstance()
				.getMap();
		System.out.println("size of map " + map.size());
		for (Map.Entry<Long, Workflow> entry : map.entrySet()) {
			System.out.print("Workflow");
			NormalWorkflow wf = (NormalWorkflow) entry.getValue();
			WorkflowTreeItem wfnew = new WorkflowGUI(wf,
					(WorkflowGUI) rootItem, 0);
			rootItem.addChild(wfnew);
			LinkedHashMap<Long, WorkflowItem> itemMap = wf.getMap();
			WorkflowTreeItem parent = wfnew;
			for (Map.Entry<Long, WorkflowItem> itemEntry : itemMap.entrySet()) {
				AlgorithmWorkflowItem algoItem = (AlgorithmWorkflowItem) itemEntry
						.getValue();
				final AlgorithmItemGUI dataItem = new AlgorithmItemGUI(
						algoItem, parent);
				parent.addChild(dataItem);
				parent = dataItem;

				Dictionary<String, Object> params = algoItem.getParameters();
				if (params == null || params.isEmpty())
					continue;
				final GeneralTreeItem paramLabel = new GeneralTreeItem(
						"Parameters", Constant.Label, dataItem, Utils.getImage(
								"matrix.png",
								"org.cishell.reference.gui.workflow"));
				dataItem.addChild(paramLabel);

				for (Enumeration e = params.keys(); e.hasMoreElements();) {
					String key = (String) e.nextElement();
					String strvalue = "";
					Object value = params.get(key);
					if (value != null) {
						strvalue = value.toString();
					}

					GeneralTreeItem paramName = new GeneralTreeItem(key,
							Constant.ParameterName, paramLabel, Utils.getImage(
									"matrix.png",
									"org.cishell.reference.gui.workflow"));

					paramLabel.addChildren(paramName);
					GeneralTreeItem paramValue = new GeneralTreeItem(strvalue,
							Constant.ParameterValue, paramName, Utils.getImage(
									"matrix.png",
									"org.cishell.reference.gui.workflow"));
					paramName.addChildren(paramValue);

				}
			}
		}
		viewer.refresh();
		viewer.expandAll();
	}

	void addworkflow(WorkflowGUI rootItem, Workflow workflow) {
		NormalWorkflow wf = (NormalWorkflow) workflow;
		WorkflowGUI wfnew = new WorkflowGUI(wf, (WorkflowGUI) rootItem, 0);
		rootItem.addChild(wfnew);
		LinkedHashMap<Long, WorkflowItem> itemMap = wf.getMap();
		WorkflowTreeItem parent = wfnew;
		for (Map.Entry<Long, WorkflowItem> itemEntry : itemMap.entrySet()) {

			AlgorithmWorkflowItem algoItem = (AlgorithmWorkflowItem) itemEntry
					.getValue();
			final AlgorithmItemGUI dataItem = new AlgorithmItemGUI(algoItem,
					parent);
			parent.addChild(dataItem);
			parent = dataItem;

			Dictionary<String, Object> params = algoItem.getParameters();
			Dictionary<String, String> nameToId = algoItem.getNameToId();

			if (params == null || params.isEmpty())
				continue;
			final GeneralTreeItem paramLabel = new GeneralTreeItem(
					"Parameters", Constant.Label, dataItem, Utils.getImage(
							"matrix.png", "org.cishell.reference.gui.workflow"));
			dataItem.addChild(paramLabel);

			for (Enumeration e = nameToId.keys(); e.hasMoreElements();) {
				String key = (String) e.nextElement();
				String strvalue = "";
				Object value = algoItem.getParameterValue(key);
				if (value != null) {
					strvalue = value.toString();
				}
				GeneralTreeItem paramName = new GeneralTreeItem(key,
						Constant.ParameterName, paramLabel, Utils.getImage(
								"matrix.png",
								"org.cishell.reference.gui.workflow"));

				paramLabel.addChildren(paramName);
				GeneralTreeItem paramValue = new GeneralTreeItem(strvalue,
						Constant.ParameterValue, paramName, Utils.getImage(
								"matrix.png",
								"org.cishell.reference.gui.workflow"));
				paramName.addChildren(paramValue);

			}
		}

	}

}

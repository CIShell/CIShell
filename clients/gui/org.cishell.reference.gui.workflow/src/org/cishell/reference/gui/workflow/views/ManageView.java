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
	private final String PARAMETER_IMAGE_NAME = "parameters.png";

	public void updateUI(Tree tree, TreeViewer viewer, String brandPluginID) {
		tree.clearAll(true);

		WorkflowTreeItem rootItem = new WorkflowGUI(null, null, 2,
				brandPluginID);
		viewer.setInput(rootItem);
		viewer.expandAll();

		LinkedHashMap<Long, Workflow> map = WorkflowManager.getInstance()
				.getMap();
		for (Map.Entry<Long, Workflow> entry : map.entrySet()) {
			NormalWorkflow wf = (NormalWorkflow) entry.getValue();
			WorkflowTreeItem wfnew = new WorkflowGUI(wf,
					(WorkflowGUI) rootItem, 0, brandPluginID);
			rootItem.addChild(wfnew);
			LinkedHashMap<Long, WorkflowItem> itemMap = wf.getMap();
			WorkflowTreeItem parent = wfnew;
			for (Map.Entry<Long, WorkflowItem> itemEntry : itemMap.entrySet()) {
				AlgorithmWorkflowItem algoItem = (AlgorithmWorkflowItem) itemEntry
						.getValue();
				final AlgorithmItemGUI dataItem = new AlgorithmItemGUI(
						algoItem, parent, brandPluginID);
				parent.addChild(dataItem);
				parent = dataItem;

				Dictionary<String, Object> params = algoItem.getParameters();
				if (params == null || params.isEmpty())
					continue;
				final GeneralTreeItem paramLabel = new GeneralTreeItem(
						"Parameters", Constant.Label, dataItem, Utils.getImage(
								PARAMETER_IMAGE_NAME, brandPluginID));
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
									PARAMETER_IMAGE_NAME, brandPluginID));

					paramLabel.addChildren(paramName);
					GeneralTreeItem paramValue = new GeneralTreeItem(strvalue,
							Constant.ParameterValue, paramName, Utils.getImage(
									PARAMETER_IMAGE_NAME, brandPluginID));
					paramName.addChildren(paramValue);

				}
			}
		}
		viewer.refresh();
		viewer.expandAll();
	}

	void addworkflow(WorkflowGUI rootItem, Workflow workflow,
			String brandPluginID) {
		NormalWorkflow wf = (NormalWorkflow) workflow;
		WorkflowGUI wfnew = new WorkflowGUI(wf, (WorkflowGUI) rootItem, 0,
				brandPluginID);
		rootItem.addChild(wfnew);
		LinkedHashMap<Long, WorkflowItem> itemMap = wf.getMap();
		WorkflowTreeItem parent = wfnew;
		for (Map.Entry<Long, WorkflowItem> itemEntry : itemMap.entrySet()) {

			AlgorithmWorkflowItem algoItem = (AlgorithmWorkflowItem) itemEntry
					.getValue();
			final AlgorithmItemGUI dataItem = new AlgorithmItemGUI(algoItem,
					parent, brandPluginID);
			parent.addChild(dataItem);
			parent = dataItem;

			Dictionary<String, Object> params = algoItem.getParameters();
			Dictionary<String, String> nameToId = algoItem.getNameToId();

			if (params == null || params.isEmpty())
				continue;
			final GeneralTreeItem paramLabel = new GeneralTreeItem(
					"Parameters", Constant.Label, dataItem, Utils.getImage(
							PARAMETER_IMAGE_NAME, brandPluginID));
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
								PARAMETER_IMAGE_NAME, brandPluginID));

				paramLabel.addChildren(paramName);
				GeneralTreeItem paramValue = new GeneralTreeItem(strvalue,
						Constant.ParameterValue, paramName, Utils.getImage(
								PARAMETER_IMAGE_NAME, brandPluginID));
				paramName.addChildren(paramValue);

			}
		}

	}

}

package org.cishell.reference.gui.workflow.model;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.cishell.app.service.datamanager.DataManagerService;
import org.cishell.framework.data.Data;
import org.cishell.reference.gui.workflow.Activator;
import org.osgi.framework.BundleContext;

public class NormalWorkflow implements Workflow {
	private String name;
	private Long id;
	private Long lastCreatedID;
	private LinkedHashMap<Long, WorkflowItem> itemMap;

	public LinkedHashMap<Long, WorkflowItem> getMap() {
		return itemMap;
	}

	public void setMap(LinkedHashMap<Long, WorkflowItem> map) {
		this.itemMap = map;
	}

	public NormalWorkflow(String name, Long id) {
		this.name = name;
		this.id = id;
		itemMap = new LinkedHashMap<Long, WorkflowItem>();
		lastCreatedID = new Long(1);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Long getInternalId() {
		return id;
	}

	@Override
	public void setInternalId(Long id) {
		this.id = id;
	}

	@Override
	public void run() {
		BundleContext bundleContext = Activator.getContext();
		DataManagerService dataManager = (DataManagerService) bundleContext
				.getService(bundleContext
						.getServiceReference(DataManagerService.class.getName()));
		Data[] data = dataManager.getSelectedData();

		for (Map.Entry<Long, WorkflowItem> entry : itemMap.entrySet()) {
			WorkflowItem item = entry.getValue();
			if (item instanceof AlgorithmWorkflowItem) {
				AlgorithmWorkflowItem algo = (AlgorithmWorkflowItem) item;
				algo.setInputData(data);
				data = (Data[]) algo.run();
			}
		}
		if (data != null && data.length != 0) {
			for (int ii = 0; ii < data.length; ii++) {
				dataManager.addData(data[ii]);
			}
			dataManager.setSelectedData(data);

		}
	}

	@Override
	public void add(WorkflowItem item) {
		itemMap.put(item.getItemId(), item);
	}

	public Long getUniqueInternalId() {
		while (itemMap.containsKey(lastCreatedID))
			lastCreatedID++;
		return lastCreatedID;
	}

	// Removes the given item and all following entries under the selected item
	public void remove(WorkflowItem item) {
		try {
			Set<Entry<Long, WorkflowItem>> set = itemMap.entrySet();
			Iterator<Entry<Long, WorkflowItem>> iterator = set.iterator();
			while (iterator.hasNext()) {
				Entry<Long, WorkflowItem> me = iterator.next();
				// Currently, the model hierarchy is based on numerical order.
				// This is not a safe approach.
				// We will eventually need to add a parent/child relationship to
				// the model.
				if (me.getKey() >= item.getItemId()) {
					iterator.remove();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}

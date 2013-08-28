package org.cishell.reference.gui.workflow.controller;

import org.cishell.reference.gui.workflow.model.Workflow;


public class WorkflowSaver {
	
	//private	LinkedHashMap<Long, Workflow> map;
	private Workflow currentWorkflow;
	
	public WorkflowSaver(Workflow wf)
	{
		this.currentWorkflow = wf;
		//this.map = WorkflowManager.getInstance().getMap();
	}

	/*public LinkedHashMap<Long, Workflow> getMap() {
		return map;
	}

	public void setMap(LinkedHashMap<Long, Workflow> map) {
		this.map = map;
	}*/

	public Workflow getCurrentWorkflow() {
		return currentWorkflow;
	}

	public void setCurrentWorkflow(Workflow currentWorkflow) {
		this.currentWorkflow = currentWorkflow;
	}		
				
}

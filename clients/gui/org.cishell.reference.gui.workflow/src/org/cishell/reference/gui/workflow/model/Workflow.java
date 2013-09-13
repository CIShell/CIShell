package org.cishell.reference.gui.workflow.model;

public interface Workflow {
	
	public String  getName();
	public void setName(String name);
	// user can create workflows with the same name
	public Long getInternalId();
	//Called to run the workflow
	public void run();
	//add a new workflow item to this worflow
	public void add(WorkflowItem item);
	//remove a workflow item to this workflow
	public void remove(WorkflowItem item);
	//sets the internal id, used for persistance
	public void setInternalId(Long id);


}

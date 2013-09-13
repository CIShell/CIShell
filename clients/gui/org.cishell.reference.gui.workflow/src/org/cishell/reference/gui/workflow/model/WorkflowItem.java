package org.cishell.reference.gui.workflow.model;

public interface WorkflowItem {
	
	// provides the type of the workflow Item
	// is it algorithmic, preprocessing etc
	public String getType();
	// run the item as a command object
	public Object[] run();
	//get the name of the workflow Item
	public String getName();
	//get the internal Id
	public Long getIternalId();    
}

package org.cishell.reference.gui.workflow.views;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.swt.graphics.Image;

public class GeneralTreeItem implements WorkflowTreeItem {
	
	private String label;
	private String type;
	private WorkflowTreeItem parent;
	private Image icon;
    private Collection<WorkflowTreeItem> children = new ArrayList<WorkflowTreeItem>();

	public GeneralTreeItem(String label,String type,WorkflowTreeItem parent,Image icon )
	{
		this.label = label;
		this.type = type;
		this.parent = parent;
		this.icon = icon;		
	}

	@Override
	public String getLabel() {
		
		return label;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public WorkflowTreeItem getParent() {
		return parent;
	}

	@Override
	public Image getIcon() {
		return icon;
	}

	@Override
	public Object[] getChildren() {
		return this.children.toArray();
	}
	
	public void addChildren(WorkflowTreeItem child){
		children.add(child);
	}

	@Override
	public void setLabel(String label) {
       this.label = label;		
	}
	public void removeAllChildren() {
		children.clear();
		
	}

	@Override
	public boolean hasChild(WorkflowTreeItem wfTreeItem) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeChild(WorkflowTreeItem wfTreeItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addChild(WorkflowTreeItem wfTreeItem) {
		// TODO Auto-generated method stub
		
	}

}

package org.cishell.reference.prefs.admin.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class BasicTree {
	
	private ListMap parentToChildren;
	private Map childToParent;
	
	private Object root;
	
	public BasicTree() {
		parentToChildren = new ListMap();
		childToParent = new Hashtable();
	}
	
	//WARNING: basically taking the users word on this
	public void setRoot(Object root) {
		this.root = root;
	}
	
	public Object getRoot() {
		return root;
	}
	
	public void addEdge(Object parent, Object child) {
		System.out.println("Adding edge from " + parent + " to " + child);
		parentToChildren.put(parent, child);
		childToParent.put(child, parent);
	}
	
	public List getChildren(Object node) {
		List children = parentToChildren.get(node);
		if (children == null) {
			System.out.println("Getting empty children list");
			return new ArrayList();
		} else {
			System.out.println("Getting children list of size "+ children.size());
			return children;
		}
	}
	
	public Object getParent(Object node) {
		return childToParent.get(node);
	}
	
	public List getAllNodes() {
		List childNodes = getChildrenRecursive(root);
		if (root != null) {
		childNodes.add(root);
		} 
		
		return childNodes;
		}
	
	//inefficient, but oh well (for now)
	private List getChildrenRecursive(Object node) {
		if (node == null) {
			return new ArrayList();
		} else {
			List childrenRecursive = new ArrayList();
			List children = getChildren(node);
			if (children != null) {
				childrenRecursive.addAll(children);
				for (int ii = 0; ii < children.size(); ii++) {
					Object child = children.get(ii);
					List childsChildrenRecursive = getChildrenRecursive(child);
					childrenRecursive.addAll(childsChildrenRecursive);
			}
			}
			
			return childrenRecursive;
		}
	}
}

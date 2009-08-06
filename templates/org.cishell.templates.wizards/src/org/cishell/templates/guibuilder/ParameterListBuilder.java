package org.cishell.templates.guibuilder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

public class ParameterListBuilder {
    private ListBuilder builder;
    private ParameterBuilderDelegate delegate;

    public ParameterListBuilder(Composite parent) {
        this(parent, SWT.NONE);
    }
    
    public ParameterListBuilder(Composite parent, int style) {
        delegate = new ParameterBuilderDelegate(parent);
        builder = new ListBuilder(parent, style, delegate);
    }
    
    public Composite getComposite() {
        return builder.getPanel();
    }
    
    public EditableAttributeDefinition[] getCreatedAttributes() {
        Display display = Display.getDefault();
        
        if (display != null) {
            GetAttributeDefinitionsAction action =
            	new GetAttributeDefinitionsAction();
            display.syncExec(action);
            
            return action.attributes;
        } else {
            return new EditableAttributeDefinition[0];
        }
    }
    
    private class GetAttributeDefinitionsAction implements Runnable {
        EditableAttributeDefinition[] attributes;

        public void run() {
            TableItem[] items = builder.getTable().getItems();
            attributes = new EditableAttributeDefinition[items.length];
            
            for (int ii = 0; ii < items.length; ii++) {
                attributes[ii] =
                	delegate.getAttributeDefinition(items[ii].getText(0));
            }
        }
    }
}

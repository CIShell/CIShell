/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 16, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.templates.guibuilder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

public class ParameterListBuilder {
    protected ListBuilder builder;
    protected ParameterBuilderDelegate delegate;

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
            GetAttributeDefinitionsAction action = new GetAttributeDefinitionsAction();
            display.syncExec(action);
            return action.attrs;
        } else {
            return new EditableAttributeDefinition[0];
        }
    }
    
    private class GetAttributeDefinitionsAction implements Runnable {
        EditableAttributeDefinition[] attrs;

        public void run() {
            TableItem[] items = builder.getTable().getItems();
            attrs = new EditableAttributeDefinition[items.length];
            
            for (int i=0; i < items.length; i++) {
                attrs[i] = delegate.getAttributeDefinition(items[i].getText(0));
            }
        }
    }
}

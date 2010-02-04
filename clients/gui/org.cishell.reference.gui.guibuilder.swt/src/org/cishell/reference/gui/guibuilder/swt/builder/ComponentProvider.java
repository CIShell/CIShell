/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Sep 15, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.gui.guibuilder.swt.builder;

import org.cishell.reference.gui.guibuilder.swt.builder.components.BooleanComponent;
import org.cishell.reference.gui.guibuilder.swt.builder.components.DirectoryComponent;
import org.cishell.reference.gui.guibuilder.swt.builder.components.FileComponent;
import org.cishell.reference.gui.guibuilder.swt.builder.components.LabelingComponent;
import org.cishell.reference.gui.guibuilder.swt.builder.components.StringComponent;
import org.osgi.service.metatype.AttributeDefinition;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class ComponentProvider {
    private static final ComponentProvider INSTANCE = new ComponentProvider();
    private ComponentProvider() {}
    
    public static ComponentProvider getInstance() {
        return INSTANCE;
    }
    
    public GUIComponent createComponent(AttributeDefinition attr) {
        GUIComponent component = createBasicComponent(attr);
        
        component = new LabelingComponent(component);
        
        return component;
    }
    
    public GUIComponent createBasicComponent(AttributeDefinition attr) {
        GUIComponent component = null;
        switch (attr.getType()) {
        case (AttributeDefinition.BOOLEAN):
            component = new BooleanComponent();
            break;
        case (AttributeDefinition.STRING):
            String[] defaultValue = attr.getDefaultValue();
            if (defaultValue != null && defaultValue.length == 1 
                    && defaultValue[0] != null) {
                if (defaultValue[0].startsWith("file:")) {
                    component = new FileComponent();
                    break;
                } else if (defaultValue[0].startsWith("directory:")) {
                    component = new DirectoryComponent();
                    break;
                } else if(defaultValue[0].startsWith("textarea:")) {
                	component = new StringComponent(true);
                	break;
                }
            }
        case (AttributeDefinition.BYTE):
        case (AttributeDefinition.CHARACTER):
        case (AttributeDefinition.DOUBLE):
        case (AttributeDefinition.FLOAT):
        case (AttributeDefinition.LONG):
        case (AttributeDefinition.SHORT):
        case (AttributeDefinition.INTEGER):
        default:
            component = new StringComponent();
            break;
        }
        
        component.setAttributeDefinition(attr);
        
        return component;
    }
}

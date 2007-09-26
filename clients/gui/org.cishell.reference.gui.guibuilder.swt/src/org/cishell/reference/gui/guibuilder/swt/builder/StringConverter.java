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

import org.osgi.service.metatype.AttributeDefinition;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class StringConverter {
    private static final StringConverter INSTANCE = new StringConverter();
    private StringConverter() {}
    
    public static StringConverter getInstance() {
        return INSTANCE;
    }

    public Object stringToObject(AttributeDefinition attr, String string) {
        Object value;
        
        try {
            switch (attr.getType()) {
            case (AttributeDefinition.STRING):                
                value = string;
                break;
            case (AttributeDefinition.BOOLEAN):
                value = new Boolean(string);
                break;
            case (AttributeDefinition.BYTE):
                value = new Byte(string);
                break;
            case (AttributeDefinition.CHARACTER):
                if (string != null && string.length() == 1) {
                    value = new Character(string.charAt(0));
                } else {
                    value = null;
                }
                break;
            case (AttributeDefinition.DOUBLE):
                value = new Double(string);
                break;
            case (AttributeDefinition.FLOAT):
                value = new Float(string);
                break;
            case (AttributeDefinition.LONG):
                value = new Long(string);
                break;
            case (AttributeDefinition.SHORT):
                value = new Short(string);
                break;
            case (AttributeDefinition.INTEGER):
                value = new Integer(string);
                break;
            default:
                value = string;
                break;
            }
        } catch (Throwable e) {
            value = null;
        }
        
        return value;
    }    
}

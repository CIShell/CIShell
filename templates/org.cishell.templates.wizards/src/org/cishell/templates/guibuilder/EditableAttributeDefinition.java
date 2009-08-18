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

import org.osgi.service.metatype.AttributeDefinition;

public class EditableAttributeDefinition implements AttributeDefinition {
    String id;
    String name;
    String description;
    String defaultValue;
    int type;
    String minValue;
    String maxValue;
    
    public EditableAttributeDefinition() {}
    
    public EditableAttributeDefinition(String id,
    								   String name,
    								   String description, 
    								   String defaultValue,
    								   int type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.type = type;
    }
    
    public String validate(String string) {
        return null;
    }
    
    public int getCardinality() {
        return 0;
    }
    
    public String[] getOptionLabels() {
        return null;
    }

    public String[] getOptionValues() {
        return null;
    }

    public String[] getDefaultValue() {
        return new String[] { defaultValue };
    }
    
    public String getActualDefaultValue() {
    	return defaultValue;
    }

    public String getDescription() {
        return description;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public void setDefaultValue(String[] defaultValue) {
        this.defaultValue = defaultValue[0];
    }
    
    public void setDefaultValue(String defaultValue) {
    	this.defaultValue = defaultValue;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setID(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }
}
package org.cishell.templates.wizards.utilities;

import org.cishell.templates.guibuilder.EditableAttributeDefinition;
import org.osgi.service.metatype.AttributeDefinition;

public class ParameterUtilities {
	public static final String ID_LABEL = "Unique ID";
	public static final String NAME_LABEL = "Name";
	public static final String INPUT_TYPE_LABEL = "Input Type";
	public static final String DESCRIPTION_LABEL = "Description";
	public static final String DEFAULT_VALUE_LABEL = "Default Value";
	public static final String MINIMUM_VALUE_LABEL = "Minimum Value";
	public static final String MAXIMUM_VALUE_LABEL = "Maximum Value";
	
	public static final String DEFAULT_FILE_VALUE = "file:";
	public static final String DEFAULT_DIRECTORY_VALUE = "directory:";
	
    public static final String[] TYPE_LABELS = new String[] {
        "String",
        "Integer",
        "Long",
        "Short",
        "Double",
        "Float",
        "Boolean",
        "Char", 
        "Byte",
        "File",
        "Directory"
    };
    
    public static final int[] TYPE_VALUES = new int[] {
        AttributeDefinition.STRING,
        AttributeDefinition.INTEGER,
        AttributeDefinition.LONG,
        AttributeDefinition.SHORT,
        AttributeDefinition.DOUBLE,
        AttributeDefinition.FLOAT,
        AttributeDefinition.BOOLEAN,
        AttributeDefinition.CHARACTER,
        AttributeDefinition.BYTE,
        AttributeDefinition.STRING,
        AttributeDefinition.STRING
    };
    
    public static final int TYPE_VALUE_INDEX_STRING = 0;
    public static final int TYPE_VALUE_INDEX_INTEGER = 1;
    public static final int TYPE_VALUE_INDEX_LONG = 2;
    public static final int TYPE_VALUE_INDEX_SHORT = 3;
    public static final int TYPE_VALUE_INDEX_DOUBLE = 4;
    public static final int TYPE_VALUE_INDEX_FLOAT = 5;
    public static final int TYPE_VALUE_INDEX_BOOLEAN = 6;
    public static final int TYPE_VALUE_INDEX_CHARACTER = 7;
    public static final int TYPE_VALUE_INDEX_BYTE = 8;
    public static final int TYPE_VALUE_INDEX_FILE = 9;
    public static final int TYPE_VALUE_INDEX_DIRECTORY = 10;
    
	public static boolean attributeHasFileOrDirectoryType(
    		EditableAttributeDefinition attribute) {
    	return attributeHasFileType(attribute) ||
    		   attributeHasDirectoryType(attribute);
    }
    
    public static boolean attributeHasFileType(
    		EditableAttributeDefinition attribute) {
    	String defaultValue = attribute.getActualDefaultValue();
    	
    	if ((attribute.getType() == AttributeDefinition.STRING) &&
    			defaultValue.equals(DEFAULT_FILE_VALUE)) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public static boolean attributeHasDirectoryType(
    		EditableAttributeDefinition attribute) {
    	String defaultValue = attribute.getActualDefaultValue();
    	
    	if ((attribute.getType() == AttributeDefinition.STRING) &&
    			defaultValue.equals(DEFAULT_DIRECTORY_VALUE)) {
    		return true;
    	} else {
    		return false;
    	}
    }
}
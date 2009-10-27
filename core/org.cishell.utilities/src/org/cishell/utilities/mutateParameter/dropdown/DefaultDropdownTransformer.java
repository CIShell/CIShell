package org.cishell.utilities.mutateParameter.dropdown;

import org.osgi.service.metatype.AttributeDefinition;

/**
 * A dropdown-forming AttributeDefinition transformer which by default performs no transformation.
 * This is a convenient access to DropdownTransformer where you may override
 * methods to transform only the arguments that you wish to modify.
 * The typical case would be extending shouldTransform (think of as a filter),
 * transformOptionLabels, and transformOptionValues.
 * 
 * @see org.cishell.utilities.MutateParameterUtilities#mutateToDropdown(ObjectClassDefinition, String, String[], String[])
 */
public abstract class DefaultDropdownTransformer extends DropdownTransformer {
	public abstract boolean shouldTransform(AttributeDefinition ad);

	public String transformID(String oldID) {
		return oldID;
	}

	public String transformName(String oldName) {
		return oldName;
	}

	public String transformDescription(String oldDescription) {
		return oldDescription;
	}

	public int transformType(int oldType) {
		return oldType;
	}

	public String[] transformOptionLabels(String[] oldOptionLabels) {
		return oldOptionLabels;
	}
	
	public String[] transformOptionValues(String[] oldOptionValues) {
		return oldOptionValues;
	}
}
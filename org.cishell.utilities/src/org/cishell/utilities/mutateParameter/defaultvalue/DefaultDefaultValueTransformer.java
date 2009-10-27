package org.cishell.utilities.mutateParameter.defaultvalue;

import org.osgi.service.metatype.AttributeDefinition;

/**
 * Default Default-Value Transformer (the first in the sense of this being the simplest
 * implementation of DefaultValueTransformer.
 * <p/>
 * A (single-valued) default-value-setting AttributeDefinition transformer
 * which by default performs no transformation.
 * <p/>
 * This is a convenient access to DefaultValueTransformer where you may override
 * methods to transform only the arguments that you wish to modify.
 * The typical case would be extending shouldTransform (think of as a filter)
 * and transformDefaultValue.
 * 
 * @see org.cishell.utilities.MutateParameterUtilities#mutateDefaultValue(ObjectClassDefinition, String, String)
 */
public abstract class DefaultDefaultValueTransformer extends DefaultValueTransformer {
	public abstract boolean shouldTransform(AttributeDefinition ad);

	public String transformDescription(String oldDescription) {
		return oldDescription;
	}

	public String transformID(String oldID) {
		return oldID;
	}

	public String transformName(String oldName) {
		return oldName;
	}

	public int transformType(int oldType) {
		return oldType;
	}
	
	public String transformDefaultValue(String oldDefaultValue) {
		return oldDefaultValue;
	}
}

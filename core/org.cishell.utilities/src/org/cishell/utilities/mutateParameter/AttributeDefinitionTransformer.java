package org.cishell.utilities.mutateParameter;

import org.osgi.service.metatype.AttributeDefinition;

/**
 * This interface and its sub-interfaces correspond to constructors of BasicObjectClassDefinition.
 */
public interface AttributeDefinitionTransformer {
	public boolean shouldTransform(AttributeDefinition ad);	
	public AttributeDefinition transform(AttributeDefinition oldAD);
	public String transformID(String oldID);
	public String transformName(String oldName);
	public String transformDescription(String oldDescription);
	public int transformType(int oldType);
}




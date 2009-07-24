package org.cishell.utilities.mutateParameter;

import org.osgi.service.metatype.AttributeDefinition;

/* There would be one implementation of this interface for each useful
 * constructor of BasicAttributeDefinition, where the transform is defined as in
 * DropdownTransformer.
 */
public interface AttributeDefinitionTransformer {
	public boolean shouldTransform(AttributeDefinition ad);	
	public AttributeDefinition transform(AttributeDefinition oldAD);
	public String transformID(String oldID);
	public String transformName(String oldName);
	public String transformDescription(String oldDescription);
	public int transformType(int oldType);
}




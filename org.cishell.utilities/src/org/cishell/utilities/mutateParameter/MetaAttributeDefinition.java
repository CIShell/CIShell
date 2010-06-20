package org.cishell.utilities.mutateParameter;

import org.osgi.service.metatype.AttributeDefinition;

public class MetaAttributeDefinition {
	private int type;
	private AttributeDefinition attributeDefinition;

	public MetaAttributeDefinition(int type, AttributeDefinition attributeDefinition) {
		this.type = type;
		this.attributeDefinition = attributeDefinition;
	}

	public int getType() {
		return this.type;
	}

	public AttributeDefinition getAttributeDefinition() {
		return this.attributeDefinition;
	}
}
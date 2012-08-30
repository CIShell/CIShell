package org.cishell.utilities.mutateParameter;

import org.osgi.service.metatype.AttributeDefinition;
/**
 * @deprecated see
 *            {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities}
 */
@Deprecated
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
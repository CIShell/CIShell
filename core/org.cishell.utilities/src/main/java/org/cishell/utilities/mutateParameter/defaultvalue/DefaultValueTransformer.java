package org.cishell.utilities.mutateParameter.defaultvalue;

import org.cishell.reference.service.metatype.BasicAttributeDefinition;
import org.cishell.utilities.mutateParameter.AttributeDefinitionTransformer;
import org.osgi.service.metatype.AttributeDefinition;
/**
 * @deprecated see
 *            {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities}
 */
@Deprecated
public abstract class DefaultValueTransformer implements AttributeDefinitionTransformer {
	/**
	 * @see BasicAttributeDefinition#BasicAttributeDefinition(String, String, String, int, String)
	 */
	public AttributeDefinition transform(AttributeDefinition oldAD) {
		if (shouldTransform(oldAD)) {
			return 
				new BasicAttributeDefinition(
					transformID(oldAD.getID()),
					transformName(oldAD.getName()),
					transformDescription(oldAD.getDescription()),
					transformType(oldAD.getType()),
					transformDefaultValue(oldAD.getDefaultValue()));
		} else {
			return oldAD;
		}
	}

	public abstract String transformDefaultValue(String[] oldDefaultValue);
}

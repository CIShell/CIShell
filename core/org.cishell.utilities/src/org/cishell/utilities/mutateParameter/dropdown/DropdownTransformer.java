package org.cishell.utilities.mutateParameter.dropdown;

import org.cishell.reference.service.metatype.BasicAttributeDefinition;
import org.cishell.utilities.mutateParameter.AttributeDefinitionTransformer;
import org.osgi.service.metatype.AttributeDefinition;
/**
 * @deprecated see
 *            {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities}
 */
@Deprecated
public abstract class DropdownTransformer
		implements AttributeDefinitionTransformer {
	/**
	 * @see BasicAttributeDefinition#BasicAttributeDefinition(String, String, String, int, String[], String[])
	 */
	public AttributeDefinition transform(AttributeDefinition oldAD) {
		if (shouldTransform(oldAD)) {
			return 
				new BasicAttributeDefinition(
					transformID(oldAD.getID()),
					transformName(oldAD.getName()),
					transformDescription(oldAD.getDescription()),
					transformType(oldAD.getType()),
					transformOptionLabels(oldAD.getOptionLabels()),
					transformOptionValues(oldAD.getOptionValues()));
		}
		else {
			return oldAD;
		}
	}
	
	public abstract String[] transformOptionLabels(String[] oldOptionLabels);
	public abstract String[] transformOptionValues(String[] oldOptionValues);
}


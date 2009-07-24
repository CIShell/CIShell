package org.cishell.utilities.mutateParameter;

import org.cishell.reference.service.metatype.BasicAttributeDefinition;
import org.osgi.service.metatype.AttributeDefinition;

public abstract class DropdownTransformer
		implements AttributeDefinitionTransformer {
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


package org.cishell.utilities.mutateParameter;

import java.util.Iterator;
import java.util.List;

import org.cishell.reference.service.metatype.BasicObjectClassDefinition;
import org.cishell.utilities.MutateParameterUtilities;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

public class ObjectClassDefinitionTransformer {
	private static final int OUTGOING_ATTRIBUTES_FILTER =
		ObjectClassDefinition.REQUIRED;
	private static final int INCOMING_ATTRIBUTES_FILTER =
		ObjectClassDefinition.ALL;

	/* Create newOCD from oldOCD by applying transformer
	 * to each AttributeDefinition.
	 */
	public static BasicObjectClassDefinition apply(
			AttributeDefinitionTransformer transformer,
			ObjectClassDefinition oldOCD) {		
		BasicObjectClassDefinition newOCD =
			MutateParameterUtilities.createNewParameters(oldOCD);
		
		AttributeDefinition[] oldADs =
			oldOCD.getAttributeDefinitions(INCOMING_ATTRIBUTES_FILTER);
		for (int ii = 0; ii < oldADs.length; ii++) {
			newOCD.addAttributeDefinition(
					OUTGOING_ATTRIBUTES_FILTER,
					transformer.transform(oldADs[ii]));
		}
		
		return newOCD;
	}
	
	// Convenience method for batching transformations.
	public static ObjectClassDefinition transform(
			ObjectClassDefinition ocd, List transformers) {
		ObjectClassDefinition newOCD = ocd;
		
		for (Iterator it = transformers.iterator(); it.hasNext();) {
			AttributeDefinitionTransformer transformer =
				(AttributeDefinitionTransformer) it.next();
			newOCD = apply(transformer, newOCD);
		}
		
		return newOCD;
	}
}
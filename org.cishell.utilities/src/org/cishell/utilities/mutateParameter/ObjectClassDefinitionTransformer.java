package org.cishell.utilities.mutateParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.cishell.reference.service.metatype.BasicObjectClassDefinition;
import org.cishell.utilities.MutateParameterUtilities;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

public class ObjectClassDefinitionTransformer {
	/**
	 * AttributeDefinition filters as described in ObjectClassDefinition.
	 * "Atomic" to exclude blanket filters like ObjectClassDefinition.ALL.
	 * @see ObjectClassDefinition#REQUIRED
	 * @see ObjectClassDefinition#OPTIONAL
	 * @see ObjectClassDefinition#ALL
	 */
	public static final List ATOMIC_ATTRIBUTE_DEFINITION_FILTERS;
	static {
		List l = new ArrayList();
		l.add(ObjectClassDefinition.REQUIRED);
		l.add(ObjectClassDefinition.OPTIONAL);
		ATOMIC_ATTRIBUTE_DEFINITION_FILTERS = Collections.unmodifiableList(l);
	}

	/* Create newOCD from oldOCD by applying transformer
	 * to each AttributeDefinition.
	 */
	public static BasicObjectClassDefinition apply(
			AttributeDefinitionTransformer transformer,
			ObjectClassDefinition oldOCD) {		
		BasicObjectClassDefinition newOCD =
			MutateParameterUtilities.createNewParameters(oldOCD);
		
		// For each kind of AttributeDefinition filter ..
		for (Iterator filterIt = ATOMIC_ATTRIBUTE_DEFINITION_FILTERS.iterator();
				filterIt.hasNext();) {
			int filter = ((Integer) filterIt.next()).intValue();
			
			// Grab all matching AttributeDefinitions and transform them.
			AttributeDefinition[] oldADs =
				oldOCD.getAttributeDefinitions(filter);
			
			for (int ii = 0; ii < oldADs.length; ii++) {
				newOCD.addAttributeDefinition(
							filter,
							transformer.transform(oldADs[ii]));
			}
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
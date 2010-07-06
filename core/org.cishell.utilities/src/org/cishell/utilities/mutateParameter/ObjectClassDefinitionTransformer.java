package org.cishell.utilities.mutateParameter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.cishell.reference.service.metatype.BasicObjectClassDefinition;
import org.cishell.utilities.MutateParameterUtilities;
import org.cishell.utilities.mutateParameter.dropdown.DropdownTransformer;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

public class ObjectClassDefinitionTransformer {
	/**
	 * AttributeDefinition filters as described in ObjectClassDefinition.
	 * "Atomic" to exclude blanket filters like ObjectClassDefinition.ALL.
	 * @see ObjectClassDefinition#REQUIRED
	 * @see ObjectClassDefinition#OPTIONAL
	 */
	public static final Collection<Integer> ATOMIC_ATTRIBUTE_DEFINITION_FILTERS =
		Collections.unmodifiableList(Arrays.asList(
			new Integer(ObjectClassDefinition.REQUIRED),
			new Integer(ObjectClassDefinition.OPTIONAL)));
//	static {
//		List l = new ArrayList();
//		l.add(new Integer(ObjectClassDefinition.REQUIRED));
//		l.add(new Integer(ObjectClassDefinition.OPTIONAL));
//		ATOMIC_ATTRIBUTE_DEFINITION_FILTERS = Collections.unmodifiableList(l);
//	}

	/* Create newOCD from oldOCD by applying transformer
	 * to each AttributeDefinition.
	 */
	public static BasicObjectClassDefinition apply(
			AttributeDefinitionTransformer transformer,
			ObjectClassDefinition oldObjectClassDefinition,
			Collection<String> attributesToIgnore) {		
		BasicObjectClassDefinition newOCD =
			MutateParameterUtilities.createNewParameters(oldObjectClassDefinition);
		
		// For each kind of AttributeDefinition filter ..
		for (Iterator<Integer> filterIt = ATOMIC_ATTRIBUTE_DEFINITION_FILTERS.iterator();
				filterIt.hasNext();) {
			int filter = filterIt.next().intValue();
			
			// Grab all matching AttributeDefinitions and transform them.
			AttributeDefinition[] oldAttributeDefintions =
				oldObjectClassDefinition.getAttributeDefinitions(filter);

			for (AttributeDefinition attributeDefinition : oldAttributeDefintions) {
				if (!attributesToIgnore.contains(attributeDefinition.getID())) {
					newOCD.addAttributeDefinition(
						filter, transformer.transform(attributeDefinition));
				}
			}
		}
		
		return newOCD;
	}
	
	// Convenience method for batching transformations.
	public static ObjectClassDefinition transform(
			ObjectClassDefinition objectClassDefinition,
			Collection<DropdownTransformer> transformers,
			Collection<String> attributesToIgnore) {
		ObjectClassDefinition newObjectClassDefinition = objectClassDefinition;
		
		for (Iterator<DropdownTransformer> it = transformers.iterator(); it.hasNext();) {
			DropdownTransformer transformer = it.next();
			newObjectClassDefinition =
				apply(transformer, newObjectClassDefinition, attributesToIgnore);
		}
		
		return newObjectClassDefinition;
	}
}
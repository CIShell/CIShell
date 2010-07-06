package org.cishell.utilities.mutateParameter.dropdown;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.cishell.utilities.ArrayUtilities;
import org.cishell.utilities.mutateParameter.ObjectClassDefinitionTransformer;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

/* For aggregating and applying DropdownTransforms.
 * Many convenience methods are given to support arrays vs. Lists
 * and default vs. no default.
 * The core functionality
 * is in add(final String, final String[], final String[])
 * and mutate(ObjectClassDefinition)
 */
public class DropdownMutator {
	private Collection<DropdownTransformer> transforms;
	private Set<String> attributesToIgnore = new HashSet<String>();

	public DropdownMutator() {
		this.transforms = new ArrayList<DropdownTransformer>();
	}
	
	public ObjectClassDefinition mutate(ObjectClassDefinition objectClassDefinition) {
		return ObjectClassDefinitionTransformer.transform(
			objectClassDefinition, this.transforms, this.attributesToIgnore);
	}

	public void add(String id, Collection<String> options, String defaultOption) {
		add(id, swapToFront(options, defaultOption));
	}
	
	public void add(String id, Collection<String> options) {
		add(id, options, options);
	}
	
	public void add(
			String id,
			Collection<String> optionLabels,
			String defaultOptionLabel,
			Collection<String> optionValues,
			String defaultOptionValue) {
		add(
			id,
			swapToFront(optionLabels, defaultOptionLabel),
			swapToFront(optionValues, defaultOptionValue));
	}
	
	public void add(String id, Collection<String> optionLabels, Collection<String> optionValues) {
		add(
			id,
			(String[]) optionLabels.toArray(new String[0]),
			(String[]) optionValues.toArray(new String[0]));
	}
	
	public void add(String id, String[] options, String defaultOption) {
		add(id, swapToFront(options, defaultOption));
	}
	
	public void add(String id, String[] options) {
		add(id, options, options);
	}
	
	public void add(
			final String id,
			final String[] optionLabels,
			String defaultOptionLabel,
			final String[] optionValues,
			String defaultOptionValue) {
		if (!shouldIgnore(id)) {
			add(
				id,
				swapToFront(optionLabels, defaultOptionLabel),
				swapToFront(optionValues, defaultOptionValue));
		}
	}
	
	public void add(
			final String id, final String[] optionLabels, final String[] optionValues) {
		if (!shouldIgnore(id)) {
			transforms.add(
				new DefaultDropdownTransformer() {
					public boolean shouldTransform(AttributeDefinition ad) {
						return id.equals(ad.getID());
					}

					public String[] transformOptionLabels(String[] oldOptionLabels) {
						return optionLabels;
					}

					public String[] transformOptionValues(String[] oldOptionValues) {
						return optionValues;
					}
				});
		}
	}

	public void ignore(String id) {
		this.attributesToIgnore.add(id);
	}

	public boolean shouldIgnore(String id) {
		return this.attributesToIgnore.contains(id);
	}
	
	private static Collection<String> swapToFront(Collection<String> items, String target) {
		String[] temp = (String[]) items.toArray(new String[]{});
		return Arrays.asList(swapToFront(temp, target));
	}
	
	private static String[] swapToFront(String[] array, String target) {
		int targetIndex = ArrayUtilities.indexOf(array, target);
		
		if (targetIndex != -1) {
			String[] swappedArray = new String[array.length];
			
			for (int ii = 0; ii < array.length; ii++) {
				swappedArray[ii] = array[ii];
			}
			
			swappedArray[0] = array[targetIndex];
			swappedArray[targetIndex] = array[0];
			
			return swappedArray;
		} else {
			return array;
		}
	}
}

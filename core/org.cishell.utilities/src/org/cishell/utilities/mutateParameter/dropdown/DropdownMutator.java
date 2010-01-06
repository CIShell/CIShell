package org.cishell.utilities.mutateParameter.dropdown;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
	private List transforms;

	public DropdownMutator() {
		transforms = new ArrayList();
	}
	
	public ObjectClassDefinition mutate(ObjectClassDefinition ocd) {
		return ObjectClassDefinitionTransformer.transform(ocd, transforms);
	}

	public void add(String id, List options, String defaultOption) {
		add(id, swapToFront(options, defaultOption));
	}
	
	public void add(String id, List options) {
		add(id, options, options);
	}
	
	public void add(String id,
					List optionLabels,
					String defaultOptionLabel,
					List optionValues,
					String defaultOptionValue) {
		add(id,
				swapToFront(optionLabels, defaultOptionLabel),
				swapToFront(optionValues, defaultOptionValue));
	}
	
	public void add(String id, List optionLabels, List optionValues) {
		add(id,
				(String[]) optionLabels.toArray(new String[0]),
				(String[]) optionValues.toArray(new String[0]));
	}
	
	public void add(String id, String[] options, String defaultOption) {
		add(id, swapToFront(options, defaultOption));
	}
	
	public void add(String id, String[] options) {
		add(id, options, options);
	}
	
	public void add(final String id,
					final String[] optionLabels,
					String defaultOptionLabel,
					final String[] optionValues,
					String defaultOptionValue) {
		add(id,
				swapToFront(optionLabels, defaultOptionLabel),
				swapToFront(optionValues, defaultOptionValue));
	}
	
	public void add(final String id,
					final String[] optionLabels,
					final String[] optionValues) {
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
	
	private static List swapToFront(List list, String target) {
		String[] temp = (String[]) list.toArray(new String[]{});
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

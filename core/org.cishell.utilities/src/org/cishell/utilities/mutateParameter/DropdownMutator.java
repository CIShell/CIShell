package org.cishell.utilities.mutateParameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

	public void add(String id, Collection options, String defaultOption) {
		List defaultedOptions = new ArrayList();
		defaultedOptions.add(defaultOption);
		defaultedOptions.addAll(options);
		
		add(id, defaultedOptions);
	}
	
	public void add(String id, Collection options) {
		add(id, options, options);
	}
	
	public void add(String id, Collection optionLabels, String defaultOptionLabel, Collection optionValues, String defaultOptionValue) {
		List defaultedOptionLabels = new ArrayList();
		defaultedOptionLabels.add(defaultOptionLabel);
		defaultedOptionLabels.addAll(optionLabels);
		
		List defaultedOptionValues = new ArrayList();
		defaultedOptionValues.add(defaultOptionValue);
		defaultedOptionValues.addAll(optionValues);
		
		add(id, defaultedOptionLabels, defaultedOptionValues);
	}
	
	public void add(String id, Collection optionLabels, Collection optionValues) {
		add(id, (String[]) optionLabels.toArray(new String[0]), (String[]) optionValues.toArray(new String[0]));
	}
	
	public void add(String id, String[] options, String defaultOption) {
		String[] defaultedOptions = new String[options.length + 1];
		defaultedOptions[0] = defaultOption;
		for (int ii = 0; ii < options.length; ii++) {
			defaultedOptions[ii+1] = options[ii];
		}
		
		System.out.println("options = ");
		for (int ii = 0; ii < defaultedOptions.length; ii++) {
			System.out.println(defaultedOptions[ii]);
		}
		
		add(id, defaultedOptions);
	}
	
	public void add(String id, String[] options) {
		add(id, options, options);
	}
	
	public void add(final String id, final String[] optionLabels, String defaultOptionLabel, final String[] optionValues, String defaultOptionValue) {
		String[] defaultedOptionLabels = new String[optionLabels.length + 1];
		defaultedOptionLabels[0] = defaultOptionLabel;
		for (int ii = 0; ii < optionLabels.length; ii++) {
			defaultedOptionLabels[ii+1] = optionLabels[ii];
		}
		
		String[] defaultedOptionValues = new String[optionValues.length + 1];
		defaultedOptionValues[0] = defaultOptionValue;
		for (int ii = 0; ii < optionValues.length; ii++) {
			defaultedOptionValues[ii+1] = optionValues[ii];
		}
		
		add(id, defaultedOptionLabels, defaultedOptionValues);
	}
	
	public void add(final String id, final String[] optionLabels, final String[] optionValues) {
		transforms.add(
			new NullDropdownTransformer() {
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

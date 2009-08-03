package org.cishell.utilities.mutateParameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

public class DropdownMutator {
	private List transforms;

	public DropdownMutator() {
		transforms = new ArrayList();
	}
	
	public void add(String id, Collection options) {
		add(id, options, options);
	}
	
	public void add(String id, Collection optionLabels, Collection optionValues) {
		add(id, (String[]) optionLabels.toArray(new String[0]), (String[]) optionValues.toArray(new String[0]));
	}
	
	public void add(String id, String[] options) {
		add(id, options, options);
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
	
	public ObjectClassDefinition mutate(ObjectClassDefinition ocd) {
		return ObjectClassDefinitionTransformer.transform(ocd, transforms);
	}
}

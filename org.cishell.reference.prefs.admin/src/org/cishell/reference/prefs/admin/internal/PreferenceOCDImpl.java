package org.cishell.reference.prefs.admin.internal;

import java.io.IOException;
import java.io.InputStream;

import org.cishell.reference.prefs.admin.PreferenceAD;
import org.cishell.reference.prefs.admin.PreferenceOCD;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

public class PreferenceOCDImpl implements ObjectClassDefinition, PreferenceOCD {
	
	private ObjectClassDefinition realOCD;
	private PreferenceAD[] allWrappedADs;
	private PreferenceAD[] optionalWrappedADs;
	private PreferenceAD[] requiredWrappedADs;
	
	private LogService log;
	
	public PreferenceOCDImpl(LogService log, ObjectClassDefinition realOCD) {
		this.log = log;
		this.realOCD = realOCD;
		//TODO: don't always return all attributeDefinitions, regardless of filter
		this.allWrappedADs = wrapAttributeDefinitions(realOCD.getAttributeDefinitions(ObjectClassDefinition.ALL));
		this.optionalWrappedADs = wrapAttributeDefinitions(realOCD.getAttributeDefinitions(ObjectClassDefinition.OPTIONAL));
		this.requiredWrappedADs =  wrapAttributeDefinitions(realOCD.getAttributeDefinitions(ObjectClassDefinition.REQUIRED));
	}
	
	private PreferenceAD[] wrapAttributeDefinitions(AttributeDefinition[] realAttributeDefinitions) {
		PreferenceAD[] wrappedADs = new PreferenceAD[realAttributeDefinitions.length];
		
		for (int i = 0; i < realAttributeDefinitions.length; i++) {
			AttributeDefinition realAD = realAttributeDefinitions[i];
			PreferenceAD wrappedAD = new PreferenceADImpl(this.log, realAD);
			
			wrappedADs[i] = wrappedAD;
		}
		
		return wrappedADs;
	}


	//use in standard way
	public AttributeDefinition[] getAttributeDefinitions(int filter) {
		return this.realOCD.getAttributeDefinitions(filter);

	}
	
	//use to get at the special preference attribute goodness.
	public PreferenceAD[] getPreferenceAttributeDefinitions(int filter) {
		if (filter == ObjectClassDefinition.ALL) {
			return this.allWrappedADs;
		} else if (filter == ObjectClassDefinition.OPTIONAL) {
			return this.optionalWrappedADs;
		} else if (filter == ObjectClassDefinition.REQUIRED) {
			return this.requiredWrappedADs;
		} else {
			log.log(LogService.LOG_WARNING,
					"Programmer Error: attempted to get preference attribute definitions with invalid filter " + filter);
			return new PreferenceAD[0];
		}
	}

	/* (non-Javadoc)
	 * @see org.cishell.service.prefadmin.shouldbeelsewhere.PreferenceObjectClassDefinition#getDescription()
	 */
	public String getDescription() {
		return this.realOCD.getDescription();
	}

	/* (non-Javadoc)
	 * @see org.cishell.service.prefadmin.shouldbeelsewhere.PreferenceObjectClassDefinition#getID()
	 */
	public String getID() {
		return this.realOCD.getID();
	}

	/* (non-Javadoc)
	 * @see org.cishell.service.prefadmin.shouldbeelsewhere.PreferenceObjectClassDefinition#getIcon(int)
	 */
	public InputStream getIcon(int size) throws IOException {
		return this.realOCD.getIcon(size);
	}

	/* (non-Javadoc)
	 * @see org.cishell.service.prefadmin.shouldbeelsewhere.PreferenceObjectClassDefinition#getName()
	 */
	public String getName() {
		return this.realOCD.getName();
	}

}

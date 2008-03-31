package org.cishell.reference.gui.menumanager.menu.metatypewrapper;

import java.util.Dictionary;

import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

public class ParamMetaTypeProvider implements MetaTypeProvider {

	private MetaTypeProvider realMTP;
	
	private Dictionary defaultOverrider;
	
	public ParamMetaTypeProvider(MetaTypeProvider realMTP, Dictionary defaultOverrider) {
		this.realMTP = realMTP;
		this.defaultOverrider = defaultOverrider;
	}
	
	public String[] getLocales() {
		return this.realMTP.getLocales();
	}

	public ObjectClassDefinition getObjectClassDefinition(String id,
			String locale) {
		ObjectClassDefinition ocd = realMTP.getObjectClassDefinition(id, locale);
		if (ocd != null) {
			return new ParamOCD(realMTP.getObjectClassDefinition(id, locale), defaultOverrider);
		} else {
			return null;
		}
	}

}

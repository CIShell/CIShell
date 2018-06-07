package org.cishell.reference.prefs.admin.internal;

import org.cishell.reference.prefs.admin.PrefPage;
import org.cishell.reference.prefs.admin.PreferenceOCD;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;

public class PrefPageImpl implements PrefPage {
	private ServiceReference prefHolder;
	private Configuration prefConf;
	private PreferenceOCD prefOCD;
	private int type;
	
	public PrefPageImpl(ServiceReference prefHolder,PreferenceOCD prefOCD,  Configuration prefConf, int type) {
		this.prefHolder = prefHolder;
		this.prefOCD = prefOCD;
		this.prefConf = prefConf;
		this.type = type;
	}
	
	public ServiceReference getServiceReference() {
		return this.prefHolder;
	}
	
	public int getType() {
		return this.type;
	}
	
	public PreferenceOCD getPrefOCD() {
		return this.prefOCD;
	}
	
	public Configuration getPrefConf() {
		return this.prefConf;
	}
}

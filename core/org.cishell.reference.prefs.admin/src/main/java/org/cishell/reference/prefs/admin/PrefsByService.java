package org.cishell.reference.prefs.admin;

import org.osgi.framework.ServiceReference;

public class PrefsByService {
	
	private ServiceReference prefHolder;
	private PrefPage[] localPrefs;
	private PrefPage[] globalPrefs;
	private PrefPage[] paramPrefs;
	public PrefsByService(ServiceReference prefHolder, PrefPage[] localPrefs, PrefPage[] globalPrefs, PrefPage[] paramPrefs) {
		this.prefHolder = prefHolder;
		this.localPrefs = localPrefs;
		this.globalPrefs = globalPrefs;
		this.paramPrefs = paramPrefs;
	}
	
	public ServiceReference getServiceReference() {
		return this.prefHolder;
	}
	
	public PrefPage[] getGlobalPrefPages() {
		return this.globalPrefs;
	}
	
	public PrefPage[] getLocalPrefPages() {
		return this.localPrefs;
	}
	
	public PrefPage[] getParamPrefPages() {
		return this.paramPrefs;
	}
}

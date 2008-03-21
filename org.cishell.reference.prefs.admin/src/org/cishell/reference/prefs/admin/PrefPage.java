package org.cishell.reference.prefs.admin;

import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;

public interface PrefPage {
	
	public static final int LOCAL = 0;
	public static final int GLOBAL = 1;
	public static final int PARAM = 2;

	public abstract ServiceReference getServiceReference();
	
	public abstract int getType();
	
	public abstract Configuration getPrefConf();
	public abstract PreferenceOCD getPrefOCD();
}
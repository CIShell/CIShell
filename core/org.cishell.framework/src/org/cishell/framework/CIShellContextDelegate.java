package org.cishell.framework;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class CIShellContextDelegate implements CIShellContext {
	private ServiceReference uniqueServiceReference;
	private CIShellContext actualCIShellContext;

	public CIShellContextDelegate(
			ServiceReference uniqueServiceReference, CIShellContext actualCIShellContext) {
		this.uniqueServiceReference = uniqueServiceReference;
		this.actualCIShellContext = actualCIShellContext;
	}

	public Object getService(String service) {
		if (LogService.class.getName().equals(service)) {
			return new LogServiceDelegate(
				this.uniqueServiceReference,
				(LogService) this.actualCIShellContext.getService(service));
		} else {
			return this.actualCIShellContext.getService(service);
		}
	}
}
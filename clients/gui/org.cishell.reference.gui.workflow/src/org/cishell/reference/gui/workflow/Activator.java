package org.cishell.reference.gui.workflow;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.cishell.app.service.scheduler.SchedulerService;
import org.cishell.framework.CIShellContext;
import org.cishell.framework.LocalCIShellContext;
import org.cishell.reference.gui.workflow.views.WorkflowView;

public class Activator extends AbstractUIPlugin implements IStartup {

	public static final String PLUGIN_ID = "org.cishell.reference.gui.workflow";
	private static Activator plugin;
	private static BundleContext context;
	private static CIShellContext ciShellContext;

	private boolean waitForBundleContext;
	private LogService logger;

	private static final int ATTEMPTS_TO_FIND_TOOLBAR = 15;
	private static final int SLEEP_TIME = 100;

	public Activator() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		Activator.context = context;
		ciShellContext = new LocalCIShellContext(context);

		if (waitForBundleContext) {
			earlyStartup();
		}
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

	public static BundleContext getContext() {
		return context;
	}

	public static SchedulerService getSchedulerService() {
		ServiceReference serviceReference = context
				.getServiceReference(SchedulerService.class.getName());
		SchedulerService manager = null;

		if (serviceReference != null) {
			manager = (SchedulerService) context.getService(serviceReference);
		}

		return manager;
	}

	public static Image createImage(String name) {
		if (Platform.isRunning()) {
			return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID,
					File.separator + "icons" + File.separator + name)
					.createImage();
		} else {
			return null;
		}

	}

	public void earlyStartup() {
		if (context != null) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					try {
						IWorkbench bench = PlatformUI.getWorkbench();
						if (bench != null) {
							IWorkbenchWindow window = bench
									.getActiveWorkbenchWindow();
							if (window != null) {
								window.getActivePage().showView(
										WorkflowView.ID_VIEW);
							}
						}

					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			});
			waitForBundleContext = false;
		} else {
			waitForBundleContext = true;
		}
	}

	public static Object getService(String servicePID) {
		ServiceReference serviceReference = Activator.context
				.getServiceReference(servicePID);

		if (serviceReference != null) {
			return Activator.context.getService(serviceReference);
		} else {
			return null;
		}
	}

	public static CIShellContext getCiShellContext() {
		return ciShellContext;
	}

	public static void setCiShellContext(CIShellContext ciShellContext) {
		Activator.ciShellContext = ciShellContext;
	}
}

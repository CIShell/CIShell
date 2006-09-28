package org.cishell.reference.gui.workspace;

import org.cishell.reference.gui.guibuilder.swt.SWTGuiBuilderService;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(600, 600));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
	}
    
    public void postWindowOpen() {
        Display display = getWindowConfigurer().getWindow().getShell().getDisplay();
        
        GUIBuilderService builder = new SWTGuiBuilderService(display);
        Activator.getDefault().registerBuilder(builder);
    }
}

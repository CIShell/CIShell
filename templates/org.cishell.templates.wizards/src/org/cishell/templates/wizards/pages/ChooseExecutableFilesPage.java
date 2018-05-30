package org.cishell.templates.wizards.pages;

import org.cishell.templates.staticexecutable.providers.PlatformOptionProvider;
import org.cishell.templates.wizards.pagepanels.SetupPlatformsPanel;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.ui.templates.TemplateOption;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Composite;

/*
 * This page allows users to specify the name of the executable file (which
 *  should be the same across all platforms), the actual executable files for
 *  the various platforms, and the related files for the various platforms.
 * The logic for this page is spread out in several locations, but for a start,
 *  check  org.cishell.templates.wizards.pagepanels.SetupPlatformsPanel .
 */
public class ChooseExecutableFilesPage extends WizardPage {
	private SetupPlatformsPanel setupPlatformsPanel;
	private TemplateOption executableNameOption;
	private PlatformOptionProvider platformOptionProvider;
	
	public ChooseExecutableFilesPage(
			String pageName,
			TemplateOption executableNameOption,
			PlatformOptionProvider platformOptionProvider) {
		super(pageName);
		
		this.executableNameOption = executableNameOption;
		this.platformOptionProvider = platformOptionProvider;
	}
	
	public void createControl(Composite parent) {
		final ScrolledComposite scrollingContainer =
			new ScrolledComposite(parent, SWT.V_SCROLL);
		final SetupPlatformsPanel setupPlatformsPanel =
			new SetupPlatformsPanel(scrollingContainer,
									SWT.NONE,
									this.executableNameOption,
									this.platformOptionProvider);
		this.setupPlatformsPanel = setupPlatformsPanel;
		
		// TODO: This control listener should maybe be on setupPlatformsPanel?
		scrollingContainer.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent controlEvent) {
				scrollingContainer.setMinSize(
					setupPlatformsPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		});
		
		scrollingContainer.setContent(setupPlatformsPanel);
		scrollingContainer.setExpandHorizontal(true);
		scrollingContainer.setExpandVertical(true);
		
		setControl(scrollingContainer);
	}
	
	public SetupPlatformsPanel getSetupPlatformsPanel() {
		return this.setupPlatformsPanel;
	}
}

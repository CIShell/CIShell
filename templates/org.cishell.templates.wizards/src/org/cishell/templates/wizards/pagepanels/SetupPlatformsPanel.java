package org.cishell.templates.wizards.pagepanels;

import java.util.ArrayList;

import org.cishell.templates.staticexecutable.providers.PlatformOptionProvider;
import org.cishell.templates.wizards.staticexecutable.NewStaticExecutableAlgorithmWizard;
import org.cishell.templates.wizards.widgets.PlatformSetupWidget;
import org.cishell.templates.wizards.widgets.ResizeCompositeHackWidget;
import org.eclipse.pde.ui.templates.TemplateOption;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

/*
 * This panel contains several platform setup widgets
 *  (org.cishell.templates.wizards.widgets.PlatformSetupWidget).  There is one
 *  platform setup widget per (operating system) platform and a special
 *  platform setup widget for files common to all (operating system) platforms.
 */
public class SetupPlatformsPanel extends ResizeCompositeHackWidget {
	public static final String SPECIFY_EXECUTABLE_NAME_LABEL =
		"Executable Name";
	
	private ArrayList platformSetupWidgets;

	public SetupPlatformsPanel(Composite parent,
							   int style,
							   TemplateOption executableNameOption,
							   PlatformOptionProvider platformOptionProvider) {
		super(parent, style);
		
		setLayout(createLayoutForThis());
		createExecutableNameOptionWidget(executableNameOption);
		
		this.platformSetupWidgets =
			createPlatformSetupWidgets(platformOptionProvider);
	}
	
	public ArrayList getPlatformSetupWidgets() {
		return this.platformSetupWidgets;
	}
	
	private Layout createLayoutForThis() {
		GridLayout layout = new GridLayout(2, true);
		layout.makeColumnsEqualWidth = false;
		
		return layout;
	}
	
	private void createExecutableNameOptionWidget(
			TemplateOption executableNameOption) {
		executableNameOption.createControl(this, 2);
	}
	
	private ArrayList createPlatformSetupWidgets(
			PlatformOptionProvider platformOptionProvider) {
		ArrayList platformSetupWidgets = new ArrayList();
		
		PlatformSetupWidget defaultPlatformSetupWidget =
			new PlatformSetupWidget(
				this,
				SWT.NONE,
				NewStaticExecutableAlgorithmWizard.DEFAULT_LABEL,
				NewStaticExecutableAlgorithmWizard.DEFAULT_PATH,
				false,
				platformOptionProvider);
		defaultPlatformSetupWidget.setLayoutData(
			createPlatformSetupWidgetLayoutData());
		platformSetupWidgets.add(defaultPlatformSetupWidget);
		
		for (int ii = 1;
				ii < NewStaticExecutableAlgorithmWizard.PLATFORM_LABELS.length;
				ii++) {
			PlatformSetupWidget platformSetupWidget =
				new PlatformSetupWidget(
					this,
					SWT.NONE,
					NewStaticExecutableAlgorithmWizard.PLATFORM_LABELS[ii],
					NewStaticExecutableAlgorithmWizard.PLATFORM_PATHS[ii],
					true,
					platformOptionProvider);
			platformSetupWidget.setLayoutData(
				createPlatformSetupWidgetLayoutData());
			platformSetupWidgets.add(platformSetupWidget);
		}
		
		return platformSetupWidgets;
	}
	
	private Object createPlatformSetupWidgetLayoutData() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		data.horizontalSpan = 2;
		
		return data;
	}
}
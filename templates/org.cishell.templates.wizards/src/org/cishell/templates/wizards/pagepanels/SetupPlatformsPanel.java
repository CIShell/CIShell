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

public class SetupPlatformsPanel extends ResizeCompositeHackWidget {
	public static final String SPECIFY_EXECUTABLE_NAME_LABEL =
		"Executable Name";
	
	// public static final int SPECIFY_EXECUTABLE_NAME_TEXT_WIDTH = 350;
	
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
	
	private void createSpecifyExecutableNameLabel() {
		Label specifyExecutableNameLabel = new Label(this, SWT.NONE);
		specifyExecutableNameLabel.setLayoutData(
			createSpecifyExecutableNameLableLayoutData());
		specifyExecutableNameLabel.setText(
			SPECIFY_EXECUTABLE_NAME_LABEL + ":");
	}
	
	private Text createSpecifyExecutableNameText() {
		Text specifyExecutableNameText = new Text(this, SWT.BORDER);
		specifyExecutableNameText.setLayoutData(
			createSpecifyExecutableNameTextLayoutData());
		
		return specifyExecutableNameText;
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
	
	private Object createSpecifyExecutableNameLableLayoutData() {
		GridData data = new GridData();
		
		return data;
	}
	
	private Object createSpecifyExecutableNameTextLayoutData() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		// data.widthHint = SPECIFY_EXECUTABLE_NAME_TEXT_WIDTH;
		
		return data;
	}
	
	private Object createPlatformSetupWidgetLayoutData() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.FILL;
		data.grabExcessHorizontalSpace = true;
		data.horizontalSpan = 2;
		
		return data;
	}
}
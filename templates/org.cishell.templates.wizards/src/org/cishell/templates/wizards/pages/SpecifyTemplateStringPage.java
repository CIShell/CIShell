package org.cishell.templates.wizards.pages;

import org.cishell.templates.staticexecutable.providers.InputDataProvider;
import org.cishell.templates.staticexecutable.providers.InputParameterProvider;
import org.cishell.templates.wizards.pagepanels.SpecifyTemplateStringPanel;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.ui.templates.TemplateOption;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class SpecifyTemplateStringPage extends WizardPage {
	private SpecifyTemplateStringPanel specifyTemplateStringPanel;
	private InputParameterProvider inputParameterProvider;
	private InputDataProvider inputDataProvider;
	private TemplateOption templateStringOption;
	
	public SpecifyTemplateStringPage(
			String pageName,
			InputParameterProvider inputParameterProvider,
			InputDataProvider inputDataProvider,
			TemplateOption templateStringOption) {
		super(pageName);
		
		this.inputParameterProvider = inputParameterProvider;
		this.inputDataProvider = inputDataProvider;
		this.templateStringOption = templateStringOption;
	}
	
	public void createControl(Composite parent) {
		this.specifyTemplateStringPanel =
			createSpecifyTemplateStringPanel(parent);
		
		setControl(specifyTemplateStringPanel);
	}
	
	public SpecifyTemplateStringPanel getSpecifyTemplateStringPanel() {
		return this.specifyTemplateStringPanel;
	}
	
	private SpecifyTemplateStringPanel createSpecifyTemplateStringPanel(
			Composite parent) {
		SpecifyTemplateStringPanel specifyTemplateStringPanel =
			new SpecifyTemplateStringPanel(
				parent, SWT.NONE, this.templateStringOption);
		
		return specifyTemplateStringPanel;
	}
	
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);
		
		if (isVisible) {
			updatePlaceholderSet();
		}
	}
	
	private void updatePlaceholderSet() {
		this.specifyTemplateStringPanel.updateControls(
			this.inputParameterProvider,
			this.inputDataProvider);
	}
}

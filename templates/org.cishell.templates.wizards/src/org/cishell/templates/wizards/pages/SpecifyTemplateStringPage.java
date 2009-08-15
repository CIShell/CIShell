package org.cishell.templates.wizards.pages;

import org.cishell.templates.staticexecutable.optiontypes.CustomStringOption;
import org.cishell.templates.staticexecutable.providers.InputDataProvider;
import org.cishell.templates.staticexecutable.providers.InputParameterProvider;
import org.cishell.templates.wizards.pagepanels.SpecifyTemplateStringPanel;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/*
 * This page provides algorithm creators with a list of possible "placeholders"
 *  that can be used in the template string.
 * The template string is the string used when invoking the static executable
 *  on the command line.  As such, it specifies all program arguments.
 * The placeholders possible are the input data items and algorithm parameters
 *  that the algorithm creator specified.
 */
public class SpecifyTemplateStringPage extends WizardPage {
	private SpecifyTemplateStringPanel specifyTemplateStringPanel;
	private InputParameterProvider inputParameterProvider;
	private InputDataProvider inputDataProvider;
	private CustomStringOption templateStringOption;
	
	public SpecifyTemplateStringPage(
			String pageName,
			InputParameterProvider inputParameterProvider,
			InputDataProvider inputDataProvider,
			CustomStringOption templateStringOption) {
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

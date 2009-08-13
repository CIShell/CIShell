package org.cishell.templates.wizards.pages;

import org.cishell.templates.wizards.pagepanels.ChooseSourceCodeFilesPanel;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.ui.templates.TemplateOption;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/*
 * This page allows algorithm creators to choose a single file that is intended
 *  to be an archive file containing their algorithm source code.
 */
public class ChooseSourceCodeFilesPage extends WizardPage {
	private ChooseSourceCodeFilesPanel chooseSourceCodeFilesPanel;
	private TemplateOption sourceCodeFilesLocationOption;
	
	public ChooseSourceCodeFilesPage(
			String pageName,
			TemplateOption sourceCodeFilesLocationOption) {
		super(pageName);
		
		this.sourceCodeFilesLocationOption = sourceCodeFilesLocationOption;
	}
	
	public void createControl(Composite parent) {
		this.chooseSourceCodeFilesPanel = new ChooseSourceCodeFilesPanel(
			parent, SWT.NONE, this.sourceCodeFilesLocationOption);
		
		setControl(this.chooseSourceCodeFilesPanel);
	}
}
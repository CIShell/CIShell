package org.cishell.templates.wizards.pagepanels;

import org.cishell.templates.staticexecutable.optiontypes.CustomStringOption;
import org.cishell.templates.wizards.widgets.ChooseFileWidget;
import org.eclipse.pde.ui.templates.TemplateOption;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

/*
 */
public class ChooseSourceCodeFilesPanel extends Composite {
	public ChooseSourceCodeFilesPanel(
			Composite parent,
			int style,
			CustomStringOption sourceCodeFilesLocationOption) {
		super(parent, style);
		
		setLayout(createLayoutForThis());
		createChooseSourceCodeFilesWidget(sourceCodeFilesLocationOption);
	}
	
	private Layout createLayoutForThis() {
		GridLayout layout = new GridLayout(3, true);
		layout.makeColumnsEqualWidth = false;
		
		return layout;
	}
	
	private void createChooseSourceCodeFilesWidget(
			CustomStringOption sourceCodeFilesLocationOption) {
		int parentWidth =
			this.getParent().computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		ChooseFileWidget fileSelector = new ChooseFileWidget(
			this, SWT.NONE, false, parentWidth, sourceCodeFilesLocationOption);
	}
}

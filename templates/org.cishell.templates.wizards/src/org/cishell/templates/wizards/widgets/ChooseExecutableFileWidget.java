package org.cishell.templates.wizards.widgets;

import org.cishell.templates.staticexecutable.optiontypes.PlatformOption;
import org.cishell.templates.staticexecutable.providers.PlatformOptionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

/*
 * This widget allows the user to choose one file, which is intended to be the
 *  executable file for the provided platform (name and path).
 */
public class ChooseExecutableFileWidget extends Composite {
	public static final String CHOOSE_EXECUTABLE_FILE_LABEL =
		"Choose Executable File";
	
	private ChooseFileWidget executableFileSelector;

	public ChooseExecutableFileWidget(
			Composite parent,
			int style,
			int parentWidth,
			String platformName,
			String platformPath,
			PlatformOptionProvider platformOptionProvider) {
		super(parent, style);
		
		setLayout(createLayoutForThis());
		
		createChooseExecutableFileLabel();
		this.executableFileSelector = createExecutableFileSelector(
			parentWidth, platformName, platformPath, platformOptionProvider);
	}
	
	public ChooseFileWidget getExecutableFileSelector() {
		return this.executableFileSelector;
	}
	
	private Layout createLayoutForThis() {
		GridLayout layout = new GridLayout(2, true);
		
		return layout;
	}
	
	private void createChooseExecutableFileLabel() {
		String labelText = CHOOSE_EXECUTABLE_FILE_LABEL + ": ";
		
		Label chooseExecutableFileLabel = new Label(this, SWT.NONE);
		chooseExecutableFileLabel.setText(labelText);
		chooseExecutableFileLabel.setLayoutData(createLabelLayoutData());
	}
	
	private ChooseFileWidget createExecutableFileSelector(
			int parentWidth,
			String platformName,
			String platformPath,
			PlatformOptionProvider platformOptionProvider) {
		PlatformOption executableFileOption =
			platformOptionProvider.createExecutableFileOption(
				platformName, platformPath);
		ChooseFileWidget fileSelector = new ChooseFileWidget(
			this, SWT.NONE, false, parentWidth, executableFileOption);
		
		// TODO: Set layout data.
		
		return fileSelector;
	}
	
	private GridData createLabelLayoutData() {
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = SWT.BEGINNING;
		data.verticalAlignment = SWT.BEGINNING;
		data.horizontalSpan = 2;
		
		return data;
	}
}
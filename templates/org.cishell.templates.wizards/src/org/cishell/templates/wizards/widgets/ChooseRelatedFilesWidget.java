package org.cishell.templates.wizards.widgets;

import java.util.ArrayList;

import org.cishell.templates.staticexecutable.providers.PlatformOption;
import org.cishell.templates.staticexecutable.providers.PlatformOptionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

public class ChooseRelatedFilesWidget extends ResizeCompositeHackWidget {
	public static final String CHOOSE_RELATED_FILES_LABEL_TEXT =
		"Choose Related Files";
	public static final String CHOOSE_COMMON_FILES_LABEL_TEXT =
		"Choose Common Files";
	
	private int parentWidth;
	private ChooseFileWidget firstFileSelector;
	private ArrayList remainingFileSelectors = new ArrayList();
	private String platformName;
	private String platformPath;
	private PlatformOptionProvider platformOptionProvider;
	
	public ChooseRelatedFilesWidget(
			Composite parent,
			int style,
			boolean filesAreRelated,
			int parentWidth,
			String platformName,
			String platformPath,
			PlatformOptionProvider platformOptionProvider) {
		super(parent, style);
		
		this.platformName = platformName;
		this.platformPath = platformPath;
		this.platformOptionProvider = platformOptionProvider;
		
		setLayout(createLayoutForThis());
		
		this.parentWidth = parentWidth;
		createChooseFilesLabel(filesAreRelated);
		this.firstFileSelector = createAndSetupFileSelector(false);
	}
	
	// TODO: How to get the file paths out?
	public ChooseFileWidget getFirstFileSelector() {
		return this.firstFileSelector;
	}
	
	public ArrayList getRemainingFileSelectors() {
		return this.remainingFileSelectors;
	}
	
	public ChooseFileWidget getLastFileSelector() {
		int remainingFileSelectorCount = this.remainingFileSelectors.size();
		
		if (remainingFileSelectorCount > 0) {
			int lastFileSelectorIndex = remainingFileSelectorCount - 1;
			
			return (ChooseFileWidget)this.remainingFileSelectors.get(
				lastFileSelectorIndex);
		}
		else {
			return this.firstFileSelector;
		}
	}
	
	public void fileWasChosen(ChooseFileWidget fileSelector, String filePath) {
		ChooseFileWidget newFileSelector = createAndSetupFileSelector(true);
		this.remainingFileSelectors.add(newFileSelector);
		
		setSize(computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	public void removeButtonWasSelected(ChooseFileWidget fileSelector) {
		fileSelector.unsetFileChosenListener();
		fileSelector.unsetRemoveElementListener();
		this.remainingFileSelectors.remove(fileSelector);
		this.platformOptionProvider.removeRelatedFileOption(
			fileSelector.getPlatformOption());
		fileSelector.dispose();
		
		setSize(computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		ChooseFileWidget lastFileSelector = getLastFileSelector();
		lastFileSelector.setFileChosenListener(this);
		lastFileSelector.setRemoveElementListener(this);
	}
	
	private Layout createLayoutForThis() {
		GridLayout layout = new GridLayout(1, true);
		
		return layout;
	}
	
	private void createChooseFilesLabel(boolean filesAreRelated) {
		String labelText;
		
		if (filesAreRelated) {
			labelText = CHOOSE_RELATED_FILES_LABEL_TEXT + ": ";
		}
		else {
			labelText = CHOOSE_COMMON_FILES_LABEL_TEXT + ": ";
		}
		
		Label chooseFilesLabel = new Label(this, SWT.NONE);
		chooseFilesLabel.setText(labelText);
		chooseFilesLabel.setLayoutData(createLabelLayoutData());
	}
	
	private ChooseFileWidget createAndSetupFileSelector(
			boolean hasRemoveButton) {
		ChooseFileWidget lastFileSelector = getLastFileSelector();
		if (lastFileSelector != null) {
			lastFileSelector.unsetFileChosenListener();
		}
		
		PlatformOption relatedFileOption =
			this.platformOptionProvider.createRelatedFileOption(
				this.platformName, this.platformPath);
		
		ChooseFileWidget fileSelector = new ChooseFileWidget(
			this, SWT.NONE,
			hasRemoveButton,
			this.parentWidth,
			relatedFileOption);
		
		fileSelector.setFileChosenListener(this);
		
		if (hasRemoveButton) {
			fileSelector.setRemoveElementListener(this);
		}
		
		return fileSelector;
	}
	
	private GridData createLabelLayoutData() {
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = SWT.BEGINNING;
		data.verticalAlignment = SWT.BEGINNING;
		
		return data;
	}
}
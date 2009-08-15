package org.cishell.templates.wizards.widgets;

import org.cishell.templates.staticexecutable.optiontypes.CustomStringOption;
import org.eclipse.pde.ui.templates.TemplateOption;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Layout;

/*
 * This widget allows the user the choose a file off of his/her hard drive.
 * It ties the chosen file's path to a provided TemplateOption so the chosen
 *  files can be processed upon the wizard's completion.
 * This widget can optionally call back upon it being filled or its Remove
 *  button being selected to allow its parent component handle any appropriate
 *  actions.
 */
public class ChooseFileWidget extends Composite implements SelectionListener {
	public static final String BROWSE_FILES_BUTTON_LABEL = "Browse";
	public static final String REMOVE_FILE_LABEL_TEXT = "Remove?";
	public static final String CLEAR_FILE_LABEL_TEXT = "Clear?";
	
	public static final int PADDING_FOR_FILE_PATH_TEXT_WITHOUT_REMOVE_BUTTON =
		5;
	public static final int PADDING_FOR_FILE_PATH_TEXT_WITH_REMOVE_BUTTON =
		5;
	
	private Button browseFilesButton;
	// TODO: Make these listeners of an interface.
	private ChooseRelatedFilesWidget fileChosenListener;
	private ChooseRelatedFilesWidget removeElementListener;
	private CustomStringOption platformOption;
	private boolean canBeRemoved;
	
	public ChooseFileWidget(Composite parent,
							int style,
							int parentParentWidth,
							CustomStringOption stringOption) {
		this(parent, style, true, parentParentWidth, stringOption);
	}
	
	public ChooseFileWidget(Composite parent,
							int style,
							boolean hasRemoveButton,
							int parentParentWidth,
							CustomStringOption platformOption) {
		super(parent, style);
		
		this.platformOption = platformOption;
		this.canBeRemoved = hasRemoveButton;
		
		setLayout(createLayoutForThis());

		createControlForTextWidget(hasRemoveButton, parentParentWidth);
		this.browseFilesButton = createBrowseFilesButton();
		createRemoveElement(hasRemoveButton);
	}
	
	public TemplateOption getPlatformOption() {
		return this.platformOption;
	}
	
	public String getFilePath() {
		return this.platformOption.getValue().toString();
	}
	
	public void setFilePath(String filePath) {
		this.platformOption.setValue(filePath);
	}
	
	public void widgetDefaultSelected(SelectionEvent selectionEvent) {
		if (selectionEvent.widget == this.browseFilesButton) {
			browseButtonSelected(selectionEvent);
		} else {
			removeButtonSelected(selectionEvent);
		}
	}
	
	public void widgetSelected(SelectionEvent selectionEvent) {
		if (selectionEvent.widget == this.browseFilesButton) {
			browseButtonSelected(selectionEvent);
		} else {
			removeButtonSelected(selectionEvent);
		}
	}
	
	public void setFileChosenListener(ChooseRelatedFilesWidget fileChosenListener) {
		this.fileChosenListener = fileChosenListener;
	}
	
	public void unsetFileChosenListener() {
		this.fileChosenListener = null;
	}
	
	public void setRemoveElementListener(ChooseRelatedFilesWidget removeElementListener) {
		this.removeElementListener = removeElementListener;
	}
	
	public void unsetRemoveElementListener() {
		this.removeElementListener = null;
	}
	
	private Layout createLayoutForThis() {
		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		
		return layout;
	}
	
	private void createControlForTextWidget(boolean hasRemoveButton,
											int parentParentWidth) {
		int widthForFilePathText;
		
		if (hasRemoveButton) {
			widthForFilePathText = parentParentWidth - 
				PADDING_FOR_FILE_PATH_TEXT_WITH_REMOVE_BUTTON;
		} else {
			widthForFilePathText = parentParentWidth -
				PADDING_FOR_FILE_PATH_TEXT_WITHOUT_REMOVE_BUTTON;
		}

		if (hasRemoveButton) {
			this.platformOption.createControl(this, -1);
		} else {
			this.platformOption.createControl(this, -1);
		}
		this.platformOption.getTextWidget().setLayoutData(
			createFilePathTextLayoutData(widthForFilePathText));
	}
	
	private Button createBrowseFilesButton() {
		Button browseFilesButton = new Button(this, SWT.PUSH);
		browseFilesButton.setLayoutData(createBrowseFilesButtonLayoutData());
		browseFilesButton.setText(BROWSE_FILES_BUTTON_LABEL);
		browseFilesButton.addSelectionListener(this);
		
		return browseFilesButton;
	}
	
	private void createRemoveElement(boolean hasRemoveButton) {
		// TODO: Make the first one say Clear.;
		Button removeButton = new Button(this, SWT.PUSH);
		removeButton.setLayoutData(createRemoveElementLayoutData());
		removeButton.addSelectionListener(this);
		
		if (hasRemoveButton) {
			removeButton.setText(REMOVE_FILE_LABEL_TEXT);
		} else {
			removeButton.setText(CLEAR_FILE_LABEL_TEXT);
		}
	}
	
	private void browseButtonSelected(SelectionEvent selectionEvent) {
		FileDialog fileDialog = new FileDialog(getShell(), SWT.NULL);
		fileDialog.setFileName(getFilePath());
		String filePath = fileDialog.open();
		
		if (filePath != null) {
			setFilePath(filePath);
			
			if (this.fileChosenListener != null) {
				this.fileChosenListener.fileWasChosen(this, filePath);
			}
		}
	}
	
	private void removeButtonSelected(SelectionEvent selectionEvent) {
		if (this.canBeRemoved) {
			if (this.removeElementListener != null) {
				this.removeElementListener.removeButtonWasSelected(this);
			}
		} else {
			setFilePath("");
		}
	}
	
	private GridData createFilePathTextLayoutData(int width) {
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.widthHint = width;
		
		return data;
	}
	
	private GridData createBrowseFilesButtonLayoutData() {
		GridData data = new GridData();
		
		return data;
	}
	
	private GridData createRemoveElementLayoutData() {
		GridData data = new GridData();
		data.widthHint = 56;
		
		return data;
	}
}
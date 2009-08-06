package org.cishell.templates.wizards.widgets;

import org.cishell.templates.staticexecutable.providers.PlatformOption;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;

public class ChooseFileWidget extends Composite implements SelectionListener {
	public static final String BROWSE_FILES_BUTTON_LABEL = "Browse";
	public static final String REMOVE_FILE_LABEL_TEXT = "Remove?";
	
	public static final int PADDING_FOR_FILE_PATH_TEXT_WITHOUT_REMOVE_BUTTON =
		75;
	public static final int PADDING_FOR_FILE_PATH_TEXT_WITH_REMOVE_BUTTON =
		135;
	
	private Text filePathText;
	private Button browseFilesButton;
	private ChooseRelatedFilesWidget fileChosenListener;
	private ChooseRelatedFilesWidget removeElementListener;
	private PlatformOption platformOption;
	
	public ChooseFileWidget(Composite parent,
							int style,
							int parentParentWidth,
							PlatformOption stringOption) {
		this(parent, style, true, parentParentWidth, stringOption);
	}
	
	public ChooseFileWidget(Composite parent,
							int style,
							boolean hasRemoveButton,
							int parentParentWidth,
							PlatformOption platformOption) {
		super(parent, style);
		
		this.platformOption = platformOption;
		
		setLayout(createLayoutForThis());
		
		Composite container =
			createContainer(hasRemoveButton, parentParentWidth);
		this.platformOption.createControl(container, 2);
//		this.filePathText = createFilePathText();
		this.browseFilesButton = createBrowseFilesButton();
		createRemoveElement(hasRemoveButton);
		
		// fixFilePathTextWidth(widthForFilePathText);
	}
	
	public PlatformOption getPlatformOption() {
		return this.platformOption;
	}
	
	public String getFilePath() {
		return this.platformOption.getText();
		// return this.filePathText.getText();
	}
	
	public void setFilePath(String filePath) {
		this.platformOption.setText(filePath);
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
	
	private Composite createContainer(boolean hasRemoveButton,
									  int parentParentWidth) {
		int widthForFilePathText;
		
		if (hasRemoveButton) {
			widthForFilePathText = parentParentWidth - 
				PADDING_FOR_FILE_PATH_TEXT_WITH_REMOVE_BUTTON;
		} else {
			widthForFilePathText = parentParentWidth -
				PADDING_FOR_FILE_PATH_TEXT_WITHOUT_REMOVE_BUTTON;
		}
		
		// TODO: Fix the layout so the buttons aren't vertically offset
		// above this!
		Composite container = new Composite(this, SWT.NONE);
		container.setLayoutData(createFilePathTextLayoutData(widthForFilePathText));
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container.setLayout(layout);
		
		return container;
	}
	
	private Text createFilePathText() {
		Text filePathText = new Text(this, SWT.BORDER);
		filePathText.setEditable(true);
		
		return filePathText;
	}
	
	private Button createBrowseFilesButton() {
		Button browseFilesButton = new Button(this, SWT.PUSH);
		browseFilesButton.setLayoutData(createBrowseFilesButtonLayoutData());
		browseFilesButton.setText(BROWSE_FILES_BUTTON_LABEL);
		browseFilesButton.addSelectionListener(this);
		
		return browseFilesButton;
	}
	
	private void createRemoveElement(boolean hasRemoveButton) {
		if (hasRemoveButton) {
			Button removeButton = new Button(this, SWT.PUSH);
			removeButton.setLayoutData(createRemoveElementLayoutData());
			removeButton.setText(REMOVE_FILE_LABEL_TEXT);
			removeButton.addSelectionListener(this);
		} else {
			Label removeLabel = new Label(this, SWT.NONE);
			removeLabel.setLayoutData(createRemoveElementLayoutData());
			removeLabel.setText(REMOVE_FILE_LABEL_TEXT);
			removeLabel.setVisible(false);
		}
	}
	
	private void fixFilePathTextWidth(int widthForFilePathText) {
		this.filePathText.setLayoutData(
			createFilePathTextLayoutData(widthForFilePathText));
	}
	
	private void browseButtonSelected(SelectionEvent selectionEvent) {
		FileDialog fileDialog = new FileDialog(getShell(), SWT.NULL);
		fileDialog.setFileName(getFilePath());
		String filePath = fileDialog.open();
		
		if (filePath != null) {
			setFilePath(filePath);
			// this.filePathText.setText(filePath);
			
			if (this.fileChosenListener != null) {
				this.fileChosenListener.fileWasChosen(this, filePath);
			}
		}
	}
	
	private void removeButtonSelected(SelectionEvent selectionEvent) {
		if (this.removeElementListener != null) {
			this.removeElementListener.removeButtonWasSelected(this);
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
		
		return data;
	}
}
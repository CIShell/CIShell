package org.cishell.templates.wizards.widgets;

import org.cishell.templates.staticexecutable.providers.PlatformOptionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

/*
 * This widget contains a header, which is a PlatformSetupHeaderWidget object,
 *  and it is associated with an operating system platform via its platformName
 *  and platformPath member variables.
 * There is also optionally a widget for choosing an executable file for the
 *  associated platform, which is of type ChooseExecutableFileWidget.
 * There is always a widget for choosing and removing one or more related
 *  files, which is of the type ChooseRelatedFilesWidget.
 */
public class PlatformSetupWidget extends ResizeCompositeHackWidget {
	public static final int WIDGET_WIDTH = 454;
	public static final int WIDGET_HEIGHT = 317;
	public static final int DIFFERENCE_IN_WIDTH = 30;
	
	public static final int CHOOSE_EXECUTABLE_FILE_WIDGET_WIDTH =
		(int)(WIDGET_WIDTH / 2) - DIFFERENCE_IN_WIDTH;;
	public static final int CHOOSE_RELATED_FILES_WIDGET_WIDTH =
		(int)(WIDGET_WIDTH / 2) + DIFFERENCE_IN_WIDTH;;
	
	private String platformName;
	private String platformPath;
	
	public PlatformSetupWidget(Composite parent,
							   int style,
							   String label,
							   String directoryPath,
							   boolean hasChooseExecutableFileWidget,
							   PlatformOptionProvider platformOptionProvider) {
		super(parent, style);
		this.platformName = label;
		this.platformPath = directoryPath;
		
		setLayout(createLayoutForThis());
		
		createHeader();
		
		if (hasChooseExecutableFileWidget) {
			createChooseExecutableFileWidget(platformOptionProvider);
			createChooseRelatedFilesWidget(true, platformOptionProvider);
		} else {
			createChooseRelatedFilesWidget(false, platformOptionProvider);
		}
		
//		setSize(computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	public String getLabelText() {
		return this.platformName;
	}
	
	public String getDirectoryPath() {
		return this.platformPath;
	}
	
	private Layout createLayoutForThis() {
		GridLayout layout = new GridLayout(2, true);
		layout.makeColumnsEqualWidth = false;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		
		return layout;
	}
	
	private void createHeader() {
		PlatformSetupHeaderWidget header =
			new PlatformSetupHeaderWidget(this, SWT.NONE, getLabelText());
		header.setLayoutData(createHeaderLayoutData());
	}
	
	private void createChooseExecutableFileWidget(
			PlatformOptionProvider platformOptionProvider) {
		ChooseExecutableFileWidget chooseExecutableFileWidget =
			new ChooseExecutableFileWidget(this,
										   SWT.BORDER,
										   CHOOSE_EXECUTABLE_FILE_WIDGET_WIDTH,
										   this.platformName,
										   this.platformPath,
										   platformOptionProvider);
		chooseExecutableFileWidget.setLayoutData(
			createChooseExecutableFileWidgetLayoutData());
	}
	
	// If the files are not related, they are common/default.
	private void createChooseRelatedFilesWidget(
			boolean filesAreRelated,
			PlatformOptionProvider platformOptionProvider) {
		ChooseRelatedFilesWidget chooseRelatedFilesWidget =
			new ChooseRelatedFilesWidget(this,
										 SWT.BORDER,
										 filesAreRelated,
										 CHOOSE_RELATED_FILES_WIDGET_WIDTH,
										 this.platformName,
										 this.platformPath,
										 platformOptionProvider);
		chooseRelatedFilesWidget.setLayoutData(
			createChooseRelatedFilesWidgetLayoutData(filesAreRelated));
	}
	
	private GridData createHeaderLayoutData() {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		data.grabExcessHorizontalSpace = true;
		
		return data;
	}
	
	private GridData createChooseExecutableFileWidgetLayoutData() {
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		
		data.widthHint = CHOOSE_EXECUTABLE_FILE_WIDGET_WIDTH;
		
		return data;
	}
	
	private GridData createChooseRelatedFilesWidgetLayoutData(
			boolean shouldSpanEntireWidth) {
		GridData data = new GridData();
		
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		
		if (!shouldSpanEntireWidth) {
			data.horizontalAlignment = GridData.FILL;
			data.grabExcessHorizontalSpace = true;
			data.horizontalSpan = 2;
		} else {
			data.widthHint = CHOOSE_RELATED_FILES_WIDGET_WIDTH;
		}
		
		return data;
	}
}
package org.cishell.templates.wizards.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/*
 * This widget contains any appropriate header widgets for a
 *  PlatformSetupWidget.
 */
public class PlatformSetupHeaderWidget extends Composite {
	public static final String PLATFORM_LABEL = "Platform";
	
	public PlatformSetupHeaderWidget(
			Composite parent, int style, String platformTitle) {
		super(parent, style);
		setLayout(createLayoutForThis());
		
		createPlatformLabel(platformTitle);
		
//		setSize(computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}
	
	private GridLayout createLayoutForThis() {
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		
		return layout;
	}
	
	private void createPlatformLabel(String platformTitle) {
		String label = PLATFORM_LABEL + ": " + platformTitle;
		
		Composite container = createPlatformLabelContainer();
		Label platformLabel = new Label(container, SWT.NONE);
		platformLabel.setFont(
			createLabelFont(platformLabel.getDisplay(),
							platformLabel.getFont().getFontData()));
		platformLabel.setText(label);
		platformLabel.setLayoutData(createLabelLayoutData());
	}
	
	private Composite createPlatformLabelContainer() {
		Composite container = new Composite(this, SWT.NONE);
		container.setLayoutData(createPlatformLabelContainerLayoutData());
		container.setLayout(createContainerLayout());
		container.setSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		return container;
	}
	
	private GridData createPlatformLabelContainerLayoutData() {
		GridData data = new GridData();
		
		return data;
	}
	
	private GridData createLabelLayoutData() {
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = SWT.CENTER;
		data.verticalAlignment = SWT.CENTER;
		
		return data;
	}
	
	private GridLayout createContainerLayout() {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		
		return layout;
	}
	
	private Font createLabelFont(Device device, FontData[] oldFontData) {
		FontData[] newFontData = new FontData[oldFontData.length];
		
		for (int ii = 0; ii < oldFontData.length; ii++) {
			newFontData[ii] = new FontData(
				oldFontData[ii].getName(),
				oldFontData[ii].getHeight() + 2,
				oldFontData[ii].getStyle() | SWT.BOLD);
		}
		
		return new Font(device, newFontData);
	}
}
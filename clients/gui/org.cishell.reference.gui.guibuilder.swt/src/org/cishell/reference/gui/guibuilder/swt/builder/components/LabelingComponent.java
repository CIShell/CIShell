/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Sep 20, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.gui.guibuilder.swt.builder.components;

import org.cishell.reference.gui.guibuilder.swt.builder.AbstractComponent;
import org.cishell.reference.gui.guibuilder.swt.builder.GUIComponent;
import org.cishell.reference.gui.guibuilder.swt.builder.StringConverter;
import org.cishell.reference.gui.guibuilder.swt.builder.UpdateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class LabelingComponent extends AbstractComponent implements UpdateListener {
	private static final String DEFAULT_DESCRIPTION_TEXT = "No help text available.";
	private static final Color DESCRIPTION_BGCOLOR = new Color(null, 250, 250, 210);
	private static final int DESCRIPTION_SHELL_LEFT_MARGIN = 12;
	private static final Point DESCRIPTION_SHELL_DIMENSIONS = new Point(250, 30);

	private GUIComponent childComponent;
	private Label label;

	public LabelingComponent(GUIComponent childComponent) {
		super(true, childComponent.getColumns());
		this.childComponent = childComponent;
		setAttributeDefinition(childComponent.getAttributeDefinition());

		if (!childComponent.drawsLabel()) {
			columnCount++;
		}

		String description = attribute.getDescription();

		if ((description != null) && (description.length() > 0)) {
			columnCount++;
		}

		childComponent.addUpdateListener(this);
	}

	public Control createGUI(Composite parent, int style) {
		if (drawsLabel && !childComponent.drawsLabel()) {

			String labelText = attribute.getName();
			if (labelText == null) {
				labelText = "";
			}

			label = new Label(parent, SWT.NONE);
			label.setText(labelText);
		}

		Control control = childComponent.createGUI(parent, style);
		setDefaultValue();

		createAndAddDescriptionButton(control, parent);

		return control;
	}

	private void createAndAddDescriptionButton(Control control, Composite parent) {
		/*
		 * Create the description button, and add it to the parent.
		 * */
		Button descriptionButton = new Button(parent, SWT.TOGGLE);

		/*
		 * set the button's layout.
		 * */
		GridData grid = new GridData(SWT.END, SWT.CENTER, false, false);
		descriptionButton.setLayoutData(grid);

		/*
		 * Give the button an image.
		 * */
		Image image = parent.getDisplay().getSystemImage(SWT.ICON_QUESTION);
		Rectangle r = image.getBounds();
		image = new Image(null, image.getImageData().scaledTo(r.width / 2,
															  r.height / 2));
		descriptionButton.setImage(image);

		/*
		 * Handle displaying the description associated with the button.
		 * */
		String descriptionText = getDescriptionText();
		if (label != null) {
			label.setToolTipText(descriptionText);
		} else {
			control.setToolTipText(descriptionText);
		}
		descriptionButton.setToolTipText(descriptionText);
		DescriptionButtonListener listener = new DescriptionButtonListener(descriptionText);
		descriptionButton.addSelectionListener(listener);
	}


	/**
	 * Sets the location for a hovering shell.
	 * 
	 * @param descriptionShell
	 *            the object that is to hover
	 * @param position
	 *            the position of a widget to hover over
	 */
	private void setHoverLocation(Shell descriptionShell, Point position) {
		Rectangle displayBounds = descriptionShell.getDisplay().getBounds();
		Rectangle shellBounds = descriptionShell.getBounds();
		shellBounds.x = Math.max(Math.min(position.x
				+ DESCRIPTION_SHELL_LEFT_MARGIN, displayBounds.width
				- shellBounds.width), 0);

		shellBounds.y = Math.max(Math.min(position.y, displayBounds.height
				- shellBounds.height), 0);
		descriptionShell.setBounds(shellBounds);
	}

	protected void setDefaultValue() {
		String[] defaults = attribute.getDefaultValue();

		if ((defaults != null) && (defaults.length > 0)) {
			Object value = StringConverter.getInstance().stringToObject(attribute, defaults[0]);
			setValue(value);
		}
	}

	public Object getValue() {
		return childComponent.getValue();
	}

	public void setValue(Object value) {
		childComponent.setValue(value);
	}

	public String validate() {
		String valid = childComponent.validate();

		// If valid is a string then the string is the error message.
		if ((valid != null) && (valid.length() > 0)) {
			label.setForeground(ERROR_COLOR);
		} else {
			label.setForeground(null);
		}

		return valid;
	}

	public void componentUpdated(GUIComponent component) {
		if (!childComponent.drawsLabel()) {
			validate();
		}

		update();
	}

	private String getDescriptionText() {
		String descriptionText = attribute.getDescription();
		if (descriptionText == null || descriptionText.length() == 0) {
			descriptionText = DEFAULT_DESCRIPTION_TEXT;
		}

		return descriptionText;
	}

	/*
	 * Adds selection listener to the button. Whenever a button is pressed it triggers
	 * the button selected event, which causes the creation of a new Description hover.
	 * Once a button is unselected it deletes the Description hover. 
	 * */
	class DescriptionButtonListener implements SelectionListener {
		private Shell descriptionShell = null;
		private String descText;

		DescriptionButtonListener(String descText) {
			this.descText = descText;
		}

		public void widgetDefaultSelected(SelectionEvent arg0) { }

		public void widgetSelected(SelectionEvent arg0) {
			Button descriptionButton = (Button) arg0.widget;
			/*
			 * When the description button is selected, the toggle state on the
			 * button gets activated. On this create the hover containing the
			 * description information.
			 */
			if (descriptionButton.getSelection()) {

				/*
				 * To handle the event if the description is opened then closed
				 * & then user wants to open it again. In such a case we have to
				 * make sure that there is an object (Description Shell) to
				 * open.
				 */
				if (this.descriptionShell == null
						|| this.descriptionShell.isDisposed()) {
					this.descriptionShell = new Shell(descriptionButton
							.getShell(), SWT.NONE);
				}

				/*
				 * Creation of a new description shell, which is a hover containing the 
				 * description textField.
				 */
				this.descriptionShell = createDescriptionShell(descText, descriptionButton);
				
				/*
				 * To get the absolute position of the opened dialog box. This will decide 
				 * the position of the hover.
				 */
				Point absoluteShellPosition = descriptionButton.toDisplay(
						descriptionButton.getBounds().width, 0);
				setHoverLocation(descriptionShell, absoluteShellPosition);

				descriptionShell.open();
			} else {

				/*
				 * When the description button is pressed, the hover description
				 * needs to be removed.
				 */
				if (!descriptionShell.isDisposed()) {
					descriptionShell.close();
				}
			}
		}

		private Shell createDescriptionShell(final String descText,
				Button descriptionButton) {
			Shell descriptionShell = new Shell(descriptionButton.getShell(), SWT.NONE);

			descriptionShell.setLayout(new FillLayout());
			descriptionShell.setSize(DESCRIPTION_SHELL_DIMENSIONS);

			Text description = new Text(descriptionShell, SWT.MULTI
					| SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);

			description.setText(descText);

			description.setBackground(DESCRIPTION_BGCOLOR);

			/*
			 * In order to enable the users to close the description by just
			 * pressing the ESC key.
			 */
			descriptionShell.addListener(SWT.Traverse, 
										 new DescriptionShellListener(descriptionButton));
			return descriptionShell;
		}
	}
	
	/*
	 * Listener for Description Shell so that when ESC key is pressed it is closed.
	 * It does so by setting the state of the Button as false, which in turn 
	 * triggers De-selection event of Button.
	 * */
	class DescriptionShellListener implements Listener {

		private Button descriptionButton;

		public DescriptionShellListener(Button descriptionButton) {
			this.descriptionButton = descriptionButton;
		}

		public void handleEvent(Event event) {
			switch (event.detail) {
			case SWT.TRAVERSE_ESCAPE:

				/*
				 * In order to reset the state of the Description button.
				 */
				descriptionButton.setSelection(false);
				break;
			default:
				break;
			}
		}
	}
}

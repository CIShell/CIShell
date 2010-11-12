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
import org.cishell.reference.gui.guibuilder.swt.builder.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class StringComponent extends AbstractComponent {
	protected Text textField;
	protected Combo combo;
	protected String[] optionValues;
	private boolean multiline;

	public StringComponent() {
		this(false, 1);
	}

	public StringComponent(boolean multiline) {
		this(false, 1);
		this.multiline = multiline;
	}

	public StringComponent(boolean drawLabel, int numColumns) {
		super(drawLabel, numColumns);
	}

	public Control createGUI(Composite parent, int style) {
		GridData gd = new GridData(SWT.FILL,SWT.CENTER,true,true);
		gd.horizontalSpan = MAX_SPAN-1;
		gd.minimumWidth = 100;
		optionValues = attribute.getOptionValues();
		if(optionValues != null) {
			combo = new Combo(parent, style | SWT.DROP_DOWN | SWT.READ_ONLY);

			String[] optionLabels = attribute.getOptionLabels();
			if(optionLabels == null) {
				combo.setItems(optionValues);
			} else {
				combo.setItems(optionLabels);
			}

			combo.select(0);

			combo.setLayoutData(gd);

			combo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					update();
				}
			});

			return combo;
		} else {
			int flags;

			if (multiline) {
				flags = style | SWT.BORDER | SWT.MULTI | SWT.V_SCROLL;
				gd.minimumHeight = 100;
				gd.minimumWidth = 250;
			} else {
				flags = style | SWT.BORDER;
			}

			textField = new Text(parent, flags);
			textField.setLayoutData(gd);

			textField.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					update();
				}
			}); 

			return textField;
		}
	}

	public Object getValue() {
		Object value;
		if(combo == null) {
			value = StringConverter.getInstance().stringToObject(attribute, textField.getText());
		} else {
			value = StringConverter.getInstance().stringToObject(attribute, getListValue());
		}

		return value;
	}

	private String getListValue() {
		if (optionValues != null) {
			return optionValues[combo.getSelectionIndex()];
		} else {
			return "You are not specifying option values, fool!";
		}
	}

	public String validate() {
		if (getValue() == null) {
			return "Invalid basic value";
		}
		if(combo == null) {
			return attribute.validate(textField.getText());
		} else {
			return attribute.validate(getListValue());
		}
	}

	public void setValue(Object value) {
		String valueString = emptyStringIfNull(value);
		valueString = fixTextFieldPrefix("textarea:", valueString);
		valueString = fixTextFieldPrefix("file:", valueString);
        valueString = fixTextFieldPrefix("directory:", valueString);

		if (textField != null) {
			textField.setText(valueString);
		} else if (combo != null) {
			int setComboToIndex = -1;

			for (int i = 0; i < optionValues.length; i++) {
				if (valueString.equals(optionValues[i])) {
					setComboToIndex = i;
				}
			}

			if (setComboToIndex != -1) {
				combo.select(setComboToIndex);
			} else {
				/* TODO: Log this (or do something with it besides printint it to a place most
				 * users won't see it)?
				 */
				/*String warningMessage =
					"Attempted to set combo box to a value that didn't exist inside the " +
					"combo box.";
				System.err.println(warningMessage);*/
			}
		}
	}

	// TODO: Use the CIShell Utilities version, but only when that's been all refactor and stuff.
	private String emptyStringIfNull(Object value) {
		if (value == null) {
			return "";
		} else {
			return value.toString();
		}
	}

	private String fixTextFieldPrefix(String prefix, String value) {
        if (value.startsWith(prefix)) {
        	return value.substring(prefix.length());
        } else {
        	return value;
        }
    }
}

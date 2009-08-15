package org.cishell.templates.staticexecutable.optiontypes;

import org.eclipse.pde.ui.templates.BaseOptionTemplateSection;
import org.eclipse.pde.ui.templates.TemplateOption;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class CustomStringOption extends TemplateOption {
	public final static int DEFAULT_STYLE = SWT.SINGLE | SWT.BORDER;
	
	protected Text textWidget;
	protected Label labelWidget;
	protected boolean shouldIgnoreListener;
	protected int style;
	
	public CustomStringOption(
			BaseOptionTemplateSection section, String name, String label) {
		super(section, name, label);
		
		this.style = DEFAULT_STYLE;
		setRequired(true);
	}
	
	public Text getTextWidget() {
		return this.textWidget;
	}
	
	public Label getLabelWidget() {
		return this.labelWidget;
	}
	
	public void setReadOnly(boolean readOnly) {
		if (readOnly) {
			this.style = DEFAULT_STYLE | SWT.READ_ONLY;
		} else {
			this.style = DEFAULT_STYLE;
		}
	}

	public String getText() {
		if (getValue() != null) {
			return getValue().toString();
		} else {
			return null;
		}
	}

	public void setText(String newText) {
		setValue(newText);
	}

	public void setValue(Object value) {
		super.setValue(value);
		
		if (this.textWidget != null) {
			this.shouldIgnoreListener = true;
			String textValue = getText();
			
			if (textValue != null) {
				this.textWidget.setText(textValue);
			} else {
				this.textWidget.setText("");
			}
			
			this.shouldIgnoreListener = false;
		}
	}

	public void createControl(Composite parent, int span) {
		int textWidgetHorizontalSpan;
		
		if (span >= 0) {
			this.labelWidget = createLabel(parent, 1);
			this.labelWidget.setEnabled(isEnabled());
		
			textWidgetHorizontalSpan = span - 1;
		} else {
			textWidgetHorizontalSpan = -span;
		}
		
		this.textWidget = new Text(parent, style);
		
		if (getValue() != null) {
			this.textWidget.setText(getValue().toString());
		}
		
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = textWidgetHorizontalSpan;
		this.textWidget.setLayoutData(gridData);
		this.textWidget.setEnabled(isEnabled());
		
		this.textWidget.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent modifyEvent) {
				if (CustomStringOption.this.shouldIgnoreListener) {
					return;
				}
				
				CustomStringOption.super.setValue(
					CustomStringOption.this.textWidget.getText());
				getSection().validateOptions(CustomStringOption.this);
			}
		});
	}

	public boolean isEmpty() {
		if (getValue() == null ||
				getValue().toString().length() == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		if (this.labelWidget != null) {
			this.labelWidget.setEnabled(enabled);
			this.textWidget.setEnabled(enabled);
		}
	}
}
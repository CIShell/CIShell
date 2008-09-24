package org.cishell.gui.prefgui.preferencepages;

import net.sf.commonclipse.preferences.LabelFieldEditor;

import org.eclipse.jface.preference.FieldEditorPreferencePage;

public class BlankPreferencePage extends FieldEditorPreferencePage {

	private String description;
	
	public BlankPreferencePage(int style, String title, String description) {
		super(style);
		this.setTitle(title);
		this.description = description;
	}
	
	protected void createFieldEditors() {
		LabelFieldEditor label = new LabelFieldEditor(description, this.getFieldEditorParent());
		this.addField(label);
	}

}

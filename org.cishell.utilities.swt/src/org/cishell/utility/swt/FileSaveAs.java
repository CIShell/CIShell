package org.cishell.utility.swt;

import java.io.File;

import org.cishell.utilities.StringUtilities;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class FileSaveAs {
	public static final String DEFAULT_WINDOW_TITLE = "Save As";
	public static final String CONFIRMATION_DIALOG_FORMAT =
		"%s already exists.\nDo you want to replace it?";
//	public static final String YES_BUTTON_LABEL = "Yes";
//	public static final String NO_BUTTON_LABEL = "No";
//	public static final String[] BUTTON_LABELS = { YES_BUTTON_LABEL, NO_BUTTON_LABEL };

	public static String saveFileAs(Shell parent) {
		FileDialog saveDialog = new FileDialog(parent);
		saveDialog.setText(DEFAULT_WINDOW_TITLE);

		return saveFileAs(saveDialog);
	}

	public static String saveFileAs(Shell parent, int style) {
		FileDialog saveDialog = new FileDialog(parent, style);
		saveDialog.setText(DEFAULT_WINDOW_TITLE);

		return saveFileAs(saveDialog);
	}

	public static String saveFileAs(FileDialog saveDialog) {
		while (true) {
			String selectedFilePath = saveDialog.open();

			if (StringUtilities.isNull_Empty_OrWhitespace(selectedFilePath)) {
				return null;
			} else {
				if (new File(selectedFilePath).exists()) {
					if (MessageDialog.openConfirm(
							saveDialog.getParent(),
							saveDialog.getText(),
							String.format(CONFIRMATION_DIALOG_FORMAT, selectedFilePath))) {
						return selectedFilePath;
					}
				} else {
					return selectedFilePath;
				}
			}
		}
	}
}
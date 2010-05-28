package org.cishell.reference.gui.persistence.load;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;

public final class FileSelectorRunnable implements Runnable {
	private IWorkbenchWindow window;
	
	private File file;

	public FileSelectorRunnable(IWorkbenchWindow window) {
		this.window = window;
	}

	public File getFile() {
		return this.file;
	}

	public void run() {
		this.file = getFileFromUser();

		if (this.file == null) {
			return;
		} else if (this.file.isDirectory()) {
			FileLoadAlgorithm.defaultLoadDirectory = this.file.getAbsolutePath();
		} else {
			FileLoadAlgorithm.defaultLoadDirectory = this.file.getParentFile().getAbsolutePath();
		}
	}

	private File getFileFromUser() {
		FileDialog fileDialog = createFileDialog();
		String fileName = fileDialog.open();

		if (fileName == null) {
			return null;
		} else {
			return new File(fileName);
		}
	}

	private FileDialog createFileDialog() {
		File currentDirectory = new File(FileLoadAlgorithm.defaultLoadDirectory);
		String absolutePath = currentDirectory.getAbsolutePath();
		FileDialog fileDialog = new FileDialog(this.window.getShell(), SWT.OPEN);
		fileDialog.setFilterPath(absolutePath);
		fileDialog.setText("Select a File");

		return fileDialog;
	}
}
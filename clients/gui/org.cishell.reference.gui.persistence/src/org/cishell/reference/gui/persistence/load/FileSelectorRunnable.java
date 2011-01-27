package org.cishell.reference.gui.persistence.load;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;

public final class FileSelectorRunnable implements Runnable {
	private IWorkbenchWindow window;
	
	private File[] files;

	public FileSelectorRunnable(IWorkbenchWindow window) {
		this.window = window;
	}

	public File[] getFiles() {
		return this.files;
	}

	public void run() {
		this.files = getFilesFromUser();

		if (this.files.length == 0) {
			return;
		/*} else if (this.files[0].isDirectory()) {
			// TODO Can we kill this branch?
			FileLoadAlgorithm.defaultLoadDirectory = this.files[0].getAbsolutePath(); */
		} else {
			FileLoadAlgorithm.defaultLoadDirectory =
				this.files[0].getParentFile().getAbsolutePath();
		}
	}

	private File[] getFilesFromUser() {
		FileDialog fileDialog = createFileDialog();
		fileDialog.open();
		String path = fileDialog.getFilterPath();
		String[] fileNames = fileDialog.getFileNames();
		// TODO: Ask Angela about the order here, i.e. should they be sorted alphabetically?

		if ((fileNames == null) || (fileNames.length == 0)) {
			return new File[0];
		} else {
			File[] files = new File[fileNames.length];

			for (int ii = 0; ii < fileNames.length; ii++) {
				String fullFileName = path + File.separator + fileNames[ii];
				files[ii] = new File(fullFileName);
			}

			return files;
		}
	}

	private FileDialog createFileDialog() {
		File currentDirectory = new File(FileLoadAlgorithm.defaultLoadDirectory);
		String absolutePath = currentDirectory.getAbsolutePath();
		FileDialog fileDialog = new FileDialog(this.window.getShell(), SWT.OPEN | SWT.MULTI);
		fileDialog.setFilterPath(absolutePath);
		fileDialog.setText("Select Files");

		return fileDialog;
	}
}
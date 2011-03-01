package org.cishell.reference.app.service.fileloader;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;

public final class FileSelectorRunnable implements Runnable {
	private IWorkbenchWindow window;
	private boolean selectSingleFile;
	private String[] filterExtensions;
	private File[] files;

	public FileSelectorRunnable(
			IWorkbenchWindow window, boolean selectSingleFile, String[] filterExtensions) {
		this.window = window;
		this.selectSingleFile = selectSingleFile;
		this.filterExtensions = filterExtensions;
	}

	public File[] getFiles() {
		return this.files;
	}

	public void run() {
		this.files = getFilesFromUser();

		if (this.files.length == 0) {
			return;
		} else {
			FileLoaderServiceImpl.defaultLoadDirectory =
				this.files[0].getParentFile().getAbsolutePath();
		}
	}

	private File[] getFilesFromUser() {
		FileDialog fileDialog = createFileDialog();
		fileDialog.open();
		String path = fileDialog.getFilterPath();
		String[] fileNames = fileDialog.getFileNames();

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
		File currentDirectory = new File(FileLoaderServiceImpl.defaultLoadDirectory);
		String absolutePath = currentDirectory.getAbsolutePath();
		FileDialog fileDialog =
			new FileDialog(this.window.getShell(), SWT.OPEN | determineSWTFileSelectFlag());
		fileDialog.setFilterPath(absolutePath);

		if ((this.filterExtensions != null) && (this.filterExtensions.length > 0)) {
			fileDialog.setFilterExtensions(this.filterExtensions);
		}

		fileDialog.setText("Select Files");

		return fileDialog;
	}

	private int determineSWTFileSelectFlag() {
		if (this.selectSingleFile) {
			return SWT.SINGLE;
		} else {
			return SWT.MULTI;
		}
	}
}
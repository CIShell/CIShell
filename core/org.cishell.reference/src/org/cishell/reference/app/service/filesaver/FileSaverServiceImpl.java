package org.cishell.reference.app.service.filesaver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.cishell.app.service.filesaver.AbstractFileSaverService;
import org.cishell.app.service.filesaver.FileSaveException;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.component.ComponentContext;

public class FileSaverServiceImpl extends AbstractFileSaverService {
	public static final String SAVE_DIALOG_TITLE = "Save";

	private DataConversionService conversionManager;
	private GUIBuilderService guiBuilder;

	protected void activate(ComponentContext componentContext) {
		this.conversionManager = (DataConversionService) componentContext.locateService("DCS");
		this.guiBuilder = (GUIBuilderService) componentContext.locateService("GBS");
		
	}

	public Converter promptForConverter(final Data outDatum, String targetMimeType)
			throws FileSaveException {
		final Converter[] converters =
			this.conversionManager.findConverters(outDatum, targetMimeType);

    	if (converters.length == 0) {
    		throw new FileSaveException("No appropriate converters.");
    	} else if (converters.length == 1) {
			// Only one possible choice in how to save data.  Do it.
			Converter onlyConverter = converters[0];

			return onlyConverter;
		} else {
			final Shell parentShell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();

	    	if (parentShell.isDisposed()) {
	    		throw new FileSaveException(
	    				"Can't create dialog window -- graphical environment not available.");
	    	}
		
    		return showDataFormatChooser(outDatum, converters, parentShell);
		}
	}

	private Converter showDataFormatChooser(
			final Data outDatum,
			final Converter[] converters,
			final Shell parentShell) throws FileSaveException{
		try {
			final Converter[] chosenConverter = new Converter[1];
			guiRun(new Runnable() {
				public void run() {
					DataFormatChooser formatChooser = new DataFormatChooser(
						outDatum, parentShell, converters, SAVE_DIALOG_TITLE);
					formatChooser.createContent(new Shell(parentShell));
					formatChooser.open();
					chosenConverter[0] = formatChooser.getChosenConverter();
				}
			});
	
			return chosenConverter[0];
		} catch (Exception e) {
			throw new FileSaveException(e.getMessage(), e);
		}
	}

	public File promptForTargetFile(final String fileName) throws FileSaveException {
		final File[] resultFile = new File[1];

		try {
			guiRun(new Runnable() {
				public void run() {
					SaveAsController saveAs =
						new SaveAsController(FileSaverServiceImpl.this.guiBuilder);

					resultFile[0] = saveAs.open(fileName);
				}
			});

			return resultFile[0];
		} catch (Throwable e) {
			throw new FileSaveException(e.getMessage(), e);
		}
	}

	public void saveTo(File sourceFile, File targetFile) throws FileSaveException {
		if ((sourceFile != null) && (targetFile != null) && sourceFile.exists()) {
			copyFile(sourceFile, targetFile);
		}
	}

	private void guiRun(Runnable run) {
		final Shell parentShell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();

        if (Thread.currentThread() == Display.getDefault().getThread()) {
            run.run();
        } else {
            parentShell.getDisplay().syncExec(run);
        }
    }

	/* TODO: Don't use cns-utilities, use Files.copy in Guava
	 * This shouldn't throw FileSaveException -- too specific for a general utility.
	 * Catch whatever this throws then rethrow as FSE
	 */
	private static void copyFile(File sourceFile, File targetFile) throws FileSaveException {
		try {
			FileInputStream inputStream = new FileInputStream(sourceFile);
			FileOutputStream outputStream = new FileOutputStream(targetFile);

			FileChannel readableChannel = inputStream.getChannel();
			FileChannel writableChannel = outputStream.getChannel();

			writableChannel.truncate(0);
			writableChannel.transferFrom(
				readableChannel, 0, readableChannel.size());
			inputStream.close();
			outputStream.close();
		} catch (IOException ioException) {
			String exceptionMessage =
				"An error occurred when copying from the file \"" +
				sourceFile.getAbsolutePath() +
				"\" to the file \"" +
				targetFile.getAbsolutePath() +
				"\".";
			
			throw new FileSaveException(exceptionMessage, ioException); // TODO Just throw IOException
		}
	}
}
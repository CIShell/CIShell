package org.cishell.reference.gui.persistence.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.reference.gui.persistence.FileUtil;
import org.cishell.service.conversion.ConversionException;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.log.LogService;

/* 
 * @author Weixia(Bonnie) Huang (huangb@indiana.edu)
 */
public class FileView implements Algorithm {
	public static final String ANY_FILE_EXTENSION_FILTER = "file-ext:*";
	private static final String CSV_FILE_EXT = "file-ext:csv";
	private static final String CSV_MIME_TYPE = "file:text/csv";
	public static final String FILE_EXTENSION_PREFIX = "file-ext:";
	public static final String ANY_TEXT_MIME_TYPE = "file:text/";
	private Data[] data;
	private CIShellContext context;
	private DataConversionService conversionManager;
	private LogService logger;
	private Program program;
	private File tempFile;
	

	public FileView(Data[] data, Dictionary parameters, CIShellContext context) {
		this.data = data;
		this.context = context;

		this.conversionManager = (DataConversionService) context
				.getService(DataConversionService.class.getName());

		this.logger = (LogService) context.getService(LogService.class.getName());
	}

	
	// Show the contents of a file to the user
	public Data[] execute() throws AlgorithmExecutionException {
		try {
			boolean lastSaveSuccessful = false;
			boolean isCSVFile = false;
			String format;

			// For each data item we want to view...
			for (int i = 0; i < data.length; i++) {
				Object theData = data[i].getData();
				format = data[i].getFormat();
				String label = (String) data[i].getMetadata().get(
						DataProperty.LABEL);

				// If it is a text file...
				if (theData instanceof File
						|| format.startsWith(ANY_TEXT_MIME_TYPE)
						|| format.startsWith(FILE_EXTENSION_PREFIX)) {

					// If it is a CSV text file...
					if (format.startsWith(CSV_MIME_TYPE)
							|| format.startsWith(CSV_FILE_EXT)) {
						// Prepare to open it like a CSV file
						tempFile = getTempFileCSV();
						isCSVFile = true;
					} else {
						// Prepare to open it like a normal text file
						String fileName = FileUtil.extractFileName(label);
						String extension = FileUtil.extractExtension(format);
						tempFile = FileUtil.getTempFile(fileName, extension,
								logger);
					}

					// Copy out data into the temp file we just created.
					copy((File) data[i].getData(), tempFile);
					lastSaveSuccessful = true;

				} else {
					/* The data item is in an in-memory format, and must be
					 * converted to a file format before the user can see it.
					 */
					final Converter[] convertersCSV =
						conversionManager.findConverters(data[i], CSV_FILE_EXT);

					// If the data item can be converted to a CSV file, do so.
					if (convertersCSV.length == 1) {
						Data newDataCSV = convertersCSV[0].convert(data[i]);
						tempFile = getTempFileCSV();
						isCSVFile = true;
						copy((File) newDataCSV.getData(), tempFile);
						lastSaveSuccessful = true;

					} else if (convertersCSV.length > 1) {
						Data newDataCSV = convertersCSV[0].convert(data[i]);
						for (int j = 1; j < convertersCSV.length; j++) {
							newDataCSV = convertersCSV[j].convert(newDataCSV);
						}
						tempFile = getTempFileCSV();
						isCSVFile = true;
						copy((File) newDataCSV.getData(), tempFile);
						lastSaveSuccessful = true;
					} else { // it cannot be converted to a .csv

						// try to convert it to any other file format

						final Converter[] converters =
							conversionManager.findConverters(
									data[i], ANY_FILE_EXTENSION_FILTER);

						// if it can't be converted to any file format...
						if (converters.length < 1) {
							// throw an error
							throw new AlgorithmExecutionException(
									"No valid converters for data type: "
											+ data[i].getData().getClass()
													.getName()
											+ ". Please install a plugin that will save the data type to a file");
						} else if (converters.length == 1) { // if there is only
																// file format
																// it can be
																// converted to
							// go ahead and convert the data item to that format
							Data newData = converters[0].convert(data[i]);

							String fileName = FileUtil.extractFileName(label);
							String extension = FileUtil
									.extractExtension(newData.getFormat());
							tempFile = FileUtil.getTempFile(fileName,
									extension, logger);
							copy((File) newData.getData(), tempFile);
							lastSaveSuccessful = true;
						} else {
							// there is more than one format that the data
							// item could be converted to
							// let the user choose
							// (get some eclipse UI stuff that we need to open
							// the data viewer)

							Display display;
							IWorkbenchWindow[] windows;
							final Shell parentShell;

							windows = PlatformUI.getWorkbench()
									.getWorkbenchWindows();
							if (windows.length == 0) {
								throw new AlgorithmExecutionException(
										"Cannot get workbench window.");
							}
							parentShell = windows[0].getShell();
							display = PlatformUI.getWorkbench().getDisplay();

							// (open the data viewer, which lets the user choose
							// which format they want to see the data item in.)

							if (!parentShell.isDisposed()) {
								DataViewer dataViewer = new DataViewer(
										parentShell, data[i], converters);
								display.syncExec(dataViewer);
								lastSaveSuccessful = dataViewer.isSaved;
								tempFile = dataViewer.outputFile;
							}
						}
					}
				}

				// If it's a CSV file
				if (isCSVFile) {// TC181
					// prepare to open the file with the default csv program
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							program = Program.findProgram("csv");
						}
					});
				} else {
					// Prepare to open it with the standard text editor.
					Display.getDefault().syncExec(
						new Runnable() {
							public void run() {
								program = Program.findProgram("txt");
							}
						}
					);
				}

				// If we can't find any program to open the file...
				if (program == null) {
					throw new AlgorithmExecutionException(
							"No valid text viewer for the .txt file. "
									+ "The file is located at: "
									+ tempFile.getAbsolutePath()
									+ ". Unable to open default text viewer.  "
									+ "File is located at: "
									+ tempFile.getAbsolutePath());
				} else {
					// We found a program to open the file.  Open it.
					if (lastSaveSuccessful == true) {
						Display.getDefault().syncExec(
							new Runnable() {
								public void run() {
									program.execute(tempFile.getAbsolutePath());
								}
							}
						);
					}
				}
			}
			
			return null;
		} catch (ConversionException e) {
			throw new AlgorithmExecutionException(
					"Error: Unable to view data:\n    " + e.getMessage(), e);
		} catch (Throwable e) {
			throw new AlgorithmExecutionException(e);
		}
	}

	public File getTempFileCSV() {
		File tempFile;

		String tempPath = System.getProperty("java.io.tmpdir");
		File tempDir = new File(tempPath + File.separator + "temp");
		if (!tempDir.exists())
			tempDir.mkdir();
		try {
			tempFile = File.createTempFile("xxx-Session-", ".csv", tempDir);

		} catch (IOException e) {
			logger.log(LogService.LOG_ERROR, e.toString(), e);
			tempFile = new File(tempPath + File.separator + "temp"
					+ File.separator + "temp.csv");

		}
		return tempFile;
	}

	public static boolean copy(File in, File out)
			throws AlgorithmExecutionException {
		try {
			FileInputStream fis = new FileInputStream(in);
			FileOutputStream fos = new FileOutputStream(out);

			FileChannel readableChannel = fis.getChannel();
			FileChannel writableChannel = fos.getChannel();

			writableChannel.truncate(0);
			writableChannel.transferFrom(readableChannel, 0, readableChannel
					.size());
			fis.close();
			fos.close();
			return true;
		} catch (IOException ioe) {
			throw new AlgorithmExecutionException("IOException during copy",
					ioe);
		}
	}

	final class DataViewer implements Runnable {
		public static final String VIEW_DIALOG_TITLE = "View";
		private Shell shell;
		private boolean isSaved;
		private File outputFile;
		private Data data;
		private Converter[] converters;

		
		DataViewer(Shell parentShell, Data data, Converter[] converters) {
			this.shell = parentShell;
			this.data = data;
			this.converters = converters;
		}

		
		public void run() {
			// Lots of persisters found, return the chooser
			ViewDataChooser vdc = new ViewDataChooser(
					VIEW_DIALOG_TITLE, shell, data, converters, context, logger);
			vdc.open();
			isSaved = vdc.isSaved();
			outputFile = vdc.outputFile;
		}
	}
}
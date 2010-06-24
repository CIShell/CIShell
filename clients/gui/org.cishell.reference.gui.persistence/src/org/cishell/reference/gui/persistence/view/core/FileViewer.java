package org.cishell.reference.gui.persistence.view.core;

import java.io.File;
import java.io.IOException;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.reference.gui.persistence.view.core.exceptiontypes.ConvertDataForViewingException;
import org.cishell.reference.gui.persistence.view.core.exceptiontypes.FileViewingException;
import org.cishell.reference.gui.persistence.view.core.exceptiontypes.NoProgramFoundException;
import org.cishell.reference.gui.persistence.view.core.exceptiontypes.UserCanceledDataViewSelectionException;
import org.cishell.service.conversion.ConversionException;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.cishell.service.database.Database;
import org.cishell.utilities.FileCopyingException;
import org.cishell.utilities.FileUtilities;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.log.LogService;

public class FileViewer {
	public static final String FILE_EXTENSION_MIME_TYPE_PREFIX = "file-ext:";
	public static final String ANY_FILE_EXTENSION_FILTER = "file-ext:*";
	public static final String FILE_EXTENSION_PREFIX = "file-ext:";
	public static final String ANY_MIME_TYPE = "file:";
	public static final String CSV_FILE_EXT = "file-ext:csv";
	public static final String CSV_MIME_TYPE = "file:text/csv";
	public static final String TEMPORARY_CSV_FILE_NAME = "CSV-";
	public static final String CSV_FILE_EXTENSION = "csv";
	public static final String TXT_FILE_EXTENSION = "txt";
	public static final String ANY_FILE_FORMAT_PATTERN =
		"(file:.*)|(file-ext:.*)";
	
	public static void viewDataFile(Data data,
									CIShellContext ciShellContext,
									DataConversionService conversionManager,
									LogService logger)
			throws FileViewingException {
		viewDataFileWithProgram(data, "", ciShellContext, conversionManager, logger);
	}
	
	public static void viewDataFileWithProgram(
			Data data,
			String customFileExtension,
			CIShellContext ciShellContext,
			DataConversionService converterManager,
			LogService logger)
			throws FileViewingException {
		FileWithExtension fileWithExtension =
			convertDataForViewing(data, ciShellContext, converterManager,
					logger);
		viewFileWithExtension(fileWithExtension, customFileExtension);
	}
	
	private static FileWithExtension convertDataForViewing(
			Data data,
			CIShellContext ciShellContext,
			DataConversionService converterManager,
			LogService logger) throws FileViewingException {
		try {
			String dataFormat = data.getFormat();
			//TODO: Add image viewing support here (shouldn't be too hard)
			if (dataIsDB(data, converterManager)) {
				try {
				Data genericDBData = converterManager.convert(data, Database.GENERIC_DB_MIME_TYPE);
				Database genericDatabase = (Database) genericDBData.getData();
				
				File dbSchemaOverview = 
					DatabaseSchemaOverviewGenerator.generateDatabaseSchemaOverview(genericDatabase);
				
				return new FileWithExtension(dbSchemaOverview, TXT_FILE_EXTENSION);
				} catch (ConversionException e) {
					//continue attempts to view for other formats
				} catch (Exception e) {
					String message = "Unexpected error occurred while generating "
						+ "database schema overview. Attempting to view the data item"
						+ "by other means.";
					logger.log(LogService.LOG_WARNING, message, e);
				}
			}
			if (isCSVFormat(data)) {
				/*
				 * The data is already a CSV file, so it just needs to
				 *  be copied.
				 */
				try {
					File csvFileForViewing =
						FileUtilities.createTemporaryFileCopy(
							(File)data.getData(),
							TEMPORARY_CSV_FILE_NAME,
							CSV_FILE_EXTENSION);
				
					return new FileWithExtension(
						csvFileForViewing, CSV_FILE_EXTENSION);
				} catch (FileCopyingException csvFileCopyingException) {
					throw new ConvertDataForViewingException(
						csvFileCopyingException);
				}
			} else if (dataIsCSVCompatible(data, converterManager)) {
				/*
				 * The data is either a CSV file already or CSV-convertible.
				 * This needs to be handled specially so data that can be
				 *  viewed in Excel gets viewed in Excel.
				 */
				File preparedFileForViewing = prepareFileForViewing(
					data, CSV_FILE_EXTENSION, converterManager);
				
				return new FileWithExtension(
					preparedFileForViewing, CSV_FILE_EXTENSION);
			} else if (dataIsFile(data, dataFormat)) {
				/*
				 * The data is already a text-based file, so it just needs to
				 *  be copied to a temporary file for viewing in the default
				 *  text-viewing program.
				 */
				return new FileWithExtension(
					prepareTextFileForViewing(data), TXT_FILE_EXTENSION);
			} else if (convertersExist(
				data, ANY_FILE_EXTENSION_FILTER, converterManager)) {
				/*
				 * The data is an another type, but it can be converted to a
				 *  text-based file type for viewing in the default
				 *  text-viewing program.
				 */
				return new FileWithExtension(
					convertDataToTextFile(
						data, converterManager, ciShellContext),
					"txt");
			} else {
				String exceptionMessage =
					"No converters exist for the data \"" +
					data.getMetadata().get(DataProperty.LABEL) +
					"\".";
				
				throw new ConvertDataForViewingException(exceptionMessage);
			}
		} catch (ConvertDataForViewingException
					convertDataForViewingException) {
			String exceptionMessage =
				"There was a problem when preparing the data \"" +
				data.getMetadata().get(DataProperty.LABEL) +
				"\" for viewing.";

			throw new FileViewingException(
				exceptionMessage, convertDataForViewingException);
		}
	}
	
	private static void viewFileWithExtension(
			FileWithExtension fileWithExtension, String customFileExtension)
			throws FileViewingException {
		try {
			final Program program = selectChosenProgramForFileExtension(
				fileWithExtension.fileExtension, customFileExtension);

			executeProgramWithFile(program, fileWithExtension.file);
		} catch (NoProgramFoundException noProgramFoundException) {
			String exceptionMessage =
				"Could not view the file \"" +
				fileWithExtension.file.getAbsolutePath() +
				"\" because no viewing program could be found for it.";
			
			throw new FileViewingException(
				exceptionMessage, noProgramFoundException);
		}
	}
	
	private static boolean isCSVFormat(Data data) {
		String dataFormat = data.getFormat();
		
		if (dataFormat.startsWith(CSV_MIME_TYPE) ||
				dataFormat.startsWith(CSV_FILE_EXT)) {
			return true;
		} else {
			return false;
		}
	}
	
	/*private static boolean dataIsCSVCompatibleFile(
			Data data,
			DataConversionService converterManager) {
		return convertersExist(data, CSV_FILE_EXT, converterManager);
	}*/
	
	private static boolean dataIsCSVCompatible(
			Data data,
			DataConversionService converterManager) {
		if (isCSVFormat(data) ||
				convertersExist(data, CSV_FILE_EXT, converterManager)) {
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean dataIsDB (
			Data data,
			DataConversionService converterManager) {
		if (has_DB_MimeType_Prefix(data) ||
				convertersExist(data, Database.GENERIC_DB_MIME_TYPE, converterManager)) {
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean has_DB_MimeType_Prefix(Data data) {
		return data.getFormat().startsWith(Database.DB_MIME_TYPE_PREFIX);
	}
	
	private static boolean dataIsFile(Data data, String dataFormat) {
		if (data.getData() instanceof File ||
				dataFormat.startsWith(ANY_MIME_TYPE) ||
				dataFormat.startsWith(FILE_EXTENSION_PREFIX)) {
			return true;
		} else {
			return false;
		}
	}
	
	private static boolean convertersExist(
			Data data,
			String targetFormat,
			DataConversionService conversionManager) {
		final Converter[] converters =
			conversionManager.findConverters(data, targetFormat);
		
		if (converters.length > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	private static File prepareFileForViewing(
			Data originalData,
			String fileExtension,
			DataConversionService converterManager)
			throws ConvertDataForViewingException {
		String dataLabel =
			(String)originalData.getMetadata().get(DataProperty.LABEL);
		
		try {
			String fileExtensionMimeType =
				FILE_EXTENSION_MIME_TYPE_PREFIX + fileExtension;
			File convertedFile = convertToFile(
				originalData, fileExtensionMimeType, converterManager);
			String fileName = FileUtilities.extractFileName(dataLabel);
			
			return FileUtilities.createTemporaryFileCopy(
				convertedFile, fileName, fileExtension);
		} catch (ConversionException convertingDataToFileException) {
			String exceptionMessage =
				"A ConversionException occurred when converting the data \"" +
				dataLabel +
				"\" to " + fileExtension + ".";
			
			throw new ConvertDataForViewingException(
				exceptionMessage, convertingDataToFileException);
		} catch (FileCopyingException temporaryFileCopyingException) {
			String exceptionMessage =
				"A FileCopyingException occurred when converting the data \"" +
				dataLabel +
				"\" to " + fileExtension + ".";
			
			throw new ConvertDataForViewingException(
				exceptionMessage, temporaryFileCopyingException);
		}
	}
	
	private static File prepareTextFileForViewing(Data originalData)
			throws ConvertDataForViewingException {
		String dataLabel =
			(String)originalData.getMetadata().get(DataProperty.LABEL);
		String dataFormat = originalData.getFormat();
		String suggestedFileName = FileUtilities.extractFileName(dataLabel);
		String cleanedSuggestedFileName =
			FileUtilities.replaceInvalidFilenameCharacters(suggestedFileName);
		String fileExtension = FileUtilities.extractExtension(dataFormat);

		try {
			File fileToView = FileUtilities.
				createTemporaryFileInDefaultTemporaryDirectory(
						cleanedSuggestedFileName, fileExtension);
			FileUtilities.copyFile((File)originalData.getData(), fileToView);
			
			return fileToView;
		} catch (IOException temporaryFileCreationException) {
			String exceptionMessage =
				"An IOException occurred when creating the temporary file \"" +
				cleanedSuggestedFileName + "." + fileExtension +
				"\" for viewing the data \"" + dataLabel + "\".";
			
			throw new ConvertDataForViewingException(
				exceptionMessage, temporaryFileCreationException);
		} catch (FileCopyingException fileCopyingException) {
			throw new ConvertDataForViewingException(fileCopyingException);
		}
	}

	private static File convertDataToTextFile(
			Data originalData,
			DataConversionService converterManager,
			CIShellContext ciShellContext)
			throws ConvertDataForViewingException {
		final Converter[] converters = converterManager.findConverters(
			originalData, ANY_FILE_EXTENSION_FILTER);

		if (converters.length == 1) {
			/*
			 * There is just one converter, so transparently do
			 *  the conversion.
			 */
			try {
				return convertToFile(
					originalData, converters[0]);
			}
			catch (ConversionException
						convertDataToFileAndPrepareForViewingException) {
				String exceptionMessage =
					"A ConversionException occurred when converting the " +
					"data \"" +
					originalData.getMetadata().get(DataProperty.LABEL) +
					"\" to a file format.";
				
				throw new ConvertDataForViewingException(
					exceptionMessage,
					convertDataToFileAndPrepareForViewingException);
			}
		} else {
			/*
			 * There are several converters available, so the user will
			 *  need to select how the dataToView is to be converted.
			 */
			try {
				return convertDataBasedOffUserChosenConverter(
					originalData, converters, ciShellContext);
			} catch (ConversionException conversionException) {
				String exceptionMessage =
					"A ConversionException occurred when converting the " +
					"data \"" +
					originalData.getMetadata().get(DataProperty.LABEL) +
					"\".";
				
				throw new ConvertDataForViewingException(
					exceptionMessage, conversionException);
			} catch (UserCanceledDataViewSelectionException
						userCanceledDataViewSelectionException) {
				String exceptionMessage =
					"A UserCanceledDataViewSelectionException occurred " +
					"when the user did not choose a converter for the " +
					"data \"" +
					originalData.getMetadata().get(DataProperty.LABEL) +
					"\".";
				
				throw new ConvertDataForViewingException(
					exceptionMessage, userCanceledDataViewSelectionException);
			}
		}
	}
	
	private static Program selectChosenProgramForFileExtension(
			final String defaultFileExtension,
			final String customFileExtension)
			throws NoProgramFoundException {
		String chosenFileExtension = null;
		
		if (customFileExtension.equals("")) {
			chosenFileExtension = defaultFileExtension;
		} else {
			chosenFileExtension = customFileExtension;
		}
		
		Program chosenProgram =
			getProgramForFileExtension(chosenFileExtension);
		
		if (chosenProgram != null) {
			return chosenProgram;
		} else {
			/*
			 * The chosen program doesn't exist, so try to get the
			 *  default viewer.
			 */
			Program defaultProgram = 
				getProgramForFileExtension(defaultFileExtension);
			
			if (defaultProgram != null) {
				return defaultProgram;
			} else {
				String exceptionMessage =
					"You do not have a valid viewer for the ." +
					chosenFileExtension +
					"file installed.";
					
				throw new NoProgramFoundException(exceptionMessage);
			}
		}
	}
	
	private static void executeProgramWithFile(final Program program,
											   final File file) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				program.execute(
					file.getAbsolutePath());
			}
		});
	}

	private static File convertToFile(Data data,
									  String targetFormat,
									  DataConversionService conversionManager)
			throws ConversionException {
		if (targetFormat.matches(ANY_FILE_FORMAT_PATTERN)) {
			Converter[] converters =
				conversionManager.findConverters(data, targetFormat);
		
			return convertToFile(data, converters[0]);
		} else {
			String exceptionMessage =
				"The target format for conversion (\"" +
				targetFormat +
				"\") is not valid.";
			
			throw new ConversionException(exceptionMessage);
		}
	}
	
	
	private static File convertToFile(Data data, Converter converter)
			throws ConversionException {
		Data newData = converter.convert(data);
		return (File)newData.getData();
	}

	private static File convertDataBasedOffUserChosenConverter(
			Data originalData,
			Converter[] converters,
			CIShellContext ciShellContext)
			throws ConversionException,
				   UserCanceledDataViewSelectionException {
		/*
		 * Open the dataToView viewer, which lets the user choose
		 *  which format he/she wants to see the data item in.
		 */
		DataViewer dataViewer = new DataViewer(
			originalData, converters, ciShellContext);

		if (dataViewer.selectedConverter != null) {
			return convertToFile(originalData, dataViewer.selectedConverter);
		} else {
			String exceptionMessage =
				"The user cancelled the selection of a converter for the " +
				"data \"" +
				originalData.getMetadata().get(DataProperty.LABEL) +
				"\".";
			
			throw new UserCanceledDataViewSelectionException(
				exceptionMessage);
		}
	}
	
	private static Program getProgramForFileExtension(
			final String fileExtension) {
		final Program[] programHolder = new Program[1];
		
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				programHolder[0] =
					Program.findProgram(fileExtension);
			}
		});
		
		return programHolder[0];
	}
	
	private final static class DataViewer implements Runnable {
		public static final String VIEW_DIALOG_TITLE = "View";
		private Shell shellWindow;
		private Converter selectedConverter;
		private Data data;
		private Converter[] converters;
		private CIShellContext ciShellContext;
		private LogService logger;
		
		public DataViewer(Data data,
						  Converter[] converters,
						  CIShellContext ciShellContext) {
			this(data,
				 converters,
				 ciShellContext,
				 (LogService)ciShellContext.getService(
					LogService.class.getName()));
		}
		
		public DataViewer(Data data,
						  Converter[] converters,
						  CIShellContext ciShellContext,
						  LogService logger) {
			IWorkbenchWindow[] windows =
				PlatformUI.getWorkbench().getWorkbenchWindows();
			this.shellWindow = windows[0].getShell();
			this.data = data;
			this.converters = converters;
			this.ciShellContext = ciShellContext;
			this.logger = logger;
			
			Display display = PlatformUI.getWorkbench().getDisplay();
			display.syncExec(this);
		}
		
		public void run() {
			// Lots of persisters found, return the chooser.
			ViewDataChooser viewDataChooser = new ViewDataChooser(
				VIEW_DIALOG_TITLE,
				this.shellWindow,
				this.data,
				this.converters,
				this.ciShellContext,
				this.logger);
			viewDataChooser.open();
			this.selectedConverter = viewDataChooser.getSelectedConverter();
		}
	}
	
	private static class FileWithExtension {
		public final File file;
		public final String fileExtension;
		
		public FileWithExtension(File file, String fileExtension) {
			this.file = file;
			this.fileExtension = fileExtension;
		}
	}
}
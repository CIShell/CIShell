package org.cishell.app.service.filesaver;

import java.io.File;

import org.cishell.framework.data.Data;
import org.cishell.service.conversion.Converter;

/* TODO Push down methods with an obvious implementation in terms of the "atomic" methods
 * (choose converter, choose file, perform save) into an abstract class that implements this.
 * Then FileSaverServiceImpl extends the abstract class.
 */
public interface FileSaverService {
	public static final String ANY_FILE_EXTENSION = "file-ext:*";

	public void registerListener(FileSaveListener listener);
	public void unregisterListener(FileSaveListener listener);

	public Converter promptForConverter(final Data outDatum, String targetMimeType)
			throws FileSaveException;

	public File promptForTargetFile() throws FileSaveException;
	public File promptForTargetFile(String defaultFileExtension) throws FileSaveException;
	public File promptForTargetFile(
			Data datum, String defaultFileExtension) throws FileSaveException;
	public File promptForTargetFile(File outputFile) throws FileSaveException;
	public File promptForTargetFile(
			String suggestedFileName, String defaultFileExtension) throws FileSaveException;

	public File saveData(Data sourceDatum) throws FileSaveException;
	public File saveData(Data sourceDatum, String targetMimeType) throws FileSaveException;
	public File saveData(Data sourceDatum, Converter converter) throws FileSaveException;
	public File saveData(
			Data sourceDatum, Converter converter, File targetFile) throws FileSaveException;

	public File save(File sourceFile) throws FileSaveException;
	public void saveTo(File sourceFile, File targetFile) throws FileSaveException;

	public String suggestFileName(Data datum);
}
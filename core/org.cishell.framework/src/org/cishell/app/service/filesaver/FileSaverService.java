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
	public File promptForTargetFile(Data datum) throws FileSaveException;
	public File promptForTargetFile(File outputFile) throws FileSaveException;
	public File promptForTargetFile(String fileName) throws FileSaveException;

	/* TODO I'm seriously tempted to recommend that all methods beyond this point be called
	 * "save" or "saveToFile" or something, and just have a bit of very concise Javadoc that
	 * explains what may be prompted for?  Alternatively, ask another dev about doing the null
	 * arguments idea.
	 */
	// TODO (NEW): Just Javadoc these really well?
	public File save(Data sourceDatum) throws FileSaveException;
	public File save(File sourceFile) throws FileSaveException;
	public void saveTo(File sourceFile, File targetFile) throws FileSaveException;

	// TODO: What to actually return here? the File object for the one on disk
	/* TODO sourceDatum always first, targetType/File last */
	public Data save(Data sourceDatum, String targetMimeType) throws FileSaveException;
	public Data save(Converter converter, Data sourceDatum) throws FileSaveException;
	public Data save(
			Converter converter, Data sourceDatum, File targetFile) throws FileSaveException;

	public String suggestFileName(Data datum);
}
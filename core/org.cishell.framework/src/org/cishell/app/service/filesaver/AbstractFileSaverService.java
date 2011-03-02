package org.cishell.app.service.filesaver;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;

import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.service.conversion.ConversionException;
import org.cishell.service.conversion.Converter;

public abstract class AbstractFileSaverService implements FileSaverService {
	public static final Collection<Character> INVALID_FILENAME_CHARACTERS =
		Collections.unmodifiableSet(new HashSet<Character>(Arrays.asList(
			new Character('\\'),
			new Character('/'),
			new Character(':'),
			new Character('*'),
			new Character('?'),
			new Character('"'),
			new Character('<'),
			new Character('>'),
			new Character('|'),
			new Character('%'))));
	public static final char FILENAME_CHARACTER_REPLACEMENT = '#';

	private Collection<FileSaveListener> listeners;

	public void registerListener(FileSaveListener listener) {
		this.listeners.add(listener);
	}

	public void unregisterListener(FileSaveListener listener) {
		this.listeners.remove(listener);
	}

	public File promptForTargetFile() throws FileSaveException {
		return promptForTargetFile("");
	}

	public File promptForTargetFile(Data datum) throws FileSaveException {
		Object dataObject = datum.getData();

		if (dataObject instanceof File) {
			return promptForTargetFile((File) datum.getData());
		} else {
			return promptForTargetFile(suggestFileName(datum));
		}
	}

	public File promptForTargetFile(File outputFile) throws FileSaveException {
		return promptForTargetFile(outputFile.getName());
	}

	public File save(Data sourceDatum) throws FileSaveException {
		return save((File) sourceDatum.getData());
	}

	public File save(File sourceFile) throws FileSaveException {
		File targetFile = promptForTargetFile(sourceFile);
		saveTo(sourceFile, targetFile);

		return targetFile;
	}

	public Data save(Data sourceDatum, String targetMimeType)
			throws FileSaveException {
		Converter converter = promptForConverter(sourceDatum, targetMimeType);

		if (converter != null) {
			return save(converter, sourceDatum);
		} else {
			// TODO: CanceledException?
			return null;
		}
	}

	// TODO: What to actually return here? Maybe Pair<Data, File> (LOL)?
	public Data save(Converter converter, Data sourceDatum)
			throws FileSaveException {
		File targetFile = promptForTargetFile(sourceDatum);

		if (targetFile != null) {
			return save(converter, sourceDatum, targetFile);
		} else {
			// TODO: CanceledException?
			return null;
		}
	}

	public Data save(
			Converter converter, Data sourceDatum, File targetFile) throws FileSaveException {
		try {
			Data convertedDatum = converter.convert(sourceDatum);
			saveTo((File) convertedDatum.getData(), targetFile);

			return convertedDatum;
		} catch (ConversionException e) {
			throw new FileSaveException(e.getMessage(), e);
		}
	}

	public String suggestFileName(Data datum) {
		return replaceInvalidFilenameCharacters(getLabel(datum));
	}

	private static String getLabel(Data datum) {
		Dictionary<String, Object> metadata = datum.getMetadata();
		Object labelObject = metadata.get(DataProperty.LABEL);

		if (labelObject != null) {
			return labelObject.toString();
		} else {
			Object shortLabelObject = metadata.get(DataProperty.SHORT_LABEL);

			if (shortLabelObject != null) {
				return shortLabelObject.toString();
			} else {
				return datum.toString();
			}
		}
	}

	private static String replaceInvalidFilenameCharacters(String fileName) {
    	String cleanedFilename = fileName;
    	
    	for (Character invalidCharacter : INVALID_FILENAME_CHARACTERS) {
			cleanedFilename =
				cleanedFilename.replace(invalidCharacter, FILENAME_CHARACTER_REPLACEMENT);
		}
    	
    	return cleanedFilename;
    }
}
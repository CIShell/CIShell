package org.cishell.app.service.filesaver;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;

import org.cishell.framework.algorithm.AlgorithmProperty;
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
	public static final String FILE_EXTENSION_PREFIX = "file-ext:";
	public static final String FILE_PREFIX = "file:";

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

	public File promptForTargetFile(String defaultFileExtension) throws FileSaveException {
		return promptForTargetFile("", defaultFileExtension);
	}

	public File promptForTargetFile(
			Data datum, String defaultFileExtension) throws FileSaveException {
		Object dataObject = datum.getData();

		if (dataObject instanceof File) {
			String fileName = ((File) datum.getData()).getName();
			return promptForTargetFile(fileName, defaultFileExtension);
		} else {
			return promptForTargetFile(suggestFileName(datum), defaultFileExtension);
		}
	}

	public File promptForTargetFile(File outputFile) throws FileSaveException {
		return promptForTargetFile(outputFile.getName(), "");
	}

	public File saveData(Data sourceDatum) throws FileSaveException {
		return saveData(sourceDatum, ANY_FILE_EXTENSION);
	}

	public File saveData(Data sourceDatum, String targetMimeType)
			throws FileSaveException {
		Converter converter = promptForConverter(sourceDatum, targetMimeType);

		if (converter != null) {
			return saveData(sourceDatum, converter);
		} else {
			// TODO: CanceledException?
			return null;
		}
	}

	public File saveData(Data sourceDatum, Converter converter) throws FileSaveException {
		String outputMimeType =
			converter.getProperties().get(AlgorithmProperty.OUT_DATA).toString();
		System.err.println("outputMimeType: " + outputMimeType);
		String suggestedFileExtension = suggestFileExtension(outputMimeType);
		System.err.println("suggestedFileExtension: " + suggestedFileExtension);
		File targetFile = promptForTargetFile(sourceDatum, suggestedFileExtension);

		if (targetFile != null) {
			return saveData(sourceDatum, converter, targetFile);
		} else {
			// TODO: CanceledException?
			return null;
		}
	}

	public File saveData(Data sourceDatum, Converter converter, File targetFile)
			throws FileSaveException {
		try {
			Data convertedDatum = converter.convert(sourceDatum);
			saveTo((File) convertedDatum.getData(), targetFile);

			return targetFile;
		} catch (ConversionException e) {
			throw new FileSaveException(e.getMessage(), e);
		}
	}

	public File save(File sourceFile) throws FileSaveException {
		File targetFile = promptForTargetFile(sourceFile);
		saveTo(sourceFile, targetFile);

		return targetFile;
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

	private static String suggestFileExtension(String targetMimeType) {
		if (targetMimeType.startsWith(FILE_EXTENSION_PREFIX)) {
			return targetMimeType.substring(FILE_EXTENSION_PREFIX.length());
		} else if (targetMimeType.startsWith(FILE_PREFIX)) {
			int forwardSlashCharacterIndex = targetMimeType.indexOf('/');

			if (forwardSlashCharacterIndex != -1) {
				int parsedOutFileExtensionStart = (forwardSlashCharacterIndex + 1);

				if (parsedOutFileExtensionStart < targetMimeType.length()) {
					return targetMimeType.substring(parsedOutFileExtensionStart);
				} else {
					return "";
				}
			} else {
				return "";
			}
		} else {
			return targetMimeType;
		}
	}
}
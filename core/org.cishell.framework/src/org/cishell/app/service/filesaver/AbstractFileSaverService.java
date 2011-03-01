package org.cishell.app.service.filesaver;

import java.io.File;
import java.util.Collection;

import org.cishell.framework.data.Data;
import org.cishell.service.conversion.ConversionException;
import org.cishell.service.conversion.Converter;

public abstract class AbstractFileSaverService implements FileSaverService {
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
		return promptForTargetFile((File) datum.getData());
	}

	public File promptForTargetFile(File outputFile) throws FileSaveException {
		return promptForTargetFile(outputFile.getAbsolutePath()); // TODO getName?
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
}
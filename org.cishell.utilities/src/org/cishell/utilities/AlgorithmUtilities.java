package org.cishell.utilities;

import java.io.File;
import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.ProgressMonitor;
import org.cishell.framework.algorithm.ProgressTrackable;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class AlgorithmUtilities {
	// TODO: ISILoadAndCleanAlgorithmFactory should use this?
	// It's copied directly from it (and cleaned up a little bit)...
	public static AlgorithmFactory getAlgorithmFactoryByFilter(
			String filter, BundleContext bundleContext) throws AlgorithmNotFoundException {
		ServiceReference[] algorithmFactoryReferences;

		try {
			algorithmFactoryReferences = bundleContext.getServiceReferences(
				AlgorithmFactory.class.getName(), filter);
		} catch (InvalidSyntaxException invalidSyntaxException) {
			throw new AlgorithmNotFoundException(invalidSyntaxException);
		}

    	if (algorithmFactoryReferences != null && algorithmFactoryReferences.length != 0) {
    		ServiceReference algorithmFactoryReference = algorithmFactoryReferences[0];
    		AlgorithmFactory algorithmFactory = (AlgorithmFactory)bundleContext.getService(
    			algorithmFactoryReference);

    		return algorithmFactory;
    	}
    	else {
    		throw new AlgorithmNotFoundException("Unable to find an " +
    			"algorithm that satisfied the following filter:\n" + filter);
    	}
	}

	public static AlgorithmFactory getAlgorithmFactoryByPID(
			String pid, BundleContext bundleContext) throws AlgorithmNotFoundException {
		String filter = "(service.pid=" + pid + ")";

		return getAlgorithmFactoryByFilter(filter, bundleContext);
	}
	
	public static Data[] cloneSingletonData(Data[] data) {
		return new Data[] {
			new BasicData(data[0].getMetadata(), data[0].getData(), data[0].getFormat())
		};
	}
	
	/**
	 * Check data's label for something that looks like a file path.
	 * If found, try to extract a filename.
	 * If either bit fails, check same on the parent data, if present.
	 * The first filename-y text found this way is returned.
	 * If we fail all the way to the top parent (that is, data == null), return the empty string.
	 * 
	 * @param data	From whose label and parent labels to attempt filename extractions.
	 * @return	A guess at the filename from data's label or its (transitively) parent labels.
	 * 			null when none can be found.
	 */
	/* TODO: A superior approach might be to define a new "standard" metadata
	 * property (i.e. DataProperty.ORIGINAL_DATASET or something).
	 * When searching for the source data (file)name, that new property would
	 * take precedent, and guessSourceDataFilename would be a default.
	 * To do this properly, the File Load algorithm would need to set this new
	 * property to the filename the user chose.  On the same token, upon
	 * receiving a new data item, the Data Manager algorithm would set this new
	 * property to the data item's label if not set already.
	 */
	@SuppressWarnings("unchecked")	// Raw Dictionary
	public static String guessSourceDataFilename(Data data) {
		if (data == null) {
			return "";
		}

		Dictionary metadata = data.getMetadata();
		String label = (String) metadata.get(DataProperty.LABEL);
		Data parent = (Data) metadata.get(DataProperty.PARENT);

		if (label != null && label.indexOf(File.separator) != -1) {
			/* If fileSeparator is a single backslash,
			 * escape it for the split() regular expression.
			 */
			String escapedFileSeparator = File.separator;

			if ("\\".equals(escapedFileSeparator)) {
				escapedFileSeparator = "\\\\";
			}			

			String[] pathTokens = label.split(escapedFileSeparator);
			String guessedFilename = pathTokens[pathTokens.length - 1];
			int lastExtensionSeparatorIndex = guessedFilename.lastIndexOf(".");

			if (lastExtensionSeparatorIndex != -1) {
				// Part before the extension ("foo" for "foo.bar").
				String guessedNameProper =
					guessedFilename.substring(0, lastExtensionSeparatorIndex);
				// ".bar" for "foo.bar".
				String guessedExtension = guessedFilename.substring(lastExtensionSeparatorIndex);
				String[] extensionTokens = guessedExtension.split("\\s+");				
				
				return guessedNameProper + extensionTokens[0];
			} else {
				return guessSourceDataFilename(parent);
			}
		} else {
			return guessSourceDataFilename(parent);
		}
	}

	@SuppressWarnings("unchecked")	// Dictionary<String, Object>
	public static Data[] executeAlgorithm(
			AlgorithmFactory algorithmFactory,
			ProgressMonitor progressMonitor,
			Data[] data,
			Dictionary parameters,
			CIShellContext ciShellContext)
    		throws AlgorithmExecutionException {
    	Algorithm algorithm = algorithmFactory.createAlgorithm(data, parameters, ciShellContext);

    	if ((progressMonitor != null) && (algorithm instanceof ProgressTrackable)) {
    		ProgressTrackable progressTrackable = (ProgressTrackable)algorithm;
    		progressTrackable.setProgressMonitor(progressMonitor);
    	}

    	Data[] result = algorithm.execute();

    	return result;
    }
}
package org.cishell.reference.app.service.fileloader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;

public class PrettyLabeler {
	public static Data[] relabelWithFileName(Data[] data, File file) {
		File absoluteFile = file.getAbsoluteFile();
		File parent = absoluteFile.getParentFile();

		// TODO parent == null
		
		String prefix;
		String parentName = parent.getName();
		if (parentName.trim().length() == 0) {
			prefix = File.separator;
		} else {
			prefix = "..." + File.separator + parentName + File.separator;
		}

		Collection<Data> newData = new ArrayList<Data>(data.length);

		/* TODO: This isn't actually correct.
		 * It will assign the same label to all of the data items if we ever do this.
		 */
		for (Data datum : data) {
			Dictionary<String, Object> originalDatumMetadata = datum.getMetadata();
			Dictionary<String, Object> labeledDatumMetadata = new Hashtable<String, Object>();

			for (Enumeration<String> keys = originalDatumMetadata.keys();
					keys.hasMoreElements();) {
				String key = keys.nextElement();
				labeledDatumMetadata.put(key, originalDatumMetadata.get(key));
			}

			Data labeledDatum =
				new BasicData(labeledDatumMetadata, datum.getData(), datum.getFormat());
			labeledDatumMetadata.put(DataProperty.LABEL, prefix + absoluteFile.getName());
			newData.add(labeledDatum);
		}

		return newData.toArray(new Data[0]);
	}
	
	/**
	 * Support Hierarchy structure labeling. The algorithm will avoid labeling
	 * on children.
	 * @param data - data that need to relabel
	 * @param file - file that contains filename to be used for relabeling
	 * @return the processed data with new labels
	 */
	public static Data[] relabelWithFileNameHierarchy(Data[] data, File file) {
		File absoluteFile = file.getAbsoluteFile();
		File parent = absoluteFile.getParentFile();

		String prefix;
		String parentName = parent.getName();
		if (parentName.trim().length() == 0) {
			prefix = File.separator;
		} else {
			prefix = "..." + File.separator + parentName + File.separator;
		}

		Collection<Data> possibleParents = new ArrayList<Data>(data.length);

		for (Data datum : data) {
			Dictionary<String, Object> labeledDatumMetadata = datum.getMetadata();
			Object labelObject = labeledDatumMetadata.get(DataProperty.LABEL);

			if ((labelObject == null) || ("".equals(labelObject.toString()))) {
				Data dataParent = getParent(labeledDatumMetadata);

				if (!possibleParents.contains(dataParent)) {
					labeledDatumMetadata.put(DataProperty.LABEL, prefix + absoluteFile.getName());
				}

				possibleParents.add(datum);
			}
		}

		return data;
	}
	
	/*
	 * Get the parent of the data
	 */
	private static Data getParent(Dictionary<String, Object> labeledDatumMetadata) {
		return (Data) labeledDatumMetadata.get(DataProperty.PARENT);
	}
}
package org.cishell.reference.gui.persistence.load;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;

import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.utilities.dictionary.DictionaryUtilities;

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
			Dictionary<String, Object> labeledDatumMetadata =
				DictionaryUtilities.copy(datum.getMetadata());
			Data labeledDatum =
				new BasicData(labeledDatumMetadata, datum.getData(), datum.getFormat());
			labeledDatumMetadata.put(DataProperty.LABEL, prefix + absoluteFile.getName());
			newData.add(labeledDatum);
		}

		return newData.toArray(new Data[0]);
	}
}
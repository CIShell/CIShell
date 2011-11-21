package org.cishell.utilities;

import java.io.File;
import java.util.Dictionary;
import java.util.Hashtable;

import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;

public final class DataFactory {
	private DataFactory() {
		// Static factory methods only.
	}
	
	/**
     * Creates a Data wrapping {@code object}.
     * 
     * @param datum		Object to wrap
     * @param format	The {@link Data#getFormat() format} of {@code object}
     * @param type		The {@link DataProperty#TYPE type} of {@code object}.
	 * 					See {@link DataProperty}.*_TYPE for possible values
     * @param parent	The {@link Data} from which {@code object} was derived
     * @param label		A concise String describing {@code object} in relation to {@code parent}
     */
    public static Data forObject(Object object, String format, String type, Data parent, String label) {
		Dictionary<String, Object> metadata = new Hashtable<String, Object>();
		metadata.put(DataProperty.TYPE, type);
		metadata.put(DataProperty.PARENT, parent);
		metadata.put(DataProperty.LABEL, label);
		
		return new BasicData(metadata, object, format);
	}
    /**
     * Creates a Data wrapping {@code object} having the same {@link Data#getFormat() format} and
     * {@link DataProperty#TYPE type} as its {@code parent}.
     * 
     * @param object	Object to wrap.
     * @param parent	The {@link Data} from which {@code object} was derived.
     * @param label		A concise String describing {@code object} in relation to {@code parent}.
     * @return	A Data wrapping {@code object} using {@code parent}'s {@code format}
     * 			and {@code type}.
     */
	public static Data likeParent(Object object, Data parent, String label) {
		return forObject(
					object,
					parent.getFormat(),
					(String) parent.getMetadata().get(DataProperty.TYPE),
					parent,
					label);
	}
	/**
	 * Creates a Data wrapping {@code object} using {@code object.getClass().getName()} as its
	 * format.
	 * 
	 * @param object	Object to wrap as Data.
	 * @param type		The {@link DataProperty#TYPE type} of {@code object}.
	 * 					See {@link DataProperty}.*_TYPE for possible values.
	 * @param parent	The {@link Data} from which {@code object} was derived.
	 * @param label		A concise String describing {@code object} in relation to {@code parent}.
	 * @return	A Data wrapping {@code object} using {@code object.getClass().getName()} as its
	 * 			format.
	 */
	public static Data withClassNameAsFormat(
			Object object, String type, Data parent, String label) {
		return forObject(object, object.getClass().getName(), type, parent, label);
	}
	/**
	 * Creates a Data wrapping {@code file} using {@code mimeType} as its format.
	 * 
	 * @param file		File to wrap as Data.
	 * @param mimeType	{@code file}'s MIME type, e.g. "file:text/plain".
	 * @param type		The {@link DataProperty#TYPE type} of {@code object}.
	 * 					See {@link DataProperty}.*_TYPE for possible values.
	 * @param parent	The {@link Data} from which {@code object} was derived.
	 * @param label		A concise String describing {@code object} in relation to {@code parent}.
	 * @return	A Data wrapping {@code file} using {@code mimeType} as its format.
	 */
	public static Data forFile(
			File file, String mimeType, String type, Data parent, String label) {
		return forObject(file, mimeType, type, parent, label);
	}
}

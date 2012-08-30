package org.cishell.utilities;

import java.io.File;
import java.util.Dictionary;
import java.util.Hashtable;

import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;

import com.google.common.base.Preconditions;
/**
 * @deprecated see
 *             http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities
 */
@Deprecated
public final class DataFactory {
	private DataFactory() {
		// Static factory methods only.
	}
	
	/**
     * Creates a Data wrapping {@code object}.
     * 
     * @param object	Object to wrap.  Must not be null.
     * @param format	The {@link Data#getFormat() format} of {@code object}.  Must not be null.
     * @param type		The {@link DataProperty#TYPE type} of {@code object}.
	 * 					See {@link DataProperty}.*_TYPE for possible values.  Should not be null.
     * @param parent	The {@link Data} from which {@code object} was derived.  
     * 					May be null (if the object is not derived from another Data).
     * @param label		A concise String describing {@code object} in relation to {@code parent}.
     * 					Should not be null.
     * @throws NullPointerException if {@code object} or {@code format} are null.
     */
    public static Data forObject(Object object, String format, String type, Data parent, String label) {
    	Preconditions.checkNotNull(object, "Object to be wrapped must not be null");
    	Preconditions.checkNotNull(format, "Data format must not be null");
    	
		Dictionary<String, Object> metadata = new Hashtable<String, Object>();
		
		putIfValueNotNull(metadata, DataProperty.TYPE, type);
		putIfValueNotNull(metadata, DataProperty.PARENT, parent);
		putIfValueNotNull(metadata, DataProperty.LABEL, label);
		
		return new BasicData(metadata, object, format);
	}
    
    /**
     * Dictionary implementations aren't required to handle null values.  This utility method
     * puts the value into the dictionary only if it's not null.
     * <p>
     * If the value is null, the Dictionary is not modified.
     * @param dict the dictionary to modify
     * @param property the key to insert
     * @param value the value to insert
     */
    private static <K,V> void putIfValueNotNull(Dictionary<? super K, ? super V> dict,
			K property, V value) {
    	if (value != null) {
    		dict.put(property, value);
    	}
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

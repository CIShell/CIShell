package org.cishell.utilities;

import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;

/**
 * Subclass adding only convenience methods and constructors,
 * chiefly to the Dictionary of properties (metadata).
 * <p/>
 * These changes would be made to {@link Data} and {@link BasicData}
 * rather than tacking on a subclass, but we are eager to keep existing code operable.
 * <p/>
 * Mind the difference between "format" and "type"!
 */
public class BasicDataPlus extends BasicData {
	/**
	 * @param inner		The object wrapped by this Data.
	 * @param format	See {@link org.cishell.framework.data.Data#getFormat()}.
	 */
	public BasicDataPlus(Object inner, String format) {
		super(inner, format);
	}
	
	/**
	 * The inner data's format is assumed to be the toString value of its Class.
	 * 
	 * @param inner		The datum wrapped by this object.
	 */
	public BasicDataPlus(Object inner) {
		this(inner, inner.getClass().toString());
	}
	
	/**
	 * The type of inner is assumed to be the toString value of its Class.
	 * 
	 * @param inner		The object wrapped by this Data.
	 * @param parent	The parent of inner.
	 */
	public BasicDataPlus(Object inner, Data parent) {
		this(inner);
		setParent(parent);
	}
	
	/**
	 * 
	 * @param inner		The object wrapped by this Data.
	 * @param format	The format of inner (like a MIME type, file extension, or class name).
	 * @param parent	The parent of inner.
	 */
	public BasicDataPlus(Object inner, String format, Data parent) {
		this(inner, format);
		setParent(parent);
	}

	/**
	 * @see DataProperty#LABEL
	 */
	public void setLabel(String label) {
		getMetadata().put(DataProperty.LABEL, label);
	}
	
	/**
	 * @see DataProperty#SHORT_LABEL
	 */
	public void setShortLabel(String shortLabel) {
		getMetadata().put(DataProperty.SHORT_LABEL, shortLabel);
	}
	
	/**
	 * @see DataProperty#PARENT
	 */
	public void setParent(Data parent) {
		getMetadata().put(DataProperty.PARENT, parent);
	}
	
	/**
	 * @see DataProperty#TYPE
	 */
	public void setType(String type) {
		getMetadata().put(DataProperty.TYPE, type);
	}
	
	/**
	 * @see DataProperty#MODIFIED
	 */
	public void setModified(boolean modified) {
		getMetadata().put(DataProperty.MODIFIED, new Boolean(modified));
	}
	public void markAsModified() {
		setModified(true);
	}
	public void markAsUnmodified() {
		setModified(false);
	}
}

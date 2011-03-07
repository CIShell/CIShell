package org.cishell.utility.datastructure.datamodel;

/** ModelDataSynchronizer<T> is an interface for synchronizing DataModelFields to their actual
 * GUI widgets. For this reason, most or all implementations of this interface will be
 * GUI-specific.
 */
public interface ModelDataSynchronizer<T> {
	public int updateListenerCode();
	public T value();
	public T synchronizeFromGUI();
	public T synchronizeToGUI(T value);
	public T reset(T defaultValue);
}
package org.cishell.utility.datastructure.datamodel;

/**
 * 
 */
public interface ModelDataSynchronizer<T> {
	public int updateListenerCode();
	public T value();
	public T synchronizeFromGUI();
	public T synchronizeToGUI(T value);
	public T reset(T defaultValue);
}
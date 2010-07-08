package org.cishell.utilities.swt.model.datasynchronizer;

public interface ModelDataSynchronizer<T> {
	public int swtUpdateListenerCode();
	public T value();
	public T synchronizeFromGUI();
	public T synchronizeToGUI(T value);
	public T reset(T defaultValue);
}
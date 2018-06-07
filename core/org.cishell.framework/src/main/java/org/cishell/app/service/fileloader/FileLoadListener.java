package org.cishell.app.service.fileloader;

import java.io.File;

public interface FileLoadListener {
	void fileLoaded(File file);
}
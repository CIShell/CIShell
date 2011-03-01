package org.cishell.app.service.filesaver;

import java.io.File;

public interface FileSaveListener {
	void fileSaved(File file);
}
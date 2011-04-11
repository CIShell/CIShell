package org.cishell.reference.app.service.filesaver;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.cishell.service.guibuilder.GUIBuilderService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class SaveAsController {
	public static final Collection<Character> INVALID_FILENAME_CHARACTERS =
		Collections.unmodifiableCollection(Arrays.asList(
			'\\', '/', ':', '*', '?', '"', '<', '>', '|', '%'));
	public static final char FILENAME_CHARACTER_REPLACEMENT = '#';

    public static final String FILE_EXTENSION_PREFIX = "file-ext:";

	private static File currentDirectory;
    
    private GUIBuilderService guiBuilder;

    public SaveAsController(GUIBuilderService guiBuilder) {
        this.guiBuilder = guiBuilder;
    }

    public File open(String suggestedFileName, String suggestedFileExtension) {
    	String fileExtension = determineFileExtension(suggestedFileName, suggestedFileExtension);
    	Shell parentShell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
    	FileDialog dialog = new FileDialog(parentShell, SWT.SAVE);

        if (currentDirectory == null) {
            currentDirectory =
            	new File(System.getProperty("user.home") + File.separator + "anything");
        }

        dialog.setFilterPath(currentDirectory.getPath());

       	if ((fileExtension != null) && !"*".equals(fileExtension) && !"".equals(fileExtension) &&
       		!suggestedFileName.endsWith(fileExtension)) {
       		
       		String isolatedFileName = stripFileExtension(suggestedFileName);
       		suggestedFileName = String.format("%s.%s", suggestedFileName, suggestedFileExtension);
           	dialog.setFilterExtensions(new String[] { String.format("*.%s", fileExtension) });
       	}
        
        dialog.setText("Choose File");
        String cleanedSuggestedFileName = replaceInvalidFilenameCharacters(suggestedFileName);
//        dialog.setFileName(cleanedSuggestedFileName + "." + fileExtension);
        dialog.setFileName(cleanedSuggestedFileName);
        
        while (true) {        
            String fileName = dialog.open();

            if (fileName != null) {
                File selectedFile = new File(fileName);

                if (!isSaveFileValid(selectedFile)) {
                    continue;
                } else {
                	return selectedFile;
                }
            } else {
                return null;
            }
        }
    }

    private String determineFileExtension(
    		String suggestedFileName, String suggestedFileExtension) {
    	if ((suggestedFileExtension != null) && !"".equals(suggestedFileExtension)) {
    		return suggestedFileExtension;
    	} else {
    		String fileExtension = getFileExtension(suggestedFileName);

    		if (!"".equals(fileExtension)) {
    			return fileExtension;
    		} else {
    			return "";
    		}
    	}
//    	if (!"".equals(fileExtension)) {
//    		return fileExtension;
//    	} else {
//    		if (defaultFileExtension != null) {
//    			return defaultFileExtension;
//    		} else {
//    			return "";
//    		}
//    	}
    }

    private boolean confirmFileOverwrite(File file) {
        String message = "The file:\n" + file.getPath()
            + "\nalready exists. Are you sure you want to overwrite it?";

        return this.guiBuilder.showConfirm("File Overwrite", message, "");
    }

    private boolean isSaveFileValid(File file) {
        if (file.isDirectory()) {
            String message = "Destination cannot be a directory. Please choose a file";
            this.guiBuilder.showError("Invalid Destination", message, "");
            
            return false;
        } else if (file.exists()) {
            return confirmFileOverwrite(file);
        } else {
            return true;
        }
    }

    // TODO: Use cns-utilities when that's done.
    public static String getFileExtension(String filePath) {
    	int periodPosition = filePath.lastIndexOf(".");
    	
    	if ((periodPosition != -1) && ((periodPosition + 1) < filePath.length())) {
    		return filePath.substring(periodPosition + 1);
    	} else {
    		return "";
    	}
    }

    // TODO: Use cns-utilities when that's done.
    private static String replaceInvalidFilenameCharacters(String filename) {
    	String cleanedFilename = filename;

    	for (char invalidCharacter : INVALID_FILENAME_CHARACTERS) {
			cleanedFilename =
				cleanedFilename.replace(invalidCharacter, FILENAME_CHARACTER_REPLACEMENT);
		}
    	
    	return cleanedFilename;
    }

    // TODO: Use cns-utilities when that's done.
    private static String stripFileExtension(String filePath) {
    	int periodPosition = filePath.lastIndexOf(".");

    	if ((periodPosition != -1) && ((periodPosition + 1) < filePath.length())) {
    		return filePath.substring(0, periodPosition);
    	} else {
    		return filePath;
    	}
    }
}
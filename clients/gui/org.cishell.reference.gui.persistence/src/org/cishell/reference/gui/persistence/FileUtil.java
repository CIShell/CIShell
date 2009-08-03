package org.cishell.reference.gui.persistence;

import java.io.File;
import java.io.IOException;

import org.osgi.service.log.LogService;

public class FileUtil {

	private static int uniqueIntForTempFile = 1;
	
    public static File getTempFile(String fileName, String extension, LogService logger){
    	File tempFile;
    
    	if (fileName == null || fileName.equals("")) {
    		fileName = "unknown";
    	}
    	
    	if (extension == null || extension.equals("")) {
    		extension = ".txt";
    	}
    	
    	if (!extension.startsWith(".")) {
    		extension = extension + ".";
    	}
    	
    	String tempPath = System.getProperty("java.io.tmpdir");
    	File tempDir = new File(tempPath+File.separator+"nwb");
    	if(!tempDir.exists())
    		tempDir.mkdir();
    	try{
    		tempFile = File.createTempFile(fileName, extension, tempDir);
		
    	}catch (IOException e1){
    		//failed with given file name and extension. Let's use a standard one.
    		logger.log(LogService.LOG_WARNING, "Failed to create temp file with provided name and extension '" + fileName + extension + "'. Trying a generic name and extension instead.", e1);
    		try {
    			tempFile = File.createTempFile("unknown", ".txt", tempDir);
    		} catch (IOException e2) {
    			//looks like it doesn't even like that. We'll have to just make a file directly.
    			tempFile = new File (tempPath+File.separator+"nwb"+File.separator+"unknown" + uniqueIntForTempFile + ".txt");
        		uniqueIntForTempFile++;
        		
        		logger.log(LogService.LOG_ERROR, "Failed to create temp file twice...");
        		logger.log(LogService.LOG_ERROR, "First Try... \r\n " + e1.toString());
        		logger.log(LogService.LOG_ERROR, "Second Try... \r\n " + e2.toString());
    		}
    	}
    	return tempFile;
    }
    
    public static String extractExtension(String format) {
    	String extension = "";
		/* TODO: We should really have explicit piece of metadata that says what
		 * the extension is, as this method is not guaranteed to yield the
		 * correct extension.
		 */
		if (format.startsWith("file:text/")) {
			extension = "." + format.substring("file:text/".length());
		} else if (format.startsWith("file-ext:")) {
			extension = "." + format.substring("file-ext:".length());
		}
		
		extension = extension.replace('+', '.');
		
		return extension;
    }
    
    public static String extractFileName(String fileLabel) {
    	
    	//index variables will be -1 if index is not found.
    	int descriptionEndIndex = fileLabel.lastIndexOf(":");
    	int filePathEndIndex = fileLabel.lastIndexOf(File.separator);

    	//doesn't matter if either variable is -1, since startIndex will be 
    	//zero and none of the string will be cut off the front.
    	int startIndex = Math.max(descriptionEndIndex, filePathEndIndex) + 1;
    	
    	String fileNameWithExtension = fileLabel.substring(startIndex);
    	
    	
    	//find the first character of the file name extension.
    	int extensionBeginIndex = fileNameWithExtension.lastIndexOf(".");
    	
    	int endIndex;
    	
    	if (extensionBeginIndex != -1) {
    		//we found a period in the file name.
    		endIndex = extensionBeginIndex; //cut off everything after 
    		//first period.
    	} else {
    		//we didn't find an extension on the file name.
    		endIndex = fileNameWithExtension.length(); // don't cut any off the end.
    	}
    	
    	String fileNameWithoutExtension = fileNameWithExtension.substring(0, endIndex);
   	
    	String fileName = fileNameWithoutExtension;
    	return fileName;
    }
}

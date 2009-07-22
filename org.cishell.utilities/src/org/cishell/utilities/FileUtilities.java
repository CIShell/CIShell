package org.cishell.utilities;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FileUtilities {
	// Return a File pointing to the directory specified in temporaryDirectoryPath,
    // creating the directory if it doesn't already exist.
    private static File createTemporaryDirectory(String temporaryDirectoryPath) {
    	return ensureDirectoryExists(temporaryDirectoryPath + File.separator + "temp");
    }
    
    // Attempt to create a temporary file on disk whose name is passed in.
    public static File createTemporaryFile(File temporaryDirectory,
    								 	   String temporaryDirectoryPath,
    								 	   String temporaryFileName,
    								 	   String temporaryFileExtension)
    {
    	ensureDirectoryExists(temporaryDirectoryPath);
    	
    	File temporaryFile;
    	
    	try {
    		temporaryFile = File.createTempFile("NWB-Session-" + temporaryFileName,
    											"." + temporaryFileExtension,
    											temporaryDirectory);
    	}
    	catch (IOException e) {
    		// We couldn't make the temporary file in the temporary directory
	    	// using the standard Java File temporary file scheme (?), so we're
    		// coming up with our own temporary file (that we hope doesn't already
    		// exist).
	    	temporaryFile = new File(temporaryDirectoryPath + File.separator +
	    		temporaryFileName + "temp." +
	    		temporaryFileExtension);
	    	
	    	if (!temporaryFile.exists()) {
	    		try {
	    			temporaryFile.createNewFile();
	    		}
	    		catch (IOException e2) {
	    			throw new RuntimeException(e2);
	    		}
	    		
	    		temporaryFile.deleteOnExit();
	    	}
    	}
    	
    	return temporaryFile;
    }
    
    // Attempt to create a temporary file on disk in a temporary directory (that may
    // also be created, if necessary).
    public static File createTemporaryFileInTemporaryDirectory
    	(String temporaryDirectoryPath,
    	 String temporaryFileName,
     	 String temporaryFileExtension) throws IOException
    {
    	// Get/create the temporary directory.
    	File temporaryDirectory = createTemporaryDirectory(temporaryDirectoryPath);
    	
    	// Attempt to create the temporary file in our temporary directory now.
    	File temporaryFile = createTemporaryFile(temporaryDirectory,
    											 temporaryDirectoryPath,
    											 temporaryFileName,
    											 temporaryFileExtension);
    	
    	// If the creation of the temporary file failed, throw an exception.
    	if (temporaryFile == null) {
    		throw new IOException
    			("Failed to generate a file in the temporary directory.");
    	}
    	
    	return temporaryFile;
    }
    
    public static String getDefaultTemporaryDirectory() {
    	return System.getProperty("java.io.tmpdir");
    }
    
    public static File createTemporaryFileInDefaultTemporaryDirectory
    	(String temporaryFileName,
    	 String temporaryFileExtension) throws IOException
    {
    	return createTemporaryFileInTemporaryDirectory
    		(getDefaultTemporaryDirectory(),
    		 temporaryFileName,
    		 temporaryFileExtension);
    }
    
    public static File writeBufferedImageIntoTemporaryDirectory
    	(BufferedImage bufferedImage,
    	 String imageType) throws IOException, Exception
    {
    	// Get the system-wide temporary directory path.
	    String temporaryDirectoryPath = getDefaultTemporaryDirectory();
	    File temporaryImageFile =
	    	createTemporaryFileInTemporaryDirectory(temporaryDirectoryPath,
	    											"nwb-temp",
	    											imageType);

	    // Attempt to write the image to the temporary file on disk.
   		if (!ImageIO.write(bufferedImage, imageType, temporaryImageFile)) {
   			throw new Exception
   				("No valid image writer was found for the image type " + imageType);
   		}
   		
   		return temporaryImageFile;
    }
    
    public static File writeTextIntoTemporaryDirectory(String text,
    												   String fileExtension)
    	throws IOException, Exception
    {
    	// Get the system-wide temporary directory path.
	    String temporaryDirectoryPath = getDefaultTemporaryDirectory();
	    File temporaryTextFile =
	    	createTemporaryFileInTemporaryDirectory(temporaryDirectoryPath,
	    											"nwb-temp",
	    											fileExtension);

	    FileWriter textFileWriter = new FileWriter(temporaryTextFile);
	    
	    textFileWriter.write(text);
	    textFileWriter.flush();
   		
   		return temporaryTextFile;
    }
    
    public static boolean isFileEmpty(File file)
			throws FileNotFoundException, IOException {
		final BufferedReader reader = new BufferedReader(new FileReader(file));
		String firstLine = reader.readLine();	
		reader.close();
		boolean fileIsEmpty = ( firstLine == null );
		return fileIsEmpty;
	}
    
    private static File ensureDirectoryExists(String directoryPath) {
    	File directory = new File(directoryPath);
    	
    	if (!directory.exists()) {
    		directory.mkdir();
    		directory.deleteOnExit();
    	}
    	
    	return directory;
    }
}
package org.cishell.utilities;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;

public class FileUtilities {
	public static final int READ_TEXT_FILE_BUFFER_SIZE = 1024;
	
	/*
	 * Return a File pointing to the directory specified in
	 *  temporaryDirectoryPath, creating the directory if it doesn't
	 *  already exist.
	 */
    private static File createTemporaryDirectory(
    		String temporaryDirectoryPath) {
    	return ensureDirectoryExists(
    		temporaryDirectoryPath + File.separator + "temp");
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
    		temporaryFile =
    			File.createTempFile("NWB-Session-" + temporaryFileName,
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
    		throw new IOException(
    				"Failed to generate a file in the temporary directory.");
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
    
    /*
     * This is basically copied off of:
     *  http://www.javazoid.com/foj_file.html
     */
    public static String readEntireTextFile(File file)
    		throws IOException {
    	StringBuffer readTextStringBuffer = new StringBuffer();
    	BufferedReader fileReader = new BufferedReader(
    		new FileReader(file));
    	char[] readInCharacters = new char[1];
    	int readCharacterCount = fileReader.read(readInCharacters);
    	
    	while (readCharacterCount > -1) {
    		readTextStringBuffer.append(String.valueOf(readInCharacters));
    		readCharacterCount = fileReader.read(readInCharacters);
    	}
    	
    	fileReader.close();
    	
    	return readTextStringBuffer.toString();
    }
    
    public static void copyFile(File sourceFile, File targetFile)
    		throws FileCopyingException {
		try {
			FileInputStream inputStream = new FileInputStream(sourceFile);
			FileOutputStream outputStream = new FileOutputStream(targetFile);

			FileChannel readableChannel = inputStream.getChannel();
			FileChannel writableChannel = outputStream.getChannel();

			writableChannel.truncate(0);
			writableChannel.transferFrom(
				readableChannel, 0, readableChannel.size());
			inputStream.close();
			outputStream.close();
		} catch (IOException ioException) {
			String exceptionMessage =
				"An error occurred when copying from the file \"" +
				sourceFile.getAbsolutePath() +
				"\" to the file \"" +
				targetFile.getAbsolutePath() +
				"\".";
			
			throw new FileCopyingException(exceptionMessage, ioException);
		}
	}
    
    public static File createTemporaryFileCopy(
    		File sourceFile, String fileName, String fileExtension)
    		throws FileCopyingException {
    	try {
    		File temporaryTargetFile =
    			createTemporaryFileInDefaultTemporaryDirectory(
    				fileName, fileExtension);
    		
    		copyFile(sourceFile, temporaryTargetFile);
    		
    		return temporaryTargetFile;
    	} catch (IOException temporaryFileCreationException) {
    		String exceptionMessage =
    			"An error occurred when trying to create the temporary file " +
    			"with file name \"" + fileName + "\" " +
    			"and file extension \"" + fileExtension + "\" " +
    			"for copying file \"" + sourceFile.getAbsolutePath() + "\".";
    		
    		throw new FileCopyingException(
    			exceptionMessage, temporaryFileCreationException);
    	}
    }
    
    public static File loadFileFromClassPath(Class clazz, String filePath)
    		throws URISyntaxException {
    	URL fileURL = clazz.getResource(filePath);
    	
    	return new File(fileURL.toURI());
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
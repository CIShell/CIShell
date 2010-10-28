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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.osgi.service.log.LogService;

public class FileUtilities {
	public static final int READ_TEXT_FILE_BUFFER_SIZE = 1024;
	public static final String DEFAULT_STREAM_TO_FILE_NAME = "stream_";
	
	/*
	 * Return a File pointing to the directory specified in
	 *  temporaryDirectoryPath, creating the directory if it doesn't
	 *  already exist.
	 */
    public static File createTemporaryDirectory(String temporaryDirectoryPath) {
    	String fullDirectoryPath =
    		String.format("%s%stemp", temporaryDirectoryPath, File.separator);

    	return ensureDirectoryExists(fullDirectoryPath);
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
    			File.createTempFile(temporaryFileName,
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
	    											"image-",
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
	    											"text-",
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

    public static String readEntireTextFile(File file) throws IOException {
    	return extractReaderContents(new BufferedReader(new FileReader(file)));
    }
    
    // stream must be guaranteed to end.
    public static String readEntireInputStream(InputStream stream) throws IOException {
    	return extractReaderContents(new BufferedReader(new InputStreamReader(stream)));
    }

    /*
     * This is basically copied off of:
     *  http://www.javazoid.com/foj_file.html
     */
    public static String extractReaderContents(BufferedReader bufferedReader) throws IOException {
    	StringBuffer contents = new StringBuffer();
    	// TODO: Use READ_TEXT_FILE_BUFFER_SIZE as the size instead of 1?
    	char[] readInCharacters = new char[1];
    	int readCharacterCount = bufferedReader.read(readInCharacters);
    	
    	while (readCharacterCount > -1) {
    		contents.append(String.valueOf(readInCharacters));
    		readCharacterCount = bufferedReader.read(readInCharacters);
    	}
    	
    	bufferedReader.close();
    	
    	return contents.toString();
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
    
    public static File loadFileFromClassPath(Class clazz, String filePath) {
    	URL fileURL = clazz.getResource(filePath);
    	
    	return new File(URI.create(fileURL.toString()));
    }
    
    public static File safeLoadFileFromClasspath(Class clazz, String filePath) throws IOException {
    	InputStream input = clazz.getResourceAsStream(filePath);
    	String fileExtension = getFileExtension(filePath);
    	
    	return writeEntireStreamToTemporaryFile(input, fileExtension);
    }
    
    public static File writeEntireStreamToTemporaryFile(InputStream stream, String fileExtension)
    		throws IOException {
    	return writeEntireStreamToTemporaryFile(
    		stream, DEFAULT_STREAM_TO_FILE_NAME, fileExtension);
    }
    
    public static File writeEntireStreamToTemporaryFile(
    		InputStream input, String fileName, String fileExtension) throws IOException {
    	File temporaryFile =
    		createTemporaryFileInDefaultTemporaryDirectory(fileName, fileExtension);
    	OutputStream output = new FileOutputStream(temporaryFile);
    	// TODO: Use READ_TEXT_FILE_BUFFER_SIZE.
    	byte[] readCharacters = new byte[1];
    	int readCharacterCount = input.read(readCharacters);
    	
    	while (readCharacterCount > 0) {
    		output.write(readCharacters, 0, readCharacterCount);
    		readCharacterCount = input.read(readCharacters);
    	}
    	
    	output.close();
    	input.close();
    	
    	return temporaryFile;
    }

    public static File writeEntireStreamToTemporaryFileInDirectory(
    		InputStream input, File directory, String fileName, String fileExtension)
    		throws IOException {
    	File temporaryFile =
    		createTemporaryFile(directory, directory.getAbsolutePath(), fileName, fileExtension);
    	OutputStream output = new FileOutputStream(temporaryFile);
		// TODO: Use READ_TEXT_FILE_BUFFER_SIZE.
    	byte[] readCharacters = new byte[1];
    	int readCharacterCount = input.read(readCharacters);
    	
    	while (readCharacterCount > 0) {
    		output.write(readCharacters, 0, readCharacterCount);
    		readCharacterCount = input.read(readCharacters);
    	}
    	
    	output.close();
    	input.close();
    	
    	return temporaryFile;
    }
    
    public static String getFileExtension(File file) {
    	return getFileExtension(file.getAbsolutePath());
    }
    
    public static String getFileExtension(String filePath) {
    	int periodPosition = filePath.lastIndexOf(".");
    	
    	if ((periodPosition != -1) && ((periodPosition + 1) < filePath.length())) {
    		return filePath.substring(periodPosition);
    	} else {
    		return "";
    	}
    }
    
    private static File ensureDirectoryExists(String directoryPath) {
    	File directory = new File(directoryPath);
    	
    	if (!directory.exists()) {
    		directory.mkdirs();
    		directory.deleteOnExit();
    	}
    	
    	return directory;
    }
    
	public static final char FILENAME_CHARACTER_REPLACEMENT = '#';
	
	/* Attempt to enumerate characters which cannot be used to name a file.
	 * For our purposes, this should be as aggressive as sensible.
	 * This includes all such characters for modern Windows systems, plus %.
	 * Please add any others.
	 */
	public static final Collection INVALID_FILENAME_CHARACTERS;
	static {
		Collection s = new HashSet();
		s.add(new Character('\\'));
		s.add(new Character('/'));
		s.add(new Character(':'));
		s.add(new Character('*'));
		s.add(new Character('?'));
		s.add(new Character('"'));
		s.add(new Character('<'));
		s.add(new Character('>'));
		s.add(new Character('|'));
		s.add(new Character('%'));
		INVALID_FILENAME_CHARACTERS = Collections.unmodifiableCollection(s);
	}
	
	private static int uniqueIntForTempFile = 1;
	
    public static File getTempFile(String fileName, String extension, LogService logger) {
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

    public static String replaceInvalidFilenameCharacters(String filename) {
    	String cleanedFilename = filename;
    	
    	for (Iterator invalidCharacters = INVALID_FILENAME_CHARACTERS.iterator();
    			invalidCharacters.hasNext();) {
    		char invalidCharacter = ((Character) invalidCharacters.next()).charValue();
			
			cleanedFilename =
				cleanedFilename.replace(invalidCharacter, FILENAME_CHARACTER_REPLACEMENT);
		}
    	
    	return cleanedFilename;
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
    	
    	return fileNameWithoutExtension.trim(); //no spaces on either end
    }

    public static String extractFileNameWithExtension(String fileLabel) {
    	return extractFileName(fileLabel) + getFileExtension(fileLabel);
    }
}
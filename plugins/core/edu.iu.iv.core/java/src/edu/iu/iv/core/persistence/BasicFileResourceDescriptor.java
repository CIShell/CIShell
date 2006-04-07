package edu.iu.iv.core.persistence;

import java.io.File;


/**
 * @author Team IVC
 */
//ndeckard
//Shashikant Penumarthy
public class BasicFileResourceDescriptor implements FileResourceDescriptor {
	
	private static final boolean DEFAULT_FILE_COMPRESSION = false;
	
	private File file ;
	private boolean fileCompression;
	
	/**
	 * Creates a file resource descriptor with no compression
	 * and file set to null.
	 *
	 */
	public BasicFileResourceDescriptor() {
		fileCompression = DEFAULT_FILE_COMPRESSION;
		file = null ;
	}
	
	/**
	 * Creates a file resource descriptor with the specified file and compression.
	 * @param file The file to be represented by this descriptor.
	 * @param fileCompression <code>true</code> if compression is to be enabled, <code>false</code> otherwise.
	 */
	public BasicFileResourceDescriptor(File file, boolean fileCompression) {
		this.file = file ;
	    this.fileCompression = fileCompression;
	}
	
	/**
	 * Creates a file resource descriptor with the specified file and no compression.
	 * @param file The file to be represented by this descriptor.
	 */
	public BasicFileResourceDescriptor(File file) {
		setFile(file) ;
		this.fileCompression = DEFAULT_FILE_COMPRESSION ;
	}
	
	/**
	 * @see edu.iu.iv.core.persistence.FileResourceDescriptor#setFile(java.io.File)
	 */
	public void setFile(File file) {
		this.file = file ;
	}
	
	
	/**
	 * @see edu.iu.iv.core.persistence.FileResourceDescriptor#getFile()
	 */
	public File getFile() {
	    return this.file ;
	}
	
	/**
	 * @see edu.iu.iv.core.persistence.FileResourceDescriptor#getFileName()
	 */
	public String getFileName() {
	    return this.file.getName() ;
	}
	
	/**
	 * @see edu.iu.iv.core.persistence.FileResourceDescriptor#getFileExtension()
	 */
	public String getFileExtension() {
	    String fileName = this.file.getName() ;
	    String extension ;
		if (fileName.lastIndexOf(".") != -1)
		    extension = fileName.substring(fileName.lastIndexOf(".")) ;
		else
		    extension = "" ;
		return extension ;
	}
	
	/**
	 * @see edu.iu.iv.core.persistence.FileResourceDescriptor#isCompressionEnabled()
	 */
	public boolean isCompressionEnabled() {
		return fileCompression;
	}

	/**
	 * @see edu.iu.iv.core.persistence.FileResourceDescriptor#setCompression(boolean)
	 */
	public void setCompression(boolean fileCompression) {
		this.fileCompression = fileCompression;
	}

	/**
	 * @see edu.iu.iv.core.persistence.FileResourceDescriptor#getFilePath()
	 */
	public String getFilePath() {
		return this.file.getPath() ;
	}
}

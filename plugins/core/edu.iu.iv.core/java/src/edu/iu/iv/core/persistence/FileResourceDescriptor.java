package edu.iu.iv.core.persistence;

import java.io.File;

/**
 * A descriptor for a data resource represented as a file on disk. This descriptor holds
 * all information necessary to persist to and restore from a file on disk. Implementations must
 * make sure that all information necessary for persistence using files is contained in this
 * descriptor.
 * 
 * @author Team IVC
 */
//ndeckard
//Shashikant Penumarthy
public interface FileResourceDescriptor extends ResourceDescriptor {
	
	
	/**
	 * Gets the name of the file.
	 * 
	 * @return The name of the file.
	 * if it hasn't been set.
	 * @see java.io.File#getName()
	 */
	public String getFileName();

	/**
	 * Returns the path to the file.
	 * 
	 * @return The file path ending with the separator character or null if it hasn't been set.
	 * @see java.io.File#separator
	 * @see java.io.File#getPath()
	 */
	public String getFilePath();
		
	/**
	 * Sets the properties of this file based on the File object passed in.
	 * 
	 * @param file The file whose properties this resource descriptor should imbibe.
	 */
	public void setFile(File file) ;
	
	/**
	 *  Gets the file represented by this resource descriptor.
	 * 
	 * @return The file represented by this file resource descriptor or null if no file has been set.
	 */
	public File getFile() ;
	
	/**
	 * Gets the extension of the file represented by this resource descriptor. 
	 * The extension of the file must start with a period.
	 * Hence a file with the name, "example.mat" would have the extension ".mat".
	 * 
	 * @return The extension of this file.
	 */
	public String getFileExtension();
	
	/**
	 * Checks to see if the file represented by this resource descriptor is to be
	 * compressed or not. 
	 * 
	 * @return true if the file should be compressed 
	 * after being persisted or decompressed when restored.
	 */
	public boolean isCompressionEnabled();
	
	/**
	 * Sets the compression to be enabled or disabled. Certain file types such as
	 * XML files occupy an obnoxious amount of disk space and implementations
	 * are encouraged to use compression for these file types. Whether a resource
	 * descriptor is to be compressed is or not can be checked using compressionEnabled().
	 * Each persister obviously also needs to check for whether a particular file is compressed
	 * or not and respond accordingly as to whether or not it can persist or restore from the
	 * resource.
	 * 
	 * @param compress true if the file should be compressed 
	 * or decompressed. false if the file shouldn't be compressed.
	 * @see edu.iu.iv.core.persistence.FileResourceDescriptor#compressionEnabled()
	 */
	public void setCompression(boolean compress);
}

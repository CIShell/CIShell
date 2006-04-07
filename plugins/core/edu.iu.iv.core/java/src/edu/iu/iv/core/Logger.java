/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Mar 8, 2005 at Indiana University.
 */
package edu.iu.iv.core;

import java.io.File;


/**
 * Defines operations that the IVC Logger supports.
 *
 * @author Team IVC
 */
public interface Logger {
    
    /**
     * Sets the maximum size that the log file can grow to, in
     * kilobytes. When the limit is reached and new items are to be
     * added, the log will trim the oldest entries until enough
     * space is available.
     *
     * @param maxSize the maximum size that the log file can grow to
     */
    public void setMaxSize(int maxSize);

    /**
     * Returns the maximum allowed size of the log file, in kilobytes.
     *
     * @return the size, in kilobytes, that this Logger's file is
     * allowed to grow to
     */
    public int getMaxSize();

    /**
     * Log the given message with the tag of INFO.
     *
     * @param message the informational message to log
     */
    public void info(String message);

    /**
     * Log the given message with the tag of WARNING.
     *
     * @param message the warning message to log
     */
    public void warning(String message);

    /**
     * Log the given message with the tag of ERROR.
     *
     * @param message the error message to log
     */
    public void error(String message);
    
    /**
     * Return the File used by this Logger
     * 
     * @return the File used by this Logger
     */
    public File getFile();
    
}

/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 29, 2005 at Indiana University.
 */
package edu.iu.iv.core.persistence;

import java.io.File;

import edu.iu.iv.core.persistence.BasicFileResourceDescriptor;

import junit.framework.TestCase;

/**
 *
 * @author Team IVC
 */
public class BasicFileResourceDescriptorTester extends TestCase {
    private BasicFileResourceDescriptor descriptor;
    private String filePath = System.getProperty("user.dir") + File.separator;
    
    protected void setUp(){
        descriptor = new BasicFileResourceDescriptor();
    }

    public void testGetAndSetFile() {
        //should be null at first
        assertNull(descriptor.getFile());
        
        File testFile = new File(filePath + "test.tmp");
        descriptor.setFile(testFile);
        assertEquals(descriptor.getFile(), testFile);
        
        descriptor = new BasicFileResourceDescriptor(testFile);
        assertEquals(descriptor.getFile(), testFile);
        
        testFile.delete();
        
        testFile = new File(filePath + "test2.tmp");
        descriptor.setFile(testFile);
        assertEquals(descriptor.getFile(), testFile);
        
        descriptor = new BasicFileResourceDescriptor(testFile);
        assertEquals(descriptor.getFile(), testFile);
        
        testFile.delete();
    }

    public void testGetFileName() {
        String filename = "test.tmp";
        File testFile = new File(filePath + filename);
        descriptor.setFile(testFile);
        assertEquals(descriptor.getFileName(), filename);               
        testFile.delete();
        
        filename = "test2.tmp";
        testFile = new File(filePath + filename);
        descriptor.setFile(testFile);
        assertEquals(descriptor.getFileName(), filename);               
        testFile.delete();
    }

    public void testGetFileExtension() {
        String filename = "test.tmp";
        File testFile = new File(filePath + filename);
        descriptor.setFile(testFile);
        assertEquals(descriptor.getFileExtension(), ".tmp");               
        testFile.delete();
        
        filename = "test2.ABC";
        testFile = new File(filePath + filename);
        descriptor.setFile(testFile);
        assertEquals(descriptor.getFileExtension(), ".ABC");               
        testFile.delete();
    }

    public void testSetAndIsCompressionEnabled() {
        //false by default
        assertFalse(descriptor.isCompressionEnabled());
        descriptor.setCompression(true);
        assertTrue(descriptor.isCompressionEnabled());
        descriptor.setCompression(false);
        assertFalse(descriptor.isCompressionEnabled());
    }

    public void testGetFilePath() {
        String filename = "test.tmp";
        File testFile = new File(filePath + filename);
        descriptor.setFile(testFile);
        assertEquals(descriptor.getFilePath(), filePath + filename);               
        testFile.delete();
        
        filename = "test2.ABC";
        testFile = new File(filePath + filename);
        descriptor.setFile(testFile);
        assertEquals(descriptor.getFilePath(), filePath + filename);               
        testFile.delete();
    }

}

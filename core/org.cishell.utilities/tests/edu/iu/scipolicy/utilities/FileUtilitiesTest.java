package edu.iu.scipolicy.utilities;

import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.File;

import org.cishell.utilities.FileUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileUtilitiesTest {
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateTemporaryFileWithValidDirectory() {
		String temporaryDirectoryPath = System.getProperty("java.io.tmpdir");
		File temporaryDirectory = new File(temporaryDirectoryPath);
		
		File temporaryFile = FileUtilities.createTemporaryFile
			(temporaryDirectory, temporaryDirectoryPath, "temp_file", "tmp");
		
		if (!temporaryFile.exists())
			fail();
	}
	
	@Test
	public void testCreateTemporaryFileWithInvalidDirectory() {
		String temporaryDirectoryPath = System.getProperty("java.io.tmpdir");
		
		File temporaryFile = FileUtilities.createTemporaryFile
			(null, temporaryDirectoryPath, "temp_file", "tmp");
		
		if (!temporaryFile.exists())
			fail();
	}

	@Test
	public void testCreateTemporaryFileInTemporaryDirectory() {
		try {
			File temporaryFile =
				FileUtilities.createTemporaryFileInTemporaryDirectory
					("temp", "temp_file", "tmp");
			
			if (!temporaryFile.exists())
				fail();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCreateTemporaryFileInDefaultTemporaryDirectory() {
		try {
			File temporaryFile =
				FileUtilities.createTemporaryFileInDefaultTemporaryDirectory
					("temp_file", "tmp");
			
			if (!temporaryFile.exists())
				fail();
		}
		catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testWriteBufferedImageOfValidTypeIntoTemporaryDirectory() {
		BufferedImage image = new BufferedImage(40, 40, BufferedImage.TYPE_INT_RGB);
		
		try {
			File temporaryImageFile =
				FileUtilities.writeBufferedImageIntoTemporaryDirectory
					(image, "jpg");
			
			if (!temporaryImageFile.exists())
				fail();
		}
		catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testWriteBufferedImageOfInvalidTypeIntoTemporaryDirectory() {
		boolean testSucceeded = false;
		
		BufferedImage image = new BufferedImage(40, 40, BufferedImage.TYPE_INT_RGB);
		
		try {
			File temporaryImageFile =
				FileUtilities.writeBufferedImageIntoTemporaryDirectory
					(image, "lolz");
			
			if (!temporaryImageFile.exists())
				testSucceeded = true;
		}
		catch (Exception e) {
			testSucceeded = true;
		}
		
		if (!testSucceeded)
			fail();
	}

	@Test
	public void testWriteTextIntoTemporaryDirectory() {
		String text = "meep meep meep";
		
		try {
			File temporaryTextFile =
				FileUtilities.writeTextIntoTemporaryDirectory(text, "txt");
			
			if (!temporaryTextFile.exists())
				fail();
		}
		catch (Exception e) {
			fail();
		}
	}
}

package org.cishell.testing.convertertester.core.tester2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.log.LogService;

public class TestFileKeeper {

	static {
		String fs = File.separator;
        DEFAULT_ROOT_DIR = 
        	System.getProperty("osgi.install.area").
        	replace("file:","") + "converter_test_files" + fs;
	}
	
	public static final String DEFAULT_ROOT_DIR;

	public static final String CONF_FILE_NAME = "filetypes.conf";

	public static final Pattern LINE_FORMAT = Pattern
			.compile("^file:[^=]*?=[^=]*$");

	private Map formatToTestFileNames = new HashMap();

	private LogService log;

	public TestFileKeeper(String rootDir, LogService log) {
		this.log = log;
		loadTestFileNames(rootDir);
	}

	public void loadTestFileNames() {
		loadTestFileNames(TestFileKeeper.DEFAULT_ROOT_DIR);
	}
	public void loadTestFileNames(String rootDir) {
		
		String line;
		BufferedReader reader = null;

		try {
			//open config file that maps formats to test file directories
			
// Replaced by Bonnie
//			String filePath = System.getProperty("user.home") + File.separator + rootDir
//					+ CONF_FILE_NAME;
			String filePath = rootDir + CONF_FILE_NAME;
			reader = new BufferedReader(new FileReader(filePath));

			//map formats to test files found in specified directories

			//for each file format...
			while ((line = reader.readLine()) != null) {
				Matcher matcher = LINE_FORMAT.matcher(line);

				if (matcher.matches()) {

					//get the file format and directory name from config file.

					String[] splitLine = line.split("=");

					String fileFormat = splitLine[0];
					String dir = splitLine[1];

//	Replaced by Bonnie
//					String testFileDirPath = System.getProperty("user.home")
//							+ "/" + rootDir + dir;
					
					String testFileDirPath =rootDir + dir;
					
					//use directory name to get names of test files inside it.
					String[] testFileNames = 
						getVisibleFileNames(testFileDirPath);
					
					//associate the file format with the names of test files.
					this.formatToTestFileNames.put(fileFormat, testFileNames);
					
				} else {
					this.log.log(LogService.LOG_ERROR, "the line '" + line
							+ "' is not formatted correctly");
				}
			}
		} catch (IOException e) {
			this.log.log(LogService.LOG_ERROR, "Incorrect directory for "
					+ "test file specified", e);
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					this.log.log(LogService.LOG_ERROR, "Cannot close reader.",
							e);
				}
			}
		}
	}

	public String[] getTestFilePaths(String format) {
		
		String[] testFileNames = (String[]) this.formatToTestFileNames
				.get(format);
		if (testFileNames == null) {
			this.log.log(LogService.LOG_ERROR, "Could not load directory "
					+ "for files of format " + format);
			return new String[0];
		}

		return testFileNames;
	}
	
	private String[] getVisibleFileNames(String dirName) {	
		List results = new ArrayList();
		
		File dir = new File(dirName);
		
		File[] dirContents = dir.listFiles();
		for (int ii = 0; ii < dirContents.length; ii++) {
			File fileInDir = dirContents[ii];
			if (! fileInDir.isHidden() && !fileInDir.isDirectory()) {
				try {
					String testFilePath = fileInDir
							.getCanonicalPath();
					results.add(testFilePath);
				} catch (IOException e) {
					this.log.log(LogService.LOG_ERROR,
							"Could not open " + "file "
									+ fileInDir, e);
				}
			}
		}
		
		return (String[]) results.toArray(new String[0]);
	}

}

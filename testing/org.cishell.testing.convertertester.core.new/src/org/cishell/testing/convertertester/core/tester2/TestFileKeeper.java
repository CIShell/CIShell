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

	public static final String DEFAULT_ROOT_DIR = "workspace/org.cishell."
			+ "testing.convertertester.core.new/src/org/cishell/testing/"
			+ "convertertester/core/test_files/";

	public static final String CONF_FILE_NAME = "filetypes.conf";
	
	public static final Pattern LINE_FORMAT = 
		Pattern.compile("^file:[^=]*?=[^=]*$"); 
	
	private Map formatToDir = new HashMap();
	private LogService log;
	
	public TestFileKeeper(String rootDir, LogService log) {
		this.log = log;
		
		String line;
		BufferedReader reader = null;
		try {
			String filePath = System.getProperty("user.home") +  "/" + 
			rootDir + CONF_FILE_NAME;
			reader = new BufferedReader(new FileReader(filePath));
			while ((line = reader.readLine()) != null) {
				
				Matcher matcher = LINE_FORMAT.matcher(line);
				if (matcher.matches()) {
					String[] splitLine = line.split("=");
					
					String fileFormat = splitLine[0];
					String dir        = splitLine[1];
					
					String testFilePath = System.getProperty("user.home") + 
					"/" + rootDir + dir;
					
					formatToDir.put(fileFormat, testFilePath);
				} else {
					this.log.log(LogService.LOG_ERROR, "the line '" + line +
							"' is not formatted correctly");
				}
			}
		} catch (IOException e) {
			this.log.log(LogService.LOG_ERROR, "Incorrect directory for " +
					"test file specified", e);
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
	
	//TODO: maybe make it cache the files instead of getting them every time?
	public String[] getTestFilePaths(String format) {
		
		List results = new ArrayList();

		String testFileDirPath = (String) this.formatToDir.get(format);
		if (testFileDirPath == null) {
			this.log.log(LogService.LOG_ERROR, "Could not load directory " +
					"for files of format " + format);
			return new String[0];
		}
		File testFileDir = new File(testFileDirPath);
		File[] dirContents = testFileDir.listFiles();

		for (int ii = 0; ii < dirContents.length; ii++) {
			if (! dirContents[ii].isHidden()) {
				try {
			String fullTestFilePath = dirContents[ii].getCanonicalPath();
			results.add(fullTestFilePath);
				} catch (IOException e) {
					this.log.log(LogService.LOG_ERROR, "Could not open " +
							"file " + dirContents[ii], e);
				}
			}
		}
		
		return (String[]) results.toArray(new String[0]);
	}

}

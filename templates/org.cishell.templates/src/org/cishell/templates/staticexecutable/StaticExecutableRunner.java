/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 4, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.templates.staticexecutable;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.algorithm.ProgressMonitor;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.templates.Activator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class StaticExecutableRunner implements Algorithm {
	public static final String EXECUTABLE_PLACEHOLDER = "executable";
	public static final String DATA_LABEL_PLACEHOLDER = "data_label";
	public static final String IN_FILE_PLACEHOLDER = "inFile";

	private String ALGORITHM;
	private String macOSX_PPC_DirectoryPath;
	private String MACOSX;
	private String ALGORITHM_WIN32;
	private String WIN32;
	private String ALGORITHM_LINUX_X86;
	private String LINUX;
	private String ALGORITHM_DEFAULT;

	private String algorithmDirectoryPath;
	private String temporaryDirectoryPath;
	private Data[] data;
	private Dictionary<String, Object> parameters;
	private Properties properties;
	private CIShellContext ciShellContext;
	private ProgressMonitor monitor;
	private BundleContext bundleContext;
	private String algorithmName;

	public StaticExecutableRunner(
			BundleContext bundleContext,
			CIShellContext ciShellContext,
			Properties properties,
			Dictionary<String, Object> parameters,
			Data[] data,
			ProgressMonitor monitor,
			String algorithmName) throws IOException {
		// Assign normal member variables.

		this.bundleContext = bundleContext;
		this.ciShellContext = ciShellContext;
		this.properties = properties;
		this.parameters = parameters;
		this.data = data;
		this.monitor = monitor;
		this.algorithmName = algorithmName;

		// Determine directory paths for each platform, based on algorithmName.

		this.ALGORITHM = algorithmName + "/";
		this.macOSX_PPC_DirectoryPath = ALGORITHM + "macosx.ppc/";
		this.MACOSX = "macosx";
		this.ALGORITHM_WIN32 = ALGORITHM + "win32/";
		this.WIN32 = "win32";
		this.ALGORITHM_LINUX_X86 = ALGORITHM + "linux.x86/";
		this.LINUX = "linux";
		this.ALGORITHM_DEFAULT = ALGORITHM + "default/";

		// if a constructor variable was null, use a null object version of it if possible

		if (this.monitor == null) {
			this.monitor = ProgressMonitor.NULL_MONITOR;
		}

		if (this.data == null) {
			this.data = new Data[0];
		}

		if (this.parameters == null) {
			this.parameters = new Hashtable<String, Object>();
		}

		// Make a temporary directory to run the executable in.

		this.temporaryDirectoryPath = makeTemporaryDirectory();
		this.algorithmDirectoryPath = String.format(
			"%s%s%s%s",
			temporaryDirectoryPath,
			File.separator,
			properties.getProperty("Algorithm-Directory"),
			File.separator);
	}

	/**
	 * @see org.cishell.framework.algorithm.Algorithm#execute()
	 */
	public Data[] execute() throws AlgorithmExecutionException {
		copyFilesUsedByExecutableIntoDir(getTempDirectory());
		makeDirExecutable(algorithmDirectoryPath);
		
		String[] commandLineArguments =
			createCommandLineArguments(algorithmDirectoryPath, this.data, this.parameters);
		
		File[] rawOutput = executeProgram(commandLineArguments, algorithmDirectoryPath);

		return formatAsData(rawOutput);
	}

	private void copyFilesUsedByExecutableIntoDir(File dir) throws AlgorithmExecutionException {
		try {
			Enumeration e = bundleContext.getBundle().getEntryPaths("/" + algorithmName);

			Set entries = new HashSet();

			while (e != null && e.hasMoreElements()) {
				String entryPath = (String) e.nextElement();
				// logger.log(LogService.LOG_DEBUG, "entry: " + entryPath + "\n\n");
				if (entryPath.endsWith("/")) {
					entries.add(entryPath);
				}
			}

			dir = new File(dir.getPath() + File.separator + algorithmName);
			dir.mkdirs();

			String os = bundleContext.getProperty("osgi.os");
			String arch = bundleContext.getProperty("osgi.arch");

			String path = null;

			// take the default, if there
			if (entries.contains(ALGORITHM_DEFAULT)) {
				String default_path = ALGORITHM_DEFAULT;
				// logger.log(LogService.LOG_DEBUG, "base path: "+default_path+
				// "\n\t"+dir.getAbsolutePath() + "\n\n");
				copyDir(dir, default_path, 0);
			}

			// but override with platform idiosyncracies
			if (os.equals(WIN32) && entries.contains(ALGORITHM_WIN32)) {
				path = ALGORITHM_WIN32;
			} else if (os.equals(MACOSX) && entries.contains(macOSX_PPC_DirectoryPath)) {
				path = macOSX_PPC_DirectoryPath;
			} else if (os.equals(LINUX) && entries.contains(ALGORITHM_LINUX_X86)) {
				path = ALGORITHM_LINUX_X86;
			}

			String platform_path = ALGORITHM + os + "." + arch + "/";
			// and always override anything with an exact match
			if (entries.contains(platform_path)) {
				path = platform_path;
			}

			if (path == null) {
				throw new AlgorithmExecutionException("Unable to find compatible executable");
			} else {
				// logger.log(LogService.LOG_DEBUG, "base path: "+path+
				// "\n\t"+dir.getAbsolutePath() + "\n\n");
				copyDir(dir, path, 0);
			}
		} catch (IOException e) {
			throw new AlgorithmExecutionException(e.getMessage(), e);
		}
	}

	protected void makeDirExecutable(String baseDir) throws AlgorithmExecutionException {
		// FIXME: Surely java has a way to do this!!!!
		if (new File("/bin/chmod").exists()) {
			try {
				String executable = baseDir + properties.getProperty("executable");
				Runtime.getRuntime().exec("/bin/chmod +x " + executable).waitFor();
			} catch (IOException e) {
				throw new AlgorithmExecutionException(e);
			} catch (InterruptedException e) {
				throw new AlgorithmExecutionException(e);
			}
		}
	}

	protected File[] executeProgram(String[] commandArray, String baseDirPath) throws AlgorithmExecutionException {
		/*
		 * Remember which files were in the directory before we ran
		 *  the program.
		 */
		File baseDir = new File(baseDirPath);
		String[] beforeFiles = baseDir.list();

		//create and run the executing process
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(commandArray, null, new File(baseDirPath));
			process.getOutputStream().close();
		} catch (IOException e1) {
			throw new AlgorithmExecutionException(e1.getMessage(), e1);
		}

		//monitor the process, printing its stdout and stderr to console
		
		monitor.start(ProgressMonitor.CANCELLABLE, -1);

		InputStream in = process.getInputStream();
		StringBuffer in_buffer = new StringBuffer();

		InputStream err = process.getErrorStream();
		StringBuffer err_buffer = new StringBuffer();

		Integer exitValue = null;
		boolean killedOnPurpose = false;
		//while the process is still running...
		while (!killedOnPurpose && exitValue == null) {
			//print its output, and watch to see if it has finished/died.
			
			in_buffer = logStream(LogService.LOG_INFO, in, in_buffer);
			err_buffer = logStream(LogService.LOG_ERROR, err, err_buffer);

			if (monitor.isCanceled()) {
				killedOnPurpose = true;
				process.destroy();
			}

			try {
				int value = process.exitValue();
				exitValue = new Integer(value);
			} catch (IllegalThreadStateException e) {
				// thrown if the process isn't done.
				// kinda nasty, but there looks to be no other option.
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// possibly normal operation
			}
		}

		monitor.done();

		// if the process failed unexpectedly...
		if (process.exitValue() != 0 && !killedOnPurpose) {
			throw new AlgorithmExecutionException("Algorithm exited unexpectedly (exit value: " + process.exitValue()
					+ "). Please check the console window for any error messages.");
		}

		//look at the files in the directory now, and compare to before we ran the program.
		//any file that is in the directory now, but wasn't before, is an output file.
		//return all the output files.
		
		String[] afterFiles = baseDir.list();

		Arrays.sort(beforeFiles);
		Arrays.sort(afterFiles);

		List outputs = new ArrayList();

		int beforeIndex = 0;
		int afterIndex = 0;

		while (beforeIndex < beforeFiles.length && afterIndex < afterFiles.length) {
			if (beforeFiles[beforeIndex].equals(afterFiles[afterIndex])) {
				beforeIndex++;
				afterIndex++;
			} else {
				outputs.add(new File(baseDirPath + afterFiles[afterIndex]));
				afterIndex++;
			}
		}

		// get any remaining new files
		while (afterIndex < afterFiles.length) {
			outputs.add(new File(baseDirPath + afterFiles[afterIndex]));
			afterIndex++;
		}

		return (File[]) outputs.toArray(new File[] {});
	}

	protected Data[] formatAsData(File[] files) {
		String outData = (String) properties.get(AlgorithmProperty.OUT_DATA);

		// if out data is null then it returns no data
		if (("" + outData).trim().equalsIgnoreCase(AlgorithmProperty.NULL_DATA)) {
			return null;
		}

		String[] formats = outData.split(",");

		Map nameToFileMap = new HashMap();
		for (int i = 0; i < files.length; i++) {
			nameToFileMap.put(files[i].getName(), files[i]);
		}

		Data[] data = null;
		if (formats.length > files.length) {
			data = new Data[formats.length];
		} else {
			data = new Data[files.length];
		}

		for (int i = 0; i < data.length; i++) {
			String file = properties.getProperty("outFile[" + i + "]", null);

			if (i < formats.length) {
				File f = (File) nameToFileMap.remove(file);

				if (f != null) {
					data[i] = new BasicData(f, formats[i]);

					String label = properties.getProperty("outFile[" + i + "].label", f.getName());
					data[i].getMetadata().put(DataProperty.LABEL, label);

					String type = properties.getProperty("outFile[" + i + "].type", DataProperty.OTHER_TYPE);
					type = type.trim();
					if (type.equalsIgnoreCase(DataProperty.MATRIX_TYPE)) {
						type = DataProperty.MATRIX_TYPE;
					} else if (type.equalsIgnoreCase(DataProperty.NETWORK_TYPE)) {
						type = DataProperty.NETWORK_TYPE;
					} else if (type.equalsIgnoreCase(DataProperty.TREE_TYPE)) {
						type = DataProperty.TREE_TYPE;
					} else if (type.equalsIgnoreCase(DataProperty.TEXT_TYPE)) {
						type = DataProperty.TEXT_TYPE;
					} else if (type.equalsIgnoreCase(DataProperty.PLOT_TYPE)) {
						type = DataProperty.PLOT_TYPE;
					} else if (type.equalsIgnoreCase(DataProperty.TABLE_TYPE)) {
						type = DataProperty.TABLE_TYPE;
					} else {
						type = DataProperty.OTHER_TYPE;
					}

					data[i].getMetadata().put(DataProperty.TYPE, type);
				}
			} else {
				Iterator iter = nameToFileMap.values().iterator();
				while (iter.hasNext()) {
					File f = (File) iter.next();

					data[i] = new BasicData(f, "file:text/plain");
					data[i].getMetadata().put(DataProperty.LABEL, f.getName());

					i++;
				}
				break;
			}
		}

		return data;
	}

	protected StringBuffer logStream(int logLevel, InputStream is, StringBuffer buffer)
			throws AlgorithmExecutionException {
		try {
			int available = is.available();
			if (available > 0) {
				byte[] b = new byte[available];
				is.read(b);
				buffer.append(new String(b));

				buffer = log(logLevel, buffer);
			}
		} catch (EOFException e) {
			// normal operation
		} catch (IOException e) {
			throw new AlgorithmExecutionException("Error when processing the algorithm's screen output", e);
		}

		return buffer;
	}

	protected StringBuffer log(int logLevel, StringBuffer buffer) {
		if (buffer.indexOf("\n") != -1) { // any new newlines to output?
			LogService log = (LogService) ciShellContext.getService(LogService.class.getName());

			int lastGoodIndex = 0;
			int fromIndex = 0;
			// print out each new line
			while (fromIndex != -1 && fromIndex < buffer.length()) {
				int toIndex = buffer.indexOf("\n", fromIndex);

				if (toIndex != -1) {
					String message = buffer.substring(fromIndex, toIndex);

					if (log == null) {
						// This will probably never come up, but if it does
						// we'll still get some output.
						System.out.println(message);
					} else {
						log.log(logLevel, message);
					}
					fromIndex = toIndex + 1;
					lastGoodIndex = toIndex + 1;
				} else {
					fromIndex = -1;
				}
			}
			// save the last part of the string that doesn't end in a newline
			if (lastGoodIndex > 0) {
				buffer = new StringBuffer(buffer.substring(lastGoodIndex));
			}
		}

		return buffer;
	}

	protected String[] createCommandLineArguments(
			String algorithmDirectory, Data[] data, Dictionary<String, Object> parameters) {
		String template = "" + this.properties.getProperty("template");
		String[] commands = template.split("\\s");

		for (int ii = 0; ii < commands.length; ii++) {
			commands[ii] = substituteVars(commands[ii], data, parameters);
		}

		// TODO: Expanded later to support .cmd and other extensions
		if (!new File(algorithmDirectory + commands[0]).exists()) {
			if (new File(algorithmDirectory + commands[0] + ".bat").exists()) {
				commands[0] = commands[0] + ".bat";
			}
		}

		commands[0] = algorithmDirectory + commands[0];

		return commands;
	}

	// replaces place-holder variables in the template with the actual arguments the executable needs to work.
	// (real names of files instead of inFile[i], for instance)
	// (also, real values like "6" or "dog" instead of placeholders for parameters)
	protected String substituteVars(String template, Data[] data, Dictionary parameters) {
		template = template.replaceAll(
			"\\$\\{" + EXECUTABLE_PLACEHOLDER + "\\}", properties.getProperty(EXECUTABLE_PLACEHOLDER));

		/* 
		 * Re-think:
	     * Shall we just name the same in config.propertities?
	     * Why not use parameter key for all inFile and data?
	     */
		for (int ii = 0; ii < data.length; ii++) {
			template = substituteForDataLabel(template, data, ii);			
			template = substituteForFilePath(template, data, ii);
		}

		for (Enumeration i = parameters.keys(); i.hasMoreElements();) {
			String key = (String) i.nextElement();
			Object value = parameters.get(key);

			if (value == null) value = "";

			template = template.replaceAll("\\$\\{" + key + "\\}", value.toString());
		}
		
		return template;
	}
	
	private String substituteForDataLabel(String template, Data[] data, int ii) {
		String key = String.format("${%s[%d]}", DATA_LABEL_PLACEHOLDER, ii);
		
		if (!template.contains(key)) {
			return template;
		} else {
			Object labelObject = data[ii].getMetadata().get(DataProperty.LABEL);
			
			String label = "unknown_data_label";
			if (labelObject != null) {
				label = labelObject.toString();
			}
			
			/* TODO Re-think converting backslash to slash for the following expects:
			 * i) Need to consider what is the system file separator
			 * ii) The backslash in data_label might have special meaning. 
			 * iii) may be we should use " to quote the data_label  
			 */
			String escapedLabel = label.replaceAll("\\\\", "/");
			
			return template.replaceAll(Pattern.quote(key), escapedLabel);
		}
	}

	private String substituteForFilePath(String template, Data[] data, int ii) {
		String key = String.format("${%s[%d]}", IN_FILE_PLACEHOLDER, ii);
		
		if (!template.contains(key)) {
			return template;
		} else {			
			Object datumObject = data[ii].getData();
			
			String filePath = "unknown_file_path";
			if (datumObject != null && datumObject instanceof File) {
				File file = (File) datumObject;
				filePath = file.getAbsolutePath();
			}
			
			if (File.separatorChar == '\\') {
				filePath = filePath.replace(File.separatorChar, '/');
			}

			String substituted = template.replaceAll(Pattern.quote(key), filePath);

			/* TODO: Re-think - This auto convert every backslash and slash.
			 * Which might convert everything that you don't intend to convert.
			 */
			if (File.separatorChar == '\\') {
				substituted = substituted.replace('/', File.separatorChar);
			}
			
			return substituted;
		}
	}

	public File getTempDirectory() {
		return new File(temporaryDirectoryPath);
	}

	protected String makeTemporaryDirectory() throws IOException {
		File sessionDir = Activator.getTempDirectory();
		File dir = File.createTempFile("StaticExecutableRunner-", "", sessionDir);

		dir.delete();
		dir.mkdirs();

		return dir.getAbsolutePath();
	}

	private void copyDir(File dir, String dirPath, int depth) throws IOException {
		Enumeration e = bundleContext.getBundle().getEntryPaths(dirPath);

		// dirPath = dirPath.replace('/', File.separatorChar);

		while (e != null && e.hasMoreElements()) {
			String path = (String) e.nextElement();

			if (path.endsWith("/")) {
				String dirName = getName(path);

				File subDirectory = new File(dir.getPath() + File.separator + dirName);
				subDirectory.mkdirs();
				// logger.log(LogService.LOG_DEBUG, "path: " + depth + " "+path+
				// "\n\t"+subDirectory.getAbsolutePath() + "\n\n");
				copyDir(subDirectory, path, depth + 1);
			} else {
				copyFile(dir, path);
			}
		}
	}

	private void copyFile(File dir, String path) throws IOException {
		URL entry = bundleContext.getBundle().getEntry(path);

		// path = path.replace('/', File.separatorChar);
		String file = getName(path);
		FileOutputStream outStream = new FileOutputStream(dir.getPath() + File.separator + file);

		ReadableByteChannel in = Channels.newChannel(entry.openStream());
		FileChannel out = outStream.getChannel();
		out.transferFrom(in, 0, Integer.MAX_VALUE);

		in.close();
		out.close();
	}

	private String getName(String path) {
		if (path.lastIndexOf('/') == path.length() - 1) {
			path = path.substring(0, path.length() - 1);
		}

		path = path.substring(path.lastIndexOf('/') + 1, path.length());

		return path;
	}
}

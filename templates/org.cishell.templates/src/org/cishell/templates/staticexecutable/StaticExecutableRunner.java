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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
	protected final String tempDir;
	protected final Data[] data;
	protected Dictionary parameters;
	protected Properties props;
	protected CIShellContext ciContext;
	protected ProgressMonitor monitor;

	public StaticExecutableRunner(BundleContext bContext,
			CIShellContext ciContext, Properties props, Dictionary parameters,
			Data[] data, ProgressMonitor monitor) throws IOException {
		this.ciContext = ciContext;
		this.props = props;
		this.parameters = parameters;
		this.data = data;
		this.monitor = monitor;
		if (monitor == null)
			this.monitor = ProgressMonitor.NULL_MONITOR;
		if (data == null)
			data = new Data[0];
		if (parameters == null)
			parameters = new Hashtable();

		tempDir = makeTempDirectory();
	}

	/**
	 * @see org.cishell.framework.algorithm.Algorithm#execute()
	 */
	public Data[] execute() throws AlgorithmExecutionException {
		String algDir = tempDir + File.separator
				+ props.getProperty("Algorithm-Directory") + File.separator;

		chmod(algDir);
		File[] output = execute(getTemplate(algDir), algDir);

		return toData(output);
	}

	protected Data[] toData(File[] files) {
		String outData = (String) props.get(AlgorithmProperty.OUT_DATA);

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
			String file = props.getProperty("outFile[" + i + "]", null);

			if (i < formats.length) {
				File f = (File) nameToFileMap.remove(file);

				if (f != null) {
					data[i] = new BasicData(f, formats[i]);

					String label = props.getProperty(
							"outFile[" + i + "].label", f.getName());
					data[i].getMetadata().put(DataProperty.LABEL, label);

					String type = props.getProperty("outFile[" + i + "].type",
							DataProperty.OTHER_TYPE);
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
					}else {
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

	protected void chmod(String baseDir) throws AlgorithmExecutionException {
		// FIXME: Surely java has a way to do this!!!!
		if (new File("/bin/chmod").exists()) {
			try {
				String executable = baseDir + props.getProperty("executable");
				Runtime.getRuntime().exec("/bin/chmod +x " + executable)
						.waitFor();
			} catch (IOException e) {
				throw new AlgorithmExecutionException(e);
			} catch (InterruptedException e) {
				throw new AlgorithmExecutionException(e);
			}
		}
	}

	protected File[] execute(String[] cmdarray, String baseDir) 
		throws AlgorithmExecutionException {
		File dir = new File(baseDir);
		String[] beforeFiles = dir.list();

		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmdarray, null,
					new File(baseDir));
			process.getOutputStream().close();
		} catch (IOException e1) {
			throw new AlgorithmExecutionException(e1.getMessage(),e1);
		}
		
		monitor.start(ProgressMonitor.CANCELLABLE, -1);
		
		InputStream in = process.getInputStream();
		StringBuffer in_buffer = new StringBuffer();
		
		InputStream err = process.getErrorStream();
		StringBuffer err_buffer = new StringBuffer();

		Integer exitValue = null;
		boolean killedOnPurpose = false;
		while (!killedOnPurpose && exitValue == null) {
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
			throw new AlgorithmExecutionException(
				"Algorithm exited unexpectedly (exit value: "+process.exitValue()+
				"). Please check the console window for any error messages.");
		}

		// get the files output from the process
		String[] afterFiles = dir.list();

		Arrays.sort(beforeFiles);
		Arrays.sort(afterFiles);

		List outputs = new ArrayList();

		int beforeIndex = 0;
		int afterIndex = 0;

		while (beforeIndex < beforeFiles.length
				&& afterIndex < afterFiles.length) {
			if (beforeFiles[beforeIndex].equals(afterFiles[afterIndex])) {
				beforeIndex++;
				afterIndex++;
			} else {
				outputs.add(new File(baseDir + afterFiles[afterIndex]));
				afterIndex++;
			}
		}

		// get any remaining new files
		while (afterIndex < afterFiles.length) {
			outputs.add(new File(baseDir + afterFiles[afterIndex]));
			afterIndex++;
		}

		return (File[]) outputs.toArray(new File[] {});
	}

	protected StringBuffer logStream(int logLevel, InputStream is,
			StringBuffer buffer) throws AlgorithmExecutionException {
		try {
			int available = is.available();
			if (available > 0) {
				byte[] b = new byte[available];
				is.read(b);
				buffer.append(new String(b));
				
				buffer = log(logLevel, buffer);
			}
		} catch (EOFException e) {
			//normal operation
		} catch (IOException e) {
			throw new AlgorithmExecutionException("Error when processing the algorithm's screen output",e);
		}

		return buffer;
	}

	protected StringBuffer log(int logLevel, StringBuffer buffer) {
		if (buffer.indexOf("\n") != -1) { // any new newlines to output?
			LogService log = (LogService) ciContext.getService(LogService.class
					.getName());

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
					fromIndex = toIndex+1;
					lastGoodIndex = toIndex+1;
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

	protected String[] getTemplate(String algDir) {
		String template = "" + props.getProperty("template");
		String[] cmdarray = template.split("\\s");

		for (int i = 0; i < cmdarray.length; i++) {
			cmdarray[i] = substiteVars(cmdarray[i]);
		}

		// TODO: Expanded later to support .cmd and other extensions
		if (!new File(algDir + cmdarray[0]).exists()) {
			if (new File(algDir + cmdarray[0] + ".bat").exists()) {
				cmdarray[0] = cmdarray[0] + ".bat";
			}
		}
		cmdarray[0] = algDir + cmdarray[0];

		return cmdarray;
	}

	protected String substiteVars(String str) {
		str = str.replaceAll("\\$\\{executable\\}", props
				.getProperty("executable"));

		for (int i = 0; i < data.length; i++) {
			String file = ((File) data[i].getData()).getAbsolutePath();

			if (File.separatorChar == '\\') {
				file = file.replace(File.separatorChar, '/');
			}

			str = str.replaceAll("\\$\\{inFile\\[" + i + "\\]\\}", file);

			if (File.separatorChar == '\\') {
				str = str.replace('/', File.separatorChar);
			}
		}

		for (Enumeration i = parameters.keys(); i.hasMoreElements();) {
			String key = (String) i.nextElement();
			Object value = parameters.get(key);

			if (value == null)
				value = "";

			str = str.replaceAll("\\$\\{" + key + "\\}", value.toString());
		}

		return str;
	}

	public File getTempDirectory() {
		return new File(tempDir);
	}

	protected String makeTempDirectory() throws IOException {
		File sessionDir = Activator.getTempDirectory();
		File dir = File.createTempFile("StaticExecutableRunner-", "",
				sessionDir);

		dir.delete();
		dir.mkdirs();

		return dir.getAbsolutePath();
	}
}

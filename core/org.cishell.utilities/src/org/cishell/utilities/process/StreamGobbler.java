package org.cishell.utilities.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.google.common.base.Objects;

/**
 * Running this thread empties {@link #inputStream} into {@link #outputStream}.
 * <p/>
 * Adapted from
 * <a href="http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html?page=4">this guide</a>.
/**
 * @deprecated see
 *             http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities
 */
@Deprecated
public class StreamGobbler extends Thread {
	private InputStream inputStream;
	private OutputStream outputStream;

	/**
	 * @param inputStream	Stream for reading.
	 * @param outputStream	Stream for writing.
	 */
	public StreamGobbler(InputStream inputStream, OutputStream outputStream) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}

	/**
	 * Empties this gobbler's {@link InputStream} into its {@link OutputStream}.
	 */
	@Override
	public void run() {
		try {
			PrintWriter printWriter = new PrintWriter(outputStream);

			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				printWriter.println(line);
			}

			printWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problem reading messages from process.", e);
		}
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("inputStream", inputStream)
				.add("outputStream", outputStream)
				.toString();
	}
}
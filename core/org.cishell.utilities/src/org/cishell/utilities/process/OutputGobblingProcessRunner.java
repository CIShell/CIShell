package org.cishell.utilities.process;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.google.common.base.Objects;

/**
 * {@link Process#waitFor()} may wait indefinitely if the running process's stdout and stderr
 * streams are not emptied as they fill.
 * 
 * See
 * <a href="http://stackoverflow.com/questions/2150723/process-waitfor-threads-and-inputstreams">here</a>
 * or
 * <a href="http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html">here</a> for details.
/**
 * @deprecated see
 *            {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities}
 */
@Deprecated
public class OutputGobblingProcessRunner {	
	private ProcessBuilder processBuilder;
	private String charsetName;

	public OutputGobblingProcessRunner(ProcessBuilder processBuilder, String charsetName) {
		this.processBuilder = processBuilder;
		this.charsetName = charsetName;
	}

	public ProcessReport run() throws IOException, InterruptedException {
		// Start the process
		Process process = processBuilder.start();
		
		// Set up and start the stdout and stderr gobblers
		ByteArrayOutputStream stdoutStream = new ByteArrayOutputStream();
		StreamGobbler stdoutGobbler = new StreamGobbler(process.getInputStream(), stdoutStream);
		
		ByteArrayOutputStream stderrStream = new ByteArrayOutputStream();
		StreamGobbler stderrGobbler = new StreamGobbler(process.getErrorStream(), stderrStream);
		
		stdoutGobbler.start();
		stderrGobbler.start();
		
		// Wait for the process to finish
		int exitValue = process.waitFor();

		// Interrupt the gobblers and wait for both to die
		stdoutGobbler.interrupt();
		stderrGobbler.interrupt();
		stdoutGobbler.join();
		stderrGobbler.join();
		
		// Dump gobbler messages
		String stdoutMessage = stdoutStream.toString(charsetName).trim();
		String stderrMessage = stderrStream.toString(charsetName).trim();
		
		return ProcessReport.of(processBuilder.command(), exitValue, stdoutMessage, stderrMessage);
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("processBuilder", processBuilder)
				.add("charsetName", charsetName)
				.toString();
	}
}

package org.cishell.utilities.process;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

/**
 * Value class for the result of {@link OutputGobblingProcessRunner#run()}.
 * Represents a process's commands, exit value, and messages to standard out and standard errors.
/**
 * @deprecated see
 *             http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities
 */
@Deprecated
public class ProcessReport {
	private final ImmutableList<String> commands;
	private final int exitValue;
	private final String stdoutMessage;
	private final String stderrMessage;

	/**
	 * @param commands 		Commands used to launch the corresponding {@link Process}.
	 * @param exitValue		A completed {@link Process}'s exit value.
	 * @param stdoutMessage	A completed {@link Process}'s messages to standard output as one String.
	 * @param stderrMessage	A completed {@link Process}'s messages to standard error as one String.
	 */
	public static ProcessReport of(
			List<String> commands, int exitValue, String stdoutMessage, String stderrMessage) {
		return new ProcessReport(commands, exitValue, stdoutMessage, stderrMessage);
	}
	private ProcessReport(
			List<String> commands, int exitValue, String stdoutMessage, String stderrMessage) {
		this.commands = ImmutableList.copyOf(commands);
		this.exitValue = exitValue;
		this.stdoutMessage = stdoutMessage;
		this.stderrMessage = stderrMessage;
	}
	
	
	public ImmutableList<String> getCommands() {
		return commands;
	}

	public int getExitValue() {
		return exitValue;
	}

	/**
	 * @return	The message to standard output.
	 */
	public String getStdoutMessage() {
		return stdoutMessage;
	}

	/**
	 * @return	The message to standard error.
	 */
	public String getStderrMessage() {
		return stderrMessage;
	}
	
	/**
	 * @return	True if and only if the exit value is zero.
	 */
	public boolean isExitNormal() {
		return (exitValue == 0);
	}
	
	@Override
	public boolean equals(Object thatObject) {
		if (!(thatObject instanceof ProcessReport)) {
			return false;
		}
		
		ProcessReport that = (ProcessReport) thatObject;
		
		return (Objects.equal(this.commands,
				  			  that.commands)
			 && Objects.equal(this.exitValue,
							  that.exitValue)
			 && Objects.equal(this.stdoutMessage,
					 		  that.stdoutMessage)
			 && Objects.equal(this.stderrMessage,
					 		  that.stderrMessage));
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(commands, exitValue, stdoutMessage, stderrMessage);
	}

	/**
	 * @return	A human-readable report of all values.
	 */
	@Override
	public String toString() {
		return Joiner.on("  ").join(
				String.format("The commands %s resulted in exit value %d.", commands, exitValue),
				reportStreamContents("standard output", stdoutMessage),
				reportStreamContents("standard error", stderrMessage));
	}
	
	private static String reportStreamContents(String streamName, String contents) {
		if (contents.length() == 0) {
			return String.format("No messages to %s.", streamName);
		} else {
			return String.format("Message to %s: [[%s]].", streamName, contents);
		}
	}
}
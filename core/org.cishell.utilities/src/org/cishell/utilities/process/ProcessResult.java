package org.cishell.utilities.process;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;

/**
 * Value class for the result of {@link OutputGobblingProcessRunner#run()}.
 * Represents a process's exit value and messages to standard out and standard error,
 * both as a single String.
 */
public class ProcessResult {
	private int exitValue;
	private String stdoutMessage;
	private String stderrMessage;

	/**
	 * @param exitValue		A completed {@link Process}'s exit value.
	 * @param stdoutMessage	A completed {@link Process}'s messages to standard output as one String.
	 * @param stderrMessage	A completed {@link Process}'s messages to standard error as one String.
	 */
	public ProcessResult(int exitValue, String stdoutMessage, String stderrMessage) {
		this.exitValue = exitValue;
		this.stdoutMessage = stdoutMessage;
		this.stderrMessage = stderrMessage;
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
	
	/**
	 * @return	A plain-text report of the exit value and standard output/error messages.
	 */
	public String report() {
		return Joiner.on("  ").join(
				String.format("The program returned exit value %d.", exitValue),
				reportStreamContents("standard output", stdoutMessage),
				reportStreamContents("standard error", stderrMessage));
	}
	
	private static String reportStreamContents(String streamName, String contents) {
		if (contents.isEmpty()) {
			return String.format("No messages to %s.", streamName);
		} else {
			return String.format("Message to %s: [[%s]].", streamName, contents);
		}
	}
	
	@Override
	public boolean equals(Object thatObject) {
		if (!(thatObject instanceof ProcessResult)) {
			return false;
		}
		
		ProcessResult that = (ProcessResult) thatObject;
		
		return (Objects.equal(this.exitValue,
							  that.exitValue)
			 && Objects.equal(this.stdoutMessage,
					 		  that.stdoutMessage)
			 && Objects.equal(this.stderrMessage,
					 		  that.stderrMessage));
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(exitValue, stdoutMessage, stderrMessage);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("exitValue", exitValue)
				.add("stdoutMessage", stdoutMessage)
				.add("stderrMessage", stderrMessage)
				.toString();
	}
}
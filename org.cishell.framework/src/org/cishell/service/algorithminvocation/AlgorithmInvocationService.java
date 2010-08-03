
/* TODO: When we have time, we should talk about the design of the Algorithm Invocation Service (if
 *  we even use that name in the end).
 * It's somewhat apparent that there is a use for this service, but exactly how it fits into
 *  CIShell and all of the tools remains to be fully clarified.
 * This is all commented out for now because the design/use need discussion.
 */

//package org.cishell.service.algorithminvocation;
//
//import java.util.Dictionary;
//
//import org.cishell.framework.CIShellContext;
//import org.cishell.framework.algorithm.Algorithm;
//import org.cishell.framework.algorithm.AlgorithmCanceledException;
//import org.cishell.framework.algorithm.AlgorithmCreationCanceledException;
//import org.cishell.framework.algorithm.AlgorithmCreationFailedException;
//import org.cishell.framework.algorithm.AlgorithmExecutionException;
//import org.cishell.framework.algorithm.AlgorithmFactory;
//import org.cishell.framework.data.Data;
//
///**
// * Provides the caller with various ways of creating algorithms, executing them, and
// *  gathering/mutating parameters.
// * When creating an algorithm (from a factory), if the factory implements ParameterMutator,
// *  mutateParameters() will be called on it.
// * All methods can optionally operate on a new thread, which is determined by shouldUseNewThread.
// */
//public interface AlgorithmInvocationService {
//	/**
//	 * Uses factory to create an algorithm, presenting the user with a GUI for parameters.
//	 */
//	public Algorithm createAlgorithm(
//			final AlgorithmFactory factory,
//			final Data[] data,
//			final CIShellContext ciShellContext,
//			boolean shouldUseNewThread)
//			throws AlgorithmCreationCanceledException, AlgorithmCreationFailedException;
//
//	/**
//	 * Uses factory to create an algorithm, using parameters (instead of presenting the user with a
//	 *  GUI for them).
//	 */
//	public Algorithm createAlgorithm(
//			final AlgorithmFactory factory,
//			final Data[] data,
//			final Dictionary<String, Object> parameters,
//			final CIShellContext ciShellContext,
//			boolean shouldUseNewThread)
//			throws AlgorithmCreationCanceledException, AlgorithmCreationFailedException;
//
//	/**
//	 * Invokes algorithm, returning the Data[] result of algorithm.execute().
//	 * If logExceptionThrown is true, any exception thrown will be logged to the
//	 *  default LogService.
//	 * If displayRuntimeException is true, the stack trace of any exception thrown will be
//	 *  displayed in an error message box.
//	 */
//	public Data[] invokeAlgorithm(
//			final Algorithm algorithm,
//			final boolean logExceptionThrown,
//			final boolean displayRuntimeException,
//			boolean shouldUseNewThread)
//			throws AlgorithmCanceledException, AlgorithmExecutionException;
//
//	/**
//	 * Invokes algorithm, assuming sensible defaults for inline algorithm execution (that is, 
//	 *  not explicitly invoked from a menu/etc.), and return the Data[] result of
//	 *  algorithm.execute().
//	 * Most likely wraps invokeAlgorithm().
//	 */
//	public Data[] simpleInvokeAlgorithm(final Algorithm algorithm, Thread thread)
//			throws AlgorithmCanceledException, AlgorithmExecutionException;
//
//	/**
//	 * Given factory, presents the user with a GUI for parameters to use for creating and executing
//	 *  an algorithm.
//	 * Most likely wraps createAlgorithm() and invokeAlgorithm().
//	 */
//	public Data[] createAndInvokeAlgorithm(
//			final AlgorithmFactory factory,
//			final Data[] data,
//			final CIShellContext ciShellContext,
//			final boolean logExceptionThrown,
//			final boolean displayRuntimeException,
//			boolean shouldUseNewThread) throws
//				AlgorithmCreationCanceledException,
//				AlgorithmCreationFailedException,
//				AlgorithmCanceledException,
//				AlgorithmExecutionException;
//
//	/**
//	 * Given factory, uses parameters to create and execute an algorithm.
//	 * Most likely wraps createAlgorithm() and invokeAlgorithm().
//	 */
//	public Data[] createAndInvokeAlgorithm(
//			final AlgorithmFactory factory,
//			final Data[] data,
//			final Dictionary<String, Object> parameters,
//			final CIShellContext ciShellContext,
//			final boolean logExceptionThrown,
//			final boolean displayRuntimeException,
//			boolean shouldUseNewThread) throws
//				AlgorithmCreationCanceledException,
//				AlgorithmCreationFailedException,
//				AlgorithmCanceledException,
//				AlgorithmExecutionException;
//
//	/**
//	 * Given factory, uses parameters to create and execute an algorithm.
//	 * Sensible defaults for inline algorithm execution (that is, not explicitly invoked from a
//	 *  menu/etc.) are used.
//	 * Returns the Data[] result of algorithm.execute().
//	 * Most likely wraps createAlgorithm() and simpleInvokeAlgorithm().
//	 */
//	public Data[] simpleCreateAndInvokeAlgorithm(
//			final AlgorithmFactory factory,
//			final Data[] data,
//			final Dictionary<String, Object> parameters,
//			CIShellContext ciShellContext,
//			boolean shouldUseNewThread) throws
//				AlgorithmCreationCanceledException,
//				AlgorithmCreationFailedException,
//				AlgorithmCanceledException,
//				AlgorithmExecutionException;
//}
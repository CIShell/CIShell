//package org.cishell.reference.app.service.algorithminvocation;
//
//import java.util.Dictionary;
//import java.util.Hashtable;
//
//import org.cishell.framework.CIShellContext;
//import org.cishell.framework.algorithm.Algorithm;
//import org.cishell.framework.algorithm.AlgorithmCreationCanceledException;
//import org.cishell.framework.algorithm.AlgorithmCreationFailedException;
//import org.cishell.framework.algorithm.AlgorithmFactory;
//import org.cishell.framework.data.Data;
//import org.cishell.service.algorithminvocation.AlgorithmInvocationService;
//import org.osgi.service.log.LogService;
//
//public class AlgorithmInvocationServiceImpl implements AlgorithmInvocationService {
//	private LogService logger;
//
//	public AlgorithmInvocationServiceImpl(LogService logger) {
//		this.logger = logger;
//	}
//
//	@SuppressWarnings("unchecked")
//	public Algorithm createAlgorithm(
//			final AlgorithmFactory factory,
//			final Data[] data,
//			final CIShellContext ciShellContext,
//			boolean shouldUseNewThread)
//			throws AlgorithmCreationCanceledException, AlgorithmCreationFailedException {
//		/* TODO: Refactor org.cishell.utilities into several plugins so there are no
//		 *  circular dependencies!
//		 */
//
//		final AlgorithmCreationCanceledException[] canceledException =
//			new AlgorithmCreationCanceledException[1];
//		final AlgorithmCreationFailedException[] failedException =
//			new AlgorithmCreationFailedException[1];
//		final Algorithm[] algorithm = new Algorithm[1];
//
//		Runnable operator = new Runnable() {
//			public void run() {
//				/* TODO: Refactor algorithm creation code out of
//				 *  org.cishell.reference.gui.menumanager, and call it here.
//				 */
//
//				try {
//					// TODO: readFromMetadataFile
//					Dictionary<String, Object> parameters = new Hashtable<String, Object>();
//					// TODO: mutateParameters
//					Dictionary<String, Object> mutatedParameters = parameters;
//					// TODO: Invoke GUI builder service, getting user-entered parameters.
//					Dictionary<String, Object> userEnteredParameters = mutatedParameters;
//
//					algorithm[0] =
//						factory.createAlgorithm(data, userEnteredParameters, ciShellContext);
//				} catch (AlgorithmCreationCanceledException e) {
//					canceledException[0] = e;
//				} catch (AlgorithmCreationFailedException e) {
//					failedException[0] = e;
//				}
//			}
//		};
//
//		if (shouldUseNewThread) {
//			new Thread(operator).start();
//		} else {
//			operator.run();
//		}
//
//		return algorithm[0];
//	}
//
//	public Algorithm createAlgorithm(
//			final AlgorithmFactory factory,
//			final Data[] data,
//			final Dictionary<String, Object> parameters,
//			final CIShellContext ciShellContext,
//			boolean shouldUseNewThread)
//			throws AlgorithmCreationCanceledException, AlgorithmCreationFailedException {
//		final AlgorithmCreationCanceledException[] canceledException =
//			new AlgorithmCreationCanceledException[1];
//		final AlgorithmCreationFailedException[] failedException =
//			new AlgorithmCreationFailedException[1];
//		
//	}
//}
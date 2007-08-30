package org.cishell.testing.convertertester.core.tester2;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.testing.convertertester.core.converter.graph.Converter;
import org.cishell.testing.convertertester.core.converter.graph.ConverterPath;
import org.cishell.testing.convertertester.core.tester.graphcomparison.ComparisonResult;
import org.cishell.testing.convertertester.core.tester2.fakelogger.FakeLogCIShellContext;
import org.cishell.testing.convertertester.core.tester2.fakelogger.LogEntry;
import org.cishell.testing.convertertester.core.tester2.graphcomparison.NewGraphComparer;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.converter.ConvFailureInfo;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass.FilePassFailure;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass.FilePassSuccess;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass.PassPhase;
import org.osgi.service.log.LogService;

import prefuse.data.Graph;

public class DefaultTestRunner implements TestRunner {

	private LogService log;

	public DefaultTestRunner(LogService log) {
		this.log = log;
	}

	public FilePassResult[] runTest(TestConfigData testData) {
		
		Data[][] testFileData = testData.getTestFileData();
		ConverterPath testConverters = testData.getTestConverters();
		ConverterPath comparisonConverters = testData
				.getComparisonConverters();

		List testResults = new ArrayList();

		for (int ii = 0; ii < testFileData.length; ii++) {
			Data[] originalFileData = testFileData[ii];

			// test conversion phase

			ConvertResult testPhaseResult = convert(originalFileData,
					testConverters, testData);

			if (!testPhaseResult.succeeded()) {
				FilePassFailure failure = createFailResult(originalFileData, 
						PassPhase.TEST_CONV_PHASE,
						testPhaseResult.getFailInfo());
				testResults.add(failure);
				continue;
			}
			Data[] resultFileData = testPhaseResult.getResult();

			// comparison conversion (for original file) phase

			ConvertResult comparePhaseOrigResult = convert(originalFileData,
					comparisonConverters, testData);

			if (!comparePhaseOrigResult.succeeded()) {
				FilePassFailure failure = createFailResult(originalFileData, 
						PassPhase.COMPARE_CONV_ORIG_PHASE,
						comparePhaseOrigResult.getFailInfo());
				testResults.add(failure);
				continue;
			}
			Data[] originalInMemory = comparePhaseOrigResult.getResult();

			//comparison conversion (for result file) phase
			
			ConvertResult comparePhaseResultResult = convert(resultFileData,
					comparisonConverters, testData);

			if (!comparePhaseResultResult.succeeded()) {
				FilePassFailure failure = createFailResult(originalFileData, 
						PassPhase.COMPARE_CONV_RESULT_PHASE,
						comparePhaseResultResult.getFailInfo());
				testResults.add(failure);
				continue;
			}
			Data[] resultInMemory = comparePhaseResultResult.getResult();

			// graph comparison phase

			Graph resultGraph = (Graph) originalInMemory[0].getData();
			Graph origGraph = (Graph) resultInMemory[0].getData();

			NewGraphComparer comparer = testData.getComparer();
			ComparisonResult graphComparisonPhaseResult = comparer.compare(
					origGraph, resultGraph);

			if (!graphComparisonPhaseResult.comparisonSucceeded()) {
				String explanation =
					graphComparisonPhaseResult.getLog();
				FilePassFailure failure = createFailResult(originalFileData, 
						explanation, PassPhase.COMPARE_CONV_RESULT_PHASE,
						null);
				testResults.add(failure);
				continue;
			}

			FilePassSuccess success = new FilePassSuccess(originalFileData,
					"");
			testResults.add(success);
		}

		FilePassResult[] result = new FilePassResult[testResults.size()];
		result = (FilePassResult[]) testResults.toArray(result);
		return result;
	}

	private ConvertResult convert(Data[] startData,
			ConverterPath converters, TestConfigData testData) {

		Data[] currentData = getFilePathData(startData);
		
		/*
		 * rig up fake CISHellContext so we can get ahold of
		 * errors sent to logger.
		 */ 		
		FakeLogCIShellContext fakeCContext = 
			new FakeLogCIShellContext(testData.getContext());
		
		Converter currentConverter = converters.get(0);
		try {
			for (int ii = 0; ii < converters.size(); ii++) {
				currentConverter = converters.get(ii);
				
				//no parameters used
				Hashtable parameters = new Hashtable();
				
				currentData = currentConverter.execute(currentData,
						parameters, fakeCContext);

				/*
				 * There are two ways that converters generally fail.
				 * 1) They throw an exception that propagates out into
				 * this method, where we catch it.
				 * 2) They catch their own exception, send it to the logger,
				 * and return null.
				 */
				if (currentData == null || currentData[0].getData() == null) {
					Converter converter = converters.get(ii);
					
					String explanation = "Result of conversion was null. \r\n";
					
					
					if (fakeCContext.hasLogEntries()) {
						String logText = extractLogText(fakeCContext);
						explanation += "Error log contains the following: \r\n" +
							logText;
					} else {
						explanation += "No errors logged. Cause unknown. \r\n";
					}
					
					ConvFailureInfo failInfo = new ConvFailureInfo(
							explanation, converter);
					
					ConvertResult result = new ConvertResult(failInfo);
					return result;
				}
			}
		} catch (Throwable t) {
			ConvFailureInfo failInfo = new ConvFailureInfo(getStackTrace(t),
					currentConverter);
			ConvertResult result = new ConvertResult(failInfo);
			return result;
		}

		Data[] resultData = currentData;
		ConvertResult result = new ConvertResult(resultData);
		return result;
	}
	
	private FilePassFailure createFailResult(Data[] origData,
			String explanation, PassPhase lastReachedPhase,
			Converter failedConverter) {
		FilePassFailure failure = new FilePassFailure(origData, explanation,
				lastReachedPhase, failedConverter);
		return failure;
	}
	
	private FilePassFailure createFailResult(Data[] origData,
			PassPhase lastReachedPhase, ConvFailureInfo failInfo) {
		return createFailResult(origData, failInfo.getExplanation(), 
				lastReachedPhase, failInfo.getFailedConverter());
	}

	private class ConvertResult {

		private boolean succeeded;

		private Data[] result;

		private ConvFailureInfo failInfo;

		public ConvertResult(Data[] result) {
			this.result = result;

			this.succeeded = true;
		}

		public ConvertResult(ConvFailureInfo failInfo) {
			this.failInfo = failInfo;

			this.succeeded = false;
		}

		public boolean succeeded() {
			return this.succeeded;
		}

		public Data[] getResult() {
			return result;
		}

		public ConvFailureInfo getFailInfo() {
			return this.failInfo;
		}
	}

	private String getStackTrace(Throwable t) {
		final Writer writer = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(writer);
		t.printStackTrace(printWriter);
		return writer.toString();

	}

	private Data[] getFilePathData(Data[] fileData) {
		Data Data = (Data) fileData[0];
		String format = Data.getFormat();
		File actualFile = (File) Data.getData();
		try {
			String filePath = actualFile.getCanonicalPath();
			Data result = new BasicData(filePath, format);
			return new Data[] { result };
		} catch (IOException e) {
			this.log.log(LogService.LOG_ERROR, "Could not get file path " +
					"from file " + actualFile, e);
			return null;
		}

	}
	
	private String extractLogText(FakeLogCIShellContext fakeLogContext) {
		LogEntry[] entries = fakeLogContext.getLogEntries();
		
		String logText = "";
		
		for (int ii = 0; ii < entries.length; ii++) {
			LogEntry entry = entries[ii];
			
			Throwable e    = entry.getThrowable();
			String message = entry.getMessage();
			
			logText += message + "\r\n";
			logText += getStackTrace(e) + "\r\n";
			logText += "\r\n";
		}
		
		return logText;
	}
} 

package org.cishell.testing.convertertester.core.tester2;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.testing.convertertester.core.tester.graphcomparison.ComparisonResult;
import org.cishell.testing.convertertester.core.tester2.filepassresults.ComparePhaseFailure;
import org.cishell.testing.convertertester.core.tester2.filepassresults.ConvertPhaseFailure;
import org.cishell.testing.convertertester.core.tester2.filepassresults.FilePassResult;
import org.cishell.testing.convertertester.core.tester2.filepassresults.FilePassSuccess;
import org.cishell.testing.convertertester.core.tester2.graphcomparison.NewGraphComparer;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.ConvFailureInfo;
import org.osgi.service.log.LogService;

import prefuse.data.Graph;

public class DefaultTestRunner implements TestRunner {

	private LogService log;

	public DefaultTestRunner(LogService log) {
		this.log = log;
	}

	public FilePassResult[] runTest(TestConfigData testData) {
		
		Data[][] testFileData = testData.getTestFileData();
		AlgorithmFactory[] testConverters = testData.getTestConverters();
		AlgorithmFactory[] comparisonConverters = testData
				.getComparisonConverters();


		// list of FilePassResult
		final List testResults = new ArrayList();

		for (int ii = 0; ii < testFileData.length; ii++) {
			Data[] originalFileData = testFileData[ii];

			// test conversion phase

			ConvertResult testPhaseResult = convert(originalFileData,
					testConverters, testData);

			if (!testPhaseResult.succeeded()) {
				ConvertPhaseFailure failure = new ConvertPhaseFailure(
						originalFileData, testConverters, comparisonConverters,
						testPhaseResult.getFailInfo(),
						ConvertPhaseFailure.TEST_PHASE);
				testResults.add(failure);
				continue;
			}
			Data[] resultFileData = testPhaseResult.getResult();

			// comparison conversion phase

			ConvertResult comparisonPhaseResult1 = convert(originalFileData,
					comparisonConverters, testData);

			if (!comparisonPhaseResult1.succeeded()) {
				ConvertPhaseFailure failure = new ConvertPhaseFailure(
						originalFileData, testConverters, comparisonConverters,
						comparisonPhaseResult1.getFailInfo(),
						ConvertPhaseFailure.TEST_PHASE);
				testResults.add(failure);
				continue;
			}
			Data[] originalInMemory = comparisonPhaseResult1.getResult();

			ConvertResult comparisonPhaseResult2 = convert(resultFileData,
					comparisonConverters, testData);

			if (!comparisonPhaseResult2.succeeded()) {
				ConvertPhaseFailure failure = new ConvertPhaseFailure(
						originalFileData, testConverters, comparisonConverters,
						comparisonPhaseResult2.getFailInfo(),
						ConvertPhaseFailure.COMPARISON_PHASE);
				testResults.add(failure);
				continue;
			}
			Data[] resultInMemory = comparisonPhaseResult2.getResult();

			// graph comparison phase

			Graph origGraph = (Graph) resultInMemory[0].getData();
			Graph resultGraph = (Graph) originalInMemory[0].getData();

			NewGraphComparer comparer = testData.getComparer();
			ComparisonResult graphComparisonPhaseResult = comparer.compare(
					origGraph, resultGraph);

			if (!graphComparisonPhaseResult.comparisonSucceeded()) {
				String explanation = graphComparisonPhaseResult.getLog();
				ComparePhaseFailure failure = new ComparePhaseFailure(
						originalFileData, testConverters, comparisonConverters,
						explanation);
				testResults.add(failure);
				continue;
			}

			FilePassSuccess success = new FilePassSuccess(originalFileData,
					testConverters, comparisonConverters);
			testResults.add(success);
		}

		FilePassResult[] result = new FilePassResult[testResults.size()];
		result = (FilePassResult[]) testResults.toArray(result);
		return result;
	}

	private ConvertResult convert(Data[] startData,
			AlgorithmFactory[] converters, TestConfigData testData) {

		// validator takes a string file path, all others just take files or
		// graphs or whatever);
		Data[] currentData = getFilePathData(startData);

		AlgorithmFactory currentConverter = converters[0];
		try {
			for (int ii = 0; ii < converters.length; ii++) {
				currentConverter = converters[ii];
				Algorithm currentAlgorithm = currentConverter.createAlgorithm(
						currentData, new Hashtable(), testData.getContext());
				currentData = currentAlgorithm.execute();

				if (currentData == null) {
					ConvFailureInfo failInfo = new ConvFailureInfo(
							"null result data", currentConverter);
					ConvertResult result = new ConvertResult(failInfo);
					return result;
				}

				BasicData currentDataImpl = (BasicData) currentData[0];
				if (currentDataImpl.getData() == null) {
					ConvFailureInfo failInfo = new ConvFailureInfo(
							"null result data contents", currentConverter);
					ConvertResult result = new ConvertResult(failInfo);
					return result;
				}
			}
		} catch (Throwable t) {
			ConvFailureInfo failInfo = new ConvFailureInfo(t.toString()
					+ getStackTrace(t), currentConverter);
			ConvertResult result = new ConvertResult(failInfo);
			return result;
		}

		Data[] resultData = currentData;
		ConvertResult result = new ConvertResult(resultData);
		return result;
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
		BasicData basicData = (BasicData) fileData[0];
		String format = basicData.getFormat();
		File actualFile = (File) basicData.getData();
		try {
			String filePath = actualFile.getCanonicalPath();
			BasicData result = new BasicData(filePath, format);
			return new Data[] { result };
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();

			return null;
		}

	}
}

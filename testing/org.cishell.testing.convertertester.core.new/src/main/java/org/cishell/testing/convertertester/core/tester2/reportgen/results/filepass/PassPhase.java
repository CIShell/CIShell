package org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass;

public final class PassPhase {

	public static final PassPhase TEST_CONV_PHASE = 
		new PassPhase("Test Conversion Phase");
	public static final PassPhase COMPARE_CONV_ORIG_PHASE = 
		new PassPhase("Comparison Conversion Phase using original file");
	public static final PassPhase COMPARE_CONV_RESULT_PHASE = 
		new PassPhase("Comparison Conversion Phase using original file");
	public static final PassPhase GRAPH_COMPARE_PHASE = 
		new PassPhase("Graph Comparison Phase");
	public static final PassPhase SUCCEEDED_PHASE = 
		new PassPhase("Succeeded Phase");
	
	private final String name;
	
	public String toString() {
		return this.name;
	}
	
	private PassPhase(String name) {
		this.name = name;
	}
}

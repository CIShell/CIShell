package org.cishell.utility.datastructure.datamodel.exception;

public class ModelStructureException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ModelStructureException() {
		super();
	}

	public ModelStructureException(String arg0) {
		super(arg0);
	}

	public ModelStructureException(Throwable arg0) {
		super(arg0);
	}

	public ModelStructureException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
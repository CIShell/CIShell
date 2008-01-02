package org.wonderly.doclets;

import com.sun.javadoc.*;
import java.io.*;
import java.util.*;

/**
 *  This class is used to manage the contents of a Java package.
 *  It accepts ClassDoc objects and examines them and groups them
 *  according to whether they are classes, interfaces, exceptions
 *  or errors.  The accumulated Vectors can then be processed to
 *  get to all of the elements of the package that fall into each
 *  catagory.
 *
 *  @version 1.0
 *  @author <a href="mailto:gregg.wonderly@pobox.com">Gregg Wonderly</a>
 */
public class Package {
	/** The name of the package this object is for */
	protected String pkg;
	/** The classes this package has in it */
	protected Vector classes;
	/** The interaces this package has in it */
	protected Vector interfaces;
	/** The exceptions this package has in it */
	protected Vector exceptions;
	/** The errors this package has in it */
	protected Vector errors;
	
	/**
	 *  Construct a new object corresponding to the passed package
	 *  name.
	 *
	 *  @param pkg the package name to use
	 */
	public Package( String pkg ) {
		this.pkg = pkg;
		if( pkg.equals("") )
			this.pkg = "<none>";
		classes = new Vector();
		interfaces = new Vector();
		exceptions = new Vector();
		errors = new Vector();
	}
	
	/**
	 *  Adds a ClassDoc element to this package.
	 *
	 *  @param cd the object to add to this package
	 */
	public void addElement( ClassDoc cd ) {
		if( cd.isInterface() ) {
			addSorted( interfaces, cd );
		} else if( cd.isClass() ) {
			if( isException(cd) ) {
				addSorted( exceptions, cd );
			} else if( isError(cd) ) {
				addSorted( errors, cd );
			} else {
				addSorted( classes, cd );
			}
		}
	}
	
	/**
	 *  Perform insertion sort (case independent compare) into vector of other docs 
	 */
	void addSorted( Vector v, ClassDoc cd ) {
		String nm = cd.name().toLowerCase();
		for( int i = 0; i < v.size(); ++i ) {
			ClassDoc od = (ClassDoc)v.elementAt(i);
			// Case independent compare...
			if( nm.compareTo( od.name().toLowerCase() ) < 0 ) {
				v.insertElementAt( cd, i );
				return;
			}
		}
		v.addElement(cd);
	}
	
	boolean isException( ClassDoc doc ) {
		ClassDoc sup = doc.superclass();
		if( sup == null )
			return false;
		if( sup.name().equals( "java.lang.Exception" ) )
			return true;
		return isException( sup );
	}

	boolean isError( ClassDoc doc ) {
		ClassDoc sup = doc.superclass();
		if( sup == null )
			return false;
		if( sup.name().equals( "java.lang.Error" ) )
			return true;
		return isError( sup );
	}
}

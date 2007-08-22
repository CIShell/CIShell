package org.cishell.testing.convertertester.core.tester2.reportgen.results;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass.FilePassFailure;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass.FilePassSuccess;
import org.cishell.testing.convertertester.core.tester2.util.ConvUtil;
import org.osgi.framework.ServiceReference;

public class ConvResult {
	
	private ServiceReference conv;
	
	private List failFilePasses = new ArrayList();
	private List successFilePasses = new ArrayList();
	private List uniqueFailureExplanations = new ArrayList();
	private Set tests      = new HashSet();
	
	private boolean isTrusted;
	
	private float chanceCorrect = 1.0f;
	
	public ConvResult(ServiceReference conv) {
		this.conv = conv;
		this.isTrusted = false;
	}
	
	public void setTrusted(boolean isTrusted) {
		this.isTrusted = isTrusted;
	}
	
	public boolean isTrusted() {
		return this.isTrusted;
	}
	
	public float getChanceOfFlaw() {
		return 1.0f - this.chanceCorrect; 
	}
	
	public int getNumFilePasses() {
		return this.failFilePasses.size() + 
			this.successFilePasses.size();
	}
	
	public float getChanceCorrect() {
		return this.chanceCorrect;
	}
	
	public float getPercentPassed() {
		float totalFilePasses = successFilePasses.size() + failFilePasses.size();
		return successFilePasses.size() / totalFilePasses;
	}
	
	public void addTest(TestResult tr) {
		this.tests.add(tr);
	}
	
	/**
	 * Returns the full unique name of the converter, including the package 
	 * it is found in.
	 * @return full unique name
	 */
	public String getNameWithPackage() {
		return (String) this.getRef().getProperty("service.pid");
	}
	
	/**
	 * Returns a shortened version of the name, which does not contain the
	 * package. This name is not guaranteed to be unique, but is easier
	 * for humans to read.
	 * @return The shortened name
	 */
	public String getNameNoPackage() {
		return ConvUtil.removePackagePrefix(getNameWithPackage());
	}
	
	/**
	 * Returns the shortened name, with either "Trusted" or "Not Trusted"
	 * prepended to the front.
	 * @return The shortened name with trust information.
	 */
	public String getNameNoPackageWithTrust() {
		String nameNoPackageWithTrust =  " - " + getNameNoPackage() ;
		if (isTrusted()) {
			nameNoPackageWithTrust = "Trusted" + nameNoPackageWithTrust;
		} else {
			nameNoPackageWithTrust = "Not Trusted" + nameNoPackageWithTrust;
		}
		
		return nameNoPackageWithTrust;
	}
	
	public TestResult[] getTests() {
		return (TestResult[]) this.tests.toArray(new TestResult[0]);
	}
	
	public void addPass(FilePassSuccess fp) {
		this.successFilePasses.add(fp);
	}
	
	public String [] getUniqueFailureExplanations() {
		return (String[]) this.uniqueFailureExplanations.toArray(new String[0]);
	}
	
	public void addPass(FilePassFailure fp, float chanceAtFault) {
		if (isUniqueFailure(fp)) {
			adjustTotalChanceAtFault(chanceAtFault);
			this.uniqueFailureExplanations.add(fp.getExplanation());
		}
		this.failFilePasses.add(fp);
		
	}
	
	public FilePassSuccess[] getSuccessFilePasses() {
		return (FilePassSuccess[]) this.successFilePasses.toArray(new FilePassSuccess[0]);
	}
	
	public FilePassFailure[] getFailFilePasses() {
		return (FilePassFailure[]) this.failFilePasses.toArray(new FilePassFailure[0]);
	}
	public FilePassResult[] getFilePasses() {
		List allFilePasses = new ArrayList();
		allFilePasses.addAll(this.successFilePasses);
		allFilePasses.addAll(this.failFilePasses);
		return (FilePassResult[]) allFilePasses.toArray(new FilePassResult[0]);
	}
	
	public ServiceReference getRef() {
		return this.conv;
	}
	
	private void adjustTotalChanceAtFault(float chanceAtFault) {
		if (this.chanceCorrect == 1.0f) {
			this.chanceCorrect = 1.0f - chanceAtFault;
		} else {
			this.chanceCorrect = this.chanceCorrect * (1.0f - chanceAtFault);
		
		}
	}
	
	private boolean isUniqueFailure(FilePassFailure fp) {
		Iterator iter = this.uniqueFailureExplanations.iterator();
		while (iter.hasNext()) {
			String failureExplanation = (String) iter.next();
			
			if (failureExplanation.equals(fp.getExplanation())) {
				return false;
			}
		}
		
		return true;
 	}
}

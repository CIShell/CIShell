package org.cishell.testing.convertertester.core.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.LocalCIShellContext;
import org.cishell.testing.convertertester.core.tester.ConverterTester;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class Activator implements BundleActivator{
	private ConverterTester ct;
	private BundleContext b;
	private CIShellContext c;
	private File configFile;

	public void start(BundleContext b){
		this.b = b;
		this.c = new LocalCIShellContext(b);
		startUp();
		}

	

	public void stop(BundleContext b){
		System.out.println("Goodbye!");
		b = null;
		c = null;
		configFile = null;
		ct = null;
	}

	private void processConfigurationFile(File f) throws FileNotFoundException {
		System.out.println("Processing " + f.getName());
		if(!f.isHidden() && (f.getName().charAt(0) != '.')){
			if(f.isDirectory()){
				File[] files = f.listFiles();
				for (int ii = 0; ii < files.length; ii++) {
					File ff = files[ii];
					processConfigurationFile(ff);
				}
			}
			else{
				try{
					ct = new ConverterTester(b, c, f);
					System.out.println(ct);
					ct.testFiles();
					ct.printResults();
				}catch(Exception ex){
					System.out.println("Failed to create ConverterTester\n\n");
					ex.printStackTrace();
				}
			}
		}
	}

	public void startUp(){
		Scanner in = new Scanner(System.in);
		for(;;){
			System.out.println("Welcome to NWB's Converter Tester\n"+
					"Please enter the name of a configuration file \n"+ 
			"or a directory of configuration files (Q/q to quit): ");
			String s = in.nextLine();
			if(s.trim().equalsIgnoreCase("Q"))
				break;
			try{
				configFile = new File(s);
				processConfigurationFile(configFile);
			}
			catch (NullPointerException ex){
				System.out.println("Invalid file name");;
			}
			catch(FileNotFoundException fnfe){
				System.out.println("Could not find the specified configuration file");
			}
		}
	}

}


package org.cishell.testing.convertertester.commandline;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.LocalCIShellContext;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.testing.convertertester.core.tester2.ConverterTester2;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;


public class Activator implements BundleActivator, AlgorithmProperty {
	private ConverterTester2 ct;
	private BundleContext b;
	private CIShellContext c;
	private File configFile;
	private LogService logger;
	
	public static final String QUIT = "q";

	public void start(BundleContext b){
		this.b = b;
		this.c = new LocalCIShellContext(b);
		
		this.logger = (LogService) c.getService(
				LogService.class.getName());
		startCommandLine();
		}

	
	public ServiceReference[] getServiceReferences() {
		  String filter = "(&("+ALGORITHM_TYPE+"="+TYPE_CONVERTER+"))";// +

		  try {
		  ServiceReference[] refs = b.getServiceReferences(
				  AlgorithmFactory.class.getName(), filter);
		  
		  return refs;
		  } catch (InvalidSyntaxException e) {
			  System.out.println("OOPS!");
			  System.out.println(e);
			  return null;
		  }
	}

	public void stop(BundleContext b){
		System.out.println("Goodbye!");
		
		b = null;
		c = null;
		configFile = null;
		ct = null;
	}
	
	public void startCommandLine(){
		Scanner in = new Scanner(System.in);
		while(true) {
			System.out.println("Welcome to NWB's Converter Tester");
			System.out.println(	"Please enter the name of a configuration " +
					"file or\n a directory of configuration files (" + QUIT +
					") to quit): ");
				
			String s = in.nextLine();
			if(s.trim().equalsIgnoreCase(QUIT))
				break;
			try{
				configFile = new File(s);
				processConfigurationFile(configFile);
			}
			catch (NullPointerException ex){
				System.out.println("Invalid file name");;
			}
			catch(FileNotFoundException fnfe){
				System.out.println("Could not find the specified " +
						"configuration file");
			}
		}
	}

	private void processConfigurationFile(File f) throws FileNotFoundException {
		System.out.println("Processing " + f.getName());
		try {
			ServiceReference[] refs = getServiceReferences();
			ct = new ConverterTester2(this.logger);
			System.out.println("NOT YET READY FOR USE.");
//			ReportGenerator overview = new OverviewReportGenerator();
//			ct.execute(new ReportGenerator[] {overview}, logger, c, b);
			System.out.println(ct);
		} catch (Exception ex) {
			System.out.println("Failed to create " + "ConverterTester\n\n");
			ex.printStackTrace();
		}
	}
}


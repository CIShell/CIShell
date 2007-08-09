package org.cishell.testing.convertertester.core.converter;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.cishell.testing.convertertester.core.converter.graph.ConverterGraph;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;



public class ConverterLoaderImpl implements AlgorithmProperty, DataConversionService, ServiceListener{

public final static String SERVICE_LIST = "SERVICE_LIST"; 
	private Map converterList;
    private BundleContext bContext;
   private CIShellContext ciContext;
 
    
    public ConverterLoaderImpl(BundleContext bContext, CIShellContext cContext){
    	this.ciContext = cContext;
    	this.bContext = bContext;
        converterList = new Hashtable();
        
     

        String filter = "(&("+ALGORITHM_TYPE+"="+TYPE_CONVERTER+"))";
        //printConverters(bContext);
        
        try {
			this.bContext.addServiceListener(this, filter);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
   		assembleGraph();
    }
    
    private void assembleGraph() {    	
        try {
            String filter = "(&("+ALGORITHM_TYPE+"="+TYPE_CONVERTER+"))";// +
            				

            ServiceReference[] refs = bContext.getServiceReferences(
                    AlgorithmFactory.class.getName(), filter);
          //  ConverterGraph g = new ConverterGraph(refs);
           // System.out.println(g.printComparisonConverterPaths());
           /* System.out.println(g.printComparisonConverterPaths() + "\n" +
            		g.getComparePaths().length + "\n" + g.printTestConverterPaths() + "\n");
            int length = 0;
            for(int i = 0; i < g.getTestPaths().length; i++){
            	for(int j = 0; j < g.getTestPaths()[i].length; j++){
            		length++;
            	}
            }
            System.out.println(length + " " + g.getTestPaths().length);
            */
           // System.out.println(g);
            
            if (refs != null) {
				for (int i = 0; i < refs.length; ++i) {
					
					this.converterList.put(refs[i].getProperty("service.pid").toString(), refs[i]);
					
				}
			}
        } catch (InvalidSyntaxException e) {
            throw new RuntimeException(e);
        }
    } 

    
   
    
	
	public void serviceChanged(ServiceEvent event) {
		ServiceReference inServiceRef = event.getServiceReference();
		
		
		if (event.getType() ==  ServiceEvent.MODIFIED) {
			this.converterList.put(inServiceRef.getProperty("service.pid").toString(), inServiceRef);
		}
		else if(event.getType() == ServiceEvent.REGISTERED) {
			this.converterList.put(inServiceRef.getProperty("service.pid").toString(), inServiceRef);
		}
		else if(event.getType() == ServiceEvent.UNREGISTERING) {
			System.out.println("Unregistering service: " + inServiceRef);
			this.converterList.remove(inServiceRef.getProperty("service.pid").toString());
			
		}
	}



	public Converter[] findConverters(String inFormat, String outFormat, int maxHops, String maxComplexity) {
		// TODO Auto-generated method stub
		return null;
	}

	public Converter[] findConverters(String inFormat, String outFormat) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	public Converter getConverter(String[] converterChain) throws Exception{
		ArrayList services = new ArrayList();
		for(int ii = 0; ii < converterChain.length; ii++){
			String s = converterChain[ii];
			ServiceReference ref = (ServiceReference) this.converterList.get(s);
			if(ref == null){
				throw new Exception("Converter: " + s + " cannot be found");
			}
			services.add(ref);
		}
		return new ConverterTesterImpl(this.bContext, this.ciContext, (ServiceReference[])services.toArray(new ServiceReference[0]));
	}
	
	private static void printConverters(BundleContext bContext){
		String filter = "(&("+ALGORITHM_TYPE+"="+TYPE_CONVERTER+"))";
		try{
			
			ServiceReference[] refs = bContext.getAllServiceReferences(AlgorithmFactory.class.getName(), filter);
			for(int ii = 0; ii < refs.length; ii++){
				ServiceReference ref = refs[ii];
				System.out.println("\t"+ref.getProperty("service.pid"));
			}
		
		}
		catch(Exception ex){
			System.err.println(ex);
		}
		
	}

	public org.cishell.framework.data.Data convert(org.cishell.framework.data.Data data, String outFormat) {
		// TODO Auto-generated method stub
		return null;
	}

	public Converter[] findConverters(org.cishell.framework.data.Data data, String outFormat) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

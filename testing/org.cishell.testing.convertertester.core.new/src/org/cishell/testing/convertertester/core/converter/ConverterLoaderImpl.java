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
	//private Converter[] testConverters;  //to store the chain of converters we want to test.
	//private Converter[] toGraphObjectConverters; //to store the chain of converters to convert the files to prefuse or jung Graph objects.
	//private BundleContext bContext;
public final static String SERVICE_LIST = "SERVICE_LIST"; 
	private Map converterList;
    private BundleContext bContext;
   private CIShellContext ciContext;
   // private Map   dataTypeToVertex;
   // private Graph graph;
    
    public ConverterLoaderImpl(BundleContext bContext, CIShellContext cContext){
    	this.ciContext = cContext;
    	this.bContext = bContext;
        converterList = new Hashtable();
        
        //this.graph = new DirectedSparseGraph();
       // this.dataTypeToVertex = new Hashtable();

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
            				 /* "("+IN_DATA+"=*) " +
                              "("+OUT_DATA+"=*)" +
                              "(!("+REMOTE+"=*))" +
                              "(!("+IN_DATA+"=file-ext:*))" + 
                              "(!("+OUT_DATA+"=file-ext:*)))";*/

            ServiceReference[] refs = bContext.getServiceReferences(
                    AlgorithmFactory.class.getName(), filter);
            ConverterGraph g = new ConverterGraph(refs);
            System.out.println("Blah!");
            System.out.println(g);
            if (refs != null) {
				for (int i = 0; i < refs.length; ++i) {
					//System.out.println(refs[i]);
					this.converterList.put(refs[i].getProperty("service.pid").toString(), refs[i]);
					/*String inData = (String) refs[i]
							.getProperty(AlgorithmProperty.IN_DATA);
					String outData = (String) refs[i]
							.getProperty(AlgorithmProperty.OUT_DATA);

					addServiceReference(inData, outData, refs[i]);*/
				}
			}
        } catch (InvalidSyntaxException e) {
            throw new RuntimeException(e);
        }
    } 

    
   /* private void addServiceReference(String srcDataType, String tgtDataType, ServiceReference serviceReference) {
		if (srcDataType != null && srcDataType.length() > 0
				&& tgtDataType != null && tgtDataType.length() > 0) {
			Vertex srcVertex = getVertex(srcDataType);
			Vertex tgtVertex = getVertex(tgtDataType);
			removeServiceReference(srcDataType, tgtDataType, serviceReference);
			this.converterList.put(serviceReference.getProperty("service.pid").toString(), serviceReference);
			Edge directedEdge = srcVertex.findEdge(tgtVertex);
			if (directedEdge == null) {
				directedEdge = new DirectedSparseEdge(srcVertex, tgtVertex);
				graph.addEdge(directedEdge);
			}

			AbstractList serviceList = (AbstractList) directedEdge.getUserDatum(SERVICE_LIST);
			if (serviceList == null) {
				serviceList = new ArrayList();
				serviceList.add(serviceReference);
			}
			directedEdge.setUserDatum(SERVICE_LIST, serviceList,
					new UserDataContainer.CopyAction.Shared());
		}
	}*/
    
   /* private Vertex getVertex(String dataType) {
		Vertex vertex = (SparseVertex)dataTypeToVertex.get(dataType);
		if (vertex== null) {
			vertex = new SparseVertex();
			vertex.addUserDatum("label", dataType,
								new UserDataContainer.CopyAction.Shared());
			graph.addVertex(vertex);
			dataTypeToVertex.put(dataType, vertex);
		}
		return vertex;
	}
    
    private void removeServiceReference(String srcDataType, String tgtDataType, ServiceReference serviceReference) {
		if (srcDataType != null && tgtDataType != null) {
			Vertex srcVertex = (Vertex) dataTypeToVertex.get(srcDataType);
			Vertex tgtVertex = (Vertex) dataTypeToVertex.get(tgtDataType);
			String pid = (String) serviceReference
					.getProperty(Constants.SERVICE_PID);

			if (srcVertex != null && tgtVertex != null) {
				Edge edge = srcVertex.findEdge(tgtVertex);
				if (edge != null) {
					AbstractList serviceList = (AbstractList) edge
							.getUserDatum(SERVICE_LIST);
					for (Iterator iterator = serviceList.iterator(); iterator
							.hasNext();) {
						ServiceReference currentServiceReference = (ServiceReference) iterator
								.next();
						String currentPid = (String) currentServiceReference
								.getProperty(Constants.SERVICE_PID);

						if (pid.equals(currentPid)) {
							iterator.remove();
						}
					}
					if (serviceList.isEmpty()) {
						graph.removeEdge(edge);
					}
				}
			}
		}
	}*/
    
	
	public void serviceChanged(ServiceEvent event) {
		ServiceReference inServiceRef = event.getServiceReference();
		
		/*String inDataType = (String)inServiceRef.getProperty(AlgorithmProperty.IN_DATA);
		String outDataType = (String)inServiceRef.getProperty(AlgorithmProperty.OUT_DATA);*/

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

/*	public Data convert(Data data, String outFormat) {
		// TODO Auto-generated method stub
		return null;
	}

	public Converter[] findConverters(Data data, String outFormat) {
		// TODO Auto-generated method stub
		return null;
	}*/

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

/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 15, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.service.conversion;

import java.io.File;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.io.GraphMLFile;
import edu.uci.ics.jung.utils.UserDataContainer;

public class DataConversionServiceImpl implements DataConversionService, AlgorithmProperty, ServiceListener {
	public final static String SERVICE_LIST = "SERVICE_LIST"; 
	
    private BundleContext bContext;
    private CIShellContext ciContext;
    private Map   dataTypeToVertex;
    private Graph graph;
    
    public DataConversionServiceImpl(BundleContext bContext, CIShellContext ciContext) {
        this.bContext = bContext;
        this.ciContext = ciContext;

        String filter = "(&("+ALGORITHM_TYPE+"="+TYPE_CONVERTER+")" +
                          "("+IN_DATA+"=*) " +
                          "("+OUT_DATA+"=*)" +
                          "(!("+REMOTE+"=*))" +
                          "(!("+IN_DATA+"=file-ext:*))" + 
                          "(!("+OUT_DATA+"=file-ext:*)))";
        
        try {
			this.bContext.addServiceListener(this, filter);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
   		assembleGraph();
    }

    /**
     * Assemble the directed graph of converters
     *
     */
    private void assembleGraph() {
    	graph = new DirectedSparseGraph();
    	
    	dataTypeToVertex = new Hashtable();
    	
        try {
            String filter = "(&("+ALGORITHM_TYPE+"="+TYPE_CONVERTER+")" +
            				  "("+IN_DATA+"=*) " +
                              "("+OUT_DATA+"=*)" +
                              "(!("+REMOTE+"=*))" +
                              "(!("+IN_DATA+"=file-ext:*))" + 
                              "(!("+OUT_DATA+"=file-ext:*)))";

            ServiceReference[] refs = bContext.getServiceReferences(
                    AlgorithmFactory.class.getName(), filter);
            
            if (refs != null) {
				for (int i = 0; i < refs.length; ++i) {
					String inData = (String) refs[i]
							.getProperty(AlgorithmProperty.IN_DATA);
					String outData = (String) refs[i]
							.getProperty(AlgorithmProperty.OUT_DATA);

					addServiceReference(inData, outData, refs[i]);
				}
			}
        } catch (InvalidSyntaxException e) {
            throw new RuntimeException(e);
        }
    } 
    
    /**
     * @see org.cishell.service.conversion.DataConversionService#findConverters(java.lang.String, java.lang.String)
     */
    public Converter[] findConverters(String inFormat, String outFormat) {
        saveGraph();
		if (inFormat != null && inFormat.length() > 0 &&
			outFormat != null && outFormat.length() > 0) {
			
            Converter[] converters = null;
            
            if (outFormat.startsWith("file-ext:")) {
                converters = getConverters(inFormat, "file:*");
                converters = addFinalStepConversions(converters, outFormat);
            } else {
                converters = getConverters(inFormat, outFormat);
            }
            
			return converters;
		}
		return new Converter[0];		
    }
    
    private Converter[] addFinalStepConversions(Converter[] converters, String outFormat) {
        Collection newConverters = new HashSet();
        
        Set formats = new HashSet();
        for (int i=0; i < converters.length; i++) {
            String format = (String) converters[i].getProperties().get(OUT_DATA);
            
            if (!formats.contains(format)) {
                String filter = "(&("+ALGORITHM_TYPE+"="+TYPE_CONVERTER+")" +
                                  "(!("+REMOTE+"=*))" +
                                  "("+IN_DATA+"="+format+")" + 
                                  "("+OUT_DATA+"="+outFormat+"))";
            
                try {
                    ServiceReference[] refs = bContext.getServiceReferences(
                            AlgorithmFactory.class.getName(), filter);
                    
                    if (refs != null && refs.length > 0) {
                        for (int j=0; j < refs.length; j++) {
                            List chain = new ArrayList(Arrays.asList(
                                    converters[i].getConverterChain()));
                            chain.add(refs[i]);
                            
                            ServiceReference[] newChain = (ServiceReference[]) 
                                chain.toArray(new ServiceReference[0]);
                            
                            newConverters.add(new ConverterImpl(bContext, ciContext, newChain));
                        }
                    
                        formats.add(format);
                    }
                } catch (InvalidSyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return (Converter[]) newConverters.toArray(new Converter[0]);
    }

    private Converter[] getConverters(String inFormat, String outFormat) {
		String inFilter = "(&(" + ALGORITHM_TYPE + "=" + TYPE_CONVERTER + ")"
				+ "(" + IN_DATA + "=" + inFormat + ") " + "(" + OUT_DATA
				+ "=*)" + "(!("+IN_DATA+"=file-ext:*))" + "(!(" + REMOTE + "=*)))";

		String outFilter = "(&(" + ALGORITHM_TYPE + "=" + TYPE_CONVERTER + ")"
				+ "(" + IN_DATA + "=*) " + "(" + OUT_DATA + "=" + outFormat
				+ ")" + "(!("+OUT_DATA+"=file-ext:*))" + "(!(" + REMOTE + "=*)))";

		try {
			ServiceReference[] inRefs = bContext.getServiceReferences(
					AlgorithmFactory.class.getName(), inFilter);
			ServiceReference[] outRefs = bContext.getServiceReferences(
					AlgorithmFactory.class.getName(), outFilter);

			if (inRefs != null && outRefs != null) {
				Set inFileTypeSet = new HashSet();
				for (int i = 0; i < inRefs.length; ++i) {
					inFileTypeSet.add(inRefs[i]
							.getProperty(AlgorithmProperty.IN_DATA));
				}
				Set outFileTypeSet = new HashSet();
				for (int i = 0; i < outRefs.length; ++i) {
					outFileTypeSet.add(outRefs[i]
							.getProperty(AlgorithmProperty.OUT_DATA));
				}

				Collection converterList = new HashSet();
				for (Iterator i = inFileTypeSet.iterator(); i.hasNext();) {
					String srcDataType = (String) i.next();
					for (Iterator j = outFileTypeSet.iterator(); j.hasNext();) {
						Converter converter = getConverter(	srcDataType,
															(String) j.next());
						if (converter != null) 
							converterList.add(converter);
					}
				}
				return (Converter[]) converterList.toArray(new Converter[0]);
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		return new Converter[0];
	}
    
    private Converter getConverter(String inType, String outType) {
		Vertex srcVertex = (Vertex)dataTypeToVertex.get(inType);
		Vertex tgtVertex = (Vertex)dataTypeToVertex.get(outType);
		
		if (srcVertex != null && tgtVertex != null) {
			DijkstraShortestPath shortestPathAlg = new DijkstraShortestPath(graph);
			List edgeList = shortestPathAlg.getPath(srcVertex, tgtVertex);
			
			if (edgeList.size() > 0) {
				ServiceReference[] serviceReferenceArray = new ServiceReference[edgeList
						.size()];
				for (int i = 0; i < serviceReferenceArray.length; ++i) {
					Edge edge = (Edge) edgeList.get(i);
					AbstractList converterList = (AbstractList) edge
							.getUserDatum(SERVICE_LIST);
					serviceReferenceArray[i] = (ServiceReference) converterList
							.get(0);
				}
				return new ConverterImpl(bContext, ciContext,	serviceReferenceArray);
			}
		}
		return null;
    }
    
    /**
     * @see org.cishell.service.conversion.DataConversionService#findConverters(java.lang.String, java.lang.String, int, java.lang.String)
     */
    public Converter[] findConverters(String inFormat, String outFormat,
            int maxHops, String maxComplexity) {
        return findConverters(inFormat, outFormat);
    }

    /**
     * @see org.cishell.service.conversion.DataConversionService#findConverters(org.cishell.framework.data.Data, java.lang.String)
     */
    public Converter[] findConverters(Data data, String outFormat) {
        if (data == null) {
            if (NULL_DATA.equalsIgnoreCase(""+outFormat)) {
                return new Converter[]{new ConverterImpl(bContext, ciContext, new ServiceReference[0])};
            } else {
                return new Converter[0];
            }
        }
        
        String format = data.getFormat();
        
        Set set = new HashSet();
        Converter[] converters = new Converter[0];
        if (format != null) {
            converters = findConverters(format, outFormat);
            set.addAll(new HashSet(Arrays.asList(converters)));
        } 
        
        if (!(data.getData() instanceof File) && data.getData() != null) {            
            Iterator iter = getClassesFor(data.getData().getClass()).iterator();
            while (iter.hasNext()) {
                Class c = (Class) iter.next();
                converters = findConverters(c.getName(), outFormat);
                set.addAll(new HashSet(Arrays.asList(converters)));
            }
        }
                
        return (Converter[]) set.toArray(new Converter[0]);
    }
    
    protected Collection getClassesFor(Class clazz) {
        Set classes = new HashSet();
        
        Class[] c = clazz.getInterfaces();
        for (int i=0; i < c.length; i++) {
            classes.addAll(getClassesFor(c[i]));
        }
        
        Class superC = clazz.getSuperclass();
        
        if (superC != Object.class) {
            if (superC != null)
                classes.addAll(getClassesFor(superC));
        } else {
            classes.add(superC);
        }
        
        classes.add(clazz);
        
        return classes;
    }
    
    /**
     * @see org.cishell.service.conversion.DataConversionService#convert(org.cishell.framework.data.Data, java.lang.String)
     */
    public Data convert(Data inDM, String outFormat) {
        String inFormat = inDM.getFormat();
        
        if (inFormat != null && inFormat.equals(outFormat)) {
            return inDM;
        }

        Converter[] converters = findConverters(inDM, outFormat);
        if (converters.length > 0) {
            inDM = converters[0].convert(inDM);
        }
        
        return inDM;
    }
    
    
	public void serviceChanged(ServiceEvent event) {
		ServiceReference inServiceRef = event.getServiceReference();
		
		String inDataType = (String)inServiceRef.getProperty(AlgorithmProperty.IN_DATA);
		String outDataType = (String)inServiceRef.getProperty(AlgorithmProperty.OUT_DATA);

		if (event.getType() ==  ServiceEvent.MODIFIED) {
			removeServiceReference(inDataType, outDataType, inServiceRef);
			addServiceReference(inDataType, outDataType, inServiceRef);
		}
		else if(event.getType() == ServiceEvent.REGISTERED) {
			addServiceReference(inDataType, outDataType, inServiceRef);
		}
		else if(event.getType() == ServiceEvent.UNREGISTERING) {
			removeServiceReference(inDataType, outDataType, inServiceRef);			
		}
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
	}
	
	private void addServiceReference(String srcDataType, String tgtDataType, ServiceReference serviceReference) {
		if (srcDataType != null && srcDataType.length() > 0
				&& tgtDataType != null && tgtDataType.length() > 0) {
			Vertex srcVertex = getVertex(srcDataType);
			Vertex tgtVertex = getVertex(tgtDataType);

			removeServiceReference(srcDataType, tgtDataType, serviceReference);
			
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
	}
	
	private Vertex getVertex(String dataType) {
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

	private void saveGraph() {
		GraphMLFile writer = new GraphMLFile();
		Graph g = (Graph)graph.copy();
		for (Iterator i = g.getEdges().iterator(); i.hasNext();) {
			Edge e = (Edge)i.next();
			e.removeUserDatum(SERVICE_LIST);
		}
		writer.save(g, System.getProperty("user.home") + File.separator + "convertGraph.xml");

	}
}

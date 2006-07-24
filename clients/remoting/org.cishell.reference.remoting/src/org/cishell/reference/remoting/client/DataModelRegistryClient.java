/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 6, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.remoting.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.datamodel.BasicDataModel;
import org.cishell.framework.datamodel.DataModel;
import org.cishell.reference.remoting.RemotingClient;
import org.cishell.remoting.service.conversion.RemoteDataConversionService;
import org.cishell.remoting.service.framework.DataModelRegistry;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class DataModelRegistryClient extends RemotingClient implements
        DataModelRegistry {
    protected CIShellContext ciContext;
    protected RemoteDataConversionService remoteConverter;
    protected Map idToDMMap;

    public DataModelRegistryClient(CIShellContext ciContext, RemoteDataConversionService remoteConverter) {
        super("/soap/services/DataModelRegistry");
        this.ciContext = ciContext;
        this.remoteConverter = remoteConverter;
        this.idToDMMap = new HashMap();
    }
    
    /**
     * @see org.cishell.remoting.service.framework.DataModelRegistry#getDataModel(String)
     */
    public DataModel getDataModel(String dataModelID) {
        DataModel dm = (DataModel) idToDMMap.get(dataModelID);
        
        if (dm == null) {
            dm = new RemoteDataModel(dataModelID);
            idToDMMap.put(dataModelID, dm);
        } 
        
        return dm;
    }
    
    /**
     * @see org.cishell.remoting.service.framework.DataModelRegistry#getDataModels(Vector)
     */
    public DataModel[] getDataModels(Vector dataModelIDs) {
        DataModel[] dm = null;
        if (dataModelIDs != null) {
            dm = new DataModel[dataModelIDs.size()];
            for (int i=0; i < dm.length; i++) {
                dm[i] = getDataModel((String) dataModelIDs.get(i));
            }
        }
        
        return dm;
    }
    
    /**
     * @see org.cishell.remoting.service.framework.DataModelRegistry#createDataModel(java.util.Hashtable, java.lang.String, byte[])
     */
    public String createDataModel(Hashtable properties, String format, byte[] data) {
        if (format != null) {
            Object[] parms = new Object[] {properties, format, data};
    
            return (String) doCall("createDataModel", parms);
        } else {
            return "-1";
        }
    }

    /**
     * @see org.cishell.remoting.service.framework.DataModelRegistry#getData(String, String)
     */
    public byte[] getData(String dataModelID, String format) {
        return (byte[]) doCall("getData", new Object[]{dataModelID, format});
    }

    /**
     * @see org.cishell.remoting.service.framework.DataModelRegistry#getDataFormats(String)
     */
    public Vector getDataFormats(String dataModelID) {
        return (Vector) doCall("getDataFormats", dataModelID);
    }

    /**
     * @see org.cishell.remoting.service.framework.DataModelRegistry#getProperties(String)
     */
    public Hashtable getProperties(String dataModelID) {
        return (Hashtable) doCall("getProperties", dataModelID);
    }

    /**
     * @see org.cishell.remoting.service.framework.DataModelRegistry#registerDataModel(org.cishell.framework.datamodel.DataModel)
     */
    public String registerDataModel(DataModel dm) {
        String id = "-1";
        if (dm instanceof RemoteDataModel 
                && ((RemoteDataModel) dm).host.equals(host)) {
            id = ((RemoteDataModel) dm).dmID;
        } else {
            Hashtable properties = null;
            if (dm.getMetaData() != null) {
                properties = new Hashtable(dm.getMetaData().size());
                for (Enumeration i = dm.getMetaData().keys(); i.hasMoreElements(); ) {
                    Object key = i.nextElement();
                    properties.put(key, dm.getMetaData().get(key).toString());
                }
            } else {
                properties = new Hashtable();
            }
            
            
            //find file-friendly format to convert to
            String format = dm.getFormat();
            String finalOutFormat = null;
            
            if (format == null || !format.startsWith("file:")) {
                DataConversionService converter = (DataConversionService) 
                    ciContext.getService(DataConversionService.class.getName());
                
                Converter[] converters = converter.findConverters(dm, "file:*");
                
                if (converters.length > 0) {
                    //see if the server has a converter to convert from a file back
                    //to the data model's original format
                    
                    Set inFormats = new HashSet();
                    Set outFormats = new HashSet();
                    
                    for (int i=0; i < converters.length; i++) {
                        inFormats.add(converters[i].getProperties().get(AlgorithmProperty.IN_DATA));
                        outFormats.add(converters[i].getProperties().get(AlgorithmProperty.OUT_DATA));
                    }
                    
                    Vector inF = new Vector(inFormats);
                    Vector outF = new Vector(outFormats);
                    
                    //give them the format that will come in (the out format on the client)
                    //and what it will need to convert back to on the other side (the in 
                    //format on the client)
                    Vector conversion = remoteConverter.findConverter(outF, inF);
                    
                    if (conversion != null && conversion.size() > 0) {
                        format = (String)conversion.get(0);
                        finalOutFormat = (String)conversion.get(1);
                        
                        dm = converter.convert(dm, format);
                    }
                } else {
                    dm = null;
                }
            }
            
            
            //convert File to byte[]
            byte[] data = null;
            if (dm != null && dm.getData() instanceof File) {
                File file = (File) dm.getData();
                if (file.exists()) {
                    try {
                        FileInputStream in = new FileInputStream(file);
                        byte[] inData = new byte[(int)file.length()];
                        
                        int offset = 0;
                        int numRead = 0;
                        
                        while (offset < inData.length && numRead >=0) {
                            numRead = in.read(inData, offset, inData.length);
                            offset += numRead;
                        }
                        
                        data = inData;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    
                    id = createDataModel(properties, format, data);
                    
                    if (finalOutFormat != null) {
                        id = remoteConverter.convert(id, finalOutFormat);
                    }
                }
            }
        }

        return id;
    }

    /**
     * @see org.cishell.remoting.service.framework.DataModelRegistry#registerDataModels(org.cishell.framework.datamodel.DataModel[])
     */
    public Vector registerDataModels(DataModel[] dataModel) {
        Vector dmIDs = null;
        if (dataModel != null) {
            dmIDs = new Vector(dataModel.length);
            for (int i=0; i < dataModel.length; i++) {
                dmIDs.add(registerDataModel(dataModel[i]));
            }
        }
        
        return dmIDs;
    }

    /**
     * @see org.cishell.remoting.service.framework.DataModelRegistry#unregisterDataModel(String)
     */
    public void unregisterDataModel(String dataModelID) {
        //probably shouldn't unregister data models since other clients could 
        //be using the same data models...
        idToDMMap.remove(dataModelID);
        
        doCall("unregisterDataModel", dataModelID);
    }
    
    protected class RemoteDataModel implements DataModel{
        String dmID;
        String host = DataModelRegistryClient.this.host;
        DataModelRegistry reg = DataModelRegistryClient.this;
        
        Object data;
        Dictionary properties;
        boolean gotData;
        
        public RemoteDataModel(String dmID) {
            this.dmID = dmID;
            gotData = false;
        }

        public Object getData() {
            String format = getFormat();
            
            if (!gotData && format != null) {                
                DataConversionService converter = (DataConversionService)
                    ciContext.getService(DataConversionService.class.getName());
                
                Converter[] convert = new Converter[0];
                
                Vector conversions = remoteConverter.getConversions(dmID, "file:*");
                for (int i=0; i < conversions.size(); i++) {
                    String outFormat = (String) conversions.get(i);
                    
                    convert = converter.findConverters(outFormat, format);
                    if (convert.length > 0) {
                        break;
                    }
                }
                
                if (convert.length > 0) {
                    String inFormat = (String)convert[0].getProperties().get(AlgorithmProperty.IN_DATA);
                    
                    try {
                        File file = File.createTempFile("dataModel-", "tmp");
                        byte[] raw = reg.getData(dmID, inFormat);
                        
                        if (raw != null) {
                            FileOutputStream out = new FileOutputStream(file);
                            out.write(raw);
                            out.close();
                            
                            data = file;
                            
                            Dictionary props = new Hashtable();
                            
                            DataModel[] dm = new DataModel[] {
                                    new BasicDataModel(props, data, inFormat)
                            };
                            
                            AlgorithmFactory factory = convert[0].getAlgorithmFactory();
                            Algorithm alg = factory.createAlgorithm(dm, new Hashtable(), ciContext);
                            dm = alg.execute();
                            
                            if (dm != null) {
                                data = dm[0].getData();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            
            return data;
        }

        public Dictionary getMetaData() {
            if (properties == null) {
                properties = reg.getProperties(dmID);
            }
            
            return properties;
        }
        
        public String getFormat() {
            return (String)reg.getDataFormats(dmID).get(0);
        }
        
        protected void finalize() {
            reg.unregisterDataModel(dmID);
        }
    }
}

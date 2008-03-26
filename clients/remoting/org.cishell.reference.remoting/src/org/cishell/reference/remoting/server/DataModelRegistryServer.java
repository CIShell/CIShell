/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 4, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.remoting.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.reference.remoting.ObjectRegistry;
import org.cishell.remoting.service.framework.DataModelRegistry;
import org.cishell.service.conversion.ConversionException;
import org.cishell.service.conversion.DataConversionService;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class DataModelRegistryServer implements DataModelRegistry {
    private ObjectRegistry registry;
    private CIShellContext ciContext;
    
    public DataModelRegistryServer(BundleContext bContext, CIShellContext ciContext) {
        this.ciContext = ciContext;
        registry = new ObjectRegistry();
    }

    /**
     * @see org.cishell.remoting.service.framework.DataModelRegistry#createDataModel(Hashtable, java.lang.String, byte[])
     */
    public String createDataModel(Hashtable properties, String format, byte[] data) {
        File dataFile = null;
        if (data != null) {
            try {
                File file = File.createTempFile("dataModel-", "tmp");
                
                FileOutputStream out = new FileOutputStream(file);
                out.write(data);
                out.close();
                
                dataFile = file;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        if (properties == null) {
            properties = new Hashtable();
        }
        
        Data dm = new BasicData(properties, dataFile, format);
        
        return registerDataModel(dm);
    }

    /**
     * @see org.cishell.remoting.service.framework.DataModelRegistry#getData(String, String)
     */
    public byte[] getData(String dataModelID, String format) {
        DataConversionService converter = (DataConversionService) 
            ciContext.getService(DataConversionService.class.getName());
        
        Data dm = getDataModel(dataModelID);
        try {
			dm = converter.convert(dm, format);
		} catch (ConversionException e1) {
			dm = null;
		}
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
            }
        }
        
        return data;
    }

    /**
     * @see org.cishell.remoting.service.framework.DataModelRegistry#getDataFormats(String)
     */
    public Vector getDataFormats(String dataModelID) {
        Data dm = getDataModel(dataModelID);
        
        String format = dm.getFormat();
        Vector v = new Vector();
        
        if (format != null){
            v.add(format);
        }
        
        //get implicit types from the java object
        if (dm.getData() != null && !(dm.getData() instanceof File)) {
            Class[] classes = dm.getData().getClass().getClasses();
            for (int i=0; i < classes.length; i++) {
                v.add(classes[i].getName());
            }
            v.add(dm.getData().getClass().getName());
        }
        
        return v;
    }

    /**
     * @see org.cishell.remoting.service.framework.DataModelRegistry#getProperties(String)
     */
    public Hashtable getProperties(String dataModelID) {
        return (Hashtable) getDataModel(dataModelID).getMetadata();
    }
    
    /**
     * @see org.cishell.remoting.service.framework.DataModelRegistry#getDataModel(String)
     */
    public Data getDataModel(String dataModelID) {
        Data dm = (Data) registry.getObject(dataModelID);
        
        return dm == null ? NULL_DM : dm;
    }

    /**
     * @see org.cishell.remoting.service.framework.DataModelRegistry#getDataModels(Vector)
     */
    public Data[] getDataModels(Vector dataModelIDs) {
        Data[] dm = null;
        
        if (dataModelIDs != null) {
            dm = new Data[dataModelIDs.size()];
            
            for (int i=0; i < dm.length; i++) {
                dm[i] = getDataModel((String)dataModelIDs.get(i));
            }
        }
        
        return dm;
    }

    /**
     * @see org.cishell.remoting.service.framework.DataModelRegistry#registerDataModel(org.cishell.framework.data.Data)
     */
    public String registerDataModel(Data dataModel) {
        if (dataModel != NULL_DM) {
            return registry.register(dataModel);
        } else {
            return "-1";
        }
    }
    
    /**
     * @see org.cishell.remoting.service.framework.DataModelRegistry#registerDataModels(org.cishell.framework.data.Data[])
     */
    public Vector registerDataModels(Data[] dataModel) {
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
        registry.unregister(dataModelID);
    }
    
    private static final Data NULL_DM = new Data() {
        public Object getData() {
            return null;
        }

        public Dictionary getMetadata() {
            return new Hashtable();
        }

        public String getFormat() {
            return "";
        }};
}

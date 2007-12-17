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

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.cishell.framework.CIShellContext;
import org.cishell.reference.remoting.ObjectRegistry;
import org.cishell.remoting.service.framework.AttributeDefinitionRegistry;
import org.cishell.remoting.service.framework.ObjectClassDefinitionRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;
import org.osgi.util.tracker.ServiceTracker;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class ObjectClassDefinitionRegistryServer implements
        ObjectClassDefinitionRegistry {        
    private ObjectRegistry registry;
    private ServiceTracker attrReg;

    public ObjectClassDefinitionRegistryServer(BundleContext bContext, CIShellContext ciContext) {
        registry = new ObjectRegistry();
        attrReg = new ServiceTracker(bContext, AttributeDefinitionRegistry.class.getName(), null);
        
        attrReg.open();
    }
    
    
    public Vector getAttributeDefinitions(String ocdID, Integer filter) {
        return getAttributeDefinitions(ocdID, filter.intValue());
    }
    
    /**
     * @see org.cishell.remoting.service.framework.ObjectClassDefinitionRegistry#getAttributeDefinitions(String, int)
     */
    public Vector getAttributeDefinitions(String ocdID, int filter) {
        AttributeDefinition[] attrs = getObjectClassDefinition(ocdID).getAttributeDefinitions(filter);
        Vector attrIDs = null;
        AttributeDefinitionRegistry attrRegistry = 
            (AttributeDefinitionRegistry) attrReg.getService();
        
        if (attrs != null && attrReg != null) {
            attrIDs = new Vector(attrs.length);
            
            for (int i=0; i < attrs.length; i++) {
                attrIDs.add(attrRegistry.registerAttributeDefinition(attrs[i]));
            }
        }
        
        return attrIDs;
    }

    /**
     * @see org.cishell.remoting.service.framework.ObjectClassDefinitionRegistry#getDescription(String)
     */
    public String getDescription(String ocdID) {
        return getObjectClassDefinition(ocdID).getDescription();
    }

    /**
     * @see org.cishell.remoting.service.framework.ObjectClassDefinitionRegistry#getID(String)
     */
    public String getID(String ocdID) {
        return getObjectClassDefinition(ocdID).getID();
    }

    /**
     * @see org.cishell.remoting.service.framework.ObjectClassDefinitionRegistry#getName(String)
     */
    public String getName(String ocdID) {
        return getObjectClassDefinition(ocdID).getName();
    }

    /**
     * @see org.cishell.remoting.service.framework.ObjectClassDefinitionRegistry#getObjectClassDefinition(String)
     */
    public ObjectClassDefinition getObjectClassDefinition(String ocdID) {
        ObjectClassDefinition ocd = (ObjectClassDefinition) registry.getObject(ocdID);
        return ocd == null ? NULL_OCD : ocd;
    }

    /**
     * @see org.cishell.remoting.service.framework.ObjectClassDefinitionRegistry#registerObjectClassDefinition(org.osgi.service.metatype.ObjectClassDefinition)
     */
    public String registerObjectClassDefinition(ObjectClassDefinition ocd) {
        return registry.register(ocd);
    }

    /**
     * @see org.cishell.remoting.service.framework.ObjectClassDefinitionRegistry#unregisterObjectClassDefinition(String)
     */
    public void unregisterObjectClassDefinition(String ocdID) {
        registry.unregister(ocdID);
    }
    
    private static final ObjectClassDefinition NULL_OCD = new ObjectClassDefinition() {
        public AttributeDefinition[] getAttributeDefinitions(int arg0) {
            return null;
        }
        public String getDescription() { return null; }
        public String getID() { return null; }
        public InputStream getIcon(int arg0) throws IOException {
            return null;
        }
        public String getName() { return null; }};
}

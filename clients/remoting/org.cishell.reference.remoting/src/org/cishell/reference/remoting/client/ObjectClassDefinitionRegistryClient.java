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

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.cishell.reference.remoting.RemotingClient;
import org.cishell.remoting.service.framework.AttributeDefinitionRegistry;
import org.cishell.remoting.service.framework.ObjectClassDefinitionRegistry;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class ObjectClassDefinitionRegistryClient extends RemotingClient
        implements ObjectClassDefinitionRegistry {
    protected AttributeDefinitionRegistry attrReg;
    
    public ObjectClassDefinitionRegistryClient(AttributeDefinitionRegistry attrReg) {
        super("/soap/services/ObjectClassDefinitionRegistry");
        this.attrReg = attrReg;
        
        setCacheing("getDescription", true);
        setCacheing("getID", true);
        setCacheing("getName", true);
        setCacheing("getAttributeDefinitions", false);
    }
    
    /**
     * @see org.cishell.remoting.service.framework.ObjectClassDefinitionRegistry#getObjectClassDefinition(String)
     */
    public ObjectClassDefinition getObjectClassDefinition(String ocdID) {
        return new RemoteObjectClassDefinition(ocdID);
    }

    /**
     * @see org.cishell.remoting.service.framework.ObjectClassDefinitionRegistry#getAttributeDefinitions(String, int)
     */
    public Vector getAttributeDefinitions(String ocdID, int filter) {
        Object[] params = new Object[] {ocdID, new Integer(filter)};
        
        return (Vector) doCall("getAttributeDefinitions", params);
    }

    /**
     * @see org.cishell.remoting.service.framework.ObjectClassDefinitionRegistry#getDescription(String)
     */
    public String getDescription(String ocdID) {
        return "" + doCall("getDescription", ocdID);
    }

    /**
     * @see org.cishell.remoting.service.framework.ObjectClassDefinitionRegistry#getID(String)
     */
    public String getID(String ocdID) {
        return "" + doCall("getID", ocdID);
    }

    /**
     * @see org.cishell.remoting.service.framework.ObjectClassDefinitionRegistry#getName(String)
     */
    public String getName(String ocdID) {
        return "" + doCall("getName", ocdID);
    }

    /**
     * @see org.cishell.remoting.service.framework.ObjectClassDefinitionRegistry#registerObjectClassDefinition(org.osgi.service.metatype.ObjectClassDefinition)
     */
    public String registerObjectClassDefinition(ObjectClassDefinition ocd) {
        return "-1";
    }

    /**
     * @see org.cishell.remoting.service.framework.ObjectClassDefinitionRegistry#unregisterObjectClassDefinition(String)
     */
    public void unregisterObjectClassDefinition(String ocdID) {
        doCall("unregisterObjectClassDefinition", ocdID);
    }

    protected class RemoteObjectClassDefinition implements ObjectClassDefinition {
        String ocdID;
        ObjectClassDefinitionRegistry ocdReg = ObjectClassDefinitionRegistryClient.this;
        
        public RemoteObjectClassDefinition(String ocdID) {
            this.ocdID = ocdID;
        }

        public AttributeDefinition[] getAttributeDefinitions(int filter) {
            Vector attrs = ocdReg.getAttributeDefinitions(ocdID, filter);
            
            AttributeDefinition[] defs = null;
            if (attrs != null) {
                defs = new AttributeDefinition[attrs.size()];
                for (int i=0; i < defs.length; i++) {
                    defs[i] = attrReg.getAttributeDefinition((String)attrs.get(i));
                }
            }
            
            return defs;
        }

        public String getDescription() {
            return ocdReg.getDescription(ocdID);
        }

        public String getID() {
            return ocdReg.getID(ocdID);
        }

        public InputStream getIcon(int size) throws IOException {
            return null;
        }

        public String getName() {
            return ocdReg.getName(ocdID);
        }
        
        protected void finalize() {
            ocdReg.unregisterObjectClassDefinition(ocdID);
        }
    }
}

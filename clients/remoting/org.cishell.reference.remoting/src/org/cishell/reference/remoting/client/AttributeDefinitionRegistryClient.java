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

import java.util.Vector;

import org.cishell.reference.remoting.RemotingClient;
import org.cishell.remoting.service.framework.AttributeDefinitionRegistry;
import org.osgi.service.metatype.AttributeDefinition;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class AttributeDefinitionRegistryClient extends RemotingClient implements
        AttributeDefinitionRegistry {

    public AttributeDefinitionRegistryClient() {
        super("/soap/services/AttributeDefinitionRegistry");
        
        setCacheing("getCardinality", false);
        setCacheing("getDefaultValue", false);
        setCacheing("getDescription", false);
        setCacheing("getID", false);
        setCacheing("getName", false);
        setCacheing("getOptionLabels", false);
        setCacheing("getOptionValues", false);
        setCacheing("getType", false);
        setCacheing("validate", true);
    }
    
    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#getAttributeDefinition(String)
     */
    public AttributeDefinition getAttributeDefinition(String attrID) {
        return new RemoteAttributeDefinition(attrID);
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#getCardinality(String)
     */
    public int getCardinality(String attrID) {
        return ((Integer) doCall("getCardinality", attrID)).intValue();
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#getDefaultValue(String)
     */
    public Vector getDefaultValue(String attrID) {
        return (Vector) doCall("getDefaultValue", attrID);
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#getDescription(String)
     */
    public String getDescription(String attrID) {
        return "" + doCall("getDescription", attrID);
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#getID(String)
     */
    public String getID(String attrID) {
        return "" + doCall("getID", attrID);
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#getName(String)
     */
    public String getName(String attrID) {
        return "" + doCall("getName", attrID);
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#getOptionLabels(String)
     */
    public Vector getOptionLabels(String attrID) {
        return (Vector) doCall("getOptionLabels", attrID);
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#getOptionValues(String)
     */
    public Vector getOptionValues(String attrID) {
        return (Vector) doCall("getOptionValues", attrID);
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#getType(String)
     */
    public int getType(String attrID) {
        return ((Integer) doCall("getType", attrID)).intValue();
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#registerAttributeDefinition(org.osgi.service.metatype.AttributeDefinition)
     */
    public String registerAttributeDefinition(AttributeDefinition attr) {
        return "-1";
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#unregisterAttributeDefinition(String)
     */
    public void unregisterAttributeDefinition(String attrID) {
        doCall("unregisterAttributeDefinition", attrID);
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#validate(String, java.lang.String)
     */
    public String validate(String attrID, String value) {
        return (String) doCall("validate", new Object[]{attrID, value});
    }
    
    protected class RemoteAttributeDefinition implements AttributeDefinition {
        String attrID;
        AttributeDefinitionRegistry reg = AttributeDefinitionRegistryClient.this;
        
        public RemoteAttributeDefinition(String attrID) {
            this.attrID = attrID;
        }

        public int getCardinality() {
            return reg.getCardinality(attrID);
        }

        public String[] getDefaultValue() {
            return toStringArray(reg.getDefaultValue(attrID));
        }

        public String getDescription() {
            return reg.getDescription(attrID);
        }

        public String getID() {
            return reg.getID(attrID);
        }

        public String getName() {
            return reg.getName(attrID);
        }

        public String[] getOptionLabels() {
            return toStringArray(reg.getOptionLabels(attrID));
        }

        public String[] getOptionValues() {
            return toStringArray(reg.getOptionValues(attrID));
        }

        public int getType() {
            return reg.getType(attrID);
        }

        public String validate(String value) {
            return reg.validate(attrID, value);
        }
        
        protected void finalize() {
            reg.unregisterAttributeDefinition(attrID);
        }
    }
}

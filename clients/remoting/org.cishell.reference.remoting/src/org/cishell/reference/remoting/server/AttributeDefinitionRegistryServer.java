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

import java.util.Arrays;
import java.util.Vector;

import org.cishell.framework.CIShellContext;
import org.cishell.reference.remoting.ObjectRegistry;
import org.cishell.remoting.service.framework.AttributeDefinitionRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.service.metatype.AttributeDefinition;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class AttributeDefinitionRegistryServer implements
        AttributeDefinitionRegistry {
    private ObjectRegistry registry;
    
    public AttributeDefinitionRegistryServer(BundleContext bContext, CIShellContext ciContext) {
        registry = new ObjectRegistry();
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#getCardinality(String)
     */
    public int getCardinality(String attrID) {
        return getAttributeDefinition(attrID).getCardinality();
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#getDefaultValue(String)
     */
    public Vector getDefaultValue(String attrID) {
        return toVector(getAttributeDefinition(attrID).getDefaultValue());
    }
    
    private Vector toVector(String[] str) {
        Vector v = null;
        if (str != null) {
            v = new Vector(Arrays.asList(str));
        }
        return v;
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#getDescription(String)
     */
    public String getDescription(String attrID) {
        return getAttributeDefinition(attrID).getDescription();
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#getID(String)
     */
    public String getID(String attrID) {
        return getAttributeDefinition(attrID).getID();
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#getName(String)
     */
    public String getName(String attrID) {
        return getAttributeDefinition(attrID).getName();
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#getOptionLabels(String)
     */
    public Vector getOptionLabels(String attrID) {
        return toVector(getAttributeDefinition(attrID).getOptionLabels());
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#getOptionValues(String)
     */
    public Vector getOptionValues(String attrID) {
        return toVector(getAttributeDefinition(attrID).getOptionValues());
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#getType(String)
     */
    public int getType(String attrID) {
        return getAttributeDefinition(attrID).getType();
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#validate(String, java.lang.String)
     */
    public String validate(String attrID, String value) {
        return getAttributeDefinition(attrID).validate(value);
    }
    
    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#getAttributeDefinition(String)
     */
    public AttributeDefinition getAttributeDefinition(String attrID) {
        AttributeDefinition attr = (AttributeDefinition) registry.getObject(attrID);
        
        return attr == null ? NULL_ATTR : attr;
    }
    
    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#registerAttributeDefinition(org.osgi.service.metatype.AttributeDefinition)
     */
    public String registerAttributeDefinition(AttributeDefinition attr) {
        return registry.register(attr);
    }

    /**
     * @see org.cishell.remoting.service.framework.AttributeDefinitionRegistry#unregisterAttributeDefinition(String)
     */
    public void unregisterAttributeDefinition(String attrID) {
        registry.unregister(attrID);
    }

    private static final AttributeDefinition NULL_ATTR = new AttributeDefinition() {
        public int getCardinality() {
            return -1;
        }

        public String[] getDefaultValue() {
            return null;
        }

        public String getDescription() {
            return null;
        }

        public String getID() {
            return null;
        }

        public String getName() {
            return null;
        }

        public String[] getOptionLabels() {
            return null;
        }

        public String[] getOptionValues() {
            return null;
        }

        public int getType() {
            return -1;
        }

        public String validate(String arg0) {
            return null;
        }};
}

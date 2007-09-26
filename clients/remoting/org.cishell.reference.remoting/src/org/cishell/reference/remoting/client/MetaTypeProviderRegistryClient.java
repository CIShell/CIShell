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
import org.cishell.remoting.service.framework.MetaTypeProviderRegistry;
import org.cishell.remoting.service.framework.ObjectClassDefinitionRegistry;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class MetaTypeProviderRegistryClient extends RemotingClient implements
        MetaTypeProviderRegistry {
    protected ObjectClassDefinitionRegistry ocdReg;

    public MetaTypeProviderRegistryClient(ObjectClassDefinitionRegistry ocdReg) {
        super("/soap/services/MetaTypeProviderRegistry");
        this.ocdReg = ocdReg;
        
        setCacheing("getLocales", false);
        setCacheing("getObjectClassDefinition", false);
    }
    
    /**
     * @see org.cishell.remoting.service.framework.MetaTypeProviderRegistry#getMetaTypeProvider(String)
     */
    public MetaTypeProvider getMetaTypeProvider(String providerID) {
        return new RemoteMetaTypeProvider(providerID);
    }
    
    /**
     * @see org.cishell.remoting.service.framework.MetaTypeProviderRegistry#getLocales(String)
     */
    public Vector getLocales(String providerID) {
        return (Vector) doCall("getLocales", providerID);
    }

    /**
     * @see org.cishell.remoting.service.framework.MetaTypeProviderRegistry#getObjectClassDefinition(String, java.lang.String, java.lang.String)
     */
    public String getObjectClassDefinition(String providerID, String id,
            String locale) {
        Object[] parms = new Object[]{ providerID, id, locale };
        Object r = doCall("getObjectClassDefinition", parms);
        
        return "" + r;
    }

    /**
     * @see org.cishell.remoting.service.framework.MetaTypeProviderRegistry#registerMetaTypeProvider(org.osgi.service.metatype.MetaTypeProvider)
     */
    public String registerMetaTypeProvider(MetaTypeProvider provider) {
        return "-1";
    }

    /**
     * @see org.cishell.remoting.service.framework.MetaTypeProviderRegistry#unregisterMetaTypeProvider(String)
     */
    public void unregisterMetaTypeProvider(String providerID) {
        doCall("unregisterMetaTypeProvider", providerID);
    }

    protected class RemoteMetaTypeProvider implements MetaTypeProvider {
        String providerID;
        MetaTypeProviderRegistry reg = MetaTypeProviderRegistryClient.this;
        
        public RemoteMetaTypeProvider(String providerID) {
            this.providerID = providerID;
        }

        public String[] getLocales() {
            return toStringArray(reg.getLocales(providerID));
        }

        public ObjectClassDefinition getObjectClassDefinition(String id, String locale) {
            String ocdID = reg.getObjectClassDefinition(providerID, id, locale);
            
            return ocdReg.getObjectClassDefinition(ocdID);
        }
        
        protected void finalize() {
            reg.unregisterMetaTypeProvider(providerID);
        }
    }
}

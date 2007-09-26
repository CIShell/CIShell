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
package org.cishell.reference.remoting;

import java.util.HashMap;
import java.util.Map;


public class ObjectRegistry {
    long lastID;
    Map idToObjectMap;
    Map objectToIDMap;
    String prefix;
    
    public ObjectRegistry() {
        this("");
    }
    
    public ObjectRegistry(String prefix) {
        this.prefix = prefix;
        lastID = 0;
        idToObjectMap = new HashMap();
        objectToIDMap = new HashMap();
    }
    
    protected synchronized String newID() {
        if (lastID == Long.MAX_VALUE) {
            lastID = 0;
        }
        
        lastID++;
        return prefix+lastID;
    }
    
    public String register(Object o) {
        String id = (String) objectToIDMap.get(o); 
        
        if (id == null) {
            id = newID();
            idToObjectMap.put(id, o);
            objectToIDMap.put(o, id);
        }
        
        return id;
    }
    
    public void unregister(String id) {
        objectToIDMap.remove(idToObjectMap.remove(id));
    }
    
    public Object getObject(String id) {
        return idToObjectMap.get(id);
    }
}
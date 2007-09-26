/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 5, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.remoting;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.knopflerfish.util.CacheMap;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.MarshalHashtable;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class RemotingClient {
    public static final String NULL_STR = "@@NULL@@";

    protected boolean bDebug = "true".equals(
                System.getProperty("org.cishell.remoting.debug", "false"));

    protected String host;
    protected String servicePath;
    protected Object callLock;
    protected String endpoint;
    protected HttpTransportSE httpTransport;
    protected SoapSerializationEnvelope soapEnvelope;
    protected Map caches;
    protected Map fastCache;
    protected Map slowCache;

    public RemotingClient(String servicePath) {
        this.servicePath = servicePath;
        callLock = new Object();
        caches = new HashMap();
        
        fastCache = new CacheMap(1000);
        slowCache = new CacheMap(10000);
    }
    
    protected void setCacheing(String command, boolean doFastCacheing) {
        if (doFastCacheing) {
            caches.put(command, fastCache);
        } else {
            caches.put(command, slowCache);
        }
    }

    public void open(String host) {
        this.host = host;
        endpoint = host + servicePath;

        httpTransport = new HttpTransportSE(endpoint);
        httpTransport.debug = bDebug;
        
        soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        new MarshalHashtable().register(soapEnvelope);
        new MarshalBase64().register(soapEnvelope);
    }

    public void close() {
    }

    protected Object doCall(String opName) {
        return doCall(opName, new Object[0]);
    }

    protected Object doCall(String opName, long val) {
        return doCall(opName, new Object[] { new Long(val) });
    }

    protected Object doCall(String opName, int val) {
        return doCall(opName, new Object[] { new Integer(val) });
    }

    protected Object doCall(String opName, Object val) {
        return doCall(opName, new Object[] { val });
    }

    protected synchronized Object doCall(String opName, Object[] params) {
        if (bDebug) {
            StackTraceElement trace = new Throwable().getStackTrace()[2];
            
            String clazz = trace.getClassName();
            clazz = clazz.substring(clazz.lastIndexOf('.')+1);
            int line = trace.getLineNumber();
            
            System.out.println("doCall " + opName + " ("+clazz+":"+line+")");
        }
        
        String cacheKey = null;
        Map cache = (Map) caches.get(opName);

        if (cache != null) {
            cacheKey = opName + ":" + toDisplay(params);
            Object cacheResult = cache.get(cacheKey);

            if (cacheResult != null) {
                if (bDebug) {
                    System.out.println("cached " + opName + "("
                            + toDisplay(params) + ")");
                }
                return cacheResult;
            }
        }
        try {
            soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
            new MarshalHashtable().register(soapEnvelope);
            new MarshalBase64().register(soapEnvelope);
            
            SoapObject rpc = new SoapObject(
                    "http://www.w3.org/2001/12/soap-envelope", opName);
            
            for (int i = 0; i < params.length; i++) {
                if (bDebug)
                    System.out.println("doCall   param " + i + " = "
                            + params[i]);
                rpc.addProperty("item" + i, params[i]);
            }
            soapEnvelope.setOutputSoapObject(rpc);
            
            if (bDebug) {
                System.out.println("doCall " + opName + "("
                        + toDisplay(params) + ")");
            }
            httpTransport.call(opName, soapEnvelope);
            Object r = soapEnvelope.getResponse();

            if (cache != null) {
                cache.put(cacheKey, r);
            }
            return r;
        } catch (Exception e) {
            throw new NestedRuntimeException("Failed to call " + opName
                    + ": ", e);
        }
    }

    protected long[] toLongArray(Object obj) {
        if (obj == null) {
            return null;
        }
        long[] la;
        if (obj instanceof SoapObject) {
            SoapObject so = (SoapObject) obj;
            la = new long[so.getPropertyCount()];
            for (int i = 0; i < la.length; i++) {
                la[i] = new Long(so.getProperty(i).toString()).longValue();
            }
        } else {
            la = new long[Array.getLength(obj)];
            for (int i = 0; i < la.length; i++) {
                la[i] = ((Long) Array.get(obj, i)).longValue();
            }
        }

        return la;
    }
    
    protected String[] toStringArray(Object obj) {
        if (obj == null) {
            return null;
        }
        String[] str;
        if (obj instanceof SoapObject) {
            SoapObject so = (SoapObject) obj;
            str = new String[so.getPropertyCount()];
            for (int i=0; i < str.length; i++) {
                str[i] = so.getProperty(i).toString();
            }
        } if (obj instanceof Vector) {
            Vector v = (Vector) obj;
            str = new String[v.size()];
            for (int i=0; i < str.length; i++) {
                str[i] = (String) v.get(i);
            }
        } else {
            str = new String[Array.getLength(obj)];
            for (int i=0; i < str.length; i++) {
                str[i] = Array.get(obj, i).toString();
            }
        }
        return str;
    }
    
    protected Hashtable toHashtable(Dictionary dict) {
        Hashtable ht = new Hashtable();

        //TODO: better hashtable parsing
        for (Enumeration i = dict.keys(); i.hasMoreElements() ;) {
            Object key = i.nextElement().toString();
            
            Object value = dict.get(key);
            
            if (value instanceof Vector) {
                
            } else if (value instanceof Dictionary) {
                value = toHashtable(dict);
            } else if (value instanceof String[]) {
                value = new Vector(Arrays.asList((String[])value));
            } else {
                value = "" + value;
            }
            
            ht.put(key, value);
        }
        
        return ht;
    }

    protected Object toDisplay(Object val) {
        if (val != null) {
            if (NULL_STR.equals(val)) {
                return "null";
            }
            if (val instanceof String) {
                return "\"" + val + "\"";
            }
            if (val.getClass().isArray()) {
                StringBuffer sb = new StringBuffer();
                sb.append("[");
                for (int i = 0; i < Array.getLength(val); i++) {
                    sb.append(toDisplay(Array.get(val, i)));
                    if (i < Array.getLength(val) - 1) {
                        sb.append(",");
                    }
                }
                sb.append("]");
                return sb.toString();
            }
        }

        return val;
    }
}

class NestedRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    Throwable nested;

    public NestedRuntimeException(String msg, Throwable t) {
        super(msg);
        this.nested = t;
    }

    public String getMessage() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.getMessage());

        if (nested != null) {
            StringWriter sw = new StringWriter();
            nested.printStackTrace(new PrintWriter(sw));
            sb.append(", Nested exception:\n" + sw.toString());
        }

        return sb.toString();
    }

    public String toString() {
        return getClass().toString() + ": " + getMessage();
    }
}

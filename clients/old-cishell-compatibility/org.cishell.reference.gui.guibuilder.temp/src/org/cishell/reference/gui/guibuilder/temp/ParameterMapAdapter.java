/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 16, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.gui.guibuilder.temp;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.common.parameter.ParameterMap;
import edu.iu.iv.common.parameter.ParameterProperty;
import edu.iu.iv.common.parameter.Validator;


public class ParameterMapAdapter extends ParameterMap implements ParameterProperty  {
    protected ObjectClassDefinition ocd;
    
    public ParameterMapAdapter(MetaTypeProvider provider, String id) {
       ObjectClassDefinition ocd = null;
       
       //String locale = Locale.getDefault().getDisplayName();
       //TODO: better locale matching
       
       String[] locales = provider.getLocales();
       for (int i=0; i < locales.length && ocd == null; i++) {
           
           try {
               ocd = provider.getObjectClassDefinition(id, locales[i]);
           } catch (IllegalArgumentException e) {
               //This will be thrown if the metatype provider 
               //doesn't have the correct ID
           }
           
           //if the pid doesn't work, try 'default.id' as a last resort
           if (ocd == null) {
               try {
                   ocd = provider.getObjectClassDefinition("default.id", locales[i]);
               } catch (IllegalArgumentException e) {
                   //same as above
               }
           }
       }
       
       if (ocd != null) {
           this.ocd = ocd;
           AttributeDefinition[] attr = 
               ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);
           
           if (attr != null) {
               for (int i=0; i < attr.length; i++) {
                   createParameter(attr[i]);
               }
           }
       }
    }
    
    protected void createParameter(AttributeDefinition attr) {
        int cardinality = attr.getCardinality();
        
        //TODO: support for arbitrary additions of types
        if (cardinality == Integer.MAX_VALUE) {
            cardinality = 5;
        } else if (cardinality == Integer.MIN_VALUE) {
            cardinality = -5;
        }
        
        String[] values = attr.getDefaultValue();
        
        if (values == null) {
            values = new String[]{};
        }
        
        if (cardinality == 0) {
            String value = null;
            if (values.length > 0) {
                value = values[0];
            }
            
            createParameter(attr, value, -1);
        } else {
            for (int i=0; i < cardinality; i++) {
                String value = null;
                if (i < values.length) {
                    value = values[i]; 
                }
                
                createParameter(attr, value, i);
            }
        }
    }
    
    protected void createParameter(final AttributeDefinition attr, String value, int index) {
        Parameter p = new Parameter();
        
        p.setPropertyValue(NAME, attr.getName());
        String desc = attr.getDescription();
        if (desc != null) p.setPropertyValue(DESCRIPTION, attr.getDescription());
        p.setPropertyValue(VALIDATOR, new Validator() {
            public boolean isValid(Object value) {
                String valid = attr.validate(value.toString());
                
                return valid == null || (valid != null && valid.length() == 0); 
            }});
        
                
        switch (attr.getType()) {
        case AttributeDefinition.BOOLEAN:
            p.setPropertyValue(INPUT_TYPE, InputType.BOOLEAN);
            if (value != null)
                p.setPropertyValue(DEFAULT_VALUE, Boolean.valueOf(value));
            break;
        case AttributeDefinition.CHARACTER:
            p.setPropertyValue(INPUT_TYPE, InputType.TEXT);
            if (value != null)
                p.setPropertyValue(DEFAULT_VALUE, value);
            //TODO: need to add a Character validator
            break;
        case AttributeDefinition.DOUBLE:
            p.setPropertyValue(INPUT_TYPE, InputType.DOUBLE);
            if (value != null)
                p.setPropertyValue(DEFAULT_VALUE, Double.valueOf(value));
            break;
        case AttributeDefinition.FLOAT:
            p.setPropertyValue(INPUT_TYPE, InputType.FLOAT);
            if (value != null)
                p.setPropertyValue(DEFAULT_VALUE, Float.valueOf(value));
            break;
        case AttributeDefinition.INTEGER:
            p.setPropertyValue(INPUT_TYPE, InputType.INTEGER);
            if (value != null)
                p.setPropertyValue(DEFAULT_VALUE, Integer.valueOf(value));
            break;
        case AttributeDefinition.LONG:
            //TODO: need to make a LONG validator...
            p.setPropertyValue(INPUT_TYPE, InputType.INTEGER);
            if (value != null)
                p.setPropertyValue(DEFAULT_VALUE, Integer.valueOf(value));
            break;
        case AttributeDefinition.SHORT:
            //TODO: need to make a SHORT validator...
            p.setPropertyValue(INPUT_TYPE, InputType.INTEGER);
            if (value != null)
                p.setPropertyValue(DEFAULT_VALUE, Integer.valueOf(value));
            break;
        case AttributeDefinition.STRING:
            p.setPropertyValue(INPUT_TYPE, InputType.TEXT);
            if (value != null)
                p.setPropertyValue(DEFAULT_VALUE, value);
            break;
        }
        
        //TODO: drop down lists...
        
        String id = attr.getID();
        if (index > 0) {
            id += "["+index+"]";
        }
        
        put(id, p);
    }
    
    public ObjectClassDefinition getObjectClassDefinition() {
        return ocd;
    }
    
    public Dictionary createDictionary() {
        Dictionary dict = new Hashtable();
        
        AttributeDefinition[] attr = new AttributeDefinition[0];
        
        if (ocd != null) {
            attr = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);
        }
        
        for (int i=0; i < attr.length; i++) {
            int cardinality = attr[i].getCardinality();
            Object value = null;
            
            if (cardinality == 0) {
                value = getValue(attr[i], get(attr[i].getID()).getValue());
            } else {
                int size = Math.abs(cardinality);
                if (size == Integer.MAX_VALUE) {
                    size = 5;
                }
                
                Vector v = new Vector(size);
                for (int j=0; j < v.size(); j++) {
                    Parameter p = get(attr[i].getID() + "[" + j + "]");
                    v.set(j, getValue(attr[i], p.getValue()));
                }
                value = v;
                
                //cardinality > 0 means to return an array, not a vector
                if (cardinality > 0) {
                    switch (attr[i].getType()) {
                    case AttributeDefinition.BOOLEAN:
                        value = v.toArray(new Boolean[]{});
                        break;
                    case AttributeDefinition.CHARACTER:
                        value = v.toArray(new Character[]{});
                        break;
                    case AttributeDefinition.DOUBLE:
                        value = v.toArray(new Double[]{});
                        break;
                    case AttributeDefinition.FLOAT:
                        value = v.toArray(new Float[]{});
                        break;
                    case AttributeDefinition.INTEGER:
                        value = v.toArray(new Integer[]{});
                        break;
                    case AttributeDefinition.LONG:
                        value = v.toArray(new Long[]{});
                        break;
                    case AttributeDefinition.SHORT:
                        value = v.toArray(new Short[]{});
                        break;
                    case AttributeDefinition.STRING:
                        value = v.toArray(new String[]{});
                        break;
                    }
                }
            }
            
            dict.put(attr[i].getID(), value);
        }
        
        return dict;
    }
    
    protected Object getValue(AttributeDefinition attr, Object value) {
        //convert the special types that aren't represented by the old GUI Builder
        switch (attr.getType()) {
        case AttributeDefinition.CHARACTER:
            if (value != null && value.toString().length() > 0) 
                value = new Character(value.toString().toCharArray()[0]);
            break;
        case AttributeDefinition.LONG:
            if (value != null) 
                value = new Long(value.toString());
            break;
        case AttributeDefinition.SHORT:
            if (value != null) 
                value = new Short(value.toString());
            break;
        }
        
        return value;
    }
}

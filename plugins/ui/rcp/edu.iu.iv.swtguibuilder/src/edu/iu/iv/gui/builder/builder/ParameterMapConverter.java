/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Nov 9, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder.builder;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.common.parameter.ParameterMap;
import edu.iu.iv.common.parameter.ParameterProperty;
import edu.iu.iv.common.parameter.Validator;

/**
 * 
 * @author Bruce Herr
 */
public class ParameterMapConverter {
    private static final ParameterMapConverter INSTANCE = new ParameterMapConverter();
    private Map infoMap;
    
    private static class InputInfo {
        InputType type;
        boolean usesDefaultSelection;
        String methodName;
        String preDefaultValue;
        String postDefaultValue;
        String altDefaultValue;
        String defaultSelection;
        String importsNeeded;
    }
    
    private ParameterMapConverter() { 
        infoMap = new HashMap();
        
        addInput(InputType.BOOLEAN, "putBooleanOption", "", "", "true", null, "");
        addInput(InputType.COLOR, "putColorOption", "new Color(", ")", "Color.GREEN", null, "import java.awt.Color;");
        addInput(InputType.DIRECTORY, "putDirectoryOption", "new File(\"", "\")", "new File(\"\")", null, "import java.io.File;");
        addInput(InputType.DOUBLE, "putDoubleOption", "", "", "0", null, "");
        addInput(InputType.FILE, "putFileOption", "new File(\"", "\")", "new File(\"\")", null, "import java.io.File;");
        addInput(InputType.FLOAT, "putFloatOption", "(float) ", "", "(float) 0", null, "");
        addInput(InputType.INTEGER, "putIntOption", "", "", "0", null, "");
        addInput(InputType.MULTI_CHOICE_LIST, "putMultiChoiceListOption", "new String[] {", "}", "new String[] {}", "new int[]{}", "");
        addInput(InputType.SINGLE_CHOICE_LIST, "putSingleChoiceListOption", "new String[] {", "}", "new String[] {}", "0", "");
        addInput(InputType.TEXT, "putTextOption", "\"", "\"", "\"\"", null, "");
        addInput(InputType.PASSWORD, "putTextOption", "\"", "\"", "\"\"", null, "");
    }

    public static ParameterMapConverter getInstance() {
        return INSTANCE;
    }
    
    private void addInput(InputType type, String methodName, String preDefaultValue, String postDefaultValue, 
            String altDefaultValue, String defaultSelection, String importsNeeded) {
        InputInfo info = new InputInfo();
        
        info.type = type;
        info.methodName = methodName;
        info.preDefaultValue = preDefaultValue;
        info.postDefaultValue = postDefaultValue;
        info.altDefaultValue = altDefaultValue;
        info.defaultSelection = defaultSelection;
        info.usesDefaultSelection = defaultSelection != null;
        info.importsNeeded = importsNeeded;
        
        infoMap.put(type, info);
    }
    
    private InputInfo getInfo(InputType type) {
        return (InputInfo) infoMap.get(type);
    }
    
    public String convertToJavaCode(ParameterMap pmap) {
        return convertToJavaCode(pmap, "", "parameterMap");
    }
    
    public String convertToJavaCode(ParameterMap pmap, String lineStart, String parameterMapName) {
        String output = "";
        
        Iterator iter = pmap.getAllKeys();
        
        while (iter.hasNext()) {
            String id = iter.next().toString();
            
            Parameter p = pmap.get(id);
            
            output += toJavaCode(id, p, lineStart, parameterMapName) + lineStart;
        }
        
        return output;
    }
    
    private String toJavaCode(String id, Parameter p, String lineStart, String parameterMapName) {
        InputType type = (InputType) p.getPropertyValue(ParameterProperty.INPUT_TYPE);
        InputInfo info = getInfo(type);
        
        String name = (String) p.getPropertyValue(ParameterProperty.NAME);
        String desc = (String) p.getPropertyValue(ParameterProperty.DESCRIPTION);
        Object defaultValue =  p.getPropertyValue(ParameterProperty.DEFAULT_VALUE);
        Object defaultSelection = p.getPropertyValue(ParameterProperty.DEFAULT_SELECTION);
        Validator validator = (Validator) p.getPropertyValue(ParameterProperty.VALIDATOR);
        
        String output = parameterMapName + "." + info.methodName + "(";
        output += "\"" + id + "\", \"" + name + "\", \"" + desc + "\",\n" + lineStart + "\t";
        
        if (defaultValue != null) {

            
            output += info.preDefaultValue + getValue(defaultValue) + info.postDefaultValue;
        } else {
            output += info.altDefaultValue;
        }
        
        output += ", ";
        
        if (info.usesDefaultSelection) {
            if (defaultSelection != null) {
                //is always null currently!
                output += info.defaultSelection;
            } else {
                output += info.defaultSelection;
            }
            
            output += ", ";
        }
        
        if (validator != null) {
            //is always null currently!
            output += generateValidator(info, lineStart + "\t");
        } else {
            output += generateValidator(info, lineStart + "\t");
        }
        
        output += ");\n";
        
        return output;
    }
    
    private String generateValidator(InputInfo info, String lineStart) {
        String output = "new Validator() {\n" + lineStart + "\t";
        
        output += "public boolean isValid(Object value) {\n" + lineStart + "\t\t";
        output += "//replace with better validation as needed.\n" + lineStart + "\t\t";
        output += "return true;\n" + lineStart + "\t";
        output += "}}";
        
        return output;
    }
    
    private String getValue(Object defaultValue) {
        String value = defaultValue.toString();
        
        if (defaultValue instanceof Color) {
            Color c = (Color) defaultValue;
            
            value = c.getRed() + ", " + c.getGreen() + ", " + c.getBlue();
        }
        
        if (defaultValue instanceof String[]) {
            String[] v = (String[]) defaultValue;
            
            value = "";
            for (int i=0; i < v.length; i++) {
                value += "\"" + v[i] + "\"";
                
                if (i != v.length -1) {
                    value += ", ";
                }
            }
        }
        
        return value;
    }
    
    public String getImportsNeeded(ParameterMap pmap, String lineStart) {
        if (pmap.size() == 0) {
            return "";
        }
        
        Set imports = new HashSet();
        imports.add("import edu.iu.iv.common.parameter.Validator;");
        Iterator iter = pmap.getAllParameters();
        
        while (iter.hasNext()) {
            Parameter p = (Parameter) iter.next();
            
            InputType type = (InputType) p.getPropertyValue(ParameterProperty.INPUT_TYPE);
            InputInfo info = getInfo(type);
            
            if (!"".equals(info.importsNeeded)) {
                imports.add(info.importsNeeded);
            }
        }
        
        iter = imports.iterator();
        String output = "";
        
        while (iter.hasNext()) {
            output += iter.next().toString() + "\n" + lineStart;
        }
        
        return output;
    }
}

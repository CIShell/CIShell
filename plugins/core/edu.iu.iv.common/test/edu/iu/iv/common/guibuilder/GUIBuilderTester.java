/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 23, 2005 at Indiana University.
 */
package edu.iu.iv.common.guibuilder;

import java.awt.Color;
import java.io.File;
import java.util.Iterator;

import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.common.parameter.ParameterMap;
import edu.iu.iv.common.parameter.ParameterProperty;
import edu.iu.iv.common.parameter.Validator;

/**
 * 
 * @author Bruce Herr
 */
public class GUIBuilderTester {
    public static void main(String[] args) {
        final ParameterMap pmap = new ParameterMap();
        String[] list = new String[]{"a","b","c"};
        
        pmap.put("Text", new Parameter("Text","Text Input (only accepts 'moo' as input)",InputType.TEXT,"text",new Validator() {
            public boolean isValid(Object value) {
                return "moo".equals(value);
            }}));
        
        pmap.put("Password", new Parameter("Password","Password Input (only accepts 'cow' as input)",InputType.PASSWORD,"notcow",new Validator() {
            public boolean isValid(Object value) {
                return "cow".equals(value);
            }}));
        
        pmap.put("Boolean", new Parameter("Boolean","Boolean Input (Must be checked)",InputType.BOOLEAN,Boolean.TRUE, new Validator() {
            public boolean isValid(Object value) {
                pmap.get("Integer").setValue(new Integer(10));
                
                if (value == Boolean.FALSE) pmap.get("Multi-List").setValue(new int[] {1});
                if (value == Boolean.FALSE) pmap.get("Text").setEnabled(true);
                pmap.get("Float").setValue(new Float(0.5));
                pmap.get("Float").setEnabled(value == Boolean.TRUE);
                
                return value == Boolean.TRUE;
            }}));
        pmap.put("Integer", new Parameter("Integer","Integer Input (Must be 10)",InputType.INTEGER,new Integer(5), new Validator(){
            public boolean isValid(Object value) {
                return ((Integer) value).intValue() == 10;
            }}));
       
        pmap.put("Double", new Parameter("Double","Double Input (Must be 0.7)",InputType.DOUBLE,new Double(5.0), new Validator() {
            public boolean isValid(Object value) {                
                return ((Double) value).doubleValue() == 0.7;
            }}));
        pmap.put("Float", new Parameter("Float","Float Input (Must be 0.5)",InputType.FLOAT,new Float(0.5), new Validator() {
            public boolean isValid(Object value) {
                return ((Float) value).doubleValue() == 0.5;
            }}));
        pmap.get("Text").setEnabled(false);
        pmap.put("File", new Parameter("File","File Input (Must be user.dir + /.project)",InputType.FILE,new File(System.getProperty("user.dir")), new Validator() {
            public boolean isValid(Object value) {
                return (System.getProperty("user.dir") + "/.project").equals(value.toString());
            }}));
        pmap.put("Directory", new Parameter("Directory","Directory Input (Must be user.dir)",InputType.DIRECTORY,new File(System.getProperty("user.dir")), new Validator() {
            public boolean isValid(Object value) {
                return System.getProperty("user.dir").equals(value.toString());
            }}));        
        pmap.put("List", new Parameter("List","Single-Selection List (Pick 'b')",InputType.SINGLE_CHOICE_LIST,list, new Integer(2), new Validator() {
            public boolean isValid(Object value) {
                return ((Integer) value).intValue() == 1;
            }}));        
        pmap.put("Multi-List", new Parameter("Multi-List","Multi-Selection List (Pick 'b')",InputType.MULTI_CHOICE_LIST,list, new int[]{0,2}, new Validator() {
            public boolean isValid(Object value) {
                int[] v = (int[]) value;
                return v.length == 1 && v[0] == 1;
            }}));    
        pmap.put("Color", new Parameter("Color","Color",InputType.COLOR,Color.GREEN));
        
        pmap.put("Unsupported", new Parameter("Unsupported","Unsupported Input Type",InputType.UNSUPPORTED,new File(System.getProperty("user.dir"))));
        
        final GUIBuilder gui = GUIBuilder.createGUI("Input Types","Here are the current input types:", pmap);
        
        gui.addSelectionListener(new SelectionListener() {
            public void widgetSelected() {
                Iterator params = pmap.getAllParameters();
                
                while (params.hasNext()) {
                    Parameter parameter = (Parameter) params.next();
                    String label = (String) parameter.getPropertyValue(ParameterProperty.NAME);
                    Object value = parameter.getValue();
                    
                    String output = label + ": ";
                    if (value instanceof int[]) {
                        int[] v = (int[]) value;
                        output += "[ ";
                        for (int i=0; i < v.length; i++) {
                            output += v[i] + " ";
                        }
                        output += "]\n";                        
                    } else {
                        output += value + "\n";
                    }
                    
                    System.out.println(output);
                }
                
                gui.close();
            }});
        
        gui.open();
        
    }
}

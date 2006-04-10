/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Nov 8, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder.builder;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.TableItem;

import edu.iu.iv.common.guibuilder.SelectionListener;
import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.common.parameter.ParameterMap;
import edu.iu.iv.common.parameter.ParameterProperty;
import edu.iu.iv.common.parameter.Validator;
import edu.iu.iv.common.property.Property;
import edu.iu.iv.gui.builder.SwtGUIBuilder;

/**
 * 
 * @author Bruce Herr
 */
public class ParameterMapBuilderDelegate implements BuilderDelegate {
    private static final Property ID_PROPERTY = new Property("ID", String.class);
    private static final int ID = 0;
    private static final int TYPE = 1;
    private static final int LABEL = 2;
    private static final int DESC = 3;
    
    private Map actionMap;
    private List paramList;
    
    private static class ActionInfo {
        InputType type;
        String label;
        Object defaultValue;
    }
    
    public ParameterMapBuilderDelegate() {
        actionMap = new HashMap();
        paramList = new ArrayList();

        addAction(InputType.BOOLEAN, Boolean.TRUE);
        addAction(InputType.COLOR, Color.WHITE);
        addAction(InputType.DIRECTORY, new File(System.getProperty("user.dir")));
        addAction(InputType.DOUBLE, new Double(0));
        addAction(InputType.FILE, new File(""));
        addAction(InputType.FLOAT, new Float(0));
        addAction(InputType.INTEGER, new Integer(0));
        addAction(InputType.MULTI_CHOICE_LIST, new String[]{"Requires","Coding"});
        addAction(InputType.PASSWORD, "");
        addAction(InputType.SINGLE_CHOICE_LIST, new String[]{"Requires","Coding"});
        addAction(InputType.TEXT, "");
    }

    private void addAction(InputType action, Object defaultValue) {
        ActionInfo info = new ActionInfo();
        info.type = action;
        info.label = info.type.getName().replaceFirst(" Input","");
        info.defaultValue = defaultValue;
        
        actionMap.put(action.getName().replaceFirst(" Input",""),info);
    }
    
    public String[] getColumns() {
        return new String[] { "ID","Type","Label","Description" };
    }
    
    public String[] createItem(String action) {
        ActionInfo info = getActionInfo(action);

        String id = "" + (int) (Math.random()*100);
        String type = info.label;
        String label = "MyLabel";
        String desc = "MyDescription";
        
        Parameter param = new Parameter();
        param.setPropertyValue(ID_PROPERTY, id);
        param.setPropertyValue(ParameterProperty.NAME, label);
        param.setPropertyValue(ParameterProperty.DESCRIPTION, desc);
        param.setPropertyValue(ParameterProperty.INPUT_TYPE, info.type);
        
        if (info.defaultValue != null) {
            param.setPropertyValue(ParameterProperty.DEFAULT_VALUE, info.defaultValue);
        }
        
        paramList.add(param);
        
        return new String[] { id, type, label, desc };
    }

    public void edit(TableItem item) {
        String id = item.getText(ID);
        
        Parameter param = getParameter(id);
        
        if (param == null) {
            param = createParameter(item);
        }
        
        editParameter(param, item);
    }
        
    private void editParameter(Parameter param, TableItem item) {
        ParameterMap pmap = new ParameterMap();
        
        String id = (String) param.getPropertyValue(ID_PROPERTY);
        String label = (String) param.getPropertyValue(ParameterProperty.NAME);
        String desc = (String) param.getPropertyValue(ParameterProperty.DESCRIPTION);
        InputType type = (InputType) param.getPropertyValue(ParameterProperty.INPUT_TYPE);
        
        Object defaultValue = param.getPropertyValue(ParameterProperty.DEFAULT_VALUE);
        
        Validator noNulls = new Validator(){
            public boolean isValid(Object value) {
                return value != null && value.toString().length() > 0;
            }
        };
        
        pmap.putTextOption("id","Unique ID","Enter a unique identifier for this input",id,noNulls);
        pmap.putTextOption("label","Label","Enter a label for this input",label,noNulls);
        pmap.putTextOption("desc","Description","Enter a description on what data is acceptable for this input",desc,noNulls);
        
        if (defaultValue != null) {
            pmap.put("defaultValue", new Parameter("Default Value","Enter a default value for this input. Dynamic values require programming.", type, defaultValue));
        } else {
            pmap.put("noDefaultValue", new Parameter("Default Value", "This input requires coding to work by updating the generated algorithm class.", InputType.UNSUPPORTED, "Requires coding"));
        }
        
        showGUI(pmap, param, type, defaultValue, item);
    }
    
    private void showGUI(final ParameterMap pmap, final Parameter param, final InputType type, 
            final Object defValue, final TableItem item) {       
        final SwtGUIBuilder builder = new SwtGUIBuilder("Input Editor", "");
        builder.addAllToGUI(pmap);
        
        builder.addSelectionListener(new SelectionListener() {
            public void widgetSelected() {
                String id = pmap.getTextValue("id");
                String label = pmap.getTextValue("label");
                String desc = pmap.getTextValue("desc");
                Object defaultValue = defValue;
                
                if (defaultValue != null && type != InputType.SINGLE_CHOICE_LIST && type != InputType.MULTI_CHOICE_LIST) {
                    defaultValue = pmap.get("defaultValue").getValue();
                }
                
                param.setPropertyValue(ID_PROPERTY, id);
                param.setPropertyValue(ParameterProperty.NAME, label);
                param.setPropertyValue(ParameterProperty.DESCRIPTION, desc);
                
                if (defaultValue != null) {
                    param.setPropertyValue(ParameterProperty.DEFAULT_VALUE, defaultValue);
                }
                
                item.setText(ID, param.getPropertyValue(ID_PROPERTY).toString());
                item.setText(LABEL, param.getPropertyValue(ParameterProperty.NAME).toString());
                item.setText(DESC,param.getPropertyValue(ParameterProperty.DESCRIPTION).toString());
                
                builder.close();
            }});
        
        builder.open();
    }
    
    private Parameter createParameter(TableItem item) {
        String id = item.getText(ID);
        String type = item.getText(TYPE);
        String label = item.getText(LABEL);
        String desc = item.getText(DESC);
        
        ActionInfo info = getActionInfo(type);
        
        Parameter param = new Parameter();
        param.setPropertyValue(ID_PROPERTY, id);
        param.setPropertyValue(ParameterProperty.NAME, label);
        param.setPropertyValue(ParameterProperty.DESCRIPTION, desc);
        param.setPropertyValue(ParameterProperty.INPUT_TYPE, info.type);
        
        if (info.defaultValue != null) {
            param.setPropertyValue(ParameterProperty.DEFAULT_VALUE, info.defaultValue);
        }
        
        paramList.add(param);
        
        return param;
    }
    
    private ActionInfo getActionInfo(String action) {
        action = action.replaceFirst(" Input","");
        ActionInfo info = (ActionInfo) actionMap.get(action);
        
        return info;
    }
    
    public Parameter getParameter(String id) {
        Iterator iter = paramList.iterator();
        
        while (iter.hasNext()) {
            Parameter p = (Parameter) iter.next();
            
            if (p.getPropertyValue(ID_PROPERTY).equals(id)) {
                return p;
            }
        }
        
        return null;
    }
}

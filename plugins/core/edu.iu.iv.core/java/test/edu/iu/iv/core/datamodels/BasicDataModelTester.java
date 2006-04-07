/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 22, 2005 at Indiana University.
 */
package edu.iu.iv.core.datamodels;

import junit.framework.TestCase;
import edu.iu.iv.common.property.PropertyMap;
import edu.iu.iv.core.datamodels.BasicDataModel;
import edu.iu.iv.core.datamodels.DataModel;
import edu.iu.iv.core.datamodels.DataModelProperty;
import edu.iu.iv.core.datamodels.DataModelType;

/**
 *
 * @author Team IVC
 */
public class BasicDataModelTester extends TestCase {

    private BasicDataModel model;
    private Object data;
    private String name;
    private DataModelType type;
    private DataModel parent;
    private Boolean modified;
        
    protected void setUp() {        
        model = new BasicDataModel(data);        
    }

    public void testGetAndSetData() {
        assertNull(model.getData());
        data = "data";
        model.setData(data);
        assertEquals(data, model.getData());
        data = new Integer(2);
        model.setData(data);
        assertEquals(data, model.getData());
        data = new Object[3][5];
        model.setData(data);
        assertEquals(data, model.getData());
    }   

    public void testGetAndSetPropertyMap() {
        
        PropertyMap map = model.getProperties();
        assertNotNull(map);
        assertNull(map.getPropertyValue(DataModelProperty.LABEL));
        assertNull(map.getPropertyValue(DataModelProperty.MODIFIED));
        assertNull(map.getPropertyValue(DataModelProperty.PARENT));
        assertNull(map.getPropertyValue(DataModelProperty.TYPE));
        
        name = "name";
        modified = new Boolean(false);
        parent = new BasicDataModel(new Object[3][4]);
        type = DataModelType.TREE;
        
        map = new PropertyMap();
        map.setPropertyValue(DataModelProperty.LABEL, name);
        map.setPropertyValue(DataModelProperty.MODIFIED, modified);
        map.setPropertyValue(DataModelProperty.PARENT, parent);
        map.setPropertyValue(DataModelProperty.TYPE, type);
        
        model.setPropertyMap(map);
        assertEquals(map.getPropertyValue(DataModelProperty.LABEL), name);
        assertEquals(map.getPropertyValue(DataModelProperty.MODIFIED), modified);
        assertEquals(map.getPropertyValue(DataModelProperty.PARENT), parent);
        assertEquals(map.getPropertyValue(DataModelProperty.TYPE), type);
    
    }
}

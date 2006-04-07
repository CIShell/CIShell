/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 30, 2005 at Indiana University.
 */
package edu.iu.iv.internal;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import edu.iu.iv.common.property.PropertyMap;
import edu.iu.iv.core.datamodels.BasicDataModel;
import edu.iu.iv.core.datamodels.DataModel;
import edu.iu.iv.core.datamodels.DataModelProperty;
import edu.iu.iv.core.datamodels.DataModelType;
import edu.iu.iv.internal.BasicModelManager;

/**
 *
 * @author Team IVC
 */
public class BasicModelManagerTester extends TestCase {

    private BasicModelManager manager;
    private DataModel model1;
    private DataModel model2;
    private DataModel model3;
    
    protected void setUp() {
        PropertyMap map;
        manager = new BasicModelManager();
        
        model1 = new BasicDataModel("model");
        map = model1.getProperties();
        map.setPropertyValue(DataModelProperty.LABEL, "model1");
        map.setPropertyValue(DataModelProperty.TYPE, DataModelType.NETWORK);
        
        model2 = new BasicDataModel("model2");
        map = model2.getProperties();
        map.setPropertyValue(DataModelProperty.LABEL, "model2");
        map.setPropertyValue(DataModelProperty.TYPE, DataModelType.TREE);
        
        model3 = new BasicDataModel("model3");
        map = model3.getProperties();
        map.setPropertyValue(DataModelProperty.LABEL, "model3");
        map.setPropertyValue(DataModelProperty.TYPE, DataModelType.MATRIX);
    }
    

    public void testAddAndRemoveModel() {
        manager.addModel(model1);
        manager.addModel(model2);
        manager.addModel(model3);
                
        Set models = manager.getModels();
        assertEquals(models.size(), 3);
        assertTrue(models.contains(model1));
        assertTrue(models.contains(model2));
        assertTrue(models.contains(model3));
        
        manager.removeModel(model1);
        assertEquals(models.size(), 2);
        assertTrue(models.contains(model2));
        assertTrue(models.contains(model3));
        
        manager.removeModel(model2);
        assertEquals(models.size(), 1);
        assertTrue(models.contains(model3));
        
        manager.removeModel(model3);
        assertTrue(models.isEmpty());
    }

    public void testGetAndSetSelectedModels() {
        manager.addModel(model1);
        manager.addModel(model2);
        manager.addModel(model3);
        
        Set selected = new HashSet();
        selected.add(model1);
        manager.setSelectedModels(selected);
        assertTrue(manager.getSelectedModels().contains(model1));
        
        selected.clear();
        manager.setSelectedModels(selected);
        assertTrue(manager.getSelectedModels().isEmpty());
        
        selected.add(model2);
        selected.add(model3);
        manager.setSelectedModels(selected);
        assertEquals(manager.getSelectedModels().size(), 2);
        assertTrue(manager.getSelectedModels().contains(model2));
        assertTrue(manager.getSelectedModels().contains(model3));
    }
}


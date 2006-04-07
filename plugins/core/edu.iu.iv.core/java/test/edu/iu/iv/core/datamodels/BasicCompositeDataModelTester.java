/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 22, 2005 at Indiana University.
 */
package edu.iu.iv.core.datamodels;

import java.util.Iterator;

import junit.framework.TestCase;
import edu.iu.iv.core.datamodels.BasicCompositeDataModel;
import edu.iu.iv.core.datamodels.BasicDataModel;
import edu.iu.iv.core.datamodels.CompositeDataModel;
import edu.iu.iv.core.datamodels.DataModel;

/**
 *
 * @author Team IVC
 */
public class BasicCompositeDataModelTester extends TestCase {
    
    BasicCompositeDataModel composite;

    protected void setUp(){
        composite = new BasicCompositeDataModel();
    }
    
    public void testAddAndRemove() {
        DataModel model1 = new BasicDataModel("data");
        DataModel model2 = new BasicDataModel(new Integer(4));
        DataModel model3 = new BasicDataModel(new Object[4][5]);
        DataModel model4 = new BasicDataModel(new Boolean(true));
        CompositeDataModel composite2 = new BasicCompositeDataModel();
        composite2.add(model1);
        composite2.add(model2);
        composite.add(model3);
        composite.add(model4);
        composite.add(composite2); //composite inside composite
        
        Iterator iterator = composite.iterator();
        int count = 0;
        int count2 = 0;
        while(iterator.hasNext()){
            count++;
            DataModel model = (DataModel)iterator.next();
            if(model instanceof CompositeDataModel){
                CompositeDataModel newComposite = (CompositeDataModel)model;
                Iterator iterator2 = newComposite.iterator();
                count2 = 0;
                while(iterator2.hasNext()){
                    count2++;
                    //DataModel innerModel = (DataModel)iterator2.next();
                    iterator2.next();
                }
                assertEquals(count2, 2);                
            }
        }        
        //top level composite has 3 items in it
        assertEquals(count, 3);
        
        composite2.remove(model2);
        composite.remove(model4);
        
        iterator = composite.iterator();
        count = 0;
        count2 = 0;
        while(iterator.hasNext()){
            count++;
            DataModel model = (DataModel)iterator.next();
            if(model instanceof CompositeDataModel){
                CompositeDataModel newComposite = (CompositeDataModel)model;
                Iterator iterator2 = newComposite.iterator();   
                count2 = 0;
                while(iterator2.hasNext()){
                    count2++;
                    //DataModel innerModel = (DataModel)iterator2.next();
                    iterator2.next();
                }
                assertEquals(count2, 1);                
            }
        }        
        //top level composite has 2 items in it
        assertEquals(count, 2);
        
        
        composite.remove(composite2);
        iterator = composite.iterator();
        count = 0;
        count2 = 0;
        while(iterator.hasNext()){
            count++;
            DataModel model = (DataModel)iterator.next();
            assertTrue(!(model instanceof CompositeDataModel));
        }        
        //top level composite has 1 item in it
        assertEquals(count, 1);
    }
}

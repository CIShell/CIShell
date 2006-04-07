/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 29, 2005 at Indiana University.
 */
package edu.iu.iv.internal;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;
import edu.iu.iv.common.property.PropertyMap;
import edu.iu.iv.core.persistence.BasicFileResourceDescriptor;
import edu.iu.iv.core.persistence.FileResourceDescriptor;
import edu.iu.iv.core.persistence.PersistenceException;
import edu.iu.iv.core.persistence.Persister;
import edu.iu.iv.core.persistence.ResourceDescriptor;
import edu.iu.iv.internal.BasicPersistenceRegistry;

/**
 *
 * @author Team IVC
 */
public class BasicPersistenceRegistryTester extends TestCase {
    private BasicPersistenceRegistry registry;
    private boolean stringPersisterFlag;
    private boolean integerPersisterFlag;
    private Persister dummyPersister1;
    private Persister dummyPersister2;
    private Persister dummyPersister3;
    private Persister dummyPersister4;
    private Persister dummyPersister5;
    
    
    protected void setUp() {
        registry = new BasicPersistenceRegistry();
        dummyPersister1 = new DummyIntegerPersister();
        dummyPersister2 = new DummyIntegerPersister();
        dummyPersister3 = new DummyStringPersister();
        dummyPersister4 = new DummyStringPersister();
        dummyPersister5 = new DummyIntegerPersister();        
        registry.register(dummyPersister1);
        registry.register(dummyPersister2);
        registry.register(dummyPersister3);
        registry.register(dummyPersister4);
        registry.register(dummyPersister5);
    }

    public void testRegister() {        
        List persisters = registry.getPersisters();
        assertEquals(persisters.size(), 5);
        assertTrue(persisters.contains(dummyPersister1));
        assertTrue(persisters.contains(dummyPersister2));
        assertTrue(persisters.contains(dummyPersister3));
        assertTrue(persisters.contains(dummyPersister4));
        assertTrue(persisters.contains(dummyPersister5));
        
        Persister dummyPersister6 = new DummyIntegerPersister();
        Persister dummyPersister7 = new DummyIntegerPersister();
        registry.register(dummyPersister6);
        registry.register(dummyPersister7);
        assertEquals(persisters.size(), 7);
        assertTrue(persisters.contains(dummyPersister6));
        assertTrue(persisters.contains(dummyPersister7));
    }

    /*
     * Class under test for List getSupportingPersisters(Object)
     */
    public void testGetSupportingPersistersObject() {        
        //1,2,5 should support an integer
        List persisters = registry.getSupportingPersisters(new Integer(5));
        assertEquals(persisters.size(), 3);
        assertTrue(persisters.contains(dummyPersister1));
        assertTrue(persisters.contains(dummyPersister2));
        assertTrue(persisters.contains(dummyPersister5));
        
        //3,4 should support a string
        persisters = registry.getSupportingPersisters("model");
        assertEquals(persisters.size(), 2);
        assertTrue(persisters.contains(dummyPersister3));
        assertTrue(persisters.contains(dummyPersister4));        
    }

    /*
     * Class under test for List getSupportingPersisters(ResourceDescriptor)
     */
    public void testGetSupportingPersistersResourceDescriptor() {
        //1,2,5 should support a ".integer" file
        String path = System.getProperty("user.dir") + File.separator;
        File file = new File(path + "test.integer");
        ResourceDescriptor descriptor = new BasicFileResourceDescriptor(file);
        List persisters = registry.getSupportingPersisters(descriptor);
        assertEquals(persisters.size(), 3);
        assertTrue(persisters.contains(dummyPersister1));
        assertTrue(persisters.contains(dummyPersister2));
        assertTrue(persisters.contains(dummyPersister5));
        file.delete();
        
        //3,4 should support a ".string" file
        file = new File(path + "test.string");
        descriptor = new BasicFileResourceDescriptor(file);
        persisters = registry.getSupportingPersisters(descriptor);
        assertEquals(persisters.size(), 2);
        assertTrue(persisters.contains(dummyPersister3));
        assertTrue(persisters.contains(dummyPersister4));
        file.delete();
    }

    public void testGetPersisters() {
        List persisters = registry.getPersisters();
        assertTrue(persisters.contains(dummyPersister1));
        assertTrue(persisters.contains(dummyPersister2));
        assertTrue(persisters.contains(dummyPersister3));
        assertTrue(persisters.contains(dummyPersister4));
        assertTrue(persisters.contains(dummyPersister5));
    }

    public void testFindPersister() {
        //1,2,5 should support a ".integer" file and an Integer model
        String path = System.getProperty("user.dir") + File.separator;
        File file = new File(path + "test.integer");
        ResourceDescriptor descriptor = new BasicFileResourceDescriptor(file);
        Persister persister = registry.findPersister(new Integer(5), descriptor);
        assertNotNull(persister);
        //should be one of the three returned
        assertTrue(persister == dummyPersister1 || persister == dummyPersister2 ||
                   persister == dummyPersister5);
        file.delete();
        
        //3,4 should support ".string" and a String model
        file = new File(path + "test.string");
        descriptor = new BasicFileResourceDescriptor(file);
        persister = registry.findPersister("model", descriptor);
        assertNotNull(persister);
        assertTrue(persister == dummyPersister3 || persister == dummyPersister4);
        file.delete();
    }

    public void testSave() {        
        String path = System.getProperty("user.dir") + File.separator;
        File file = new File(path + "test.integer");
        ResourceDescriptor descriptor = new BasicFileResourceDescriptor(file);
        integerPersisterFlag = false;
        try {
            registry.save(new Integer(5), descriptor);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        assertTrue(integerPersisterFlag);
        file.delete();
        
        file = new File(path + "test.string");
        descriptor = new BasicFileResourceDescriptor(file);
        stringPersisterFlag = false;
        try {
            registry.save("model", descriptor);
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (PersistenceException e1) {
            e1.printStackTrace();
        }
        assertTrue(stringPersisterFlag);
        file.delete();
    }

    public void testLoad() {
        String path = System.getProperty("user.dir") + File.separator;
        File file = new File(path + "test.integer");
        ResourceDescriptor descriptor = new BasicFileResourceDescriptor(file);
        integerPersisterFlag = false;
        try {
            registry.load(descriptor);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        assertTrue(integerPersisterFlag);
        file.delete();
        
        file = new File(path + "test.string");
        descriptor = new BasicFileResourceDescriptor(file);
        stringPersisterFlag = false;
        try {
            registry.load(descriptor);
        } catch (OutOfMemoryError e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (PersistenceException e1) {
            e1.printStackTrace();
        }
        assertTrue(stringPersisterFlag);
        file.delete();
    }

    public void testClear() {
        registry.clear();
        List persisters = registry.getPersisters();
        assertEquals(persisters.size(), 0);
    }

    private class DummyStringPersister implements Persister {

        public void persist(Object model, ResourceDescriptor resource) throws IOException, PersistenceException {
            stringPersisterFlag = true;
        }

        public Object restore(ResourceDescriptor resource) throws IOException, OutOfMemoryError, PersistenceException {
            stringPersisterFlag = true;
            return null;
        }

        public boolean canPersist(Object model) {
            return model instanceof String;
        }

        public boolean canRestore(ResourceDescriptor resource) {
            FileResourceDescriptor descriptor = (FileResourceDescriptor)resource;
            return descriptor.getFileExtension().equals(".string");
        }

        public PropertyMap getProperties() {
            return null;
        }           
    }
    
    private class DummyIntegerPersister implements Persister {

        public void persist(Object model, ResourceDescriptor resource) throws IOException, PersistenceException {
            integerPersisterFlag = true;
        }

        public Object restore(ResourceDescriptor resource) throws IOException, OutOfMemoryError, PersistenceException {
            integerPersisterFlag = true;
            return null;
        }

        public boolean canPersist(Object model) {
            return model instanceof Integer;
        }

        public boolean canRestore(ResourceDescriptor resource) {
            FileResourceDescriptor descriptor = (FileResourceDescriptor)resource;
            return descriptor.getFileExtension().equals(".integer");
        }

        public PropertyMap getProperties() {
            return null;
        }           
    }    
}

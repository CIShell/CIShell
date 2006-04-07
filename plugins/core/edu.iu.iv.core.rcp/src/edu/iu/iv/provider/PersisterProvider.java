/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Sep 11, 2005 at Indiana University.
 */
package edu.iu.iv.provider;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleContext;

import edu.iu.iv.common.util.PathScanner;
import edu.iu.iv.core.IVC;
import edu.iu.iv.core.persistence.PersistenceRegistry;
import edu.iu.iv.core.persistence.Persister;

/**
 * 
 * @author Bruce Herr
 */
public class PersisterProvider extends Plugin implements IStartup {    
    //Resource bundle.
    private ResourceBundle resourceBundle;
    
    /**
     * The constructor.
     */
    public PersisterProvider(String pluginName) {
        super();
        try {
            resourceBundle = ResourceBundle.getBundle(pluginName);
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }
    }
    
    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        
        String path = IVC.getInstance().getPluginPath(this);
        setupPersisters(path, this.getClass().getClassLoader());
    }

    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
    }

    /**
     * Returns the string from the plugin's resource bundle,
     * or 'key' if not found.
     */
    public String getResourceString(String key) {
        ResourceBundle bundle = getResourceBundle();
        try {
            return (bundle != null) ? bundle.getString(key) : key;
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Returns the plugin's resource bundle,
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    //required for load on startup
    public void earlyStartup() {}
    
    //// Start persister scanning code \\\\
    
    public void setupPersisters(Object plugin) {
        String path = IVC.getInstance().getPluginPath(plugin);
        setupPersisters(path, plugin.getClass().getClassLoader());
    }
    
    
    /**
     * Registers the Persisters on the given path with IVC's
     * PersistenceRegistry
     */
    public void setupPersisters(String path, ClassLoader classLoader){                 
        try {
            PathScanner scanner = new PathScanner();
            scanner.setParentClassLoader(classLoader);          
            //Scan the Plugin directory and find all the classes held in them
            scanner.setScanPaths(new String[]{path});
            //grab all the Persister classes and register them
            fillPersisterRegistry(scanner.getInstantiableSubtypes(Persister.class));
        } catch (IOException e) {}      
    }
    
    /**
     * Instantiates each of the persister classes in the given list and
     * inserts the instances into the persister registry.
     *
     * @param persisterList list of persister classes to load into the registry
     */
    private void fillPersisterRegistry(List persisterList) {        
        PersistenceRegistry registry = IVC.getInstance().getPersistenceRegistry();
        
        Iterator persisters = persisterList.iterator();

        while (persisters.hasNext()) {
            Class clazz = (Class) persisters.next();
            Persister persister = (Persister) instantiateClass(clazz);

            if (persister != null) {                
                registry.register(persister);
            }
        }
    }

    /**
     * Given a Class, it instantiates it. This only instantiates no-arg constructor
     * classes. If it cannot be instantiated by these means or an error is found
     * it returns null.
     *
     * @param clazz the class to instantiate
     * @return the instantiated class or null if there was an error.
     */
    private Object instantiateClass(Class clazz) {
        try {
            return clazz.newInstance();
        }
         catch (InstantiationException e) {
            e.printStackTrace();
            System.err.println("Error instantiating Class: " +
                clazz.toString());
        }
         catch (IllegalAccessException e) {
            e.printStackTrace();
            System.err.println("Error instantiating Class: " +
                clazz.toString());
        }

        return null;
    }
}
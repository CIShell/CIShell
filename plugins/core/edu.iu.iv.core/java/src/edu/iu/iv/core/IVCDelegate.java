/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Sep 10, 2005 at Indiana University.
 */
package edu.iu.iv.core;

import edu.iu.iv.common.configuration.Configuration;
import edu.iu.iv.core.messaging.ConsoleManager;
import edu.iu.iv.core.persistence.PersistenceRegistry;
import edu.iu.iv.internal.IVCLogManager;

public interface IVCDelegate {

    /**
     * Sets default settings for the Delegate
     */
    public abstract void setDefaultSettings();

    /**
     * Returns the absolute path in the file system to the Plugin specified by the 
     * given Plugin ID. This ID is what is specified in the the Plugin's plugin.xml file.
     * 
     * @param pluginID the ID of the Plugin to find the absolute path to.
     * @return the absolute path in the file system to the Plugin
     */
    public abstract String getPluginPath(String pluginID);

    /**
     * Returns the absolute path in the file system to the Plugin given.  This Plugin 
     * should be use an implementation of the backend version of the Plugin. Implementors
     * of this interface are not required for IVC plugins, so if none exists, the
     * getPluginPath(String pluginID) method should be used instead.
     * 
     * @param plugin the Plugin to find the absolute path in the file system to
     * @return the absolute path in the file system to the Plugin
     */
    public abstract String getPluginPath(Object plugin);

    /**
     * Returns the path to the default data folder, as specified
     * in IVC preferences.
     * 
     * @return the path to the default data folder
     */
    public abstract String getDefaultDataFolder();

    /**
     * Returns the path to the temporary files folder, as specified
     * in IVC preferences.
     * 
     * @return the path to the temporary files folder
     */
    public abstract String getTemporaryFilesFolder();

    /**
     * Gets the scheduler currently in use by the system. The scheduler will allow for algorithms
     * to be scheduled however the current scheduler does its scheduling.
     * 
     * @return the scheduler
     */
    public abstract Scheduler getScheduler();

    /**
     * Sets the scheduler to be used by the system.
     * 
     * @param scheduler the scheduler to be used.
     */
    public abstract void setScheduler(Scheduler scheduler);

    /**
     * Gets the persistence registry. This is the global registry where all
     * persisters will be housed for the system.
     * 
     * @return the persistence registry
     */
    public abstract PersistenceRegistry getPersistenceRegistry();

    /**
     * Set the persistence registry.
     * @param persistenceRegistry
     */
    public abstract void setPersistenceRegistry(
            PersistenceRegistry persistenceRegistry);

    /**
     * Get the modelManger. This manages the models that are in memory.
     * 
     * @return
     */
    public abstract ModelManager getModelManager();

    /**
     * Set the model manager to be used by IVC
     * @param modelManager
     */
    public abstract void setModelManager(ModelManager modelManager);

    /**
     * Get the console that console messages are printed to.
     * @return
     */
    public abstract ConsoleManager getConsole();

    /**
     * Set the console manager to be used by IVC.
     * @param consoleManager
     */
    public abstract void setConsole(ConsoleManager consoleManager);

    /**
     * Get the configuration. This holds all the preferences
     * for IVC.
     * 
     * @return
     */
    public abstract Configuration getConfiguration();

    /**
     * Set the configuration.
     * @param configuration
     */
    public abstract void setConfiguration(Configuration configuration);

    /**
     * Get the log Manager. This manages the logs that are 
     * used by IVC. 
     * @return
     */
    public abstract IVCLogManager getLogManager();

    /**
     * Set the log manager.
     * @param logManager
     */
    public abstract void setLogManager(IVCLogManager logManager);

}
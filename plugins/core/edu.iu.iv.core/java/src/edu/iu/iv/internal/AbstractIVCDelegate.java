/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Aug 20, 2005 at Indiana University.
 */
package edu.iu.iv.internal;

import edu.iu.iv.common.configuration.Configuration;
import edu.iu.iv.core.IVCDelegate;
import edu.iu.iv.core.IVCPreferences;
import edu.iu.iv.core.ModelManager;
import edu.iu.iv.core.Scheduler;
import edu.iu.iv.core.messaging.ConsoleManager;
import edu.iu.iv.core.persistence.PersistenceRegistry;

/**
 * The Delegate for the IVC facade. It houses the many registries
 * and backend specific methods the IVC uses.
 * 
 * @author Bruce Herr
 */
public abstract class AbstractIVCDelegate implements IVCDelegate {
	private PersistenceRegistry persistenceRegistry;
	private Scheduler scheduler;
	private IVCLogManager logManager;
	private ModelManager modelManager;
	private Configuration configuration;
	private ConsoleManager consoleManager;
    
    public AbstractIVCDelegate(Configuration configuration) {
        this.configuration = configuration;
    }
    
    /**
     * @see edu.iu.iv.core.IVCDelegate#setDefaultSettings()
     */
    public void setDefaultSettings() {
        setPersistenceRegistry(new BasicPersistenceRegistry());
        setConsole(new BasicConsoleManager());
        setScheduler(new BasicScheduler());
        setLogManager(new IVCLogManager());
		setModelManager(new BasicModelManager());
        
        if (configuration != null)
            setupDefaultConfigurationSettings();
    }
    
    /**
     * Sets up default configuration settings.
     */
    private void setupDefaultConfigurationSettings() {
        Configuration c = configuration;
        
        if (!c.contains(IVCPreferences.EXIT_WITHOUT_PROMPT)) {
            c.setValue(IVCPreferences.EXIT_WITHOUT_PROMPT, IVCPreferences.EXIT_WITHOUT_PROMPT_DEFAULT);
        }
        if (!c.contains(IVCPreferences.DEFAULT_DATA_FOLDER_PREFERERNCE)) {
            c.setValue(IVCPreferences.DEFAULT_DATA_FOLDER_PREFERERNCE, IVCPreferences.DEFAULT_DATA_FOLDER_PREFERERNCE_DEFAULT);
        }
        if (!c.contains(IVCPreferences.ERROR_LOG_PREFERENCE)) {
            c.setValue(IVCPreferences.ERROR_LOG_PREFERENCE, IVCPreferences.ERROR_LOG_PREFERENCE_DEFAULT);
        }
        if (!c.contains(IVCPreferences.ERROR_LOG_SIZE_PREFERENCE)) {
            c.setValue(IVCPreferences.ERROR_LOG_SIZE_PREFERENCE, IVCPreferences.ERROR_LOG_SIZE_PREFERENCE_DEFAULT);
        }
        if (!c.contains(IVCPreferences.MAX_NUMBER_OF_ERROR_LOGFILES)) {
            c.setValue(IVCPreferences.MAX_NUMBER_OF_ERROR_LOGFILES, IVCPreferences.MAX_NUMBER_OF_ERROR_LOGFILES_DEFAULT);
        }
        if (!c.contains(IVCPreferences.MAX_NUMBER_OF_USER_LOGFILES)) {
            c.setValue(IVCPreferences.MAX_NUMBER_OF_USER_LOGFILES, IVCPreferences.MAX_NUMBER_OF_USER_LOGFILES_DEFAULT);
        }
        if (!c.contains(IVCPreferences.MAX_SIMULTANEOUS_ALGORITHMS_PREFERENCE)) {
            c.setValue(IVCPreferences.MAX_SIMULTANEOUS_ALGORITHMS_PREFERENCE, IVCPreferences.MAX_SIMULTANEOUS_ALGORITHMS_PREFERENCE_DEFAULT);
        }
        if (!c.contains(IVCPreferences.SHOW_ALL_ERRORS_PREFERENCE)) {
            c.setValue(IVCPreferences.SHOW_ALL_ERRORS_PREFERENCE, IVCPreferences.SHOW_ALL_ERRORS_PREFERENCE_DEFAULT);
        }
        if (!c.contains(IVCPreferences.SHOW_CRITICAL_ERRORS_PREFERENCE)) {
            c.setValue(IVCPreferences.SHOW_CRITICAL_ERRORS_PREFERENCE, IVCPreferences.SHOW_CRITICAL_ERRORS_PREFERENCE_DEFAULT);
        }
        if (!c.contains(IVCPreferences.TEMPORARY_FILES_FOLDER_PREFERENCE)) {
            c.setValue(IVCPreferences.TEMPORARY_FILES_FOLDER_PREFERENCE, IVCPreferences.TEMPORARY_FILES_FOLDER_PREFERENCE_DEFAULT);
        }
        if (!c.contains(IVCPreferences.USER_LOG_PREFERENCE)) {
            c.setValue(IVCPreferences.USER_LOG_PREFERENCE, IVCPreferences.USER_LOG_PREFERENCE_DEFAULT);
        }
        if (!c.contains(IVCPreferences.USER_LOG_SIZE_PREFERENCE)) {
            c.setValue(IVCPreferences.USER_LOG_SIZE_PREFERENCE, IVCPreferences.USER_LOG_SIZE_PREFERENCE_DEFAULT);
        }
    }
    
    
	/**
     * @see edu.iu.iv.core.IVCDelegate#getPluginPath(java.lang.String)
     */
	public abstract String getPluginPath(String pluginID);
	
	/**
     * @see edu.iu.iv.core.IVCDelegate#getPluginPath(java.lang.Object)
     */
	public abstract String getPluginPath(Object plugin);
	
	/**
     * @see edu.iu.iv.core.IVCDelegate#getDefaultDataFolder()
     */
	public abstract String getDefaultDataFolder();

	/**
     * @see edu.iu.iv.core.IVCDelegate#getTemporaryFilesFolder()
     */
	public abstract String getTemporaryFilesFolder();
    
	/**
     * @see edu.iu.iv.core.IVCDelegate#getScheduler()
     */
    public Scheduler getScheduler() {
        return scheduler;
    }
    
	/**
     * @see edu.iu.iv.core.IVCDelegate#setScheduler(edu.iu.iv.core.Scheduler)
     */
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
    
	/**
     * @see edu.iu.iv.core.IVCDelegate#getPersistenceRegistry()
     */
    public PersistenceRegistry getPersistenceRegistry() {
        return persistenceRegistry;
    }
    
    /**
     * @see edu.iu.iv.core.IVCDelegate#setPersistenceRegistry(edu.iu.iv.core.persistence.PersistenceRegistry)
     */
    public void setPersistenceRegistry(PersistenceRegistry persistenceRegistry) {
        this.persistenceRegistry = persistenceRegistry;
    }
    
    /**
     * @see edu.iu.iv.core.IVCDelegate#getModelManager()
     */
	public ModelManager getModelManager() {
		return modelManager;
	}
	
    /**
     * @see edu.iu.iv.core.IVCDelegate#setModelManager(edu.iu.iv.core.ModelManager)
     */
	public void setModelManager(ModelManager modelManager) {
	    this.modelManager = modelManager;
	}
	
    /**
     * @see edu.iu.iv.core.IVCDelegate#getConsole()
     */
    public ConsoleManager getConsole() {
        return consoleManager;
    }
    
    /**
     * @see edu.iu.iv.core.IVCDelegate#setConsole(edu.iu.iv.core.messaging.ConsoleManager)
     */
    public void setConsole(ConsoleManager consoleManager) {
        this.consoleManager = consoleManager;
    }
    
    /**
     * @see edu.iu.iv.core.IVCDelegate#getConfiguration()
     */
    public Configuration getConfiguration() {
        
        return configuration;
    }
    
    /**
     * @see edu.iu.iv.core.IVCDelegate#setConfiguration(edu.iu.iv.common.configuration.Configuration)
     */
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
    
    /**
     * @see edu.iu.iv.core.IVCDelegate#getLogManager()
     */
    public IVCLogManager getLogManager() {
        return logManager;
    }
    
    /**
     * @see edu.iu.iv.core.IVCDelegate#setLogManager(edu.iu.iv.internal.IVCLogManager)
     */
    public void setLogManager(IVCLogManager logManager) {
        this.logManager = logManager;
    }
}

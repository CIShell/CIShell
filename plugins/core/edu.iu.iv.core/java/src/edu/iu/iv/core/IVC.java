/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 13, 2004 at Indiana University.
 */
package edu.iu.iv.core;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.iu.iv.common.configuration.Configuration;
import edu.iu.iv.core.datamodels.BasicCompositeDataModel;
import edu.iu.iv.core.datamodels.BasicDataModel;
import edu.iu.iv.core.datamodels.CompositeDataModel;
import edu.iu.iv.core.datamodels.DataModel;
import edu.iu.iv.core.messaging.BasicMessage;
import edu.iu.iv.core.messaging.ConsoleManager;
import edu.iu.iv.core.messaging.Message;
import edu.iu.iv.core.messaging.MessageHandler;
import edu.iu.iv.core.messaging.MessageProperty;
import edu.iu.iv.core.persistence.PersistenceRegistry;
import edu.iu.iv.core.plugin.Plugin;

/**
 * A singleton central IVC system. This houses most of the registries, the GUI, and other
 * important functions that will be used by new plugins, the GUI, and any other new
 * classes added that need the data the IVC class provides. 
 * 
 * @author Team IVC
 */
//Created by: Josh Bonner
//Modified Shashikant Penumarthy
//Modified Bruce Herr
//Modified James Ellis
public class IVC {
	private static final IVC INSTANCE = new IVC();
	
	private static IVCDelegate delegate;
	private Set modelListeners;
	private Set messageHandlers;

	/**
	 * Gets the singleton instance of the IVC system.
	 * 
	 * @return the IVC system.
	 */
	public static IVC getInstance() {
		return INSTANCE;
	}

    // private constructor makes this a singleton.
	private IVC() {
		modelListeners = new HashSet();
		messageHandlers = new HashSet();
		
		//create sampledata and licenses folders if needed
		String path = System.getProperty("user.dir");
		File sampleDataDir = new File(path + File.separator + "sampledata");
		if(!sampleDataDir.exists())
		    sampleDataDir.mkdir();
		File licenseDataDir = new File(path + File.separator + "licenses");
		if(!licenseDataDir.exists())
		    licenseDataDir.mkdir();
	}
	
    /**
     * Gets the IVCDelegate that provides the registries and
     * other methods needed by the IVC.
     * 
     * @return the delegate
     */
	public static IVCDelegate getDelegate() {
        if (delegate == null) {
            throw new IVCInitializationError("Delegate has not been set!");
        }
        
	    return delegate;
	}
	
    /**
     * Sets the IVCDelegate that the IVC needs for its 
     * registries and other methods the IVC provides
     * 
     * @param delegate the delegate
     */
	public static void setDelegate(IVCDelegate delegate) {
	    IVC.delegate = delegate;
	}
	
	/**
	 * Adds a model to the system. This will usually result in a new model 
	 * being available through the GUI.
	 * 
	 * @param model the model to add. Its data can be any object that is supported by the plugins.
	 * @throws UnsupportedModelException if there is no plugin that can handle the model added, 
	 * then an exception will be thrown.
	 */
	public void addModel( DataModel model) throws UnsupportedModelException {
	    getDelegate().getModelManager().addModel(model);
	    
	    Iterator listeners = modelListeners.iterator();	    
		while (listeners.hasNext()) {
			AddModelListener listener = (AddModelListener) listeners.next();
			//notify all listeners that a model was added.
			listener.addModel(model);
		}
	}

	/**
	 * Adds an add model listener to the IVC. Every time a model is added to the system,
	 * all listeners will be notified. 
	 *  
	 * @param listener a listener for models being added to the system.
	 */
	public void addAddModelListener( AddModelListener listener ) {
		modelListeners.add(listener);
	}
	
	/**
	 * Removes an add model listener from the list of those that will be
	 * notified when a model is added to the IVC.
	 * 
	 * @param listener the listener to remove
	 */
	public void removeAddModelListener( AddModelListener listener ) {
		modelListeners.remove(listener);

		if (!modelListeners.contains(listener)) {
			modelListeners.add(listener);
		}
	}
	
	/**
	 * Launches the given Plugin, by checking if the currently selected DataModel(s) are
	 * supported and then passing them to the Plugin.
	 * 
	 * @param plugin the Plugin to launch
	 */
	public void launch(Plugin plugin) {
       boolean supports = true;
       Set models = getModelManager().getSelectedModels();
       CompositeDataModel composite = new BasicCompositeDataModel();
       
       //see if selected models are supported
       if(!models.isEmpty()){
           if(models.size() == 1){
               supports = plugin.supports((DataModel)models.toArray()[0]);
           }
           else{
               Iterator iterator = models.iterator();               
    	       while(iterator.hasNext()){
    	           DataModel model = (DataModel)iterator.next();
    	           composite.add(model);
    	       }
    	       supports = plugin.supports(composite);               
           }
	       
       } 
       //if no models were selected, see if the plugin supports
       //null model
       else {
           try{
               supports = plugin.supports(new BasicDataModel(null));
           } catch (NullPointerException e){
               supports = false;
           }
       }
       
       //this is problematic...should the entire set always be sent?
       //Most plugins are just expecting one model, so that is what should
       //be sent for single selections probably
       if(supports){
           if(models.size() > 1){
               plugin.launch(composite);
           }
           else if(models.size() == 1){
               plugin.launch((DataModel)models.toArray()[0]);
           }
           else{
               //if supports is true, but nothing is selected,then
               //we know it is ok to launch a null model because it passed
               //the test above
               plugin.launch(new BasicDataModel(null));
           }
       }
       
       else{
           String message;
           if(!models.isEmpty()){
	           if(models.size() == 1)
	               message = plugin.unsupportedReason((DataModel)models.toArray()[0]);
	           else
	               message = plugin.unsupportedReason(composite);	           
           }
           else{
               message = "No Model Selected";
           }
           showWarning("Unsupported Model", message, "");
       }                         
	}

	/**
	 * Returns the absolute path in the file system to the Plugin specified by the 
	 * given Plugin ID. This ID is what is specified in the the Plugin's plugin.xml file.
	 * 
	 * @param pluginID the ID of the Plugin to find the absolute path to.
	 * @return the absolute path in the file system to the Plugin
	 */
	public String getPluginPath(String pluginID) {    
        return getDelegate().getPluginPath(pluginID);
	}
	
	/**
	 * Returns the absolute path in the file system to the Plugin given.  This Plugin 
	 * should be use an implementation of the backend version of the Plugin. Implementors
	 * of this interface are not required for IVC plugins, so if none exists, the
	 * getPluginPath(String pluginID) method should be used instead.
	 * 
	 * @param plugin the Plugin to find the absolute path in the file system to
	 * @return the absolute path in the file system to the Plugin
	 */
	public String getPluginPath(Object plugin) {
        return getDelegate().getPluginPath(plugin);    
	}
	
	/**
	 * Returns the path to the default data folder, as specified
	 * in IVC preferences.
	 * 
	 * @return the path to the default data folder
	 */
	public String getDefaultDataFolder() {
        return getDelegate().getDefaultDataFolder();
	}

	/**
	 * Returns the path to the temporary files folder, as specified
	 * in IVC preferences.
	 * 
	 * @return the path to the temporary files folder
	 */
	public String getTemporaryFilesFolder() {
        return getDelegate().getTemporaryFilesFolder();
	}
	
	/**
	 * Adds the given MessageHandler to IVC, to be notified of all
	 * messages passed by plugins.
	 * 
	 * @param handler the MessageHandler to add to IVC
	 */
	public void addMessageHandler(MessageHandler handler) {
        messageHandlers.add(handler);
    }
	
	/**
	 * Removes the given Message handler from IVC, causing it to no longer
	 * be notified of messages passed by plugins.
	 * 
	 * @param handler the MessageHandler to remove from IVC
	 */
	public void removeMessageHandler(MessageHandler handler) {
        messageHandlers.remove(handler);
    }
	
	/**
	 * Shortcut to show an Error message to the user
	 * 
	 * @param title the title of the Error message
	 * @param message the error message to show the user
	 * @param details any additional details about the message that would
	 *  be useful to the user
	 */
	public static void showError(String title, String message, String details){
	    Message msg = new BasicMessage();
	    msg.getProperties().setPropertyValue(MessageProperty.TITLE, title);
	    msg.getProperties().setPropertyValue(MessageProperty.MESSAGE, message);
	    msg.getProperties().setPropertyValue(MessageProperty.DETAILS, details);
	    getInstance().showError(msg);
	}

	/**
	 * Shortcut to show a warning message to the user
	 * 
	 * @param title the title of the warning message
	 * @param message the warning message to show the user
	 * @param details any additional details about the message that would
	 *  be useful to the user
	 */	
	public static void showWarning(String title, String message, String details){
	    Message msg = new BasicMessage();
	    msg.getProperties().setPropertyValue(MessageProperty.TITLE, title);
	    msg.getProperties().setPropertyValue(MessageProperty.MESSAGE, message);
	    msg.getProperties().setPropertyValue(MessageProperty.DETAILS, details);
	    getInstance().showWarning(msg);
	}
	
	/**
	 * Shortcut to show an informational message to the user
	 * 
	 * @param title the title of the informational message
	 * @param message the informational message to show the user
	 * @param details any additional details about the message that would
	 *  be useful to the user
	 */	
	public static void showInformation(String title, String message, String details){
	    Message msg = new BasicMessage();
	    msg.getProperties().setPropertyValue(MessageProperty.TITLE, title);
	    msg.getProperties().setPropertyValue(MessageProperty.MESSAGE, message);
	    msg.getProperties().setPropertyValue(MessageProperty.DETAILS, details);
	    getInstance().showInformation(msg);	       
	}

	/**
	 * Shortcut to show a yes/no question to the user
	 * 
	 * @param title the title of the question message
	 * @param message the question to ask the user
	 * @param details any additional details about the message that would
	 *  be useful to the user
	 *
	 * @return true if the user selected "Yes", false otherwise 
	 */
	public static boolean showQuestion(String title,  String message, String details){	    
	    Message msg = new BasicMessage();
	    msg.getProperties().setPropertyValue(MessageProperty.TITLE, title);
	    msg.getProperties().setPropertyValue(MessageProperty.MESSAGE, message);
	    msg.getProperties().setPropertyValue(MessageProperty.DETAILS, details);
	    return getInstance().showQuestion(msg);
	}
	
	/**
	 * Shortcut to show a confirmation question to the user with the given
	 * message.
	 * 
	 * @param title the title of the confirmation question
	 * @param message the message to show the user to get confirmation of
	 * @param details any additional details about the message that would
	 *  be useful to the user
	 * 
	 * @return true if the user selected "OK", false otherwise
	 */
	public static boolean showConfirm(String title, String message, String details){
	    Message msg = new BasicMessage();
	    msg.getProperties().setPropertyValue(MessageProperty.TITLE, title);
	    msg.getProperties().setPropertyValue(MessageProperty.MESSAGE, message);
	    msg.getProperties().setPropertyValue(MessageProperty.DETAILS, details);
	    return getInstance().showConfirm(msg);	   
	}
	
    public void showError(Message message){
        Iterator handlerIterator = messageHandlers.iterator();
        while(handlerIterator.hasNext()){
            MessageHandler handler = (MessageHandler)handlerIterator.next();
            handler.showError(message);
        }
    }
    public void showWarning(Message message){
        Iterator handlerIterator = messageHandlers.iterator();
        while(handlerIterator.hasNext()){
            MessageHandler handler = (MessageHandler)handlerIterator.next();
            handler.showWarning(message);
        }
    }
    
    public void showInformation(Message message){
        Iterator handlerIterator = messageHandlers.iterator();
        while(handlerIterator.hasNext()){
            MessageHandler handler = (MessageHandler)handlerIterator.next();
            handler.showInformation(message);
        }
    }
    
    public boolean showQuestion(Message message){
        Iterator handlerIterator = messageHandlers.iterator();
        boolean result = true;
        while(handlerIterator.hasNext()){
            MessageHandler handler = (MessageHandler)handlerIterator.next();
            result = handler.showQuestion(message);
        }
        return result;
    }
    
    public boolean showConfirm(Message message){
        Iterator handlerIterator = messageHandlers.iterator();
        boolean result = true;
        while(handlerIterator.hasNext()){
            MessageHandler handler = (MessageHandler)handlerIterator.next();
            result = handler.showConfirm(message);
        }
        return result;
    }
	
    /**
     * Get the configuration object for the IVC
     * 
     * @return the configuration object
     */
    public Configuration getConfiguration() {
        return getDelegate().getConfiguration();
    }
    
	/**
	 * Gets the persistence registry. This is the global registry where all
	 * persisters will be housed for the system.
	 * 
	 * @return the persistence registry
	 */
	public PersistenceRegistry getPersistenceRegistry() {
        return getDelegate().getPersistenceRegistry();
	}
	
	/**
	 * Returns the model manager being used by the IVC
	 * 
	 * @return the Current Model Manager
	 */
	public ModelManager getModelManager() {  
        return getDelegate().getModelManager();
	}
	
	/**
	 * Returns the ConsoleManager used by IVC.  This ConsoleManager has
	 * methods to print information to the console, which will result
	 * in each ConsoleHandler that is registered with it being notified
	 * of the print so they can handle it accordingly.
	 * 
	 * @return the Consolemanager used by IVC.
	 */
	public ConsoleManager getConsole() {
        return getDelegate().getConsole();
	}
	
	/**
	 * Gets the scheduler currently in use by the system. The scheduler will allow for algorithms
	 * to be scheduled however the current scheduler does its scheduling.
	 * 
	 * @return the scheduler
	 */
	public Scheduler getScheduler() {
        return getDelegate().getScheduler();
	}
	
	/**
	 * Gets the logger associated with the IVC so that messages can
	 * be logged.
	 * 
	 * @return The IVC's logger.
	 */
	public Logger getErrorLogger() {        
        return getDelegate().getLogManager().getErrorLogger();
	}
	
	/**
	 * Gets the user logger associated with the IVC so that messages can
	 * be logged.
	 * 
	 * @return The IVC's logger.
	 */
	public Logger getUserLogger() {
        return getDelegate().getLogManager().getUserLogger(); 
	}
}

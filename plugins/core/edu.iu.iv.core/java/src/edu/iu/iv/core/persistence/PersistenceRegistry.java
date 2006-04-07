/*
 * Created on May 21, 2004
 * Shashikant Penumarthy
 */
package edu.iu.iv.core.persistence;

import java.io.IOException;
import java.util.List;


/**
 * Holds all the available registered Persisters in the IVC. This is the only way to find out
 * which Persisters are available in the system and hence all Persisters must be registered
 * with this registry.
 * 
 * It is strongly recommended that all users of the persistence layer use this registry 
 * instead of trying to use the persisters directly.
 * It may happen that several persisters are capable of persistence for a given
 * ResourceDescriptor or a given data model. In this case, its up to the implementation to
 * decide which persister to use.
 * 
 * User interfaces systems that have the capability of taking user input and giving 
 * feedback should use the registry to get a list of available persisters for a specified 
 * data model or resource descriptor and let the user choose which one to use.
 * Applications are expected to use the PersistenceRegistry and offload
 * the decision making onto the user.
 * 
 * @author TeamIVC
 * @version 0.1
 *
 */
public interface PersistenceRegistry {
	
	/**
	 * Registers the persister with the registry. A persister needs to be registered
	 * with the registry only once during the runtime of the IVC.
	 *
	 * @param persister The persister to add to the registry.
	 */
	public void register(Persister persister);

	/**
	 * Returns the list of persisters that can persist the specified data model. It is expected
	 * that several persisters would be able to save out a data model to a resource. Hence,
	 * UI based systems should take advantage of this and provide the user with several
	 * choices about what persister to use. If no persisters are found, then an empty list is
	 * returned. Note that this behaviour is different from the behaviour of the save() method
	 * of the PersistenceFacade, which throws a PersisterNotFoundException if no persisters are
	 * found capable of satisfying the save request.
	 *
	 * @param model The data model for which persistence is desired.
	 * @return The list of Persisters that can persist this model or empty list if not persisters
	 * are found.
	 * @see edu.iu.iv.core.persistence.Persister
	 * @see edu.iu.iv.core.persistence.PersistenceRegistry#save(Object, ResourceDescriptor)
	 * @see edu.iu.iv.core.persistence.PersisterNotFoundException
	 *
	 */
	public List getSupportingPersisters(Object model);

	/**
	  * Returns the list of persisters that can restore from the specified data resource.
	  * It is expected that several persisters would be able to load data from a resource.
	  * Hence, UI based systems should take advantage of this and provide the user with several
	  * choices about what persister to use. If no persisters are found, then an empty list is
	  * returned. Note that this behaviour is different from the behaviour of the load() method
	  * of the PersistenceFacade, which throws a PersisterNotFoundException if no persisters are
	  * found capable of satisfying the load request.
	  *
	  * @param source The data source from which the data model needs to be restored.
	  * @return The list of Persisters or an empty list if no persisters are found.
	  * 
	  * @see edu.iu.iv.core.persistence.Persister
	  * @see edu.iu.iv.core.persistence.PersistenceRegistry#load(ResourceDescriptor)
	  * @see edu.iu.iv.core.persistence.PersisterNotFoundException
	  */
	public List getSupportingPersisters(ResourceDescriptor source);

	/**
	 * Gets the list of all persisters currently registered with the registry. If nothing
	 * is registered then an empty list is returned. Applications that wish to select a
	 * suitable persister in their own way instead of using the default implementation
	 * should make use of this method. For example, an application might want to keep track
	 * of the frequency of usage of a certain persister in order to optimize the ones most
	 * used in addition to the average size of the data that is persisted with a certain persister.
	 * All such information about a persister can easily be maintained in a lookup table indexed
	 * by the persisters available in the system.
	 *
	 * @return The list of all registered persisters or an empty list if no persisters are
	 * registered.
	 */
	public List getPersisters();

	/**
	 * Finds a persister that can persist a specified data model to a specified data resource or
	 * one that can restore from a particular resource. Currently we assume that there is only
	 * one persister that maps a particular data model to a particular data resource. In future
	 * this method will likely be changed to return a list of persisters that can successfully
	 * satisfy this request, hence applications are discouraged from using it directly.
	 *
	 * @param model The data model that needs to be persisted or restored.
	 * @param resource The resource to which this data needs to be persisted to or restored
	 * from.
	 * @return The persister than can satisfy this request or null if none found.
	 */
	public Persister findPersister(Object model, ResourceDescriptor resource);

	/**
	 * Saves a model to a data resource such as a file or a database.
	 *
	 * @param model The model to be saved.
	 * @param destination The object describing the properties of the data resource
	 * where this model should be saved.
	 *
	 */
	
	public void save(Object model, ResourceDescriptor destination)
		throws IOException, PersistenceException ;

	/**
	 * Loads the given data model from the given resource. An example of a resource is a file on disk.
	 * This method tries to find a persister that can satisfy this request. If it fails to find a
	 * suitable persister, it throws an exception to signify this.
	 *
	 * @param model The data model to be loaded.
	 * @param source The resource from where the data model is to be loaded.
	 * @return The data model populated with the data from the resource or null if it fails.
	 * @throws PersisterNotFoundException
	 * @see edu.iu.iv.core.persistence.PersisterNotFoundException
	 */
	public Object load(ResourceDescriptor source) throws IOException, OutOfMemoryError, PersistenceException ;
	
	public void clear();
}

package edu.iu.iv.core.persistence;

import java.io.IOException;

import edu.iu.iv.common.property.PropertyAssignable;
import edu.iu.iv.common.property.PropertyMap;

/**
 * A Persister is responsible for saving and restoring models 
 * to and from a resource. It is recommended that all components in the IVC
 * communicate their persistence needs to the Persistence layer instead of directly
 * writing file readers and writers or establishing database connections. 
 * This ensures that the framework is 'aware' of all such processes at runtime. 
 * This awareness can be communicated to the user at all times; such transparency 
 * of operation is fundamental to the IVC.
 * 
 * Delegating the persistence operations to the persistence layer also helps the algorithm
 * implementations to concentrate on optimizing the algorithm itself and cleanly separates 
 * the algorithm from its data persistence functions.
 * 
 * @see edu.iu.iv.core.IVC
 * @see edu.iu.iv.core.persistence.ResourceDescriptor
 * 
 * @author Team IVC
 * @version 0.1
 */

// Created: ndeckard
// modified: Shashikant Penumarthy

public interface Persister extends PropertyAssignable {
	
	/**
	 * Persists a data model to a resource such as a file 
	 * or database.
	 * 
	 * @param model The model to be persisted.
	 * @param resource The resource to persist the model to.
	 * @throws Exception 
	 */
	public void persist(Object model, ResourceDescriptor resource) throws IOException, PersistenceException ;
	
	/**
	 * Restores a model from the specified resource. This method 
	 * 
	 * @param dataSource The Data Source to restore the model from.
	 * @return The model to be restored from the data source.
	 */
	public Object restore(ResourceDescriptor resource) throws IOException, OutOfMemoryError, PersistenceException ;
	
	/**
	 * Determines if this persister can persist the given data model.  
	 * 
	 * @param model The model for which persistence is desired.
	 * @return true if this persister can persist this object, false otherwise.
	 */
	public boolean canPersist(Object model) ;
	
	/**
	 * 
	 * @param resource The resource from which restoration is desired.
	 * An example of a resource is a file on disk.
	 * @return true if this persister can persist to or restore from this resource, false otherwise.
	 */
	public boolean canRestore(ResourceDescriptor resource);
	
	/**
	 * Gets a property map describing this persister and file format (if applicable).
	 * 
	 * If a persister does not wish to return a property map, it may return null. <i> Returning
	 * a null value is highly discouraged. </i> A persister must always provide enough information
	 * to let an application allow the user to make an informed choice.
	 * 
	 * @return The property map of persister properties or null if the persister does not wish to return
	 * any map.
	 * 
	 * @see edu.iu.iv.core.persistence.PersisterPropertyMap
	 */
	public PropertyMap getProperties() ;
}

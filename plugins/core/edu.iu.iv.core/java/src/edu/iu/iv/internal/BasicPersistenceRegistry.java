/*
 * Created on May 21, 2004
 *
 */
package edu.iu.iv.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.iu.iv.core.persistence.PersistenceException;
import edu.iu.iv.core.persistence.PersistenceRegistry;
import edu.iu.iv.core.persistence.Persister;
import edu.iu.iv.core.persistence.PersisterNotFoundException;
import edu.iu.iv.core.persistence.ResourceDescriptor;

/**
 * @author Shashikant
 * @version 0.1
 *
 */
public class BasicPersistenceRegistry implements PersistenceRegistry {

	private List persisterList = new ArrayList();

	/**
	 * @see edu.iu.iv.core.persistence.PersistenceRegistry#register(edu.iu.iv.core.persistence.Persister)
	 */
	public void register(Persister persister) {
		if (!persisterList.contains(persister)){
		    //System.out.println("registered: " + persister.getClass().getName());
			persisterList.add(persister);
		}
	}

	/**
	 * @see edu.iu.iv.core.persistence.PersistenceRegistry#getSupportingPersisters(java.lang.Object)
	 */
	public List getSupportingPersisters(Object model) {
		List supportingList = new ArrayList();
		Iterator iterator = persisterList.iterator();

		while (iterator.hasNext()) {
			Persister persister = (Persister) iterator.next();
			if (persister.canPersist(model))
				supportingList.add(persister);
		}
		return supportingList;
	}

	/**
	 * @see edu.iu.iv.core.persistence.PersistenceRegistry#getSupportingPersisters(edu.iu.iv.core.persistence.ResourceDescriptor)
	 */
	public List getSupportingPersisters(ResourceDescriptor resource) {
		List supportingList = new ArrayList();
		Iterator iterator = persisterList.iterator();
		while (iterator.hasNext()) {
			Persister persister = (Persister) iterator.next();
			if (persister.canRestore(resource))
			    supportingList.add(persister);
		}
		return supportingList;
	}

	/**
	 * @see edu.iu.iv.core.persistence.PersistenceRegistry#getPersisters()
	 */
	public List getPersisters() {
		return persisterList;
	}

	public Persister findPersister(Object model, ResourceDescriptor resource) {
		Iterator iterator = persisterList.iterator() ;
		Persister persister = null ;
		boolean found = false ;
		
		while (iterator.hasNext()) {
			persister = (Persister) iterator.next() ;
			if (persister.canPersist(model) && persister.canRestore(resource)) {
				found = true ;
				break ;
			}
		}
		if (!found)
			persister = null ;
			
		return persister ;
	}
	
	/**
		 * Attempts to save the specified data model's data to the specified resource. This method scans the
		 * available persisters to find a suitable one to satisfy this request, failing which it throws an
		 * exception to signify the fact that no suitable persister was found.
		 *
		 * @see edu.iu.iv.core.persistence.PersistenceRegistry#save(Object, ResourceDescriptor)
		 */
		public void save(Object model, ResourceDescriptor destination)
			throws IOException, PersistenceException {
			Persister persister = findPersister(model, destination);

			if (persister != null) {
				persister.persist(model, destination);
			}
			else {
				throw new PersisterNotFoundException("Persister not found ");
			}
		}

		/**
		 * Attempts to load the specified data model with data from the specified source.
		 * This method scans the available persisters to find a suitable persister, failing which
		 * it throws an exception to signify the fact that no suitable persister was found.
		 * Once it gets a list of persisters the method uses one of the available persisters to load
		 * the model in memory.
		 *
		 * @see edu.iu.iv.core.persistence.PersistenceRegistry#load(ResourceDescriptor)
		 */
		public Object load(ResourceDescriptor source)
			throws IOException, OutOfMemoryError, PersistenceException {
			Object model = null ;
		
			List persisterList = getSupportingPersisters(source) ;
			if (!persisterList.isEmpty()) {
				model = ((Persister)persisterList.get(0)).restore(source) ;
			}
			else {
				throw new PersisterNotFoundException("Persister not found.");
			}
			return model ;
		}
		
		public void clear(){
		    persisterList.clear();
		}
}

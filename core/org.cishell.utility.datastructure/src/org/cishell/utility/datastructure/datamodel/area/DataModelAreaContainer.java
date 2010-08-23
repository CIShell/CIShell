package org.cishell.utility.datastructure.datamodel.area;

import java.util.Collection;

import org.cishell.utility.datastructure.datamodel.exception.ModelStructureException;
import org.cishell.utility.datastructure.datamodel.exception.UniqueNameException;

// TODO: addToContainer type stuff
public interface DataModelAreaContainer {
	public Collection<String> getAreaNames();
	public Collection<DataModelArea> getAreas();
	public DataModelArea getArea(String name);
	public DataModelArea createArea(String name) throws UniqueNameException;
	/**
	 * @throws ClassCastException if componentForArea is not of the proper GUI container type.
	 * @throws ModelStructureException if componentForArea's parent is not this area's internal
	 *  GUI container.
	 * @throws UniqueNameException if an area with name already exists.
	 */
	public DataModelArea createArea(String name, Object componentForArea)
			throws ClassCastException, ModelStructureException, UniqueNameException;
	public void addArea(DataModelArea area)
			throws ClassCastException, ModelStructureException, UniqueNameException;
	
	//TODO: why is this returning boolean?
	public boolean areaDisposed(String name);
	public boolean areaDisposed(DataModelArea area);
}
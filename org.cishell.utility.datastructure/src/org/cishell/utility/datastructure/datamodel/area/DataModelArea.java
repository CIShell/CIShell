package org.cishell.utility.datastructure.datamodel.area;

import java.util.Collection;

import org.cishell.utility.datastructure.datamodel.exception.ModelStructureException;
import org.cishell.utility.datastructure.datamodel.exception.UniqueNameException;
import org.cishell.utility.datastructure.datamodel.field.DataModelField;
import org.cishell.utility.datastructure.datamodel.field.DataModelFieldContainer;

/**
 * DataModelArea corresponds to a physical area tied to a DataModel GUI.
 * DataModelAreas can contain other DataModelAreas, as well as DataModelFields.
 */
public interface DataModelArea extends DataModelAreaContainer, DataModelFieldContainer {
	// Miscellaneous methods

	public DataModelArea getParentArea();
	public Object getParentComponent();
	public String getName();

	// DataModelAreaContainer methods

	public Collection<String> getAreaNames();
	public Collection<DataModelArea> getAreas();
	public DataModelArea getArea(String name);
	public DataModelArea createArea(String name) throws UniqueNameException;
	public DataModelArea createArea(String name, Object componentForArea)
			throws ClassCastException, ModelStructureException, UniqueNameException;
	public boolean areaDisposed(String name);
	public boolean areaDisposed(DataModelArea area);

	// DataModelFieldContainer methods

	public Collection<String> getFieldNames();
	public Collection<DataModelField<?>> getFields();
	public DataModelField<?> getField(String fieldName);
	public<T> void addField(DataModelField<T> field) throws UniqueNameException;
	public boolean fieldDisposed(String fieldName);
	public<T> boolean fieldDisposed(DataModelField<T> field);
}
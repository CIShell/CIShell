package org.cishell.utility.datastructure.datamodel;

import java.util.Collection;
import java.util.Map;

import org.cishell.utility.datastructure.datamodel.area.DataModelArea;
import org.cishell.utility.datastructure.datamodel.area.DataModelAreaContainer;
import org.cishell.utility.datastructure.datamodel.exception.ModelStructureException;
import org.cishell.utility.datastructure.datamodel.exception.UniqueNameException;
import org.cishell.utility.datastructure.datamodel.field.DataModelField;
import org.cishell.utility.datastructure.datamodel.group.DataModelGroup;

/**
 * DataModel is intended to organize and structure the manipulation of data elements
 * (@link DataModelField) in a generic fashion.
 * DataModel is inspired by GUIs--a GUI requiring user input (through various input fields) could
 * be tied to a DataModel, which automatically contains values from the GUI.  When the GUI is done
 * (as in, the user finished), the DataModel could then be used for data retrieval in a
 * GUI-agnostic fashion.
 * Though of course implementation-specific, DataModel is designed to allow the
 * addition/modification/deletion of user input fields without any knowledge of the particular
 * (type of) GUI the DataModel is tied to.  In other words, a DataModel could be purely data,
 * AWT-/Swing-, or SWT-based, and input fields could still be interacted with on an
 * abstract level.
 * DataModels consist of areas {@link DataModelArea} and groups (@link DataModelGroup).
 * Areas and groups both contain fields (@link DataModelField).
 * Where as groups are purely organization structures (so one could get all fields within a group,
 * say), areas are also tied to physical composite GUI structures so GUI-agnostic code can add
 * fields to a given specific area.
 * When adding fields, an area is not required.  This implies that areas are not required for a
 * valid model, and thus the ability to extend areas with sub-areas and/or fields is up to the
 * GUI designer.
 * A group is required when adding fields.  DataModel itself actually provides no mechanism for
 * retrieving fields directly.  Rather, the recommended/somewhat-enforced method for field
 * retrieval is via groups.
 * Currently, there is no mechanism for abstractly expressing field component styles.
 * DataModel is not thread-synchronized.
 * Further usage details are described on a per-method basis.
 */
public interface DataModel extends DataModelAreaContainer {
	public static final String DEFAULT_GROUP_NAME = "defaultGroup";

	// Miscellaneous methods

	/**
	 * Set the parent GUI component.
	 * GUI-agnostic on the interface level.
	 * The GUI-specific implementations of this interface must type check parent for validity.
	 * This has to exist so fields can be added to GUIs without knowing about the GUI or how the
	 * GUI works.
	 * Parent components can be retrieved from specific fields or specific areas.
	 * 
	 * @param currentParentComponent the current parent/containing GUI component to which this
	 * model should add subsequent fields to.
	 */
	public void setCurrentParentComponent(Object currentParentComponent) throws ClassCastException;

	// DataModelAreaContainer methods

	/** {@inheritDoc} */
	public Collection<String> getAreaNames();
	/** {@inheritDoc} */
	public Collection<DataModelArea> getAreas();
	/** {@inheritDoc} */
	public DataModelArea getArea(String name);
	/** {@inheritDoc} */
	public DataModelArea createArea(String name) throws UniqueNameException;
	/** {@inheritDoc} */
	public DataModelArea createArea(String name, Object componentForArea)
			throws ClassCastException, ModelStructureException, UniqueNameException;
	/** {@inheritDoc} */
	public void addArea(DataModelArea area)
			throws ClassCastException, ModelStructureException, UniqueNameException;
	/** {@inheritDoc} */
	public boolean areaDisposed(String name);
	/** {@inheritDoc} */
	public boolean areaDisposed(DataModelArea area);

	// Group methods

	/** @return all of the group names in this DataModel. */
	public Collection<String> getGroupNames();
	/** @return all of the groups in this DataModel. */
	public Collection<DataModelGroup> getGroups();
	/**
	 * Get a group by specific name.
	 * 
	 * @return the DataModelGroup with name if found.  Otherwise, null.
	 */
	public DataModelGroup getGroup(String name);
	/**
	 * Explicitly create a group with name.
	 * 
	 * @param name the name of the new group.
	 * @return the created group.
	 * @throws UniqueNameException if a group with name has already been created.
	 */
	public DataModelGroup createGroup(String name) throws UniqueNameException;

	// Add Field methods

	/**
	 * Adds a checkbox field to the parent/containing GUI component of the specified area or the
	 *  current parent/containing GUI component if no area is specified.
	 * Also creates the GUI-specific component.
	 * 
	 * @see addField
	 * @param name the name of the new field.
	 * @param areaName the name of the area to add this field to.  Not required.
	 * @param groupName the name of the group to add this field to.  Not required, though a default
	 *  will be used if not provided.
	 * @param defaultOn if true, the default value of this check box will be checked.
	 * @throws UniqueNameException if a field with name has already been added.
	 */
	public DataModelField<Boolean> addCheckBox(
			String name, String areaName, String groupName, boolean defaultOn)
			throws UniqueNameException;
	/**
	 * Adds a single-selection drop down field to the parent/containing GUI component of the
	 *  specified area or the current parent/containing GUI component if no area is specified.
	 * Also creates the GUI-specific component.
	 * 
	 * @see addField
	 * @param name the name of the new field.
	 * @param areaName the name of the area to add this field to.  Not required.
	 * @param groupName the name of the group to add this field to.  Not required, though a default
	 *  will be used if not provided.
	 * @param selectedIndex the element of unorderedOptionLabels to have selected by default.
	 * @param unorderedOptionLabels the literal items that get displayed as user-selectable
	 *  options.  Note: selectedIndex should fall within these bounds, and these should be exactly
	 *  the keys into optionValuesByLabels.
	 * @param optionValuesByLabels the option label-to-value mapping used to tie user-selected
	 *  options to actual values used by code later on.
	 * @throws UniqueNameException if a field with name has already been added.
	 */
	public<T> DataModelField<T> addDropDown(
			String name,
			String areaName,
			String groupName,
			int selectedIndex,
			Collection<String> unorderedOptionLabels,
			Map<String, T> optionValuesByLabels) throws UniqueNameException;
	/**
	 * Adds a single-selection list field to the parent/containing GUI component of the specified
	 *  area or the current parent/containing GUI component if no area is specified.
	 * Also creates the GUI-specific component.
	 * 
	 * @see addField
	 * @param name the name of the new field.
	 * @param areaName the name of the area to add this field to.  Not required.
	 * @param groupName the name of the group to add this field to.  Not required, though a default
	 *  will be used if not provided.
	 * @param selectedIndex the element of unorderedOptionLabels to have selected by default.
	 * @param unorderedOptionLabels the literal items that get displayed as user-selectable
	 *  options.  Note: selectedIndex should fall within these bounds, and these should be exactly
	 *  the keys into optionValuesByLabels.
	 * @param optionValuesByLabels the option label-to-value mapping used to tie user-selected
	 *  options to actual values used by code later on.
	 * @throws UniqueNameException if a field with name has already been added.
	 */
	public<T> DataModelField<T> addList(
			String name,
			String areaName,
			String groupName,
			int selectedIndex,
			Collection<String> unorderedOptionLabels,
			Map<String, T> optionValuesByLabels) throws UniqueNameException;
	/**
	 * Adds a text field to the parent/containing GUI component of the specified area or the
	 *  current parent/containing GUI component if no area is specified.
	 * 
	 * @see addField
	 * @param name the name of the new field.
	 * @param areaName the name of the area to add this field to.  Not required.
	 * @param groupName the name of the group to add this field to.  Not required, though a default
	 *  will be used if not provided.
	 * @param defaultValue the default value of this text field.
	 * @param isMultiLined if true, this text field will be multi-lined.  Otherwise, it will be
	 *  single-lined.
	 * @throws UniqueNameException if a field with name has already been added.
	 */
	public DataModelField<String> addText(
			String name,
			String areaName,
			String groupName,
			String defaultValue,
			boolean isMultiLined) throws UniqueNameException;
	/**
	 * Adds an already-created field to the area named areaName and group named groupName, both
	 *  if specified.
	 * Note: It is up to the caller to have added field to the specified area's parent/containing
	 *  GUI component, and the implementing class should verify this. 
	 * 
	 * @param areaName the name of the area to add this field to.  Not required.
	 * @param groupName the name of the group to add this field to.  Not required, though a default
	 *  will be used if not provided.
	 * @throws UniqueNameException if a field with name has already been added.
	 */
	public<T> void addField(String areaName, String groupName, DataModelField<T> field)
			throws UniqueNameException;
}
package org.cishell.utility.swt.model;

import org.cishell.utility.datastructure.datamodel.area.AbstractDataModelArea;
import org.cishell.utility.datastructure.datamodel.area.DataModelArea;
import org.cishell.utility.datastructure.datamodel.exception.ModelStructureException;
import org.cishell.utility.datastructure.datamodel.exception.UniqueNameException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

public class SWTModelArea extends AbstractDataModelArea<Widget, Composite> {
	private int newAreaStyle;
	private Composite swtArea;

	public SWTModelArea(
			DataModelArea parentArea, Composite parentComponent, String name, int newAreaStyle) {
		this(
			parentArea,
			parentComponent,
			name,
			new Composite(parentComponent, newAreaStyle),
			newAreaStyle);
	}

	public SWTModelArea(
			DataModelArea parentArea,
			Composite parentComponent,
			String name,
			Composite swtArea,
			int newAreaStyle) {
		super(parentArea, parentComponent, name);
		this.newAreaStyle = newAreaStyle;
		this.swtArea = swtArea;
	}

	public DataModelArea createArea(String name, Object componentForArea)
			throws ClassCastException, ModelStructureException, UniqueNameException {
		if (getArea(name) != null) {
			String exceptionMessage = String.format(
				"The area '%s' already exists.  All areas must have unique names.", name);
			throw new UniqueNameException(exceptionMessage);
		} else {
			DataModelArea area = new SWTModelArea(
				this,
				getParentComponentWithType(),
				name,
				(Composite) componentForArea,
				this.newAreaStyle);
			addArea(area);

			return area;
		}
	}

	protected DataModelArea internalCreateArea(String name) {
		return new SWTModelArea(this, this.swtArea, name, this.newAreaStyle);
	}
}
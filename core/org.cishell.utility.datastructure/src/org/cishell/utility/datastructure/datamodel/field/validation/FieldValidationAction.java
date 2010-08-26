package org.cishell.utility.datastructure.datamodel.field.validation;

import java.util.Collection;

public interface FieldValidationAction {
	public void doesValidate();
	public void doesNotValidate(Collection<String> errorMessages);
}
package org.cishell.utility.swt.model.field.validation;

import java.util.Collection;

import org.cishell.utility.datastructure.datamodel.field.validation.FieldValidator;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class Utilities {
	public static<T> Collection<FieldValidator<T>> allFieldValidatorsExcept(
			Collection<FieldValidator<T>> allValidators,
			final Collection<FieldValidator<T>> except) {
		return Collections2.filter(
			allValidators, new Predicate<FieldValidator<T>>() {
				public boolean apply(FieldValidator<T> input) {
					return !except.contains(input);
				}
			});
	}
}
package org.cishell.utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.List;

import org.cishell.reference.service.metatype.BasicObjectClassDefinition;
import org.cishell.utilities.mutateParameter.AttributeDefinitionTransformer;
import org.cishell.utilities.mutateParameter.ObjectClassDefinitionTransformer;
import org.cishell.utilities.mutateParameter.defaultvalue.DefaultDefaultValueTransformer;
import org.cishell.utilities.mutateParameter.dropdown.DefaultDropdownTransformer;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

import prefuse.data.Table;

public class MutateParameterUtilities {
	/* TODO The mutateParameter subpackage is meant to replace most of the loops
	 * that invoke the formFooAttributeDefinition methods.
	 */
	
	public static AttributeDefinition formLabelAttributeDefinition(
			AttributeDefinition oldAttributeDefinition, Table table)
			throws ColumnNotFoundException {
		Collection<String> originalLabelColumnNames =
			Arrays.asList(TableUtilities.getValidStringColumnNamesInTable(table));
	
		AttributeDefinition labelAttributeDefinition = cloneToDropdownAttributeDefinition(
			oldAttributeDefinition, originalLabelColumnNames, originalLabelColumnNames);

		return labelAttributeDefinition;
	}
	
	/**
	 * Support additional default labels to be added to the front.
	 * TODO: Look at other utilities for future refactoring.  (This may not be needed.)
	 */
	public static AttributeDefinition formLabelAttributeDefinition(
			AttributeDefinition oldAttributeDefinition, Table table, List<String> additionalLabels)
			throws ColumnNotFoundException {
		Collection<String> originalLabelColumnNames = 
			Arrays.asList(TableUtilities.getValidStringColumnNamesInTable(table));

		Collection<String> newLabelColumnNames = new ArrayList<String>();
		newLabelColumnNames.addAll(additionalLabels);
		newLabelColumnNames.addAll(originalLabelColumnNames);

		AttributeDefinition labelAttributeDefinition = cloneToDropdownAttributeDefinition(
			oldAttributeDefinition, newLabelColumnNames, newLabelColumnNames);

		return labelAttributeDefinition;
	}

	public static AttributeDefinition formDateAttributeDefinition(
			AttributeDefinition oldAttributeDefinition, Table table)
			throws ColumnNotFoundException {
		Collection<String> validDateColumnsInTable =
			Arrays.asList(TableUtilities.getValidDateColumnNamesInTable(table));

		AttributeDefinition dateAttributeDefinition = cloneToDropdownAttributeDefinition(
			oldAttributeDefinition, validDateColumnsInTable, validDateColumnsInTable);

		return dateAttributeDefinition;
	}

	public static AttributeDefinition formIntegerAttributeDefinition(
			AttributeDefinition oldAttributeDefinition, Table table)
			throws ColumnNotFoundException {
		Collection<String> validIntegerColumnsInTable =
			Arrays.asList(TableUtilities.getValidIntegerColumnNamesInTable(table));

		AttributeDefinition integerAttributeDefinition = cloneToDropdownAttributeDefinition(
			oldAttributeDefinition, validIntegerColumnsInTable, validIntegerColumnsInTable);

		return integerAttributeDefinition;
	}

	public static AttributeDefinition formNumberAttributeDefinition(
			AttributeDefinition oldAttributeDefinition, Table table)
			throws ColumnNotFoundException {
		Collection<String> validNumberColumnsInTable =
			Arrays.asList(TableUtilities.getValidNumberColumnNamesInTable(table));
		
		AttributeDefinition numberAttributeDefinition = cloneToDropdownAttributeDefinition(
			oldAttributeDefinition, validNumberColumnsInTable, validNumberColumnsInTable);
		
		return numberAttributeDefinition;
	}

	public static AttributeDefinition formAttributeDefinitionFromMap(
			AttributeDefinition oldAttributeDefinition,
			Map<String, String> attributes,
			Collection<String> types,
			Collection<String> keysToSkip,
			Collection<String> keysToAddToFront) {
		Collection<String> validNumberKeysInMap =
			MapUtilities.getValidKeysOfTypesInMap(attributes, types, keysToSkip);
		validNumberKeysInMap =
			ArrayListUtilities.unionCollections(keysToAddToFront, validNumberKeysInMap, null);
		
		AttributeDefinition numberAttributeDefinition = cloneToDropdownAttributeDefinition(
			oldAttributeDefinition, validNumberKeysInMap, validNumberKeysInMap);
		
		return numberAttributeDefinition;
	}

	public static AttributeDefinition cloneToDropdownAttributeDefinition(
			AttributeDefinition oldAD,
			final Collection<String> optionLabels,
			final Collection<String> optionValues) {
		AttributeDefinitionTransformer transformer = new DefaultDropdownTransformer() {
			public boolean shouldTransform(AttributeDefinition ad) {
				return true;
			}

			public String[] transformOptionLabels(String[] oldOptionLabels) {
				return optionLabels.toArray(new String[0]);
			}

			public String[] transformOptionValues(String[] oldOptionValues) {
				return optionValues.toArray(new String[0]);
			}
		};
			
		return transformer.transform(oldAD);
	}
	
	@SuppressWarnings("unchecked")	// Raw Collection
	public static BasicObjectClassDefinition mutateToDropdown(
			ObjectClassDefinition oldOCD,
			final String parameterID,
			Collection optionLabels,
			Collection optionValues) {
		return mutateToDropdown(
			oldOCD,
			parameterID,
			(String[])optionLabels.toArray(new String[0]),
			(String[])optionValues.toArray(new String[0]));
	}
	
	/* Convenience method for a common mutation:
	 * Replacing a parameter (identified by its ID) with a dropdown list of
	 * options.
	 */
	public static BasicObjectClassDefinition mutateToDropdown(
			ObjectClassDefinition oldOCD,
			final String parameterID,
			final String[] optionLabels,
			final String[] optionValues) {
		AttributeDefinitionTransformer dropdownTransformer = new DefaultDropdownTransformer() {
			public boolean shouldTransform(AttributeDefinition ad) {
				return ad.getID().equals(parameterID);
			}
			
			public String[] transformOptionLabels(
					String[] oldOptionLabels) {
				return optionLabels;
			}
			public String[] transformOptionValues(
					String[] oldOptionValues) {
				return optionValues;
			}
		};
		
		return ObjectClassDefinitionTransformer.apply(
			dropdownTransformer, oldOCD, new ArrayList<String>());
	}
	
	public static BasicObjectClassDefinition mutateDefaultValue(
			ObjectClassDefinition oldOCD,
			final String parameterID,
			final String defaultValue) {
		AttributeDefinitionTransformer transformer = new DefaultDefaultValueTransformer() {
			public boolean shouldTransform(AttributeDefinition ad) {
				return ad.getID().equals(parameterID);
			}

			public String transformDefaultValue(String[] oldDefaultValue) {
				return defaultValue;
			}
		};
		
		return ObjectClassDefinitionTransformer.apply(
			transformer, oldOCD, new ArrayList<String>());
	}
	
	public static BasicObjectClassDefinition createNewParameters(
			ObjectClassDefinition oldParameters) {
		try {
			return new BasicObjectClassDefinition(
				oldParameters.getID(),
				oldParameters.getName(),
				oldParameters.getDescription(),
				oldParameters.getIcon(16));
		}
		catch (IOException e) {
			return new BasicObjectClassDefinition(
				oldParameters.getID(),
				oldParameters.getName(),
				oldParameters.getDescription(),
				null);
		}
	}
}
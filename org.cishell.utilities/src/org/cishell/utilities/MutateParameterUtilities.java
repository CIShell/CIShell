package org.cishell.utilities;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;

import org.cishell.reference.service.metatype.BasicObjectClassDefinition;
import org.cishell.utilities.mutateParameter.AttributeDefinitionTransformer;
import org.cishell.utilities.mutateParameter.NullDropdownTransformer;
import org.cishell.utilities.mutateParameter.ObjectClassDefinitionTransformer;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

import prefuse.data.Table;

public class MutateParameterUtilities {
	/* TODO The mutateParameter subpackage is meant to eliminate all of the loops
	 * that invoke the formFooAttributeDefinition methods.
	 */
	
	public static AttributeDefinition formLabelAttributeDefinition(
			AttributeDefinition oldAttributeDefinition, Table table)
				throws ColumnNotFoundException {
		String[] validStringColumnsInTable =
			TableUtilities.getValidStringColumnNamesInTable(table);
	
		AttributeDefinition labelAttributeDefinition =
			cloneToDropdownAttributeDefinition(oldAttributeDefinition,
											   validStringColumnsInTable,
											   validStringColumnsInTable);
	
		return labelAttributeDefinition;
	}

	public static AttributeDefinition formDateAttributeDefinition(
			AttributeDefinition oldAttributeDefinition, Table table)
				throws ColumnNotFoundException {
		String[] validDateColumnsInTable =
			TableUtilities.getValidDateColumnNamesInTable(table);

		AttributeDefinition dateAttributeDefinition =
			cloneToDropdownAttributeDefinition(oldAttributeDefinition,
											   validDateColumnsInTable,
											   validDateColumnsInTable);

		return dateAttributeDefinition;
	}

	public static AttributeDefinition formIntegerAttributeDefinition(
			AttributeDefinition oldAttributeDefinition, Table table)
				throws ColumnNotFoundException {
		String[] validIntegerColumnsInTable =
			TableUtilities.getValidIntegerColumnNamesInTable(table);

		AttributeDefinition integerAttributeDefinition =
			cloneToDropdownAttributeDefinition(oldAttributeDefinition,
											   validIntegerColumnsInTable,
											   validIntegerColumnsInTable);

		return integerAttributeDefinition;
	}
	
	public static AttributeDefinition formNumberAttributeDefinition(
			AttributeDefinition oldAttributeDefinition, Table table)
				throws ColumnNotFoundException {
		String[] validNumberColumnsInTable =
			TableUtilities.getValidNumberColumnNamesInTable(table);
		
		AttributeDefinition numberAttributeDefinition =
			cloneToDropdownAttributeDefinition(oldAttributeDefinition,
											   validNumberColumnsInTable,
											   validNumberColumnsInTable);
		
		return numberAttributeDefinition;
	}
	
	// TODO: Change LinkedHashMap to Map?
	public static AttributeDefinition formAttributeDefinitionFromMap(
			AttributeDefinition oldAttributeDefinition,
			LinkedHashMap map,
			String[] types,
			String[] keysToSkip,
			String[] keysToAdd) {
		String[] validNumberKeysInMap =
			MapUtilities.getValidKeysOfTypesInMap(
				map, types, keysToSkip, keysToAdd);
		
		AttributeDefinition numberAttributeDefinition =
			cloneToDropdownAttributeDefinition(oldAttributeDefinition,
											   validNumberKeysInMap,
											   validNumberKeysInMap);
		
		return numberAttributeDefinition;
	}
	
	public static AttributeDefinition cloneToDropdownAttributeDefinition(
			AttributeDefinition oldAD,
			final String[] optionLabels,
			final String[] optionValues) {
		AttributeDefinitionTransformer transformer =
			new NullDropdownTransformer() {
				public boolean shouldTransform(AttributeDefinition ad) {
					return true;
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
			
		return transformer.transform(oldAD);
	}
	
	public static BasicObjectClassDefinition mutateToDropdown(
			ObjectClassDefinition oldOCD,
			final String parameterID,
			Collection optionLabels,
			Collection optionValues) {
		return mutateToDropdown(oldOCD,
								parameterID,
								(String[]) optionLabels.toArray(new String[0]),
								(String[]) optionValues.toArray(new String[0]));
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
		AttributeDefinitionTransformer dropdownTransformer =
			new NullDropdownTransformer() {
				public boolean shouldTransform(AttributeDefinition ad) {
					return parameterID.equals(ad.getID());
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
		
		return ObjectClassDefinitionTransformer.apply(dropdownTransformer,
													  oldOCD);
	}
	
	public static BasicObjectClassDefinition createNewParameters(
			ObjectClassDefinition oldParameters) {
		try {
			return
				new BasicObjectClassDefinition(oldParameters.getID(),
											   oldParameters.getName(),
											   oldParameters.getDescription(),
											   oldParameters.getIcon(16));
		}
		catch (IOException e) {
			return new BasicObjectClassDefinition
				(oldParameters.getID(),
				 oldParameters.getName(),
				 oldParameters.getDescription(), null);
		}
	}
}
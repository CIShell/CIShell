package org.cishell.utilities;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.cishell.reference.service.metatype.BasicAttributeDefinition;
import org.cishell.reference.service.metatype.BasicObjectClassDefinition;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

import prefuse.data.Table;

public class MutateParameterUtilities {
	public static AttributeDefinition formLabelAttributeDefinition
		(AttributeDefinition oldAttributeDefinition, Table table)
			throws ColumnNotFoundException
	{
		String[] validStringColumnsInTable =
			TableUtilities.getValidStringColumnNamesInTable(table);
	
		AttributeDefinition labelAttributeDefinition =
			new BasicAttributeDefinition(oldAttributeDefinition.getID(),
										 oldAttributeDefinition.getName(),
										 oldAttributeDefinition.getDescription(),
										 AttributeDefinition.STRING,
										 validStringColumnsInTable,
										 validStringColumnsInTable);
	
		return labelAttributeDefinition;
	}

	public static AttributeDefinition formDateAttributeDefinition
		(AttributeDefinition oldAttributeDefinition, Table table)
			throws ColumnNotFoundException
	{
		String[] validDateColumnsInTable =
			TableUtilities.getValidDateColumnNamesInTable(table);

		AttributeDefinition dateAttributeDefinition =
			new BasicAttributeDefinition(oldAttributeDefinition.getID(),
										 oldAttributeDefinition.getName(),
										 oldAttributeDefinition.getDescription(),
										 AttributeDefinition.STRING,
										 validDateColumnsInTable,
										 validDateColumnsInTable);

		return dateAttributeDefinition;
	}

	public static AttributeDefinition formIntegerAttributeDefinition
		(AttributeDefinition oldAttributeDefinition, Table table)
			throws ColumnNotFoundException
	{
		String[] validIntegerColumnsInTable =
			TableUtilities.getValidIntegerColumnNamesInTable(table);

		AttributeDefinition integerAttributeDefinition =
			new BasicAttributeDefinition(oldAttributeDefinition.getID(),
										 oldAttributeDefinition.getName(),
										 oldAttributeDefinition.getDescription(),
										 AttributeDefinition.STRING,
										 validIntegerColumnsInTable,
										 validIntegerColumnsInTable);

		return integerAttributeDefinition;
	}
	
	public static AttributeDefinition formNumberAttributeDefinition
		(AttributeDefinition oldAttributeDefinition, Table table)
			throws ColumnNotFoundException {
		String[] validNumberColumnsInTable =
			TableUtilities.getValidNumberColumnNamesInTable(table);
		
		AttributeDefinition numberAttributeDefinition =
			new BasicAttributeDefinition(oldAttributeDefinition.getID(),
										 oldAttributeDefinition.getName(),
										 oldAttributeDefinition.getDescription(),
										 AttributeDefinition.STRING,
										 validNumberColumnsInTable,
										 validNumberColumnsInTable);
		
		return numberAttributeDefinition;
	}
	
	public static AttributeDefinition formAttributeDefinitionFromMap
			(AttributeDefinition oldAttributeDefinition,
			 LinkedHashMap map,
			 String[] types,
			 String[] keysToSkip,
			 String[] keysToAdd) {
		String[] validNumberKeysInMap =
			MapUtilities.getValidKeysOfTypesInMap(
				map, types, keysToSkip, keysToAdd);
		
		AttributeDefinition numberAttributeDefinition =
			new BasicAttributeDefinition(
				oldAttributeDefinition.getID(),
				oldAttributeDefinition.getName(),
				oldAttributeDefinition.getDescription(),
				AttributeDefinition.STRING,
				validNumberKeysInMap,
				validNumberKeysInMap);
		
		return numberAttributeDefinition;
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
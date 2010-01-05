package org.cishell.reference.gui.persistence.view.core;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.cishell.service.database.Database;
import org.cishell.utilities.FileUtilities;

public class DatabaseSchemaOverviewGenerator {

	private static final String newLine = System.getProperty("line.separator");

	public static File generateDatabaseSchemaOverview(Database db) throws Exception {

		// Setup

		Connection dbConnection = db.getConnection();
		String applicationSchemaName = db.getApplicationSchemaName();

		StringBuffer dbOverview = new StringBuffer();

		// Generating text

		String headerText = getHeaderText();
		dbOverview.append(headerText);
		List<String> tableNames = getTableNames(dbConnection, applicationSchemaName);
		for (String tableName : tableNames) {

			String tableSchemaText = getTableSchemaAsText(dbConnection, tableName,
					applicationSchemaName);
			String foreignKeyReferencesText = getForeignKeyReferencesAsText(dbConnection,
					tableName, applicationSchemaName);

			dbOverview.append(tableSchemaText);
			dbOverview.append(foreignKeyReferencesText);
			dbOverview.append(newLine);
		}

		// Returning text as file

		File dbOverviewFile = FileUtilities.writeTextIntoTemporaryDirectory(dbOverview.toString(),
				"txt");
		return dbOverviewFile;
	}

	private static String getHeaderText() {
		StringBuffer headerText = new StringBuffer();

		headerText.append("Database Tables:" + newLine);
		headerText.append("********************" + newLine);
		headerText.append(newLine);
		headerText.append(newLine);

		return headerText.toString();
	}

	// write the table's name, and the names and types of all the columns in
	// that table
	private static String getTableSchemaAsText(Connection dbConnection, String tableName,
			String applicationSchemaName) throws SQLException {

		StringBuffer tableSchemaAsText = new StringBuffer();
		tableSchemaAsText.append(tableName + " (");

		ResultSet columns = dbConnection.getMetaData().getColumns(null, applicationSchemaName,
				tableName, null);
		while (columns.next()) {
			String columnName = columns.getString("COLUMN_NAME");
			String columnType = columns.getString("TYPE_NAME");

			tableSchemaAsText.append(" " + columnName + " " + columnType + ",");
		}
		tableSchemaAsText.deleteCharAt(tableSchemaAsText.length() - 1); // (delete
																		// final
																		// comma)
		tableSchemaAsText.append(")" + newLine);

		return tableSchemaAsText.toString();
	}

	// write which foreign keys in the table reference which other tables
	private static String getForeignKeyReferencesAsText(Connection dbConnection, String tableName,
			String applicationSchemaName) throws SQLException {
		StringBuffer foreignKeyReferencesAsText = new StringBuffer();

		ResultSet foreignKeys = dbConnection.getMetaData().getImportedKeys(null,
				applicationSchemaName, tableName);
		while (foreignKeys.next()) {
			String foreignKey = foreignKeys.getString("FKCOLUMN_NAME");
			String referencedTable = foreignKeys.getString("PKTABLE_NAME");
			String referencedTableKey = foreignKeys.getString("PKCOLUMN_NAME");

			foreignKeyReferencesAsText.append("    " + foreignKey + " -----> " + referencedTable
					+ "." + referencedTableKey);
			foreignKeyReferencesAsText.append(newLine);
		}
		return foreignKeyReferencesAsText.toString();
	}

	// ---UTIL--

	private static List<String> getTableNames(Connection dbConnection, String defaultSchemaName)
			throws SQLException {
		DatabaseMetaData dbMetadata = dbConnection.getMetaData();
		ResultSet allTableNames = dbMetadata.getTables(null, defaultSchemaName, null, null);

		// Names of tables that are not system tables.
		List<String> nonSystemTableNames = new ArrayList<String>();
		while (allTableNames.next()) {
			String schemaName = allTableNames.getString("TABLE_SCHEM");
			if (isNonSystemSchemaName(schemaName, defaultSchemaName)) {
				String tableName = allTableNames.getString("TABLE_NAME");
				nonSystemTableNames.add(tableName);
			}
		}

		return nonSystemTableNames;
	}

	private static boolean isNonSystemSchemaName(String tableSchemaName, String defaultSchemaName) {
		return tableSchemaName.indexOf(defaultSchemaName) != -1;
	}
}
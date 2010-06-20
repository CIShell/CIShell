package org.cishell.utilities.database;

import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Column {
	
	public final String name;
	public final int type;
	public final int size;
	
	public static final Map<Integer, String> TYPE_MAP = constructTypeMap();
	public static final Set<Integer> SIZED_TYPES = constructSizedTypes();
	
	private static Map<Integer, String> constructTypeMap() { //if this ever gets derby specific, it shouldn't go here
		Map<Integer, String> typeMap = new HashMap<Integer, String>() {{
			put(Types.CHAR, "char");
			put(Types.DATE, "date");
			put(Types.DOUBLE, "double");
			put(Types.FLOAT, "float");
			put(Types.INTEGER, "integer");
			put(Types.SMALLINT, "smallint");
			put(Types.TIME, "time");
			put(Types.VARCHAR, "varchar");
		}};
		return Collections.unmodifiableMap(typeMap);
	}
	
	private static Set<Integer> constructSizedTypes() {
		Set<Integer> sizedTypes = new HashSet<Integer>() {{
			add(Types.CHAR);
			add(Types.VARCHAR);
			
		}};
		return null;
	}

	public Column(String name, int type, int size) {
		this.name = name;
		this.type = type;
		this.size = size;
	}

	

	public String getDefinition() {
		// TODO Auto-generated method stub
		return null;
	}

}

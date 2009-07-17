package edu.iu.scipolicy.utilities;

import static org.junit.Assert.fail;

import java.util.Date;

import org.cishell.reference.service.metatype.BasicAttributeDefinition;
import org.cishell.utilities.MutateParameterUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.metatype.AttributeDefinition;

import prefuse.data.Table;

public class MutateParameterUtilitiesTest {
	Table table;
	AttributeDefinition oldAttributeDefinition;
	
	@Before
	public void setUp() throws Exception {
		table = formTestTableWithValidSchema();
		oldAttributeDefinition = formTestAttributeDefinition();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFormLabelAttributeDefinition() {
		try {
			MutateParameterUtilities.formLabelAttributeDefinition
				(this.oldAttributeDefinition, this.table);
		}
		catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testFormDateAttributeDefinition() {
		try {
			MutateParameterUtilities.formDateAttributeDefinition
				(this.oldAttributeDefinition, this.table);
		}
		catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testFormIntegerAttributeDefinition() {
		try {
			MutateParameterUtilities.formIntegerAttributeDefinition
				(this.oldAttributeDefinition, this.table);
		}
		catch (Exception e) {
			fail();
		}
	}
	
	private Table formTestTableWithValidSchema() {
		Table table = new Table();
		
		table.addColumn("string1", String.class);
		table.addColumn("date1", Date.class);
		table.addColumn("integer1", Integer.class);
		table.addColumn("string2", String.class);
		table.addColumn("date2", Date.class);
		table.addColumn("integer2", Integer.class);
		
		return table;
	}
	
	private AttributeDefinition formTestAttributeDefinition() {
		return new BasicAttributeDefinition("testID",
											"test_name",
											"test description",
											AttributeDefinition.STRING,
											null,
											null);
	}
}

package org.cishell.framework.tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.osgi.framework.BundleContext;

public class ActivatorTest {

	@Test
	public void test() {
		BundleContext context = Activator.getContext();
		assertNotNull(context);
	}

}

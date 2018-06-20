package org.cishell.framework.tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.osgi.framework.BundleContext;

import static org.ops4j.pax.exam.CoreOptions.*;

import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.regression.pde.HelloService;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleActivator;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class ActivatorTest {


	@Inject
	private BundleContext context;


	@Configuration
	public Option[] config() {

		return options(
			junitBundles()
			);
	}

	@Test
	public void test() {
		assertNotNull(context);
	}

}

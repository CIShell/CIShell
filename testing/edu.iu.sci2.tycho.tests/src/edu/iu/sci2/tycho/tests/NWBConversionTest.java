package edu.iu.sci2.tycho.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.ConversionException;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import edu.iu.nwb.util.nwbfile.NWBFileProperty;
import edu.iu.nwb.util.nwbfile.NWBFileUtilities;
import edu.iu.nwb.util.nwbfile.NWBFileWriter;

public class NWBConversionTest {
	private static CIShellContext ciShellContext;
	private static File tempNWBFile;

	@BeforeClass
	public static void retrieveContext() {
		ciShellContext = Activator.getCIShellContext();
	}
	
	@BeforeClass
	public static void createNWBFile() throws IOException {
		tempNWBFile = NWBFileUtilities.createTemporaryNWBFile();
		NWBFileWriter writer = new NWBFileWriter(tempNWBFile);
		writer.setNodeSchema(Maps.newLinkedHashMap(ImmutableMap.of("id", NWBFileProperty.TYPE_INT,
				"label", NWBFileProperty.TYPE_STRING)));
		writer.addNode(1, "Othello", ImmutableMap.<String,Object>of());
		writer.addNode(2, "White Women", ImmutableMap.<String,Object>of());
		writer.addNode(3, "Green Jello", ImmutableMap.<String,Object>of());
		writer.setDirectedEdgeSchema(Maps.newLinkedHashMap(ImmutableMap.of(
				NWBFileProperty.ATTRIBUTE_SOURCE, NWBFileProperty.TYPE_INT,
				NWBFileProperty.ATTRIBUTE_TARGET, NWBFileProperty.TYPE_INT,
				"label", NWBFileProperty.TYPE_STRING)));
		writer.addDirectedEdge(1, 2, ImmutableMap.<String,Object>of("label", "liked"));
		writer.addDirectedEdge(1, 3, ImmutableMap.<String,Object>of("label", "liked"));
		writer.finishedParsing();
	}
	
	
	@Test
	public void testCIShellContextNotNull() {
		assertNotNull(this.ciShellContext);
	}
	
	@Test
	public void testAnyAlgorithms() throws InvalidSyntaxException {
		ServiceReference<?>[] refs = Activator.getContext().getServiceReferences(AlgorithmFactory.class.getName(), null);
		assertNotNull(refs);
	}
	
	@Test
	public void testNWBConversion() throws ConversionException {
		Data nwbData = new BasicData(tempNWBFile, "file:text/nwb");
		
		DataConversionService conversionService = 
				(DataConversionService) this.ciShellContext.getService(DataConversionService.class.getName());
		assertNotNull(conversionService);
		
		Converter[] converters = conversionService.findConverters(nwbData, "file:text/graphml+xml");
		assertTrue(converters.length > 0);
		
		Data outData = conversionService.convert(nwbData, "file:text/graphml+xml");
		assertEquals(File.class, outData.getData().getClass());
		assertEquals("file:text/graphml+xml", outData.getFormat());
	}
	
}

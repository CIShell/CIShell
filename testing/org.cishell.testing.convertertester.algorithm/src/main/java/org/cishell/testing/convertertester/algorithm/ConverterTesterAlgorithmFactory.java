package org.cishell.testing.convertertester.algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.ParameterMutator;
import org.cishell.framework.data.Data;
import org.cishell.reference.service.metatype.BasicAttributeDefinition;
import org.cishell.reference.service.metatype.BasicObjectClassDefinition;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;


public class ConverterTesterAlgorithmFactory implements AlgorithmFactory, ParameterMutator {
	
	public static final String SELECTED_CONVERTER_PARAM_ID = "selectedConverter";
	public static final String NUM_HOPS_PARAM_ID = "numHops";
	public static final String TEST_ALL_CONVS_PARAM_ID = "testAllConvs";
    private BundleContext bContext;
    private MetaTypeInformation originalProvider;
	private String pid;

    protected void activate(ComponentContext ctxt) {
        this.bContext = ctxt.getBundleContext();;       
        MetaTypeService mts = (MetaTypeService)ctxt.locateService("MTS");
		this.pid = (String) ctxt.getServiceReference().getProperty(org.osgi.framework.Constants.SERVICE_PID);
		this.originalProvider = mts.getMetaTypeInformation(ctxt.getBundleContext().getBundle());
    }
    
    protected void deactivate(ComponentContext ctxt) {
    	originalProvider = null;
    }

    public Algorithm createAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
        return new ConverterTesterAlgorithm(data, parameters, context, bContext);
    }
    public ObjectClassDefinition mutateParameters(Data[] data,
			ObjectClassDefinition parameters) {

    	String[] converterNames = extractConverterNames(
    			ConverterTesterAlgorithmUtil.
    			getConverterReferences(bContext));
    	
    	Arrays.sort(converterNames);
    	
    	

		ObjectClassDefinition oldDefinition = originalProvider.getObjectClassDefinition(this.pid, null);

		BasicObjectClassDefinition definition;
		try {
			definition = new BasicObjectClassDefinition(oldDefinition.getID(), oldDefinition.getName(), oldDefinition.getDescription(), oldDefinition.getIcon(16));
		} catch (IOException e) {
			definition = new BasicObjectClassDefinition(oldDefinition.getID(), oldDefinition.getName(), oldDefinition.getDescription(), null);
		}

		definition.addAttributeDefinition(ObjectClassDefinition.REQUIRED,
				new BasicAttributeDefinition(SELECTED_CONVERTER_PARAM_ID,
						"Converter To Test",
						"The converter that you wish to test",
						AttributeDefinition.STRING,
						converterNames,
						converterNames));
		
		 definition.addAttributeDefinition(ObjectClassDefinition.REQUIRED,
					new BasicAttributeDefinition(TEST_ALL_CONVS_PARAM_ID,
							"Test All Converters?",
							"Should we test all the converters or just the one selected?",
							AttributeDefinition.BOOLEAN));
			
	    definition.addAttributeDefinition(ObjectClassDefinition.REQUIRED,
							new BasicAttributeDefinition(NUM_HOPS_PARAM_ID,
									"Max Test Path Length",
									"What is the maximum length a test path should have to be included",
									AttributeDefinition.INTEGER,
									"6"));
	    
	   
			
		return definition;
    }
    
    private String[] extractConverterNames(ServiceReference[] converterRefs) {
    	List converterNames = new ArrayList();
    	for (int ii = 0; ii < converterRefs.length; ii++) {
    		ServiceReference converterRef = converterRefs[ii];
    		
    		String converterName = removePackagePrefix(
    				(String) converterRef.getProperty("service.pid"));
    		converterNames.add(converterName);
    	}
    	
    	return (String[]) converterNames.toArray(new String[0]);
    }
    
    /*
	 * Returns everything after the last period in the OSGi service pid.
	 */
	private String removePackagePrefix(String pid) {
		int startIndex = pid.lastIndexOf(".") + 1;
		return pid.substring(startIndex);
	}

	public ObjectClassDefinition mutateParameters() {
		// TODO Auto-generated method stub
		return null;
	}
}
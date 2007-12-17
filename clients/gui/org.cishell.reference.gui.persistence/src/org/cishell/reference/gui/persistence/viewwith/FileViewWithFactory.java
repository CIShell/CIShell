package org.cishell.reference.gui.persistence.viewwith;

import java.io.IOException;
import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.cishell.reference.service.metatype.BasicAttributeDefinition;
import org.cishell.reference.service.metatype.BasicMetaTypeProvider;
import org.cishell.reference.service.metatype.BasicObjectClassDefinition;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeInformation;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.ObjectClassDefinition;


// Felix:
// See edu.iu.nwb.visualization.prefuse.beta.common
// It implements AlgorithmFactory and adds dropbox boxes...
// You will need to do something similar, but much less complicated, here, I believe.

public class FileViewWithFactory implements AlgorithmFactory {
	Program programTxt;
    Program programDoc;
    Program programHtml;
	private MetaTypeProvider provider;

    protected void activate(ComponentContext ctxt) {
        //You may delete all references to metatype service if 
        //your algorithm does not require parameters and return
        //null in the createParameters() method
    }
    protected void deactivate(ComponentContext ctxt) {
        provider = null;
    }

    public Algorithm createAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
        return new FileViewWith(data, parameters, context);
    }
    
    public MetaTypeProvider createParameters(Data[] data) {
    	
		BasicObjectClassDefinition definition;
		definition = new BasicObjectClassDefinition("fileViewWithDefinition", "Application Viewer Type", "Please choose an application viewer to read this file.", null);
					
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                programTxt = Program.findProgram("txt");
            }});

        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                programDoc = Program.findProgram("doc");
            }});
        
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                programHtml = Program.findProgram("htm");
                
            }});
	    
		System.err.println(definition.getID());
		
		String[] defValStringArray = null; //doesn't actually work yet...
		//String[] myOptionLabels = new String[] {programTxt.getName(),programDoc.getName(),programHtml.getName()};
		//String[] myOptionValues = new String[] {"txt","doc","html"};
		
		int possiblePrograms = 0;
		int counter = 0;
		if (programHtml != null) {
			possiblePrograms++;
		}
		if (programDoc != null) {
			possiblePrograms++;
		}
		if (programTxt != null) {
			possiblePrograms++;
		}
		
		String[] myOptionLabels = new String[possiblePrograms];
		String[] myOptionValues = new String[possiblePrograms];
		if (programHtml != null) {
			myOptionLabels[counter] = programHtml.getName();
			myOptionValues[counter++] = "html";
		}
		if (programDoc != null) {
			myOptionLabels[counter] = programDoc.getName();
			myOptionValues[counter++] = "doc";
		}
		if (programTxt != null) {
			myOptionLabels[counter] = programTxt.getName();
			myOptionValues[counter++] = "txt";
		}
		
		
		AttributeDefinition ad = new BasicAttributeDefinition("viewWith", "View file as", "Type of viewer", AttributeDefinition.STRING /*string*/, 0, defValStringArray/*String[] defaultValue*/, null /*validator*/, myOptionLabels, myOptionValues);
		definition.addAttributeDefinition(ObjectClassDefinition.REQUIRED, ad);

		MetaTypeProvider provider = new BasicMetaTypeProvider(definition);
        return provider;
    }
}
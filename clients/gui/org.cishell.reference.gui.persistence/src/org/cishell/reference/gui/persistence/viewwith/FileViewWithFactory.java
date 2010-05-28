package org.cishell.reference.gui.persistence.viewwith;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.ParameterMutator;
import org.cishell.framework.data.Data;
import org.cishell.reference.service.metatype.BasicAttributeDefinition;
import org.cishell.reference.service.metatype.BasicObjectClassDefinition;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;


// Felix:
// See edu.iu.nwb.visualization.prefuse.beta.common
// It implements AlgorithmFactory and adds dropbox boxes...
// You will need to do something similar, but much less complicated, here, I believe.

public class FileViewWithFactory implements AlgorithmFactory, ParameterMutator {
	Program programTxt;
    Program programDoc;
    Program programHtml;
    Program programCsv; //TS181
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
    
	public ObjectClassDefinition mutateParameters(Data[] data,
			ObjectClassDefinition parameters) {

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
        //TC181
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                programCsv = Program.findProgram("csv");
                
            }});
		
		String[] defValStringArray = null; //doesn't actually work yet...
		
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
		if (programCsv != null) {
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
		//TC181
		if (programTxt != null) {
			myOptionLabels[counter] = programCsv.getName();
			myOptionValues[counter++] = "csv";
		}
		
		
		AttributeDefinition ad = new BasicAttributeDefinition("viewWith", "View file as", "Type of viewer", AttributeDefinition.STRING, 0, defValStringArray, null, myOptionLabels, myOptionValues);
		definition.addAttributeDefinition(ObjectClassDefinition.REQUIRED, ad);

		return definition;
	}
}
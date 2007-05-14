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
//  private MetaTypeInformation originalProvider;  // taken from PrefuseBetaAlgorithmFactory
//	private String pid; // taken from PrefuseBetaAlgFactory

    protected void activate(ComponentContext ctxt) {
        //You may delete all references to metatype service if 
        //your algorithm does not require parameters and return
        //null in the createParameters() method
       //MetaTypeService mts = (MetaTypeService)ctxt.locateService("MTS");
       //provider = mts.getMetaTypeInformation(ctxt.getBundleContext().getBundle());
       // bruce says these should be commented out...
    }
    protected void deactivate(ComponentContext ctxt) {
        provider = null;
    }

    public Algorithm createAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
        return new FileViewWith(data, parameters, context);
    }
    
	
   private String[] createKeyArray() {
		String[] keys = new String[1];
		keys[0] = "Viewer";
		/*
		for(int ii = 1; ii <= schema.getColumnCount(); ii++) {
			keys[ii] = schema.getColumnName(ii - 1);
			System.out.println("keys["+ii+"] = " + schema.getColumnName(ii - 1) + "; ");
		}
		*/
	
		return keys;
	}
   
    public MetaTypeProvider createParameters(Data[] data) {
    	//ObjectClassDefinition oldDefinition = provider.getObjectClassDefinition(this.pid, null);
    	// read the API for creating on ObjectClassDefinition
    	// and create a basic one ...
 //   	BasicObjectClassDefinition oldDefinition = new ObjectClassDefinition();
    	
		BasicObjectClassDefinition definition;
		//try {
			definition = new BasicObjectClassDefinition("fileViewWithDefinition", "Application viewer type", "Please choose an application viewer to read this file.\nThe application associated with the chosen extension will be called.", null);
		//} catch (IOException e) {
			//definition = new BasicObjectClassDefinition("fileViewWithDef", "fileViewWithName", "This is an OCD for fileViewWith", null);
		//}
		
//		String[] dialogAttributesArray = createKeyArray();
		
			
		Display display;	
		
		
	    display = PlatformUI.getWorkbench().getDisplay();

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
		String[] myOptionLabels = new String[] {"TXT","DOC","HTML"};
		String[] myOptionValues = new String[] {"txt","doc","html"};
		
		if (programTxt == null) {
			myOptionLabels[0] = "";
		}
		if (programDoc == null) {
			myOptionLabels[1] = "";
		}
		if (programHtml == null) {
			myOptionLabels[2] = "";
		}
		AttributeDefinition ad = new BasicAttributeDefinition("viewWith", "View file as", "Type of viewer", AttributeDefinition.STRING /*string*/, 0, defValStringArray/*String[] defaultValue*/, null /*validator*/, myOptionLabels, myOptionValues);
		definition.addAttributeDefinition(ObjectClassDefinition.REQUIRED, ad);
		
		AttributeDefinition[] definitions = definition.getAttributeDefinitions(ObjectClassDefinition.ALL);
		

		MetaTypeProvider provider = new BasicMetaTypeProvider(definition);
        return provider;
    }
}
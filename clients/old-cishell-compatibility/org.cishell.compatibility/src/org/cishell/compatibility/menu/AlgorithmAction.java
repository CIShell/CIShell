/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 16, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.compatibility.menu;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.cishell.compatibility.algorithm.AlgorithmAdapter;
import org.cishell.compatibility.datamodel.NewDataModelAdapter;
import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.algorithm.DataModelValidator;
import org.cishell.framework.datamodel.DataModel;
import org.cishell.service.conversion.DataConversionService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import edu.iu.iv.core.IVC;
import edu.iu.iv.core.datamodels.BasicCompositeDataModel;
import edu.iu.iv.core.datamodels.BasicDataModel;
import edu.iu.iv.core.datamodels.CompositeDataModel;
import edu.iu.iv.core.messaging.ConsoleManager;


public class AlgorithmAction extends Action implements AlgorithmProperty, ISelectionListener {
    protected CIShellContext ciContext;
    protected BundleContext bContext;
    protected ServiceReference ref;
    
    public AlgorithmAction(ServiceReference ref, BundleContext bContext, CIShellContext ciContext) {
        this.ref = ref;
        this.ciContext = ciContext;
        this.bContext = bContext;
        
        setText((String)ref.getProperty(LABEL));
        setToolTipText((String)ref.getProperty(AlgorithmProperty.DESCRIPTION));
        selectionChanged(null, null);
    }
    
    public void run() {
        //Convert old datamodel to new datamodel
        Set modelSet = IVC.getInstance().getModelManager().getSelectedModels();
        DataModel[] dm = new DataModel[modelSet.size()];
        Iterator iter = modelSet.iterator();
        for (int i=0; i < modelSet.size(); i++) {
            dm[i] = new NewDataModelAdapter((edu.iu.iv.core.datamodels.DataModel) iter.next());
        }

        printPluginInformation();
        AlgorithmFactory factory = (AlgorithmFactory) bContext.getService(ref);
        AlgorithmAdapter algorithm = new AlgorithmAdapter(getText(),factory,dm,ciContext);
        algorithm.createGUIandRun(getText(), "");
    }
    
    protected void printPluginInformation() {
        ConsoleManager console = IVC.getInstance().getConsole();
        console.printAlgorithmInformation("...........\n");
 
        console.printAlgorithmInformation(getText() + " was selected.\n");

//        String author = (String)ref.getProperty(AlgorithmProperty.AUTHOR);
//        String citation = (String) ref.getProperty(AlgorithmProperty.CITATION);
//        String docu = (String) ref.getProperty(AlgorithmProperty.DOCUMENTATION);
//
//        if (author != null)
//            console.printAlgorithmInformation("Author: " + author + "\n");
//        if (citation != null)
//            console.printAlgorithmInformation("See also: \"" + citation + "\"\n");
//        if (docu != null)
//            console.printAlgorithmInformation("Documentation URL: " + docu + "\n");

        console.printAlgorithmInformation("\n");
    }

    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        Set selectedModels = IVC.getInstance().getModelManager().getSelectedModels();        
        Iterator iterator = selectedModels.iterator();        
        Set models = new HashSet();
        while(iterator.hasNext()){
            Object next = iterator.next();
            if(next instanceof edu.iu.iv.core.datamodels.DataModel){
                models.add((edu.iu.iv.core.datamodels.DataModel)next);
            }                    
        }
        if(models.size() == 0){
            boolean supports;
            try{
            supports = supports(new BasicDataModel(null));
            } catch(NullPointerException e){
                supports = false;
            }
            setEnabled(supports);
        }
        if(models.size() == 1){
            setEnabled(supports((edu.iu.iv.core.datamodels.DataModel)models.toArray()[0]));
        } else {        
            CompositeDataModel composite = new BasicCompositeDataModel();
            Iterator modelIterator = models.iterator();
            while(modelIterator.hasNext()){
                composite.add((edu.iu.iv.core.datamodels.DataModel)modelIterator.next());
            }
            setEnabled(supports(composite));
        }
    }
    
    private boolean supports(edu.iu.iv.core.datamodels.DataModel dm) {
        DataModelValidator validator = null;
        String[] interfaces = (String[])ref.getProperty(Constants.OBJECTCLASS);
        if (interfaces != null) {
            for (int i=0; i < interfaces.length; i++) {
                if (interfaces[i].equals(DataModelValidator.class.getName())) {
                    validator = (DataModelValidator) bContext.getService(ref);
                }
            }
        }
        
        
        String inData = (String) ref.getProperty(IN_DATA);
        String outData = (String) ref.getProperty(OUT_DATA);
        boolean supports = false;

        if (inData == null || inData.toLowerCase().equals(NULL_DATA)) {
            supports = true;
        } else if (dm == null || dm.getData() == null) {
            supports = false;
        } else if (dm instanceof CompositeDataModel) {
            CompositeDataModel cdm = (CompositeDataModel) dm;
            String[] types = inData.split(",");
            
            if (types.length > 1) {
                Set goodData = new HashSet();
                for (int i=0; i < types.length; i++) {
                    Iterator iter = cdm.iterator();
                    while (iter.hasNext()) {
                        Object data = ((edu.iu.iv.core.datamodels.DataModel) iter.next()).getData();
                        if (!goodData.contains(data) && isAsignableFrom(types[i], data)) {
                            goodData.add(data);
                        }
                    }
                }
                
                if (types.length == goodData.size()) {
                    supports = true;
                    
                    //TODO: extra validation for multi-dm algorithms
                }
            } else {
                Object data = dm.getData();
                
                if (types.length == 1 && isAsignableFrom(inData, data)) {
                    supports = true;
                    
                    if (validator != null) {
                        String valid = validator.validate(new DataModel[]{new NewDataModelAdapter(dm)});
                        supports = valid == null || (valid != null && valid.length() == 0);
                    }
                }
            }
        } else {
            Object data = dm.getData();
            String types[] = inData.split(",");
            
            if (types.length == 1 && isAsignableFrom(inData, data)) {
                supports = true;
                
                if (validator != null) {
                    String valid = validator.validate(new DataModel[]{new NewDataModelAdapter(dm)});
                    supports = valid == null || (valid != null && valid.length() == 0);
                }
            }
        }
        
//        if (supports == false && (outData != null || 
//                AlgorithmProperty.NULL_DATA.equalsIgnoreCase(outData))) {
//            supports = getDataConverter(inData, outData) != null;
//        }
        
        return supports;
    } 
    
    private AlgorithmFactory getDataConverter(String inFormat, String outFormat) {
        //TODO: automatic datamodel conversion
        DataConversionService converter = (DataConversionService)
            ciContext.getService(DataConversionService.class.getName());
        return converter.converterFor(inFormat, outFormat);
    }
    
    private boolean isAsignableFrom(String type, Object data) {
        try {
            Class c = Class.forName(type, false, data.getClass().getClassLoader());
            if (c.isInstance(data)) {
                return true;
            }
        } catch (ClassNotFoundException e) {
            //Ignore
        }
        
        return false;
    }
}
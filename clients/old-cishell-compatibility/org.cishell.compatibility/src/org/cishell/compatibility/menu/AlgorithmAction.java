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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cishell.compatibility.algorithm.AlgorithmAdapter;
import org.cishell.compatibility.datamodel.NewDataModelAdapter;
import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.algorithm.DataValidator;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.Converter;
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
        Data[] dm = new Data[modelSet.size()];
        Iterator iter = modelSet.iterator();
        for (int i=0; i < modelSet.size(); i++) {
            dm[i] = new NewDataModelAdapter((edu.iu.iv.core.datamodels.DataModel) iter.next());
        }

        printPluginInformation();
        AlgorithmFactory factory = (AlgorithmFactory) bContext.getService(ref);
        AlgorithmAdapter algorithm = new AlgorithmAdapter(ref, getText(),factory,dm,ciContext);
        algorithm.createGUIandRun(getText(), "");
    }
    
    protected void printPluginInformation() {
        ConsoleManager console = IVC.getInstance().getConsole();
        console.printAlgorithmInformation("...........\n");
 
        console.printAlgorithmInformation(getText() + " was selected.\n");

        //TODO: Print Algorithm Information
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
        } else if(models.size() == 1){
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
        DataValidator validator = null;
        String[] interfaces = (String[])ref.getProperty(Constants.OBJECTCLASS);
        if (interfaces != null) {
            for (int i=0; i < interfaces.length; i++) {
                if (interfaces[i].equals(DataValidator.class.getName())) {
                    validator = (DataValidator) bContext.getService(ref);
                }
            }
        }
        
        String inData = (String) ref.getProperty(IN_DATA);
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
                        if (!goodData.contains(data) && isAsignableFrom(inData, data)) {
                            goodData.add(data);
                        }
                    }
                }
                
                if (types.length == goodData.size()) {
                    supports = true;
                    
                    if (validator != null) {
                        List datamodels = new ArrayList();
                        
                        for (Iterator i=cdm.iterator(); i.hasNext(); ) {
                            datamodels.add(new NewDataModelAdapter(
                                    (edu.iu.iv.core.datamodels.DataModel) i.next()));
                        }
                        
                        Data[] dms = (Data[]) datamodels.toArray(new Data[0]);
                        String valid = validator.validate(dms);
                        
                        supports = valid == null || (valid != null && valid.length() == 0);
                    }
                }
            } else {
                Object data = dm.getData();
                
                if (types.length == 1 && isAsignableFrom(inData, data)) {
                    supports = true;
                    
                    if (validator != null) {
                        String valid = validator.validate(new Data[]{new NewDataModelAdapter(dm)});
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
                    String valid = validator.validate(new Data[]{new NewDataModelAdapter(dm)});
                    supports = valid == null || (valid != null && valid.length() == 0);
                }
            }
        }
        
        return supports;
    }
    
    private boolean isAsignableFrom(String type, Object data) {
        DataConversionService converter = (DataConversionService)
            ciContext.getService(DataConversionService.class.getName());
        
        boolean assignable = false;
        Class c = null;
        try {
            c = Class.forName(type, false, data.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            //Ignore
        }
        
        if (c != null && c.isInstance(data)) {
            assignable = true;
        } else {
            Converter[] converters = converter.findConverters(
                    new BasicData(data,data.getClass().getName()), type);
            
            assignable = converters.length > 0;
        }
        
        return assignable;
    }
}
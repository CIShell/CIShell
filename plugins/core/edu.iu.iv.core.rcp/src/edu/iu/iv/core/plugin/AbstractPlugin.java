package edu.iu.iv.core.plugin;

import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import edu.iu.iv.common.property.PropertyMap;
import edu.iu.iv.core.IVC;
import edu.iu.iv.core.datamodels.BasicCompositeDataModel;
import edu.iu.iv.core.datamodels.BasicDataModel;
import edu.iu.iv.core.datamodels.CompositeDataModel;
import edu.iu.iv.core.datamodels.DataModel;
import edu.iu.iv.core.messaging.ConsoleManager;

/**
 * An abstract plugin to aid in creating plugins. The implementation
 * of this abstract plugin may change as different backends are used
 * but should in no way effect the plugin. ALL IVC plugins should 
 * extend this plugin class.
 *
 * @author Team IVC
 */
public abstract class AbstractPlugin implements Plugin, IWorkbenchWindowActionDelegate, IStartup {

    protected PropertyMap propertyMap;
    protected IWorkbenchWindow window;
    protected IAction action;
    protected ConsoleManager console;
//    private static File infoFile;    
    public AbstractPlugin(){
        propertyMap = new PropertyMap();
        console = IVC.getInstance().getConsole();
        
//        if (infoFile == null) {
//            infoFile = new File(System.getProperty("user.dir") + File.separator + "PluginInformation.txt");
//            infoFile.delete();
//        }
    }
        
    /**
     * @see edu.iu.iv.common.property.PropertyAssignable#getProperties()
     */
    public PropertyMap getProperties() {
        return propertyMap;
    }

    public void dispose() {}

     
    public void init(IWorkbenchWindow window) {
        this.window = window;
        
//        try {
//            infoFile.createNewFile();
//            
//            PrintStream out = new PrintStream(new FileOutputStream(infoFile, true));
//            
//            Iterator iter = propertyMap.getAllPropertiesSet().iterator();
//            
//            out.println("................\n");
//            
//            out.println("Plugin: " + getClass());
//            out.println("Description: " + getDescription());
//            
//            while (iter.hasNext()) {
//                Property p = (Property) iter.next();
//                
//                Object v = propertyMap.getPropertyValue(p);
//                
//                out.println(p.getName() + ": " + v);
//            }
//        } catch (IOException e) {}
    }

    public void run(IAction action) {
        this.action = action;
        printPluginInformation();
        IVC.getInstance().launch(this);
    }

    public void selectionChanged(IAction action, ISelection selection) {
        Set selectedModels = IVC.getInstance().getModelManager().getSelectedModels();        
        Iterator iterator = selectedModels.iterator();        
        Set models = new HashSet();
        while(iterator.hasNext()){
            Object next = iterator.next();
            if(next instanceof DataModel){
                models.add((DataModel)next);
            }                    
        }
        if(models.size() == 0){
            boolean supports;
            try{
            supports = supports(new BasicDataModel(null));
            } catch(NullPointerException e){
                supports = false;
            }
            action.setEnabled(supports);
        }
        if(models.size() == 1){
            action.setEnabled(supports((DataModel)models.toArray()[0]));
        } else {        
            CompositeDataModel composite = new BasicCompositeDataModel();
            Iterator modelIterator = models.iterator();
            while(modelIterator.hasNext()){
                DataModel next = (DataModel)modelIterator.next();
                composite.add(next);
            }
            action.setEnabled(supports(composite));
        }
        
    }

    protected void printPluginInformation() {
        console = IVC.getInstance().getConsole();
        console.printAlgorithmInformation("...........\n");
 
        if (action != null) {
            console.printAlgorithmInformation(action.getText() + 
                    " was selected.\n");
        }

        PropertyMap propertyMap = getProperties();

        if (propertyMap != null) {
            String author = (String) propertyMap
                    .getPropertyValue(PluginProperty.AUTHOR);
            String citation = (String) propertyMap
                    .getPropertyValue(PluginProperty.CITATION_STRING);
            URL docu = (URL) propertyMap
                    .getPropertyValue(PluginProperty.DOCUMENTATION_LINK);

            if (author != null)
                console.printAlgorithmInformation("Author: " + author + "\n");
            if (citation != null)
                console.printAlgorithmInformation("See also: \"" + citation + "\"\n");
            if (docu != null)
                console.printAlgorithmInformation("Documentation URL: "
                        + docu.toExternalForm() + "\n");

            console.printAlgorithmInformation("\n");
        }
    }


    public void earlyStartup() {
        //required by implementation of IStartup
    }
}

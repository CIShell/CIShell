/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Feb 22, 2005 at Indiana University.
 */
package edu.iu.iv.persistence.save;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.iu.iv.core.IVC;
import edu.iu.iv.core.datamodels.DataModel;
import edu.iu.iv.core.datamodels.DataModelProperty;
import edu.iu.iv.core.datamodels.DataModelType;
import edu.iu.iv.datamodelview.DataModelTreeView;
import edu.iu.iv.gui.IVCDialog;

/**
 *
 * @author Team IVC
 */
public class SaveDataModelDialog extends IVCDialog {
    
    private List models;
    private int index;
    
    private Button yes;
    private Button yesToAll;
    private Button no;
    private Button noToAll;
    private Button cancel;
    private DataModel currentModel;
    
    public SaveDataModelDialog(Shell shell, List models){
        super(shell, "Unsaved Model(s)", IVCDialog.QUESTION);
        setDescription("The following data model is unsaved, " +
        		"do you want to save it?");
        setDetails("One or more data models in IVC are not saved" +
        		" and are in danger of being lost at this point if they " +
        		"are not saved now.");
        this.models = models;
        index = 0;
    }

    //Yes, Yes To All, No, No To All, Cancel
    public void createDialogButtons(Composite parent) {
        yes = new Button(parent, SWT.PUSH);
        yes.setText("Yes");
        yes.addSelectionListener(new YesListener());
        yesToAll = new Button(parent, SWT.PUSH);
        yesToAll.setText("Yes To All");
        yesToAll.addSelectionListener(new YesToAllListener());
        no = new Button(parent, SWT.PUSH);
        no.setText("No");
        no.addSelectionListener(new NoListener());
        noToAll = new Button(parent, SWT.PUSH);
        noToAll.setText("No To All");
        noToAll.addSelectionListener(new NoToAllListener());
        cancel = new Button(parent, SWT.PUSH);
        cancel.setText("Cancel");
        cancel.addSelectionListener(new CancelListener());
    }

    public Composite createContent(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        content.setLayout(layout);
        content.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER));
        
        if(index < models.size()){
            currentModel = (DataModel)models.get(index);
            String label = (String)currentModel.getProperties().getPropertyValue(DataModelProperty.LABEL);
            DataModelType type = (DataModelType)currentModel.getProperties().getPropertyValue(DataModelProperty.TYPE);
            String typeName = "Unknown";
            if(type != null && type.getName() != null)
                typeName = type.getName();            
            Label lbl = new Label(content, SWT.NONE);
            lbl.setText("Name: " + label);
            Label typeLbl = new Label(content, SWT.NONE);
            typeLbl.setText("Type: " + typeName);
        }        
        
        return content;
    }
    
    
    private class CancelListener extends SelectionAdapter {
        public void widgetSelected(SelectionEvent e) {
            close(false);
        }        
    }
    
    private class YesListener extends SelectionAdapter {
        public void widgetSelected(SelectionEvent e) {
            init();
            if(currentModel == null){
                close(false);
                return;
            }
            SavePlugin.getDefault().printInformation();
            SavePlugin.getDefault().launch(currentModel);
            if(!SavePlugin.getDefault().getSuccess()){
                close(false);
                return;
            }
            index++; //increment index in list
            DataModelTreeView.getDefault().remove(currentModel);
            IVC.getInstance().getModelManager().removeModel(currentModel);
            if(index < models.size()){ //go to the next
                open();
            }
            else {
                close(true);
            }           
        }
    }
    
    private class YesToAllListener extends SelectionAdapter {
        public void widgetSelected(SelectionEvent e) {
            init();
            for(int i = index; i < models.size(); i++){
                DataModel currentModel = (DataModel)models.get(i);
                SavePlugin.getDefault().printInformation();
                SavePlugin.getDefault().launch(currentModel);
                if(!SavePlugin.getDefault().getSuccess()){
                    close(false);
                    return;
                }
                DataModelTreeView.getDefault().remove(currentModel);
                IVC.getInstance().getModelManager().removeModel(currentModel);
            }
            close(true);
        }
    }
    
    private class NoListener extends SelectionAdapter {
        public void widgetSelected(SelectionEvent e) {
            init();
            if(currentModel == null){
                close(false);
                return;
            }
            index++; //increment index in list
            DataModelTreeView.getDefault().remove(currentModel);
            IVC.getInstance().getModelManager().removeModel(currentModel);
            if(index < models.size()){ //go to the next
                open();
            }
            else {
                close(true);
            }
        }
    }
    
    private class NoToAllListener extends SelectionAdapter {
        public void widgetSelected(SelectionEvent e) {
            for(int i = index; i < models.size(); i++){
                DataModel currentModel = (DataModel)models.get(i);
                DataModelTreeView.getDefault().remove(currentModel);
                IVC.getInstance().getModelManager().removeModel(currentModel);
            }
            close(true);
        }
    }

}

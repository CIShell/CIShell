/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Oct 11, 2005 at Indiana University.
 */
package edu.iu.iv.core.util.staticexecutable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import edu.iu.iv.core.IVC;

/**
 * 
 * @author Bruce Herr
 */
public class OutputtedFilesDialog extends Dialog implements SelectionListener {
    private static final String UNSAVED_PREFIX = "* ";
    private File[] files;
    private boolean[] saved;
    private List list;
    private Button save, view;
    private Shell shell;

    
    public OutputtedFilesDialog(Shell shell, File[] files) {
        super(shell);
        this.files = files;
        this.saved = new boolean[files.length];
        
        for (int i=0; i < saved.length; i++) {
            saved[i] = false;
        }
    }
    
    public Object open() {
        Shell parent = getParent();
        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setText("Output");
        
        createGUI(shell);
        
        shell.open();
        Display display = parent.getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
        
        return null;
    }
    
    private void createGUI(Shell group) {
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        group.setLayout(gridLayout);
        
        Label title = new Label(group, SWT.NONE);
        title.setText("Outputted Files:");
        setAlign(title, SWT.CENTER);
        
        list = new List(group, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        
        for (int i=0; i < files.length; i++) {
            list.add(UNSAVED_PREFIX + files[i].getName());
        }
        
        list.addSelectionListener(this);
        setAlign(list, SWT.CENTER);
        
        if (list.getItemCount() == 1) {
            list.setSelection(0);
        }
        
        Composite buttons = new Composite(group,SWT.NONE);
        buttons.setLayout(new RowLayout());
        
        save = makeButton(buttons, "Save");
        view = makeButton(buttons, "View");
        
        setAlign(buttons, SWT.CENTER);

        group.pack();
    }
    
    private void setAlign(Control widget, int alignment) {
        GridData gridData = new GridData();
        gridData.horizontalAlignment = alignment;
        widget.setLayoutData(gridData);
    }
    
    private Button makeButton(Composite group, String name) {
        Button button = new Button(group, SWT.FLAT);
        button.setText(name);
        button.addSelectionListener(this);
        
        return button;
    }

    public void widgetSelected(SelectionEvent e) {
        Object src = e.getSource();
        
        if (src == save) {
            save();  
        } else if (src == view) {
            view();  
        } 
        
        //updateLoadButton();
    }

    public void widgetDefaultSelected(SelectionEvent e) {
        widgetSelected(e);
    }
    
    public static void showGUI(final File[] files) {
        if (files.length == 0) {
            return;
        }
        
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                OutputtedFilesDialog dialog = new OutputtedFilesDialog(Display.getCurrent().getActiveShell(), files);
                dialog.open();
            }});
    }
    
    
//    private void updateLoadButton() {
//        int[] selection = list.getSelectionIndices();
//        
//        if (selection.length == 0) {
//            load.setEnabled(false);
//            return;
//        }
//        
//        for (int i=0; i < selection.length; i++) {
//            if (!saved[selection[i]]) {
//                load.setEnabled(false);
//                return;
//            }
//        }
//        
//        load.setEnabled(true);
//    }
    
    
    private void save() {
        int[] selection = list.getSelectionIndices();
        String dir = System.getProperty("user.dir");
        
        for (int i=0; i < selection.length; i++) {
            File src = files[selection[i]];
            FileDialog dialog = new FileDialog(shell, SWT.SAVE);
            dialog.setFilterPath(dir);
            dialog.setFileName(src.getName());
            
            String destination = dialog.open();
            
            if (destination != null) {
                File dest = new File(destination);
                
                try {
                    dest.createNewFile();
                    
                    FileChannel srcC = new FileInputStream(src).getChannel();
                    FileChannel destC = new FileOutputStream(dest).getChannel();
                    
                    destC.transferFrom(srcC,0,srcC.size());
                    
                    destC.close();
                    srcC.close();
                    
                    updateSaved(selection[i], true);
                    
                    //update so viewing after saving will view the saved file
                    files[selection[i]] = dest;
                } catch (FileNotFoundException e) {
                    IVC.showError("Error!","" + e.getMessage(),"");
                } catch (IOException e) {
                    IVC.showError("Error!","" + e.getMessage(),"");
                }
            }
        }
    }
    
    private void updateSaved(int selection, boolean isSaved) {
        saved[selection] = isSaved;
        String item = list.getItem(selection);
        
        if (isSaved && item.startsWith(UNSAVED_PREFIX)) {
            list.setItem(selection, item.substring(UNSAVED_PREFIX.length()));
        } else if (!isSaved && !item.startsWith(UNSAVED_PREFIX)) {
            list.setItem(selection, UNSAVED_PREFIX + item);
        }
    }
    
    private void view() {
        int[] selection = list.getSelectionIndices();
        
        for (int i=0; i < selection.length; i++) {
            Program.findProgram("txt").execute(files[selection[i]].toString());
            //Program.launch(files[selection[i]].toString());
        }
    }
}

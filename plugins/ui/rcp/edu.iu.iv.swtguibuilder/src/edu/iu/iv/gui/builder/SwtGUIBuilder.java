/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 23, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.iu.iv.SwtGUIBuilderImageLoader;
import edu.iu.iv.common.guibuilder.GUIBuilder;
import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;

/**
 * A GUIBuilder that uses SWT to create its GUI
 * 
 * @author Bruce Herr
 */
public class SwtGUIBuilder extends GUIBuilder implements ChangeListener {
    //A singleton Instance used for setting GUIBuilder's default builder.
    private static final GUIBuilder INSTANCE = new SwtGUIBuilder();
    //the icon for the shell 
    private static final Image icon = SwtGUIBuilderImageLoader.createImage("icon.gif");

    private Shell shell;
    protected static Shell parent;
    private SwtCompositeBuilder userArea;    
    private Font defaultFont;
    // button that signifies the user has entered information
    private Button okButton; 
    
    /**
     * A constructor used only for setting GUIBuilder's default builder.
     */
    private SwtGUIBuilder() {}
    
    /**
     * creates a new blank SwtGUIBuilder given a title and message
     * 
     * @param title the title 
     * @param message the message to appear at the top of the GUI
     */
    public SwtGUIBuilder(String title, String message) {
        this(Display.getCurrent().getActiveShell(),title,message);
    }
    
    /**
     * Creates a new SwtGUIBuilder given a shell, title, and message.
     *
     * @param parent parent Shell of this SwtGUIBuilder's Shell
     * @param title title of this SwtGUIBuilder's Shell
     * @param message a message that will be displayed at the top of this SwtGUIBuilder's
     *                Shell
     */
    public SwtGUIBuilder(Shell parent, String title, String message) {
        shell = new Shell(parent, SWT.RESIZE | SWT.BORDER | SWT.CLOSE);
        shell.setText(title);
        shell.setImage(icon);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        shell.setLayout(gridLayout);

        defaultFont = new Font(shell.getDisplay(), "SanSerif", 8, SWT.NONE);
        
        //stuff to display a message
        if(message != null && !message.equals("")){
            Label msg = new Label(shell, SWT.CENTER);
            msg.setText(message);
            
            GridData labelData = new GridData();
            labelData.horizontalAlignment = SWT.CENTER;            
            msg.setLayoutData(labelData);
        }

        //set up the user area where the main GUI will be set up using Parameters
        userArea = new SwtCompositeBuilder(shell);
        userArea.addChangeListener(this); 
        
        //the group w/ ok and cancel
        Composite buttonsGroup = new Composite(shell, SWT.NONE);
        FillLayout rowLayout = new FillLayout();
        rowLayout.spacing = 5;
        buttonsGroup.setLayout(rowLayout);

        //place them at the bottom right
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        gridData.grabExcessHorizontalSpace = false;
        buttonsGroup.setLayoutData(gridData);

        Button cancel = new Button(buttonsGroup, SWT.NONE);
        cancel.setText("Cancel");
        cancel.setSize(40, 20);
        cancel.setFont(defaultFont);
        cancel.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    shell.dispose();
                }
            });

        okButton = new Button(buttonsGroup, SWT.PUSH);
        okButton.setText("OK");
        okButton.setSize(40, 20);
        okButton.setFont(defaultFont);
    }
    
    /**
     * @see edu.iu.iv.common.guibuilder.GUIBuilder#addToGUI(edu.iu.iv.common.parameter.Parameter, edu.iu.iv.common.parameter.InputType)
     */
    protected void addToGUI(Parameter parameter, InputType type) {
        userArea.addToGUI(parameter,type);
    }

    /**
     * @see edu.iu.iv.common.guibuilder.GUIBuilder#addSelectionListener(org.eclipse.swt.events.SelectionListener)
     */
    public void addSelectionListener(final edu.iu.iv.common.guibuilder.SelectionListener listener) {
        okButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                listener.widgetSelected();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                listener.widgetSelected();
            }
        });
    }
    
    /**
     * notifies the main shell that the userArea has changed and
     * then updates the OK Button.
     */
    public void changeOccured() {
        if (userArea.isValid()) {
            okButton.setEnabled(true);
        } else {
            okButton.setEnabled(false);
        }
    }
    
    /**
     * Returns the Shell used by this SwtGUIBuilder
     *
     * @return the Shell used by this SwtGUIBuilder
     */
    public Shell getShell() {        
        shell.pack();

        return shell;
    }
    
    /**
     * @see edu.iu.iv.core.gui.builder.GUIBuilder#open()
     */
    public void open() {
        getShell().open();
    }

    /**
     * @see edu.iu.iv.core.gui.builder.GUIBuilder#close()
     */
    public void close() {
        shell.close();
    }
    
    public static void setParent(Shell parent) {
        SwtGUIBuilder.parent = parent;
    }

    /**
     * get the static instance of the SwtGUIBuilder for setting as the 
     * default GUIBuilder
     * 
     * @return the gui builder
     */
    public static GUIBuilder getGUIBuilder() {
        return INSTANCE;
    }
    

    /**
     * @see edu.iu.iv.common.guibuilder.GUIBuilder#createGUIBuilder(java.lang.String, java.lang.String)
     */
    public GUIBuilder createGUIBuilder(String title, String message) {
        return new SwtGUIBuilder(title, message);
    }
}

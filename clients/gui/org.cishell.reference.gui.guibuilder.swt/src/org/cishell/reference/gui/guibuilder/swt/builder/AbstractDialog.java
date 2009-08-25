/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Feb 22, 2005 at Indiana University.
 */
package org.cishell.reference.gui.guibuilder.swt.builder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * This class provides a DialogBox structure that can be extended to create Dialogs for CIShell.
 * This framework will enforce consistency in the look and feel of Dialogs in CIShell by providing a
 * standard layout of description, content, and buttons[, along with a choice of icon images
 * defined as constants in this class].  An optional details section allows the Dialog designer
 * to provide additional information when the details button is pressed.
 *
 * @author Team IVC
 */
public abstract class AbstractDialog extends Dialog {
    private static final int DETAILS_HEIGHT = 75;
    
    public static Image INFORMATION;
    public static Image WARNING;
    public static Image ERROR;
    public static Image QUESTION;
    public static Image WORKING;

    static {
        Runnable runner = new Runnable() {
            public void run() {
                INFORMATION = Display.getDefault().getSystemImage(SWT.ICON_INFORMATION);
                WARNING = Display.getDefault().getSystemImage(SWT.ICON_WARNING);
                ERROR = Display.getDefault().getSystemImage(SWT.ICON_ERROR);
                QUESTION = Display.getDefault().getSystemImage(SWT.ICON_QUESTION);
                WORKING = Display.getDefault().getSystemImage(SWT.ICON_WORKING);
            }};
        
        if (Display.getDefault().getThread() == Thread.currentThread()) {
            runner.run();
        } else {
            Display.getDefault().asyncExec(runner);
        }
    }
    

    private String description = "";
    private String detailsString = "";
    private Text detailsText;
    private Shell shell;
    private Image image;
    private boolean success;
    private Composite header;
    private Composite content;
    private Composite buttons;
    private Shell parent;

    /**
     * Creates a new AbstractDialog object.
     *
     * @param parent the parent Shell of this AbstractDialog
     * @param title the title to put in the title bar of this AbstractDialog
     * @param image the Image to display to the left of the description specified
     *        for this AbstractDialog. This will usually be one of:
     * <ul>
     * <li>AbstractDialog.WARNING</li>
     * <li>AbstractDialog.INFORMATION</li>
     * <li>AbstractDialog.ERROR</li>
     * <li>AbstractDialog.WORKING</li>
     * <li>AbstractDialog.QUESTION</li>
     * </ul>
     */
    public AbstractDialog(Shell parent, String title, Image image) {
        super(parent, 0);
        setText(title);
        this.image = image;
        this.parent = parent;
        init();
    }

    /**
     * Closes this AbstractDialog.
     * 
     * @param success true if the dialog was successful, false if it
     * was cancelled by the user (or closed prematurely)
     */
    public void close(boolean success){
        shell.dispose();
        this.success = success;
    }
    
    /**
     * Returns the shell used by this AbstractDialog
     * 
     * @return the shell used by this AbstractDialog
     */
    public Shell getShell(){
        return shell;
    }
    
    /**
     * Initializes this AbstractDialog. This consists of resetting all of the
     * customizable components like the content area, details pane, buttons,
     * and description label, and readying the dialog to be refilled with
     * new content.
     */
    public void init(){
        if(shell != null)
            shell.dispose();
        
        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
        
        if (parent != null) 
            shell.setImage(parent.getImage());
        
        shell.setText(getText());
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        shell.setLayout(layout);
    }
    
    /**
     * Opens this AbstractDialog.
     * 
     * @return true if this AbstractDialog was closed by clicking the 'x' in the upper right
     * corner of the window, signifying a cancellation, false if the dialog is exited otherwise.
     */
    public boolean open() {
        if (shell.getDisplay().getThread() == Thread.currentThread()) {
            doOpen();
        } else {
            shell.getDisplay().syncExec(new Runnable() {
                public void run() {
                    doOpen();
                }});
        }
        
        Display display = getParent().getDisplay();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        return success;
    }
    
    protected void doOpen() {
        success = true;        
        
        setupHeader();
        setupContent();
        setupButtons();

        shell.pack();
        setLocation();        
        shell.open();
        shell.addShellListener(new ShellAdapter(){
            public void shellClosed(ShellEvent e) {
                success = false;
            }
        });
    }
    
    /*
     * centers the dialog on its parents shell
     */
    private void setLocation(){
        Point parentLocation = parent.getLocation();
        int parentWidth = parent.getSize().x;
        int parentHeight = parent.getSize().y;
        int shellWidth = shell.getSize().x;
        int shellHeight = shell.getSize().y;        
        
        int x = parentLocation.x + (parentWidth - shellWidth)/2;
        int y = parentLocation.y + (parentHeight - shellHeight)/2;
        shell.setLocation(x, y);
    }

    /**
     * Sets the Description of this AbstractDialog.  This is the textField that is displayed in the
     * top section of the Dialog window, giving information about the question that is being
     * asked or the information that is being given.
     *
     * @param description the description for this AbstractDialog to use
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the details textField of this AbstractDialog.  This is the textField that is displayed in the lower
     * section of the Dialog window when the user presses the "Details >>" button.  If this String
     * is null or the empty string, the details button will be disabled.
     *
     * @param details DOCUMENT ME!
     */
    public void setDetails(String details) {
        this.detailsString = details;
    }

    /**
     * Creates the Buttons to use in this AbstractDialog based on the given parent. These are
     * the buttons that show up at the bottom of the dialog for user input, such as a
     * "Yes/No" group or "Continue/Cancel" or something like that. This does not encompass all
     * Buttons created in the dialog (such as those created in the content section), just those
     * to display at the bottom of the dialog.
     * 
     * @param parent the parent to be used to create the Buttons for this AbstractDialog
     */
    public abstract void createDialogButtons(Composite parent);

    /**
     * Creates the content section of this AbstractDialog based on the given parent.
     * This section is where all of the "guts" of the AbstractDialog go, specifying the controls
     * that are needed to interact with the user and provide whatever questions or information
     * are needed.
     *
     * @param parent the parent to be used to create the Buttosn for this AbstractDialog
     *
     * @return the Composite that is created to display the content of this AbstractDialog
     */
    public abstract Composite createContent(Composite parent);

    /*
     * Sets up the header section of the dialog.  This section contains the image for the
     * type of dialog it is, as well as the description label
     */
    private void setupHeader() {
        header = new Composite(shell, SWT.NONE);
        header.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        header.setLayout(layout);

        Label canvas = new Label(header, SWT.NONE);
        if (image != null) {
            canvas.setImage(image);
        }
        GridData canvasData = new GridData();
        canvasData.heightHint = image.getBounds().height;        
        canvas.setLayoutData(canvasData);

        Label desc = new Label(header, SWT.WRAP);
        
        if ((description != null) && !description.equals("")) {
            desc.setText(description);
        }
    }

    /*
     * sets up the content section of the dialog, this calls the abstract method to
     * create the content that must be implemented by all subclasses
     */
    private void setupContent() {
        content = createContent(shell);

        if (content != null) {
            content.setLayoutData(new GridData(GridData.FILL_BOTH));
        }        
    }

    /*
     * sets up the button section in the bottom of the dialog.  These buttons
     * are created in the abstract method createDialogButtons(parent).  In addition to
     * any created buttons, a "Details >>" button is added to allow the user to see any
     * details that are available in the current Dialog.
     */
    private void setupButtons() {
        buttons = new Composite(shell, SWT.NONE);
        buttons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END));

        //there are two sections, all the user stuff to the left, and
        //then the details button on the far right
        //User Buttons Section        
        createDialogButtons(buttons);
        Control[] controls = buttons.getChildren();
        GridLayout buttonsLayout = new GridLayout();
        buttonsLayout.numColumns = controls.length + 1;
        buttonsLayout.makeColumnsEqualWidth = true;
        buttons.setLayout(buttonsLayout);

        //setup the grid data for each button for standard look
        for (int i = 0; i < controls.length; i++) {
            controls[i].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }

        //Details Button section
        final Button details = new Button(buttons, SWT.PUSH);
        details.setText("Details >>");
        details.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        details.addSelectionListener(new SelectionAdapter() {
                public synchronized void widgetSelected(SelectionEvent e) {                    
                    GridData data = (GridData) detailsText.getLayoutData();
                
                    if (detailsText.getVisible()) {
                        detailsText.setText("");
                        details.setText("Details >>");
                        data.heightHint = 0;
                        data.grabExcessHorizontalSpace = false;
                        data.grabExcessVerticalSpace = false;
                    } else {
                        detailsText.setText(detailsString);               
                        details.setText("Details <<");
                        data.heightHint = DETAILS_HEIGHT;
                        data.grabExcessHorizontalSpace = true;
                        data.grabExcessVerticalSpace = true;
                    }
                    
                    detailsText.setLayoutData(data);
                    detailsText.setVisible(!detailsText.getVisible());
                    
                    shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                    shell.layout();
                }
            });
        
        setupDetails();
        details.setEnabled(detailsString != null && !detailsString.equals(""));
    }

    /*
     * creates the details textField box when the "Details >>" button is toggled
     */
    private void setupDetails() {
        detailsText = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);       
        detailsText.setEditable(false);
        detailsText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        
        GridData data = new GridData(GridData.FILL_BOTH | 
        		GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL);
        data.widthHint = 400;
        
        detailsText.setLayoutData(data);
        detailsText.setVisible(false);
    }
    
    /**
     * Open a standard error dialog with OK button
     * 
     * @param parent the parent Shell of this dialog
     * @param title the textField to display in the title bar of this dialog
     * @param message the message to give in the dialog's body
     * @param details the textField to put in the details pane to be visible when the
     *        "Details >>" button is pressed (can be null or empty, resulting
     *        in the "Details >>" button not being enabled)
     * @return true if the dialog was exited by pressing the OK button, false
     * if it was cancelled by pressing the 'x' in the title bar
     */
    public static boolean openError(Shell parent, String title, String message, String details){
        return openOKDialog(parent, ERROR, title, message, details);
    }
    
    /**
     * Open a standard information dialog with OK button
     * 
     * @param parent the parent Shell of this dialog
     * @param title the textField to display in the title bar of this dialog
     * @param message the message to give in the dialog's body
     * @param details the textField to put in the details pane to be visible when the
     *        "Details >>" button is pressed (can be null or empty, resulting
     *        in the "Details >>" button not being enabled)     * @return true if the dialog was exited by pressing the OK button, false
     * if it was cancelled by pressing the 'x' in the title bar
     */
    public static boolean openInformation(Shell parent, String title, String message, String details){
        return openOKDialog(parent, INFORMATION, title, message, details);
    }
    
    /**
     * Open a standard warning dialog with OK button
     * 
     * @param parent the parent Shell of this dialog
     * @param title the textField to display in the title bar of this dialog
     * @param message the message to give in the dialog's body
     * @param details the textField to put in the details pane to be visible when the
     *        "Details >>" button is pressed (can be null or empty, resulting
     *        in the "Details >>" button not being enabled)     * @return true if the dialog was exited by pressing the OK button, false
     * if it was cancelled by pressing the 'x' in the title bar
     */
    public static boolean openWarning(Shell parent, String title, String message, String details){
        return openOKDialog(parent, WARNING, title, message, details);
    }
    
    /**
     * Open a standard question dialog with Yes/No buttons 
     * 
     * @param parent the parent Shell of this dialog
     * @param title the textField to display in the title bar of this dialog
     * @param message the message to give in the dialog's body
     * @param details the textField to put in the details pane to be visible when the
     *        "Details >>" button is pressed (can be null or empty, resulting
     *        in the "Details >>" button not being enabled)     * @return true if the dialog was exited by pressing the OK button, false
     * if it was cancelled by pressing the 'x' in the title bar or pressing the 
     * No button
     */
    public static boolean openQuestion(Shell parent, String title, String message, String details){
        return openConfirmDenyDialog(parent, QUESTION, title, message, details, "Yes", "No");
    }
    
    /**
     * Open a standard confirmation dialog with OK/Cancel buttons
     * 
     * @param parent the parent Shell of this dialog
     * @param title the textField to display in the title bar of this dialog
     * @param message the message to give in the dialog's body
     * @param details the textField to put in the details pane to be visible when the
     *        "Details >>" button is pressed (can be null or empty, resulting
     *        in the "Details >>" button not being enabled)     * @return true if the dialog was exited by pressing the OK button, false
     * if it was cancelled by pressing the 'x' in the title bar or pressing
     * the Cancel button
     */
    public static boolean openConfirm(Shell parent, String title, String message, String details){
        return openConfirmDenyDialog(parent, QUESTION, title, message, details, "OK", "Cancel");
    }       
    
    /*
     * helper to create OK dialogs: error, warning, information
     */
    private static boolean openOKDialog(Shell parent, Image image, String title, String message, String details){
        AbstractDialog okDialog = new AbstractDialog(parent, title, image){
            public void createDialogButtons(Composite parent) {
                Button ok = new Button(parent, SWT.PUSH);
                ok.setText("OK");
                ok.addSelectionListener(new SelectionAdapter(){
                    public void widgetSelected(SelectionEvent e) {
                        close(true);
                    }
                });
            }

            public Composite createContent(Composite parent) {
                return null;
            }            
        };
        okDialog.setDescription(message);
        okDialog.setDetails(details);
        return okDialog.open();
    }
    
    /*
     * helper to create confirm/deny dialogs: question, confirmation
     */
    private static boolean openConfirmDenyDialog(Shell parent, Image image, String title, String message, String details, final String confirmLabel, final String denyLabel){
        AbstractDialog dialog = new AbstractDialog(parent, title, image){
            public void createDialogButtons(Composite parent) {
                Button confirm = new Button(parent, SWT.PUSH);
                if(confirmLabel != null)
                    confirm.setText(confirmLabel);
                confirm.addSelectionListener(new SelectionAdapter(){
                    public void widgetSelected(SelectionEvent e) {
                        close(true);
                    }
                });
                Button deny = new Button(parent, SWT.PUSH);
                if(denyLabel != null)
                    deny.setText(denyLabel);
                deny.addSelectionListener(new SelectionAdapter(){
                    public void widgetSelected(SelectionEvent e) {
                        close(false);
                    }
                });
            }

            public Composite createContent(Composite parent) {
                return null;
            }            
        };
        dialog.setDescription(message);
        dialog.setDetails(details);
        return dialog.open();
    }
}

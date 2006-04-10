/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Feb 24, 2005 at Indiana University.
 */
package edu.iu.iv.schedulerview;

import java.util.Calendar;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

import edu.iu.iv.core.IVC;
import edu.iu.iv.core.algorithm.Algorithm;
import edu.iu.iv.gui.IVCApplication;
import edu.iu.iv.gui.IVCImageLoader;


/**
 * SchedulerView is the IVC visual representation of the internal scheduler for
 * Algorithms.  It contains a table listing of all algorithms finished, running,
 * or scheduled, and allows the user to modify the ordering and reschedule or
 * cancel Algorithms as desired.
 *
 * @author Team IVC
 */
public class SchedulerView extends ViewPart
    implements SchedulerContentModelListener {
    public static final String ID_VIEW = "edu.iu.iv.schedulerview.SchedulerView";

    //images for up and down buttons
    private static Image upImage = IVCImageLoader.createImage("up.gif");
    private static Image downImage = IVCImageLoader.createImage("down.gif");

    //singleton instance
    private static SchedulerView defaultView;
    
    private Composite parent;
    private Table table;
    private Button removeButton;
    private Button scheduleButton;
    private Button removeAutomatically;
    private Button up;
    private Button down;
    private SchedulerContentModel model;
    private SchedulerItem[] currentSelection;
    private boolean running; //flag for if something in the current selection is running
    private boolean completed; //flag for if something in the current selection is completed (or errored out)

    /**
     * Creates a new SchedulerView object.
     */
    public SchedulerView() {
        defaultView = this;
        model = SchedulerContentModel.getInstance();
        model.addListner(this);
        currentSelection = new SchedulerItem[0];
        
    }

    /**
     * Returns the default instance of SchedulerView
     *
     * @return the default instance of SchedulerView
     */
    public static SchedulerView getDefault() {
        return defaultView;
    }

    /**
     * Asks this view part to take focus within the workbench.
     */
    public void setFocus() {
        table.setFocus();
    }

    /**
     * Creates the controls for this View.  SchedulerView consists of 
     * buttons for removing items and rescheduling items, a checkbox to
     * toggle the 'remove automatically' functionality, a button to 
     * remove all completed algorithms, buttons to move the selected item
     * up or down in the running queue, and finally the table that displays
     * all of the items.        
     *
     * @param parent the parent Composite of this view
     */
    public void createPartControl(Composite parent) {
        this.parent = parent;

        Composite control = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;

        control.setLayout(layout);

        //create the buttons
        scheduleButton = new Button(control, SWT.PUSH);
        scheduleButton.setText("Schedule...");
        scheduleButton.setToolTipText(
            "Reschedule the selected item to another " + "date/time");
        scheduleButton.setEnabled(false);
        scheduleButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    //this button is only enabled if a single selection is made
                    SchedulerItem item = currentSelection[0];
                    SchedulerDialog dialog = new SchedulerDialog();
                    Algorithm algorithm = item.getAlgorithm();
                    IVC.getInstance().getScheduler().block(algorithm);

                    boolean success = dialog.open();

                    if (success) {
                        Calendar date = dialog.getDate();
                        boolean rescheduled = IVC.getInstance().getScheduler()
                                                 .reschedule(algorithm, date);

                        if (rescheduled) {
                            //a new item is created on reschedule, get rid of the old one
                            //first set the name, this is a bit of a hack right now..
                            model.getMostRecentAddition().setName(item.getName());
                            model.remove(item);
                        }
                    }

                    IVC.getInstance().getScheduler().unblock(algorithm);
                }
            });
        removeButton = new Button(control, SWT.PUSH);
        removeButton.setText("Remove From List");
        removeButton.setEnabled(false);
        removeButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    removeSelection();
                }
            });
        removeAutomatically = new Button(control, SWT.CHECK);
        removeAutomatically.setText("Remove completed automatically");
        removeAutomatically.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event) {
                    model.setRemoveAutomatically(removeAutomatically.getSelection());
                }
            });

        Button removeAllCompleted = new Button(control, SWT.PUSH);
        removeAllCompleted.setText("Remove all completed");
        removeAllCompleted.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    model.removeCompleted();
                }
            });

        GridData removeAllCompletedData = new GridData();
        removeAllCompletedData.horizontalAlignment = SWT.RIGHT;
        removeAllCompleted.setLayoutData(removeAllCompletedData);

        //composite for up and down  buttons and table
        Composite tableComposite = new Composite(control, SWT.NONE);
        GridLayout tableCompositeLayout = new GridLayout();
        tableCompositeLayout.numColumns = 2;
        tableComposite.setLayout(tableCompositeLayout);

        GridData compositeData = new GridData(GridData.FILL_BOTH);
        compositeData.horizontalSpan = 4;
        tableComposite.setLayoutData(compositeData);

        //up and down buttons
        Composite upAndDown = new Composite(tableComposite, SWT.NONE);
        GridLayout upAndDownLayout = new GridLayout();
        upAndDownLayout.numColumns = 1;
        upAndDown.setLayout(upAndDownLayout);

        up = new Button(upAndDown, SWT.PUSH);
        up.setToolTipText(
            "Moves the selected item up in the queue if possible. Only" +
            " queued items can be moved without rescheduling.");
        up.setEnabled(false);
        up.setImage(upImage);
        up.addSelectionListener(new UpButtonListener());

        down = new Button(upAndDown, SWT.PUSH);
        down.setToolTipText(
            "Moves the selected item down in the queue if possible. Only" +
            " queued items can be moved without rescheduling.");
        down.setEnabled(false);
        down.setImage(downImage);
        down.addSelectionListener(new DownButtonListener());

        // Create the table 
        createTable(tableComposite);

        GridData data = new GridData(GridData.FILL_BOTH);
        table.setLayoutData(data);
    
        IMenuManager menu = IVCApplication.getMenuManager();
        IContributionItem item = menu.findUsingPath("tools/scheduler");
        if(item != null){
            final IAction action = ((ActionContributionItem) item).getAction();
            action.setChecked(true);
        }

        //initialize based on data in the model
        refreshView();               
    }

    public void dispose(){
        IMenuManager menu = IVCApplication.getMenuManager();
        IContributionItem item = menu.findUsingPath("tools/scheduler");
        if(item != null){
            final IAction action = ((ActionContributionItem) item).getAction();
            action.setChecked(false);
        }
    }

    /**
     * Notifies this SchedulerView of changes in the SchedulerContentModel, and
     * updates the gui controls appropriately
     */
    public void refreshView() {
        if ((table != null) && !table.isDisposed()) {
            refreshTable();
            updateUpAndDown();
            refreshButtons();
        }
    }

    /*
     * Create the Table control
     */
    private void createTable(Composite parent) {
        int style = SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL |
            SWT.FULL_SELECTION;

        table = new Table(parent, style);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        // 1st column with checkboxes for completion
        TableColumn column = new TableColumn(table, SWT.CENTER, 0);
        column.setText("!");
        column.setWidth(20);

        // 2nd column with Algorithm name
        column = new TableColumn(table, SWT.LEFT, 1);
        column.setText("Algorithm Name");
        column.setWidth(150);

        // 3rd column with date
        column = new TableColumn(table, SWT.LEFT, 2);
        column.setText("Date");
        column.setWidth(75);

        // 4th column with time
        column = new TableColumn(table, SWT.LEFT, 3);
        column.setText("Time");
        column.setWidth(75);

        // 5th column with task PercentComplete 
        column = new TableColumn(table, SWT.CENTER, 4);
        column.setText("% Complete");
        column.setWidth(120);

        //selection listener to keep currentSelection variable up to date
        table.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    TableItem[] selection = table.getSelection();
                    currentSelection = new SchedulerItem[selection.length];

                    for (int i = 0; i < selection.length; i++) {
                        SchedulerItem item = SchedulerItem.getSchedulerItem(selection[i]);
                        currentSelection[i] = item;
                    }

                    updateUpAndDown();
                    refreshButtons();
                }
            });

        //key listener to allow you to remove items with the delete key
        table.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                    if (e.keyCode == SWT.DEL) {
                        removeSelection();
                    }
                }
            });

        //listener for dragging of items up and down in the running queue
        ItemDragListener dragListener = new ItemDragListener();
        table.addMouseMoveListener(dragListener);
        table.addMouseListener(dragListener);
    }

    /*
     * removes all of the items in the current selection if none of them
     * are running
     */
    private void removeSelection() {
        if (!running) {
            SchedulerItem[] items = currentSelection;

            //clear selection    	        
            currentSelection = new SchedulerItem[0];

            for (int i = 0; i < items.length; i++) {
                model.remove(items[i]);
            }

            removeButton.setEnabled(false);
            scheduleButton.setEnabled(false);
            up.setEnabled(false);
            down.setEnabled(false);
        }
    }
    
    /*
     * loop through the items in the model and refresh their gui appearance
     * based on recent changes in the model
     */
    private void refreshTable() {       
        //NOTE:using sync exec here since we cant go on until the items are created
        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    table.removeAll();

                    SchedulerItem[] items = model.getItems();

                    for (int i = 0; i < items.length; i++) {
                        items[i].init(table);
                    }

                    TableItem[] selection = new TableItem[currentSelection.length];

                    for (int i = 0; i < currentSelection.length; i++) {
                        selection[i] = currentSelection[i].getTableItem();
                    }

                    table.setSelection(selection);
                }
            });
    }

    /*
     * set the enablement of the remove and schedule buttons based on
     * the status of the current selection (i.e. can remove a running
     * algorithm, cant reschedule a running or finished algorithm)     
     */
    private void refreshButtons() {
        running = false;
        completed = false;

        //if the selection is a running algorithm, remove button is
        //disabled
        for (int i = 0; i < currentSelection.length; i++) {
            SchedulerItem item = currentSelection[i];

            if (item.isRunning()) {
                running = true;
            }

            if (item.isComplete() || item.isError()) {
                completed = true;
            }
        }

        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    removeButton.setEnabled(!running && currentSelection.length > 0);
                    scheduleButton.setEnabled((currentSelection.length == 1) &&
                        !running && !completed);
                }
            });
    }

    /*
     * toggles the enablement of the up and down buttons based on whether
     * the current selection can be moved in the running queue
     */
    private void updateUpAndDown() {
        Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    up.setEnabled((currentSelection.length == 1) &&
                        model.canMoveUp(currentSelection[0]));
                    down.setEnabled((currentSelection.length == 1) &&
                        model.canMoveDown(currentSelection[0]));
                }
            });
    }

    /*
     * moves the selected item up the in the running queue when the
     * up button is pressed 
     */
    private class UpButtonListener extends SelectionAdapter {
        public void widgetSelected(SelectionEvent event) {
            if ((currentSelection.length != 1) ||
                    !model.canMoveUp(currentSelection[0])) {
                return;
            }

            SchedulerItem item = currentSelection[0];
            Algorithm algorithm = item.getAlgorithm();
            IVC.getInstance().getScheduler().moveUp(algorithm);
        }
    }

    /*
     * moves the selected item down the in the running queue when the
     * down button is pressed 
     */
    private class DownButtonListener extends SelectionAdapter {
        public void widgetSelected(SelectionEvent event) {
            if ((currentSelection.length != 1) ||
                    !model.canMoveDown(currentSelection[0])) {
                return;
            }

            SchedulerItem item = currentSelection[0];
            Algorithm algorithm = item.getAlgorithm();
            IVC.getInstance().getScheduler().moveDown(algorithm);
        }
    }

    /*
     * Listener for mouse drags of selected items in the table to move them
     * up or down in the running queue if possible
     */
    private class ItemDragListener extends MouseAdapter implements MouseMoveListener {
        private boolean down = false;
        private SchedulerItem movingItem;
        private int index;
        private int newIndex;

        //if the mouse is down (dragging), discover when it is dragged over
        //a new table item and swap them if possible in the running queue
        public void mouseMove(MouseEvent e) {
            if (down && (movingItem != null)) {
                //cant move multiple items
                if (currentSelection.length != 1) {
                    Cursor cursor = new Cursor(parent.getDisplay(),
                            SWT.CURSOR_NO);
                    table.setCursor(cursor);
                } else {
                    TableItem item = table.getItem(new Point(e.x, e.y));

                    if (item == null) {
                        return;
                    }

                    newIndex = table.indexOf(item);

                    //check if item needs to be moved down
                    if ((newIndex > index) && model.canMoveDown(movingItem)) {
                        Algorithm algorithm = movingItem.getAlgorithm();
                        boolean success = IVC.getInstance().getScheduler()
                                             .moveDown(algorithm);

                        if (success) {
                            index = newIndex;
                        }                        
                    } 
                    
                    //check if item needs to be moved up
                    else if ((newIndex < index) &&
                            model.canMoveUp(movingItem)) {
                        Algorithm algorithm = movingItem.getAlgorithm();
                        boolean success = IVC.getInstance().getScheduler()
                                             .moveUp(algorithm);

                        if (success) {
                            index = newIndex;
                        }
                    }
                }
            }
        }

        //reset the selected item and set the flag that the mouse is down
        public void mouseDown(MouseEvent e) {
            if (e.button == 1) {
                down = true;

                TableItem item = table.getItem(new Point(e.x, e.y));
                if(item == null) return;
                
                index = table.indexOf(item);

                SchedulerItem schedulerItem = SchedulerItem.getSchedulerItem(item);

                if (schedulerItem != null) {
                    movingItem = schedulerItem;
                }
            }
        }

        //unset the mouse down flag and clear the selected item
        public void mouseUp(MouseEvent e) {
            if (e.button == 1) {
                down = false;
                movingItem = null;

                Cursor cursor = new Cursor(parent.getDisplay(), SWT.CURSOR_ARROW);
                table.setCursor(cursor);
            }
        }
    }
    
}

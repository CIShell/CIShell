/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Mar 2, 2005 at Indiana University.
 */
package edu.iu.iv.schedulerview;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import edu.iu.iv.core.IVC;
import edu.iu.iv.core.algorithm.Algorithm;
import edu.iu.iv.core.algorithm.AlgorithmProperty;
import edu.iu.iv.core.algorithm.ProgressiveAlgorithm;
import edu.iu.iv.gui.IVCImageLoader;


/**
 * SchedulerItem is a class that encapsulates an Algorithm in the IVC Scheduler with
 * other various peices of data that are needed to display it appropriatly in the
 * SchedulerView.
 *
 * @author Team IVC
 */
public class SchedulerItem {
    //for completed algorithms
    private static Image checkImage = IVCImageLoader.createImage("check.gif"); 
    //for uncompleted algorithms
    private static Image uncheckImage = IVCImageLoader.createImage("uncheck.gif");
    //algorithms that exited from error
    private static Image errorImage = IVCImageLoader.createImage("error.gif");   

    //map used for unique name generation
    private static Map substringToNumberMap = new HashMap();

    //map used to lookup a SchedulerItem based on a TableItem
    private static Map tableItemToSchedulerItem = new HashMap();

    // Set the table column indicies
    private final int COMPLETED_COLUMN = 0;
    private final int ALGORITHM_COLUMN = 1;
    private final int DATE_COLUMN = 2;
    private final int TIME_COLUMN = 3;
    private final int PERCENT_COLUMN = 4;
    
    private Algorithm algorithm; //algorithm used
    private Calendar calendar; //date/time information
    private String date; //date in string format for display
    private String time; //time in string format for display
    private String name; //name of algorithm
    private int percentComplete; //progress (if progressive)
    private boolean progressive; //flag to determine if the algorithm is progressive
    private boolean error; //flag to determine if an error has occured
    private TableItem item; //table item currently associated with this item in the view
    private Table table; //table used by the view
    private TableEditor editor; //used to display progress bar
    private ProgressBar bar; //progress bar to display
    private boolean running; //flag to determine if algorithm is currently running

    /**
     * Creates a new SchedulerItem object.
     *
     * @param algorithm Algorithm that is used by this SchedulerItem
     * @param calendar date/time when this Algorithm has been scheduled to run
     */
    public SchedulerItem(Algorithm algorithm, Calendar calendar) {
        this.algorithm = algorithm;
        this.calendar = calendar;

        //init fields
        date = getDateString(calendar);
        time = getTimeString(calendar);
        name = (String) algorithm.getProperties().getPropertyValue(AlgorithmProperty.LABEL);

        if (name == null) {
            name = "Unknown";
        }
        name = findUniqueLabel(name);

        percentComplete = 0; //by default
        progressive = false;

        if (algorithm instanceof ProgressiveAlgorithm) {
            progressive = true;
            percentComplete = ((ProgressiveAlgorithm) algorithm).getPercentageDone();
        }

        error = false;
        running = false;
    }

    /**
     * Initializes this SchedulerItem on the given Table.  This method is used
     * to reset the SchedulerItems GUI appearance by creating a new TableItem for
     * it and setting up the progress bar appropriately.
     *
     * @param table The table that this SchedulerItem should be displayed on
     */
    public void init(Table table) {
        this.table = table;

        if (item != null) {
            tableItemToSchedulerItem.remove(item);
        }

        if (bar != null) {
            bar.dispose();
        }

        item = new TableItem(table, SWT.NONE);
        item.setImage(COMPLETED_COLUMN, getImage());
        item.setText(ALGORITHM_COLUMN, name);
        item.setText(DATE_COLUMN, date);
        item.setText(TIME_COLUMN, time);

        if (running && !progressive) {
            bar = new ProgressBar(item.getParent(), SWT.INDETERMINATE);
        } else {
            bar = new ProgressBar(table, SWT.NONE);
            bar.setSelection(percentComplete);
        }

        editor = new TableEditor(table);
        editor.grabHorizontal = editor.grabVertical = true;
        editor.setEditor(bar, item, 4);

        tableItemToSchedulerItem.put(item, this);
    }

    /**
     * Returns the SchedulerItem that is associated with the given
     * TableItem.  This is used by the SchedulerView when it needs to lookup
     * the SchedulerItem based on the selected TableItem for example
     *
     * @param item the TableItem to find the associated SchedulerItem for
     *
     * @return the SchedulerItem that is associated with the given TableItem
     */
    public static SchedulerItem getSchedulerItem(TableItem item) {
        return (SchedulerItem) tableItemToSchedulerItem.get(item);
    }

    /*
     * Refreshes all of the progress bars to be with the correct table items,
     * this is needed after something is removed or added to keep everything
     * synched up, since the progress bars dont keep track of their item
     * correctly on their own.
     */
    private static void refresh() {
        Iterator iterator = tableItemToSchedulerItem.values().iterator();

        while (iterator.hasNext()) {
            //resets all of the progress bars (and editors) to be with the
            //correct items
            SchedulerItem item = (SchedulerItem) iterator.next();
            Table table = item.table;
            int index = table.indexOf(item.getTableItem());

            if (index != -1) {
                item.editor.setItem(table.getItem(index));
            }
        }
    }

    /**
     * Returns the TableItem that is currently being used by this
     * SchedulerItem.
     *
     * @return the TableItem that is currently being used by this
     * SchedulerItem
     */
    public TableItem getTableItem() {
        return item;
    }

    /**
     * Returns the Algorithm that is encapsulated by this
     * SchedulerItem.
     *
     * @return the Algorithm that is encapsulated by this
     * SchedulerItem.
     */
    public Algorithm getAlgorithm() {
        return algorithm;
    }

    /**
     * Returns the Calendar that represents the date/time information
     * about when this SchedulerItem was scheduled to run.
     *
     * @return the Calendar that represents the date/time information
     * about when this SchedulerItem was scheduled to run
     */
    public Calendar getCalendar() {
        return calendar;
    }

    /**
     * Sets the String representation of the date at which this SchedulerItem's
     * Algorithm is to run
     *
     * @param date the String representation of the date at which this SchedulerItem's
     * Algorithm is to run
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Sets the String representation of the time at which this SchedulerItem's
     * Algorithm is to run
     *
     * @param time the String representation of the time at which this SchedulerItem's
     * Algorithm is to run
     */    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Sets the name of this SchedulerItem's Algorithm to be displayed
     *
     * @param name the name of this SchedulerItem's Algorithm to be displayed
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the displayed name of this SchedulerItem
     *
     * @return the displayed name of this SchedulerItem
     */
    public String getName() {
        return name;
    }

    /**
     * Monitors the progress of this SchedulerItem's Algorithm, if the
     * Algorithm is a ProgressiveAlgorithm, and updates the progress bar
     * appropriately.
     */
    public void monitorProgress() {
        if(progressive){
	        int done = ((ProgressiveAlgorithm) algorithm).getPercentageDone();
	
	        if ((done <= 100) && (done >= 0)) {
	            percentComplete = done;
	        }
	
	        if (progressive) {
	            Display.getDefault().asyncExec(new Runnable() {
	                    public void run() {
	                        if ((bar != null) && !bar.isDisposed()) {
	                            bar.setSelection(percentComplete);
	                        }
	                    }
	                });
	        }
        }
    }

    /**
     * Determines if this SchedulerItem's Algorithm has completed.
     *
     * @return true if this SchedulerItem's Algorithm is complete, false if not.
     */
    public boolean isComplete() {
        return percentComplete == 100;
    }

    /**
     * Determines if this SchedulerItem's Algorithm is running
     *
     * @return true if this SchedulerItem's Algorithm is running, false if not.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Determines if this SchedulerItem's Algorithm has had an error
     *
     * @return true if this SchedulerItem's Algorithm has had an error, false if not.
     */
    public boolean isError() {
        return error;
    }

    /**
     * Update this SchedulerItem to reflect that an error has occured in the
     * Algorithm.  This involves changing the image icon to a red 'X', stopping
     * the ProgressBar, and indicating the error with text in the date/time columns.
     */
    public void signalError() {
        //if an error occured, update the gui
        running = false;
        error = true;
        date = "error";
        time = "error";
        Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    if ((table == null) || table.isDisposed()) {
                        return;
                    }

                    //set the icon on the left to be a red x
                    item.setImage(COMPLETED_COLUMN, getImage());

                    if (progressive) {
                        //if its progressive, just kill the progress bar
                        bar.setSelection(percentComplete);
                    } else {
                        //if its not progressive, replace the indeterminite progress bar
                        //with a determinite one, set to zero 
                        bar.dispose();
                        editor.dispose();
                        bar = new ProgressBar(item.getParent(), SWT.NONE);
                        bar.setSelection(0);
                        editor = new TableEditor(item.getParent());
                        editor.grabHorizontal = editor.grabVertical = true;
                        editor.setEditor(bar, item, PERCENT_COLUMN);
                    }
                }
            });
    }


    /**
     * Determines whether or not this SchedulerItem's Algorithm is a ProgressiveAlgorithm.
     *
     * @return true if this SchedulerItem's Algorithm is progressive, false if not
     */
    public boolean isProgressive() {
        return progressive;
    }

    /**
     * Updates this SchedulerItem to reflect that its Algorithm has started.
     * This involves initializing the ProgressBar and displaying text in the
     * data/time columns to indicate it is now running
     */
    public void start() {
        running = true;
        date = "running";
        time = "running";

        if (!progressive) {
            //use an INDETERMINITE ProgressBar to show the working algorithm
            //if not progress information is available
            Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        if ((table == null) || !table.isDisposed()) {
                            return;
                        }

                        bar.dispose();
                        editor.dispose();
                        bar = new ProgressBar(table, SWT.INDETERMINATE);
                        editor = new TableEditor(table);
                        editor.grabHorizontal = editor.grabVertical = true;
                        editor.setEditor(bar, item, PERCENT_COLUMN);
                    }
                });
        }
    }

    /**
     * Updates this SchedulerItem to reflect that its Algorithm has finished.
     * This involves finalizing the ProgressBar and displaying text in the
     * data/time columns to indicate it is now finished, as well as changing the
     * unchecked icon to a checked icon to indicate completion
     */    
    public void finish() {
        date = "complete";
        time = "complete";
        percentComplete = 100;
        running = false;
        Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    if ((table == null) || table.isDisposed()) {
                        return;
                    }

                    item.setImage(COMPLETED_COLUMN, getImage());

                    if (algorithm instanceof ProgressiveAlgorithm) {
                        bar.setSelection(percentComplete);
                    } else {
                        bar.dispose();
                        editor.dispose();
                        bar = new ProgressBar(item.getParent(), SWT.NONE);
                        bar.setSelection(percentComplete);
                        editor = new TableEditor(item.getParent());
                        editor.grabHorizontal = editor.grabVertical = true;
                        editor.setEditor(bar, item, PERCENT_COLUMN);
                    }
                }
            });
    }

    /**
     * Removes this SchedulerItem by removing its Algorithm from the 
     * scheduler and removing all of its GUI componenets
     */
    public void kill() {
        //remove algorithm from scheduler
        if (!running && !isComplete() && !error) {
            IVC.getInstance().getScheduler().unschedule(algorithm);
        }

        Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    if ((table == null) || table.isDisposed()) {
                        return;
                    }

                    bar.dispose();
                    editor.dispose();
                    tableItemToSchedulerItem.remove(item);
                    refresh();
                }
            });
    }

    /*
     * return a properly formatted date from the given Calendar
     */
    private String getDateString(Calendar time) {
        String month = (time.get(Calendar.MONTH) + 1) + "";
        String day = time.get(Calendar.DAY_OF_MONTH) + "";
        String year = time.get(Calendar.YEAR) + "";

        if (month.length() == 1) {
            month = "0" + month;
        }

        if (day.length() == 1) {
            day = "0" + day;
        }

        return month + "/" + day + "/" + year;
    }

    /*
     * return a properly formatted time from the given Calendar
     */
    private String getTimeString(Calendar time) {
        String minute = time.get(Calendar.MINUTE) + "";
        String hour = time.get(Calendar.HOUR) + "";
        String second = time.get(Calendar.SECOND) + "";
        int ampm = time.get(Calendar.AM_PM);
        String amPmString = "PM";

        if (ampm == Calendar.AM) {
            amPmString = "AM";
        }

        if (minute.length() == 1) {
            minute = "0" + minute;
        }

        if (hour.length() == 1) {
            hour = "0" + hour;
        }

        if (second.length() == 1) {
            second = "0" + second;
        }

        return hour + ":" + minute + ":" + second + " " + amPmString;
    }

    /*
     * find a unique name for the given label, to display in the
     * SchedulerView for the algorithm name
     */
    private String findUniqueLabel(String label) {
        Integer oldNumber = (Integer) substringToNumberMap.get(label);

        if (oldNumber != null) {
            oldNumber = new Integer(oldNumber.intValue() + 1);
        } else {
            oldNumber = new Integer(1);
        }

        substringToNumberMap.put(label, oldNumber);

        label = label + "." + oldNumber;

        return label;
    }
    
    /*
     * Returns the Image that is used by this SchedulerItem, based on the current
     * state of the Algorithm
     *
     * @return the Image that is used by this SchedulerItem, based on the current
     * state of the Algorithm
     */
    private Image getImage() {
        if (error) {
            return errorImage;
        }

        if (percentComplete == 100) {
            return checkImage;
        } else {
            return uncheckImage;
        }
    }
}

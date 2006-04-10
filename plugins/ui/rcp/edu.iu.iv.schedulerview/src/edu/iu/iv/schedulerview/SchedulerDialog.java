/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Mar 2, 2005 at Indiana University.
 */
package edu.iu.iv.schedulerview;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.iu.iv.core.IVC;
import edu.iu.iv.gui.IVCDialog;


/**
 * SchedulerDialog is a dialog box for entering a new date and time for an
 * item in the Scheduler to be rescheduled to.
 *
 * @author Team IVC
 */

//dialog for scheduling a new time
public class SchedulerDialog extends IVCDialog {
    private Text dateText;
    private Text timeText;
    private Button am;
    private Button pm;
    private Calendar date;

    /**
     * Creates a new SchedulerDialog object.
     */
    public SchedulerDialog() {
        super(SchedulerView.getDefault().getSite().getShell(), "Schedule",
            IVCDialog.WORKING);
        setDescription(
            "Please select the date and time which you want this Algorithm to run.");
        setDetails(
            "This dialog allows you to specify the exact time which you want the IVC" +
            " scheduler to run an Algorithm.  The Algorithm will be run at this time as" +
            " long as this is a valid date and time and the maximum number of simultaneous" +
            " Algorithms is not already running (configurable in IVC Preferences).");
    }

    /**
     * Creates the buttons used in this dialog.  The SchedulerDialog has an
     * OK/Cancel button set.
     *
     * @param parent the parent Composite to create the Buttons on
     */
    public void createDialogButtons(Composite parent) {
        Button ok = new Button(parent, SWT.PUSH);
        ok.setText("OK");
        ok.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    try {
                        //parse out the input and construct the date object
                        String dateString = dateText.getText();
                        String timeString = timeText.getText();

                        StringTokenizer dateSt = new StringTokenizer(dateString,
                                "/");

                        if (dateSt.countTokens() != 3) {
                            throw new IllegalArgumentException();
                        }

                        StringTokenizer timeSt = new StringTokenizer(timeString,
                                ":");

                        if (timeSt.countTokens() != 3) {
                            throw new IllegalArgumentException();
                        }

                        int month = Integer.parseInt(dateSt.nextToken());
                        int day = Integer.parseInt(dateSt.nextToken());
                        String yearString = dateSt.nextToken();

                        if (yearString.length() == 2) {
                            yearString = "20" + yearString;
                        }

                        int year = Integer.parseInt(yearString);

                        int hour = Integer.parseInt(timeSt.nextToken());
                        int minute = Integer.parseInt(timeSt.nextToken());
                        int second = Integer.parseInt(timeSt.nextToken());

                        //try to create a calendar object based on the input
                        date = new GregorianCalendar();
                        date.setLenient(false);

                        //month is zero based, hour is out of 24
                        if (pm.getSelection()) {
                            date.set(Calendar.AM_PM, Calendar.PM);

                            if (hour < 12) {
                                hour = hour + 12;
                            }
                        } else {
                            date.set(Calendar.AM_PM, Calendar.AM);
                        }

                        date.set(year, month - 1, day, hour, minute, second);
                    } catch (NumberFormatException ex) {
                        //invalid input
                        IVC.showError("Error!",
                            "Invalid input for date and time, " +
                            "all values must be integers.", "");

                        return;
                    } catch (IllegalArgumentException ex) {
                        //invalid input
                        date = null;
                        IVC.showError("Error!",
                            "Invalid input for date and time", "");

                        return;
                    }

                    //go ahead and close if date got setup ok
                    close(true);
                }
            });

        Button cancel = new Button(parent, SWT.PUSH);
        cancel.setText("Cancel");
        cancel.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    date = null;
                    close(false);
                }
            });
    }

    /**
     * Returns the date which was constructed based on the input to
     * this SchedulerDialog.
     *
     * @return the date which was constructed based on the input to
     * this SchedulerDialog
     */
    public Calendar getDate() {
        return date;
    }

    /**
     * Creates the content area of this SchedulerDialog. This consists of
     * two text fields for input of date and time, as well as radio buttons
     * for AM/PM with the time.
     *
     * @param parent the parent Composite to create the content area on
     *
     * @return the new Composite for the content area of this dialog
     */
    public Composite createContent(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        content.setLayout(layout);

        Label date = new Label(content, SWT.NONE);
        date.setText("Date (MM/DD/YYYY):");
        dateText = new Text(content, SWT.BORDER);

        GridData dateGridData = new GridData();
        dateGridData.horizontalSpan = 2;
        dateText.setLayoutData(dateGridData);

        Label time = new Label(content, SWT.NONE);
        time.setText("Time (HH:MM:SS):");
        timeText = new Text(content, SWT.BORDER);

        Composite amPmGroup = new Composite(content, SWT.NONE);
        amPmGroup.setLayout(new RowLayout());
        am = new Button(amPmGroup, SWT.RADIO);
        am.setText("AM");
        pm = new Button(amPmGroup, SWT.RADIO);
        pm.setText("PM");
        am.setSelection(true);

        return content;
    }
}

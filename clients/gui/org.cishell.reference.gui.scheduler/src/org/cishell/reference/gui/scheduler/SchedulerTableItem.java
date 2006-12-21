package org.cishell.reference.gui.scheduler;

import java.util.Calendar;

import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.ProgressMonitor;
import org.cishell.framework.algorithm.ProgressTrackable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * Controls a single item in the table per algorithm, and monitors the algorithm
 * if it is monitorable.
 */
public class SchedulerTableItem {
	private Algorithm algorithm;
	private Calendar  cal;
	private String    algorithmLabel;
	
	private TableItem   tableItem;
	private TableEditor tableEditor;
	private int         progressSelection;
	private ProgressBar progressBar;
	
    private static Image checkedImage	= Activator.createImage("check.gif");
    private static Image uncheckedImage	= Activator.createImage("uncheck.gif");
    private static Image errorImage		= Activator.createImage("error.gif");
    
    private boolean encounteredError;

    private String  workBeingDone;
    private boolean cancelRequested;
    private boolean pauseRequested;
    private boolean started;
    private boolean done;
    
    private boolean isCancellable;
    private boolean isPauseable;
    private boolean isWorkTrackable;
    
    private AlgorithmProgressMonitor algorithmProgressMonitor;

    /**
     * Initializes flags and records the current algorithm to monitor
     * 
     * @param algorithmLabel
     * @param algorithm
     * @param cal
     */
    public SchedulerTableItem(String algorithmLabel, Algorithm algorithm, Calendar cal) {
    	this.algorithm = algorithm;
    	this.cal = cal;
    	
    	this.encounteredError = false;
    	
    	this.cancelRequested = false;
    	this.started         = false;
    	this.done            = false;
    	
    	this.isCancellable   = false;
    	this.isPauseable     = false;
    	this.isWorkTrackable = false;
    	
    	this.algorithmLabel = algorithmLabel;

    	
    	if (algorithm instanceof ProgressTrackable) {
        	algorithmProgressMonitor = new AlgorithmProgressMonitor();
        	((ProgressTrackable)algorithm).setProgressMonitor(algorithmProgressMonitor);
    	}
    }
    
    /**
     * Request a cancel for the running algorithm
     * @param request Cancel request
     */
    public void requestCancel(boolean request) {
    	cancelRequested = request;
    }
    
    /**
     * Request the algorithm to pause
     * @param request Pause request
     */
    public void requestPause(boolean request) {
    	pauseRequested = request;
    }
    
    /**
     * Initialize the table entry with the parent table and location
     * in the table
     * @param table The parent table
     * @param tblNdx The entry number to insert the table
     */
    public void initTableEntry(final Table table, final int tblNdx) {
		guiRun(new Runnable() {
			public void run() {
				drawTableEntry(table, tblNdx);
			}
		});
	}
    
    /**
     * Mark the algorithm as finished
     * @param table The parent table
     */
    public void finishTableEntry(final Table table) {
    	done = true;

		if (!tableItem.isDisposed()) {
			guiRun(new Runnable() {
				public void run() {
					progressBar.dispose();
					progressBar = new ProgressBar(table, SWT.NONE);
					
					progressSelection = progressBar.getMaximum();
					drawTableEntry(table, table.indexOf(tableItem));
				}
			});
		}
    }
    
    /**
     * Moves this entry to the provided index
     * @param table The parent table
     * @param tblNdx The target index into the table
     */
    public void moveTableEntry(final Table table, final int tblNdx) {
		guiRun(new Runnable() {
			public void run() {
				progressSelection  = progressBar.getSelection();
				drawTableEntry(table, tblNdx);
			}
		});    	
    }
    
    /**
     * Draws a table entry with the current state provided
     * the parent table and index of the new entry
     * 
     * @param table Parent table
     * @param tblNdx Index into the table
     */
    private void drawTableEntry(final Table table, final int tblNdx) {
		guiRun(new Runnable() {
			public void run() {
				if (tableItem != null) {
					tableItem.dispose();
				}
				tableItem = new TableItem(table, SWT.NONE, tblNdx);
				
				if (done) {
					tableItem.setImage(SchedulerView.COMPLETED_COLUMN, checkedImage);
				}
				else if (encounteredError) {
					tableItem.setImage(SchedulerView.COMPLETED_COLUMN, errorImage);					
				}
				else {
					tableItem.setImage(SchedulerView.COMPLETED_COLUMN, uncheckedImage);
				}
				
				tableItem.setText(SchedulerView.ALGORITHM_COLUMN, algorithmLabel);
				setCalendar();

				if (started) {
					if (progressBar != null)
						progressBar.dispose();
					if (isWorkTrackable || done) {
						progressBar = new ProgressBar(table, SWT.NONE);
						progressBar.setSelection(progressSelection);
					} else {
						progressBar = new ProgressBar(table, SWT.INDETERMINATE);
					}
				} else {
					progressBar = new ProgressBar(table, SWT.NONE);
				}
				tableEditor = new TableEditor(table);
				tableEditor.grabHorizontal = tableEditor.grabVertical = true;
				tableEditor.setEditor(progressBar, tableItem,
						SchedulerView.PERCENT_COLUMN);
			}
		});    	
    }
    
    /**
     * Sets the calendar entry for the current table.
     */
    private void setCalendar() {
		guiRun(new Runnable() {
			public void run() {
				final String date = getDateString(cal);
				final String time = getTimeString(cal);
				tableItem.setText(SchedulerView.DATE_COLUMN, date);
				tableItem.setText(SchedulerView.TIME_COLUMN, time);
			}
		});
	}
    
    /**
     * Notification of the start of the algorithm
     * 
     * @param table The parent table
     */
    public void algorithmStarted(Table table) {
    	done    = false;
    	started = true;
    	drawTableEntry(table, table.indexOf(tableItem));
    }
    
    /**
     * Notification of rescheduling of the algorithm
     * @param cal The rescheduled time
     */
    public void reschedule(Calendar cal) {
		this.cal = cal;
		setCalendar();
	}

    /**
     * Notification of an error during algorithm execution
     * @param table Parent table
     */
    public void errorTableEntry(Table table) {
    	encounteredError = true;
		drawTableEntry(table, table.indexOf(tableItem));
    }
    
    /**
     * Refresh the table item
     *
     */
    public void refresh() {
		guiRun(new Runnable() {
			public void run() {
				if (!progressBar.isDisposed()) {
					progressBar.setSelection(progressSelection);
					tableEditor.grabHorizontal = tableEditor.grabVertical = true;
					tableEditor.setEditor(progressBar, tableItem,
							SchedulerView.PERCENT_COLUMN);
				}
			}
		});
	}
    
    /**
     * Removes the current table item
     *
     */
    public void remove() {
		guiRun(new Runnable() {
			public void run() {
				progressBar.dispose();
				tableItem.dispose();
			}
		});
	}
    
    /**
     * Returns the current table item
     * @return current table item
     */
    public TableItem getTableItem() {
    	return tableItem;
    }
    
    /**
     * A properly formatted date from the given Calendar
     * @return formatted calendar
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

    /**
     * A properly formatted time from the given Calendar
     * @return formatted calendar
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
    
    /**
     * Insures that the current thread is sync'd with the UI thread
     * @param run
     */
	private void guiRun(Runnable run) {
		if (Thread.currentThread() == Display.getDefault().getThread()) {
			run.run();
		} else {
			Display.getDefault().syncExec(run);
		}
	}
	
	/**
	 * Whether or not the current algorithm is cancellable, if the algorithm
	 * is done, it will return false.
	 * @return cancellable state
	 */
    public boolean isCancellable() {
    	if (done) return false;
    	return isCancellable;
    }
    
    /**
     * Whether or not the current algorithm is pausable, if the algorithm
     * is done, it will return false.
     * @return Pausable state
     */
    public boolean isPausable() {
    	if (done) return false;
    	return isPauseable;
    }
    
    /**
     * Whether or not the current algorithm is work trackable
     * @return Trackable state
     */
    public boolean isWorkTrackable() {
    	return isWorkTrackable();
    }
    
    /**
     * Whether or not the current algorithm is paused
     * @return Paused state
     */
    public boolean isPaused() {
    	if (algorithmProgressMonitor.isPaused() && !done) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    /**
     * Whether or not the current algorithm is running
     * 
     * @return Running state
     */
    public boolean isRunning() {
    	if (cancelRequested) {
    		return false;
    	}
    	return true;
    }
    
    /**
     * The algorithm done state
     * @return Done state
     */
    public boolean isDone() {
    	return done;
    }
    

    /**
     * Monitors an algorithm 
     *
     */
	private class AlgorithmProgressMonitor implements ProgressMonitor {
		private int totalWorkUnits;

		public void describeWork(String currentWork) {
			workBeingDone = currentWork;
		}

		public void done() {
			done = true;
		}

		public boolean isCanceled() {
			return cancelRequested;
		}

		public boolean isPaused() {
			return pauseRequested;
		}

		public void setCanceled(boolean value) {
			cancelRequested = value;
		}

		public void setPaused(boolean value) {
			pauseRequested  = value;
		}

		public void start(int capabilities, int totalWorkUnits) {
			
			if ((capabilities & ProgressMonitor.CANCELLABLE) > 0){
				isCancellable = true;
			}
			if ((capabilities & ProgressMonitor.PAUSEABLE) > 0){
				isPauseable = true;
			}
			if ((capabilities & ProgressMonitor.WORK_TRACKABLE) > 0){
				refresh();
				isWorkTrackable = true;
				guiRun(new Runnable() {
					public void run() {
						Table table = (Table)progressBar.getParent();
						progressBar.dispose();
						progressBar = new ProgressBar(table, SWT.NONE);
						progressBar.setSelection(progressBar.getMinimum());
						tableEditor = new TableEditor(table);
						tableEditor.grabHorizontal = tableEditor.grabVertical = true;
						tableEditor.setEditor(progressBar, tableItem, SchedulerView.PERCENT_COLUMN);
					}
				});
			}
			this.totalWorkUnits = totalWorkUnits;
		}

		public void worked(final int work) {
			// final int totalWorkUnits = this.totalWorkUnits;
			guiRun(new Runnable() {
				public void run() {
					if (!progressBar.isDisposed()) {
						progressSelection = (int) (progressBar.getMaximum() * ((double) work / (double) totalWorkUnits));
						// progressBar.setSelection(progress);
					}
				}
			});
			refresh();
		}
	}
}
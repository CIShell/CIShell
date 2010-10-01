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
	public static final Image CHECKED_IMAGE = Activator.createImage("check.gif");
    public static final Image UNCHECKED_IMAGE = Activator.createImage("uncheck.gif");
    public static final Image ERROR_IMAGE = Activator.createImage("error.gif");

	private String algorithmLabel;
	private Calendar calendar;
	
	private TableItem tableItem;
	private TableEditor tableEditor;
	private int progressSelection;
	private ProgressBar progressBar;
    
    private boolean encounteredError;

    @SuppressWarnings("unused")
    private String workBeingDone;
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
     * @param calendar
     * @param algorithm
     */
    public SchedulerTableItem(String algorithmLabel, Calendar calendar, Algorithm algorithm) {
    	this.algorithmLabel = algorithmLabel;
    	this.calendar = calendar;

    	this.encounteredError = false;
    	this.cancelRequested = false;
    	this.started = false;
    	this.done = false;
    	this.isCancellable = false;
    	this.isPauseable = false;
    	this.isWorkTrackable = false;

    	if (algorithm instanceof ProgressTrackable) {
        	this.algorithmProgressMonitor = new AlgorithmProgressMonitor();
        	((ProgressTrackable)algorithm).setProgressMonitor(this.algorithmProgressMonitor);
    	}
    }
    
    /**
     * Request a cancel for the running algorithm
     * @param request Cancel request
     */
    public void requestCancel(boolean request) {
    	this.cancelRequested = request;
    }
    
    /**
     * Request the algorithm to pause
     * @param request Pause request
     */
    public void requestPause(boolean request) {
    	this.pauseRequested = request;
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
    	this.done = true;

		if (!this.tableItem.isDisposed()) {
			guiRun(new Runnable() {
				public void run() {
					SchedulerTableItem.this.progressBar.dispose();
					SchedulerTableItem.this.progressBar = new ProgressBar(table, SWT.NONE);
					
					SchedulerTableItem.this.progressSelection =
						SchedulerTableItem.this.progressBar.getMaximum();
					drawTableEntry(table, table.indexOf(SchedulerTableItem.this.tableItem));
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
				SchedulerTableItem.this.progressSelection  =
					SchedulerTableItem.this.progressBar.getSelection();
				drawTableEntry(table, tblNdx);
			}
		});    	
    }
    
    /**
     * Draws a table entry with the current state provided the parent table and index of the
     * new entry.
     */
    private void drawTableEntry(final Table table, final int tableIndex) {
		guiRun(new Runnable() {
			public void run() {
				if (SchedulerTableItem.this.tableItem != null) {
					SchedulerTableItem.this.tableItem.dispose();
				}

				SchedulerTableItem.this.tableItem = new TableItem(table, SWT.NONE, tableIndex);
				
				if (SchedulerTableItem.this.done) {
					SchedulerTableItem.this.tableItem.setImage(
						SchedulerView.COMPLETED_COLUMN, CHECKED_IMAGE);
				}
				else if (SchedulerTableItem.this.encounteredError) {
					SchedulerTableItem.this.tableItem.setImage(
						SchedulerView.COMPLETED_COLUMN, ERROR_IMAGE);					
				}
				else {
					SchedulerTableItem.this.tableItem.setImage(
						SchedulerView.COMPLETED_COLUMN, UNCHECKED_IMAGE);
				}
				
				SchedulerTableItem.this.tableItem.setText(
					SchedulerView.ALGORITHM_COLUMN, SchedulerTableItem.this.algorithmLabel);
				setCalendar();

				if (SchedulerTableItem.this.started) {
					if (SchedulerTableItem.this.progressBar != null)
						SchedulerTableItem.this.progressBar.dispose();
					if (SchedulerTableItem.this.isWorkTrackable || SchedulerTableItem.this.done) {
						SchedulerTableItem.this.progressBar = new ProgressBar(table, SWT.NONE);
						SchedulerTableItem.this.progressBar.setSelection(
							SchedulerTableItem.this.progressSelection);
					} else {
						SchedulerTableItem.this.progressBar =
							new ProgressBar(table, SWT.INDETERMINATE);
					}
				} else {
					SchedulerTableItem.this.progressBar = new ProgressBar(table, SWT.NONE);
				}

				SchedulerTableItem.this.tableEditor = new TableEditor(table);
				SchedulerTableItem.this.tableEditor.grabHorizontal = true;
				SchedulerTableItem.this.tableEditor.grabVertical = true;
				SchedulerTableItem.this.tableEditor.setEditor(
					SchedulerTableItem.this.progressBar,
					SchedulerTableItem.this.tableItem,
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
				String date = getDateString(SchedulerTableItem.this.calendar);
				String time = getTimeString(SchedulerTableItem.this.calendar);
				SchedulerTableItem.this.tableItem.setText(SchedulerView.DATE_COLUMN, date);
				SchedulerTableItem.this.tableItem.setText(SchedulerView.TIME_COLUMN, time);
			}
		});
	}
    
    /**
     * Notification of the start of the algorithm
     * 
     * @param table The parent table
     */
    public void algorithmStarted(Table table) {
    	this.done = false;
    	this.started = true;
    	drawTableEntry(table, table.indexOf(this.tableItem));
    }
    
    /**
     * Notification of rescheduling of the algorithm
     * @param calendar The rescheduled time
     */
    public void reschedule(Calendar calendar) {
		this.calendar = calendar;
		setCalendar();
	}

    /**
     * Notification of an error during algorithm execution
     * @param table Parent table
     */
    public void errorTableEntry(Table table) {
    	this.encounteredError = true;
		drawTableEntry(table, table.indexOf(this.tableItem));
    }
    
    /**
     * Refresh the table item
     *
     */
    public void refresh() {
		guiRun(new Runnable() {
			public void run() {
				if (!SchedulerTableItem.this.progressBar.isDisposed()) {
					SchedulerTableItem.this.progressBar.setSelection(SchedulerTableItem.this.progressSelection);
					SchedulerTableItem.this.tableEditor.grabHorizontal = true;
					SchedulerTableItem.this.tableEditor.grabVertical = true;
					SchedulerTableItem.this.tableEditor.setEditor(
						SchedulerTableItem.this.progressBar,
						SchedulerTableItem.this.tableItem,
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
				SchedulerTableItem.this.progressBar.dispose();
				SchedulerTableItem.this.tableItem.dispose();
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
    	if (this.done) {
    		return false;
    	}

    	return this.isCancellable;
    }
    
    /**
     * Whether or not the current algorithm is pausable, if the algorithm
     * is done, it will return false.
     * @return Pausable state
     */
    public boolean isPausable() {
    	if (this.done) {
    		return false;
    	}

    	return this.isPauseable;
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
    	if (this.algorithmProgressMonitor.isPaused() && !this.done) {
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
    	if (this.cancelRequested) {
    		return false;
    	}
    	return true;
    }
    
    /**
     * The algorithm done state
     * @return Done state
     */
    public boolean isDone() {
    	return this.done;
    }

    /**
     * Monitors an algorithm 
     */
	private class AlgorithmProgressMonitor implements ProgressMonitor {
		private double totalWorkUnits;

		public void describeWork(String currentWork) {
			SchedulerTableItem.this.workBeingDone = currentWork;
		}

		public void done() {
			SchedulerTableItem.this.done = true;
		}

		public boolean isCanceled() {
			return SchedulerTableItem.this.cancelRequested;
		}

		public boolean isPaused() {
			return SchedulerTableItem.this.pauseRequested;
		}

		public void setCanceled(boolean value) {
			SchedulerTableItem.this.cancelRequested = value;
		}

		public void setPaused(boolean value) {
			SchedulerTableItem.this.pauseRequested  = value;
		}

		public void start(int capabilities, int totalWorkUnits) {
			start(capabilities, (double) this.totalWorkUnits);
		}

		public void start(int capabilities, double totalWorkUnits) {
			if ((capabilities & ProgressMonitor.CANCELLABLE) > 0){
				SchedulerTableItem.this.isCancellable = true;
			}

			if ((capabilities & ProgressMonitor.PAUSEABLE) > 0) {
				SchedulerTableItem.this.isPauseable = true;
			}

			if ((capabilities & ProgressMonitor.WORK_TRACKABLE) > 0) {
				refresh();
				SchedulerTableItem.this.isWorkTrackable = true;
				guiRun(new Runnable() {
					public void run() {
						Table table = (Table) progressBar.getParent();
						SchedulerTableItem.this.progressBar.dispose();
						SchedulerTableItem.this.progressBar = new ProgressBar(table, SWT.NONE);
						SchedulerTableItem.this.progressBar.setSelection(progressBar.getMinimum());
						SchedulerTableItem.this.tableEditor = new TableEditor(table);
						SchedulerTableItem.this.tableEditor.grabHorizontal = true;
						SchedulerTableItem.this.tableEditor.grabVertical = true;
						SchedulerTableItem.this.tableEditor.setEditor(
							SchedulerTableItem.this.progressBar,
							SchedulerTableItem.this.tableItem,
							SchedulerView.PERCENT_COLUMN);
					}
				});
			}

			this.totalWorkUnits = totalWorkUnits;
		}

		public void worked(final int work) {
			worked((double) work);
		}

		public void worked(final double work) {
			guiRun(new Runnable() {
				public void run() {
					if (!SchedulerTableItem.this.progressBar.isDisposed()) {
						SchedulerTableItem.this.progressSelection = (int) (
							SchedulerTableItem.this.progressBar.getMaximum() *
							(work / AlgorithmProgressMonitor.this.totalWorkUnits));
					}
				}
			});

			refresh();
		}
	}
}
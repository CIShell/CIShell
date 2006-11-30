package org.cishell.reference.gui.scheduler;

import java.util.Calendar;

import org.cishell.app.service.scheduler.SchedulerService;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.algorithm.ProgressMonitor;
import org.cishell.framework.algorithm.ProgressTrackable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.osgi.framework.ServiceReference;

public class SchedulerTableItem {
	private SchedulerService schedulerService;
	private Algorithm algorithm;
	private Calendar cal;
	private String    algorithmLabel;
	
	private Table       table;
	private TableItem   tableItem;
	private TableEditor tableEditor;
	private ProgressBar progressBar;
	
    private static Image checkedImage	= Activator.createImage("check.gif");
    private static Image uncheckedImage	= Activator.createImage("uncheck.gif");
    private static Image errorImage		= Activator.createImage("error.gif");

    private String  workBeingDone;
    private boolean cancelRequested;
    private boolean pauseRequested;
    private boolean done;
    
    private boolean isCancellable;
    private boolean isPauseable;
    
    private AlgorithmProgressMonitor algorithmProgressMonitor;

    public SchedulerTableItem(SchedulerService schedulerService, Algorithm algorithm, 
    							Calendar cal, Table table) {
    	this.schedulerService = schedulerService;
    	this.algorithm = algorithm;
    	this.cal = cal;
    	this.table = table;
    	
    	this.cancelRequested = false;
    	this.done            = false;
    	
    	this.isCancellable   = false;
    	this.isPauseable     = false;
    	
		final ServiceReference serviceReference = schedulerService.getServiceReference(algorithm);
		if (serviceReference != null) {
			algorithmLabel = (String)serviceReference.getProperty(AlgorithmProperty.LABEL);
		}

    	
    	if (algorithm instanceof ProgressTrackable) {
        	algorithmProgressMonitor = new AlgorithmProgressMonitor();
        	((ProgressTrackable)algorithm).setProgressMonitor(algorithmProgressMonitor);
    	}
    }
    
    public void requestCancel(boolean request) {
    	cancelRequested = request;
    }
    
    public void requestPause(boolean request) {
    	pauseRequested = request;
    }
    
    public void initTableEntry(final int tblNdx) {
		done = false;
		guiRun(new Runnable() {
			public void run() {
				drawTableEntry(tblNdx, uncheckedImage, 0);
			}
		});
	}
    
    public void finishTableEntry(final int tblNdx) {
    	done = true;
		if (!tableItem.isDisposed()) {
			guiRun(new Runnable() {
				public void run() {
					int currentTblNdx;
					if (tblNdx == -1) {
						currentTblNdx = table.indexOf(tableItem);
					}
					else {
						currentTblNdx = tblNdx;
					}
					tableItem.dispose();
					progressBar.dispose();
					progressBar = new ProgressBar(table, SWT.NONE);
					drawTableEntry(currentTblNdx, checkedImage, progressBar
							.getMaximum());
				}
			});
		}
    }
    
    public void moveTableEntry(final int tblNdx) {
		guiRun(new Runnable() {
			public void run() {
				Image image            = tableItem.getImage(SchedulerView.COMPLETED_COLUMN);
				int progressSelection  = progressBar.getSelection();
				drawTableEntry(tblNdx, image, progressSelection);
			}
		});    	
    }
        
    private void drawTableEntry(final int tblNdx, final Image image, final int progressBarStatus) {
		guiRun(new Runnable() {
			public void run() {
				if (tableItem != null) {
					tableItem.dispose();
				}
				tableItem = new TableItem(table, SWT.NONE, tblNdx);
				tableItem.setImage(SchedulerView.COMPLETED_COLUMN, image);
				tableItem.setText(SchedulerView.ALGORITHM_COLUMN, algorithmLabel);
				setCalendar();

				if (progressBar == null) {
					progressBar = new ProgressBar(table, SWT.NONE);
				}
				progressBar.setSelection(progressBarStatus);
				tableEditor = new TableEditor(table);
				tableEditor.grabHorizontal = tableEditor.grabVertical = true;
				tableEditor.setEditor(progressBar, tableItem,
						SchedulerView.PERCENT_COLUMN);
			}
		});    	
    }

    
    private void createIndeterminateProgressBar() {
		if (!tableItem.isDisposed()) {
			guiRun(new Runnable() {
				public void run() {
					progressBar.dispose();
					progressBar = new ProgressBar(table, SWT.INDETERMINATE);
					tableEditor = new TableEditor(table);
					tableEditor.grabHorizontal = tableEditor.grabVertical = true;
					tableEditor.setEditor(progressBar, tableItem,
							SchedulerView.PERCENT_COLUMN);
				}
			});
		}
	}
    
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
    
    public void algorithmStarted() {
		if (!(algorithm instanceof ProgressTrackable)) {
			createIndeterminateProgressBar();
		} else {
			ProgressMonitor monitor = ((ProgressTrackable) algorithm)
					.getProgressMonitor();
			if (monitor == null) {
				createIndeterminateProgressBar();
			}
		}
	}
    
    public void reschedule(Calendar cal) {
		this.cal = cal;
		setCalendar();
	}
        
    public void errorTableEntry(int tblNdx) {
		drawTableEntry(tblNdx, errorImage, progressBar.getSelection());
    }
    
    public void refresh() {
		guiRun(new Runnable() {
			public void run() {
				tableEditor.grabHorizontal = tableEditor.grabVertical = true;
				tableEditor.setEditor(progressBar, tableItem, SchedulerView.PERCENT_COLUMN);
			}
		});
	}
    
    public void remove() {
		guiRun(new Runnable() {
			public void run() {
				progressBar.dispose();
				tableItem.dispose();
			}
		});
	}
    
    public TableItem getTableItem() {
    	return tableItem;
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
    
	private void guiRun(Runnable run) {
		if (Thread.currentThread() == Display.getDefault().getThread()) {
			run.run();
		} else {
			Display.getDefault().syncExec(run);
		}
	}
	
    public boolean isCancellable() {
    	return isCancellable;
    }
    
    public boolean isPauseable() {
    	return isPauseable;
    }
    
    public boolean isWorkTrackable() {
    	return isWorkTrackable();
    }
    
    public boolean isPaused() {
    	if (algorithmProgressMonitor.isPaused()) {
    		return false;
    	}
    	else {
    		return true;
    	}
    }
    
    public boolean isRunning() {
    	if (cancelRequested || done) {
    		return false;
    	}
    	return true;
    }
    
    public boolean isDone() {
    	return done;
    }
	
	private class AlgorithmProgressMonitor implements ProgressMonitor {
		private int totalWorkUnits;

		public void describeWork(String currentWork) {
			workBeingDone = currentWork;
		}

		public void done() {
			finishTableEntry(-1);
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
				guiRun(new Runnable() {
					public void run() {
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
			if (!progressBar.isDisposed()) {
				final int totalWorkUnits = this.totalWorkUnits;
				guiRun(new Runnable() {
					public void run() {
						int progress = (int) (progressBar.getMaximum() * ((double) work / (double) totalWorkUnits));
						progressBar.setSelection(progress);
					}
				});
			}
		}
	}
}

/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 21, 2006 at Indiana University.
 * Changed on Dec 19, 2006 at Indiana University
 * 
 * Contributors:
 * 	   Weixia(Bonnie) Huang, Bruce Herr, Ben Markines
 *     School of Library and Information Science, Indiana University 
 * ***************************************************************************/
package org.cishell.reference.gui.scheduler;

import java.util.Calendar;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.cishell.app.service.scheduler.SchedulerListener;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.algorithm.ProgressMonitor;
import org.cishell.framework.algorithm.ProgressTrackable;
import org.cishell.framework.data.Data;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.ServiceReference;


/**
 * Creates and maintains the overall GUI for the scheduler.  Controls the
 * table and controls (moving, removing, etc.).
 * 
 * @author Ben Markines (bmarkine@cs.indiana.edu)
 */
public class SchedulerView extends ViewPart implements SchedulerListener {
    private static SchedulerView schedulerView;
    public static final String ID_VIEW = "org.cishell.reference.gui.scheduler.SchedulerView";
    
    private static Image upImage 		= Activator.createImage("up.gif");
    private static Image downImage 		= Activator.createImage("down.gif");
    private static Image playImage 		= Activator.createImage("play.png");
    private static Image pauseImage 	= Activator.createImage("pause.png");
    

    private SchedulerContentModel schedulerContentModel;
	
	private Map  algorithmToGuiItemMap;
	private Map  tableItemToAlgorithmMap;

	private static Composite parent;
	private Button removeButton;
	private Button removeAutomatically;
	private Button up;
	private Button down;
	
	private Menu menu;
	
	private Button pauseStateButton;
	private Button playStateButton;
	
	private Table table;
	private boolean autoRemove;

	public static final int RESUME_INDEX  = 0;
	public static final int PAUSE_INDEX  = 1;
	public static final int CANCEL_INDEX = 2;
	
	private PauseListener  pauseListener;
	private CancelListener cancelListener;
	private StartListener  startListener;
	
    public static final int COMPLETED_COLUMN = 0;
    public static final int ALGORITHM_COLUMN = 1;
    public static final int DATE_COLUMN = 2;
    public static final int TIME_COLUMN = 3;
    public static final int PERCENT_COLUMN = 4;
    

    /**
     * Registers itself to a model, and creates the map from algorithm to 
     * GUI item.
     */
    public SchedulerView() {
    	schedulerContentModel = SchedulerContentModel.getInstance();
    	
    	schedulerContentModel.register(this);
    	algorithmToGuiItemMap = (Map)schedulerContentModel.getPersistedObject(this.getClass().getName());
    	if (algorithmToGuiItemMap == null) {
    		algorithmToGuiItemMap = Collections.synchronizedMap(new Hashtable());
    	}
    	else {
    		algorithmToGuiItemMap = Collections.synchronizedMap(algorithmToGuiItemMap);    		
    	}
		schedulerView = this;
    }
    
    /**
     * Get the current scheduler view
     * @return The scheduler view
     */
    public static SchedulerView getDefault() {
    	return schedulerView;
    }

    /**
     * Creates buttons, table, and registers listeners
     * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 * @param parent The SWT parent
	 */
    public void createPartControl(Composite parent) {
        this.parent = parent;

        Composite control = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        control.setLayout(layout);

        removeButton = new Button(control, SWT.PUSH);
        removeButton.setText("Remove From List");
        removeButton.setEnabled(true);
        removeButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    removeSelection();
                    refresh();
                }
            });

        
        removeAutomatically = new Button(control, SWT.CHECK);
        removeAutomatically.setText("Remove completed automatically");
        removeAutomatically.addSelectionListener(new SelectionAdapter() {	
                public void widgetSelected(SelectionEvent event) {
                    autoRemove = removeAutomatically.getSelection();
                }
            });

        Button removeAllCompleted = new Button(control, SWT.PUSH);
        removeAllCompleted.setText("Remove all completed");
        removeAllCompleted.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeCompleted();
				refresh();
			}
		});
        
		playStateButton = new Button(control, SWT.PUSH);
		playStateButton.setImage(playImage);
		playStateButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//schedulerService.setRunning(true);
				schedulerContentModel.schedulerRunStateChanged(true);
			}
		});

		pauseStateButton = new Button(control, SWT.PUSH);
		pauseStateButton.setImage(pauseImage);
		pauseStateButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//schedulerService.setRunning(false);
				schedulerContentModel.schedulerRunStateChanged(false);
			}
		});
		
		if (schedulerContentModel.isRunning()) {
			playStateButton.setEnabled(false);
		}
		else {
			pauseStateButton.setEnabled(false);
		}

		GridData removeAllCompletedData = new GridData();
		removeAllCompletedData.horizontalAlignment = SWT.RIGHT;
        removeAllCompleted.setLayoutData(removeAllCompletedData);

        // composite for up and down buttons for table entries
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
        createTableEntries(table);
        
        table.addSelectionListener(new TableListener());
		
		//Set right click menu
		menu = new Menu(table);
		menu.setVisible(false);

		MenuItem startItem = new MenuItem(menu, SWT.PUSH);
		startItem.setText("resume");
		startListener = new StartListener();
		startItem.addListener(SWT.Selection, startListener);

		MenuItem pauseItem = new MenuItem(menu, SWT.PUSH);
		pauseItem.setText("pause");
		pauseListener = new PauseListener();
		pauseItem.addListener(SWT.Selection, pauseListener);

		MenuItem cancelItem = new MenuItem(menu, SWT.PUSH);
		cancelItem.setText("cancel");
		cancelListener = new CancelListener();
		cancelItem.addListener(SWT.Selection, cancelListener);

		table.setMenu(menu);
        
        GridData gridData = new GridData(GridData.FILL_BOTH);
        table.setLayoutData(gridData);
    }

	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Notifies the corresponding table item of the offending algorithm
	 * @param algorithm The algorithm that errored
	 * @param error The throwable object
	 */
	public void algorithmError(final Algorithm algorithm, Throwable error) {
		guiRun(new Runnable() {
			public void run() {
				SchedulerTableItem schedulerTableItem = (SchedulerTableItem)algorithmToGuiItemMap.get(algorithm);
				if (schedulerTableItem != null)
					schedulerTableItem.errorTableEntry(table);
			}
		});
		refresh();
	}

	/**
	 * Notifies the corresponding table entry when an algorithm has completed
	 * its' task
	 * 
	 * @param algorithm The finished task
	 * @param createData List of data objects created
	 */
	public void algorithmFinished(Algorithm algorithm, Data[] createdData) {
		SchedulerTableItem schedulerTableItem = (SchedulerTableItem)algorithmToGuiItemMap.get(algorithm);
		if (schedulerTableItem != null) {
			schedulerTableItem.finishTableEntry(table);
			tableItemToAlgorithmMap.put(schedulerTableItem.getTableItem(), algorithm);
			if (autoRemove) {
				schedulerTableItem.remove();
				TableItem tableItem = schedulerTableItem.getTableItem();
				tableItemToAlgorithmMap.remove(tableItem);
				algorithmToGuiItemMap.remove(algorithm);
			} 
		}
		refresh();
	}

	/**
	 * Notifies the corresponding table item of an algorithm being rescheduled
	 * @param algorithm The task that is rescheduled
	 * @param time The rescheduled time
	 */
	public void algorithmRescheduled(Algorithm algorithm, Calendar time) {
		SchedulerTableItem schedulerTableItem = (SchedulerTableItem)algorithmToGuiItemMap.get(algorithm);
		if (schedulerTableItem != null)
			schedulerTableItem.reschedule(time);
		refresh();		
	}

	/**
	 * Creates a table item for the the algorithm, and adds an entry to the 
	 * appropriate maps.
	 * @param algorithm The task that is to execute
	 * @param cal When the task will begin execution
	 */
	public void algorithmScheduled(final Algorithm algorithm, final Calendar cal) {
		final Table table = this.table;
		guiRun(new Runnable() {
			public void run() {
				ServiceReference serviceReference = Activator
						.getSchedulerService().getServiceReference(algorithm);
				String algorithmLabel = "";
				if (serviceReference != null) {
					algorithmLabel = (String) serviceReference
							.getProperty(AlgorithmProperty.LABEL);
				}

				SchedulerTableItem schedulerTableItem =
					new SchedulerTableItem(algorithmLabel, cal, algorithm);
				schedulerTableItem.initTableEntry(table, 0);
				algorithmToGuiItemMap.put(algorithm, schedulerTableItem);

				TableItem tableItem = schedulerTableItem.getTableItem();
				tableItemToAlgorithmMap.put(tableItem, algorithm);
			}
		});
		
		refresh();
	}
	
	/**
	 * Notifies the corresponding table item that an algorithm has started
	 * @param algorithm The task that is started
	 */
	public void algorithmStarted(final Algorithm algorithm) {
		guiRun(new Runnable() {
			public void run() {
				SchedulerTableItem schedulerTableItem = (SchedulerTableItem) algorithmToGuiItemMap
						.get(algorithm);
				schedulerTableItem.algorithmStarted(table);
				TableItem tableItem = schedulerTableItem.getTableItem();
				tableItemToAlgorithmMap.put(tableItem, algorithm);
			}
		});
		refresh();
	}

	/**
	 * Notifies the corresponding table item that an algorithm became unscheduled
	 * @param algorithm The task that became unscheduled
	 */
	public void algorithmUnscheduled(Algorithm algorithm) {
		SchedulerTableItem schedulerTableItem = (SchedulerTableItem)algorithmToGuiItemMap.get(algorithm);
		schedulerTableItem.remove();
		refresh();
	}

	/**
	 * Clear the current scheduler of all jobs
	 */
	public void schedulerCleared() {
		for (Iterator i = algorithmToGuiItemMap.values().iterator(); i
				.hasNext();) {
			SchedulerTableItem schedulerTableItem = (SchedulerTableItem) i
					.next();
			schedulerTableItem.remove();
		}
		algorithmToGuiItemMap.clear();
		tableItemToAlgorithmMap.clear();
		refresh();
	}

	/**
	 * Notification of the state of the scheduler has changed
	 * @param isRunning Flag determining if the scheduler is running
	 */
	public void schedulerRunStateChanged(boolean isRunning) {
		if (isRunning) {
			pauseStateButton.setEnabled(true);
			playStateButton.setEnabled(false);
		}
		else {
			playStateButton.setEnabled(true);
			pauseStateButton.setEnabled(false);			
		}
		refresh();
	}

	/**
	 * This will create the table entries if there are any in the map
	 * @param table The parent table to create the entries
	 */
    private void createTableEntries(Table table) {
		Set keys = algorithmToGuiItemMap.keySet();

		tableItemToAlgorithmMap = Collections.synchronizedMap(new Hashtable());

		for (Iterator i = keys.iterator(); i.hasNext();) {
			Algorithm algorithm = (Algorithm) i.next();
			SchedulerTableItem schedulerTableItem = (SchedulerTableItem) algorithmToGuiItemMap
					.get(algorithm);
			schedulerTableItem.initTableEntry(table, 0);

			TableItem tableItem = schedulerTableItem.getTableItem();
			tableItemToAlgorithmMap.put(tableItem, algorithm);
		}
	}
    
    /**
	 * Create the Table control
	 * @param parent The parent of the Table
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
        column.setWidth(90);

        // 4th column with time
        column = new TableColumn(table, SWT.LEFT, 3);
        column.setText("Time");
        column.setWidth(90);

        // 5th column with task PercentComplete 
        column = new TableColumn(table, SWT.CENTER, 4);
        column.setText("% Complete");
        column.setWidth(120);

        table.addKeyListener(new KeyAdapter() {
                public void keyReleased(KeyEvent e) {
                    if (e.keyCode == SWT.DEL) {
                        removeSelection();
                        refresh();
                    }
                }
            });

        //listener for dragging of items up and down in the running queue
        ItemDragListener dragListener = new ItemDragListener();
        table.addMouseMoveListener(dragListener);
        table.addMouseListener(dragListener);
    }
    
    /**
     * Remove all of the table items that are selected
     */
    private void removeSelection() {
		TableItem[] tableItems = table.getSelection();
		for (int i = 0; i < tableItems.length; ++i) {
			for (Iterator j = algorithmToGuiItemMap.keySet().iterator(); j
					.hasNext();) {
				Algorithm algorithm = (Algorithm) j.next();
				SchedulerTableItem schedulerTableItem = (SchedulerTableItem) algorithmToGuiItemMap
						.get(algorithm);
				if (tableItems[i].equals(schedulerTableItem.getTableItem())) {
					if (algorithmIsProgressTrackable(algorithm)) {
						ProgressMonitor monitor = ((ProgressTrackable) algorithm)
								.getProgressMonitor();
						monitor.setCanceled(true);
					}
					schedulerTableItem.remove();
					algorithmToGuiItemMap.remove(algorithm);
					break;
				}
			}
		}
	}

    /**
     * Removes the elements that have completed
     *
     */
    private void removeCompleted() {
		for (Iterator i = algorithmToGuiItemMap.values().iterator(); i
				.hasNext();) {
			SchedulerTableItem schedulerTableItem = (SchedulerTableItem) i
					.next();
			if (schedulerTableItem.isDone()) {
				i.remove();
				schedulerTableItem.remove();
			}
		}
	}
    
    /**
     * Cleans the tableItemToAlgorithmMap of disposed items.  Refreshes
     * each active table item.  Refreshes the up and down buttons.
     *
     */
    private void refresh() {
		for (Iterator i = tableItemToAlgorithmMap.keySet().iterator(); i
				.hasNext();) {
			final TableItem tableItem = (TableItem) i.next();
			if (tableItem.isDisposed()) {
				i.remove();
			}
		}

		for (Iterator i = algorithmToGuiItemMap.values().iterator(); i
				.hasNext();) {
			SchedulerTableItem schedulerTableItem = (SchedulerTableItem) i
					.next();
			schedulerTableItem.refresh();
		}
		refreshUpAndDownButtons();
	}
    
    /**
     * Check whether or not the algorithm implements the interface
     * ProgressTrackable
     * 
     * @param algorithm The algorithm to interrogate
     * @return Whether or not the algorithm is trackable
     */
    private boolean algorithmIsProgressTrackable(Algorithm algorithm) {
		if (algorithm != null) {
			ProgressMonitor monitor = ((ProgressTrackable) algorithm)
					.getProgressMonitor();
			if (monitor != null) {
				return true;
			}
		}
		return false;
	}
    
    /**
     * Given an 
     * @param algorithm
     */
    private void setEnabledMenuItems(Algorithm algorithm) {
		SchedulerTableItem schedulerTableItem = (SchedulerTableItem) algorithmToGuiItemMap
				.get(algorithm);

		for (int i = 0; i < menu.getItemCount(); ++i) {
			MenuItem menuItem = menu.getItem(i);
			menuItem.setEnabled(false);
		}
		if (schedulerTableItem.isRunning()
				&& schedulerTableItem.isCancellable()) {
			MenuItem menuItem = menu.getItem(CANCEL_INDEX);
			menuItem.setEnabled(true);
		}

		if (schedulerTableItem.isPausable()) {
			if (schedulerTableItem.isPaused()) {
				MenuItem menuItem = menu.getItem(RESUME_INDEX);
				menuItem.setEnabled(true);
			} else {
				MenuItem menuItem = menu.getItem(PAUSE_INDEX);
				menuItem.setEnabled(true);
			}
		}
	}
    
    /**
     * Moves a table item to another slot
     * @param ndxToMove Original table item to move
     * @param destNdx Destination of table item
     */
    private void moveTableItems(int ndxToMove, int destNdx) {
		TableItem item = table.getItem(ndxToMove);
		if (item != null) {
			Algorithm algorithm = (Algorithm) tableItemToAlgorithmMap
					.get(item);
			tableItemToAlgorithmMap.remove(item);

			SchedulerTableItem schedulerTableItem = (SchedulerTableItem) algorithmToGuiItemMap
					.get(algorithm);
			schedulerTableItem.moveTableEntry(table, destNdx);
			table.setSelection(destNdx);

			TableItem tableItem = schedulerTableItem.getTableItem();
			tableItemToAlgorithmMap.put(tableItem, algorithm);

			refresh();
		}    	
    }
    
    /**
     * Refreshes the up and down buttons depending on the items selected and location
     * in the table
     *
     */
    private void refreshUpAndDownButtons() {
		guiRun(new Runnable() {
			public void run() {
				if (table.getItemCount() > 1 && table.getSelectionCount() == 1) {
					if (table.getSelectionIndex() > 0) {
						up.setEnabled(true);
					}
					else {
						up.setEnabled(false);							
					}
					if (table.getSelectionIndex() < table.getItemCount()-1) {
						down.setEnabled(true);						
					}
					else {
						down.setEnabled(false);							
					}
				}
				else {
					up.setEnabled(false);
					down.setEnabled(false);
				}
			}
		});    	
    }
    
    /**
     * Insures that the current thread is the UI thread
     * @param run Thread to sync with
     */
	private void guiRun(Runnable run) {
		if (Thread.currentThread() == Display.getDefault().getThread()) {
			run.run();
		} else {
			Display.getDefault().syncExec(run);
		}
	}
	
	/**
	 * When the view is disposed, this will persist the current items
	 * it manages, and removes itself from the monitor
	 */
	public void dispose() {
		schedulerContentModel.persistObject(this.getClass().getName(), algorithmToGuiItemMap);
		schedulerContentModel.deregister(this);
	}

    /**
     * Any interaction to the table will be checked for enabling and
     * disabling items in the table.
     */
    private class TableListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			TableItem[] items = table.getSelection();
			for (int i = 0; i < items.length; ++i) {
				TableItem item = items[i];
				Algorithm algorithm = (Algorithm) tableItemToAlgorithmMap
						.get(item);
				if (algorithm != null) {
					if (algorithmIsProgressTrackable(algorithm)) {
						removeButton.setEnabled(true);
					} else {
						SchedulerTableItem schedulerTableItem = (SchedulerTableItem) algorithmToGuiItemMap
								.get(algorithm);
						if (schedulerTableItem.isDone()) {
							removeButton.setEnabled(true);
						} else {
							removeButton.setEnabled(false);
						}
					}
					setEnabledMenuItems(algorithm);
				}
			}
			refresh();
		}
	}

    
    /**
     * Pauses an algorithm if it is pausable
     */
	private class PauseListener implements Listener {
		public void handleEvent(Event event) {
			TableItem item   = table.getItem(table.getSelectionIndex());
			if (item != null) {
				Algorithm algorithm = (Algorithm)tableItemToAlgorithmMap.get(item);
				if (algorithm instanceof ProgressTrackable) {
					ProgressMonitor monitor = ((ProgressTrackable)algorithm).getProgressMonitor();
					if (monitor != null) {
						monitor.setPaused(true);
						setEnabledMenuItems(algorithm);
					}
				}
			}
		}
	}

	/**
	 * Cancels an algorithm if it is cancellable
	 * @author bmarkine
	 *
	 */
	private class CancelListener implements Listener {
		public void handleEvent(Event event) {
			TableItem item   = table.getItem(table.getSelectionIndex());
			if (item != null) {
				Algorithm algorithm = (Algorithm) tableItemToAlgorithmMap
						.get(item);
				if (algorithm instanceof ProgressTrackable) {
					ProgressMonitor monitor = ((ProgressTrackable) algorithm)
							.getProgressMonitor();
					if (monitor != null) {
						monitor.setCanceled(true);
						setEnabledMenuItems(algorithm);
					}
				}
			}
		}
	}
	
	/**
	 * Starts an algorithm to start
	 */
	private class StartListener implements Listener {		
		public void handleEvent(Event event) {
			TableItem item   = table.getItem(table.getSelectionIndex());
			if (item != null) {
				Algorithm algorithm = (Algorithm) tableItemToAlgorithmMap
						.get(item);
				if (algorithm instanceof ProgressTrackable) {
					ProgressMonitor monitor = ((ProgressTrackable) algorithm)
							.getProgressMonitor();
					if (monitor != null) {
						monitor.setPaused(false);
						setEnabledMenuItems(algorithm);
					}
				}
			}
		}
	}
	
	/**
	 * Moves a table item up on the table
	 */
	private class UpButtonListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			int tblNdx = table.getSelectionIndex();
			if (tblNdx != -1) {
				moveTableItems(tblNdx, tblNdx-1);
			}
		}		
	}
	
	/**
	 * Moves a table item down on the table
	 *
	 */
	private class DownButtonListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			int tblNdx = table.getSelectionIndex();
			if (tblNdx != -1) {
				TableItem item = table.getItem(tblNdx);
				if (item != null && tblNdx < table.getItemCount()-1) {
					moveTableItems(tblNdx, tblNdx+1);
				}
			}
		}		
	}
	
	/**
	 * Listens for mouse dragging to move the items around the table
	 */
    private class ItemDragListener extends MouseAdapter implements MouseMoveListener {
        private boolean down = false;
        private Algorithm movingAlgorithm;
        private int movingIndex;
        private int currentIndex;

        //if the mouse is down (dragging), discover when it is dragged over
        //a new table item and swap them if possible in the running queue
        public void mouseMove(MouseEvent e) {
            if (down && (movingAlgorithm != null)) {
            	SchedulerTableItem schedulerTableItem = (SchedulerTableItem)algorithmToGuiItemMap.get(movingAlgorithm);
            	TableItem movingTableItem = schedulerTableItem.getTableItem();
            	
                TableItem currentItem = table.getItem(new Point(e.x, e.y));

                if (currentItem == null || movingTableItem.equals(currentItem)) {
                  return;
                }

                movingIndex  = table.indexOf(movingTableItem);
                currentIndex = table.indexOf(currentItem);
                
   				moveTableItems(movingIndex, currentIndex);
            }
        }

        //reset the selected item and set the flag that the mouse is down
        public void mouseDown(MouseEvent e) {
            TableItem item = table.getItem(new Point(e.x, e.y));
            if(item == null) return;

            if (e.button == 1) {
                down = true;
                
				movingAlgorithm = (Algorithm) tableItemToAlgorithmMap.get(item);
            }
        }

        //unset the mouse down flag and clear the selected item
        public void mouseUp(MouseEvent e) {
            if (e.button == 1) {
                down = false;
                movingAlgorithm = null;

                Cursor cursor = new Cursor(parent.getDisplay(), SWT.CURSOR_ARROW);
                table.setCursor(cursor);
            }
        }
    }
}
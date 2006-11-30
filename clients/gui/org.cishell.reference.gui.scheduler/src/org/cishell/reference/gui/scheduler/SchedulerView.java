/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 21, 2006 at Indiana University.
 * 
 * Contributors:
 * 	   Weixia(Bonnie) Huang, Bruce Herr
 *     School of Library and Information Science, Indiana University 
 * ***************************************************************************/
package org.cishell.reference.gui.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cishell.app.service.scheduler.SchedulerListener;
import org.cishell.app.service.scheduler.SchedulerService;
import org.cishell.framework.algorithm.Algorithm;
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


/**
 * @author Ben Markines (bmarkine@cs.indiana.edu)
 */
public class SchedulerView extends ViewPart implements SchedulerListener {
    private static Image upImage 		= Activator.createImage("up.gif");
    private static Image downImage 		= Activator.createImage("down.gif");
    private static Image playImage 		= Activator.createImage("play.jpeg");
    private static Image pauseImage 	= Activator.createImage("pause.jpeg");

	private SchedulerService schedulerService;
	
	private Map  algorithmToGuiItemMap;
	private Map  tableItemToAlgorithmMap;
	private List algorithmDoneList;

	private static Composite parent;
	//private Button scheduleButton;
	private Button removeButton;
	private Button removeAutomatically;
	private Button up;
	private Button down;
	
	private Menu menu;
	
	private Button algorithmStateButton;
	private boolean isActive;
	
	private Table table;
	private boolean autoRemove;

	public static final int PAUSE_INDEX  = 0;
	public static final int CANCEL_INDEX = 1;
	public static final int START_INDEX  = 2;
	
	private PauseListener  pauseListener;
	private CancelListener cancelListener;
	private StartListener  startListener;
	
    public static final int COMPLETED_COLUMN = 0;
    public static final int ALGORITHM_COLUMN = 1;
    public static final int DATE_COLUMN = 2;
    public static final int TIME_COLUMN = 3;
    public static final int PERCENT_COLUMN = 4;
    

    /**
     * Constructor
     */
    public SchedulerView() {
		schedulerService = Activator.getSchedulerService();
		if (schedulerService != null) {
			schedulerService.addSchedulerListener(this);
		}
		algorithmToGuiItemMap   = new Hashtable();
		tableItemToAlgorithmMap = new Hashtable();
		algorithmDoneList = new ArrayList();
		isActive = true;
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent) {
        this.parent = parent;

        Composite control = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 4;

        control.setLayout(layout);

        //create the buttons
        //scheduleButton = new Button(control, SWT.PUSH);
        //scheduleButton.setText("Schedule...");
        //scheduleButton.setToolTipText(
        //    "Reschedule the selected item to another " + "date/time");
        //scheduleButton.setEnabled(false);
        /*
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
        */
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
        
//      algorithmStateButton = new Button(control, SWT.PUSH);
//		algorithmStateButton.setImage(pauseImage);
//		algorithmStateButton.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(SelectionEvent e) {
//            	if (isActive) {
//                    schedulerService.setRunning(false);
//            	}
//            	else {
//                    schedulerService.setRunning(true);
//            	}
//            }
//		});

		
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
        
        table.addSelectionListener(new TableListener());
		//table.addMouseListener(new ContextMenuListener());

		
		//Set right click menu
		menu = new Menu(table);
		menu.setVisible(false);

		MenuItem pauseItem = new MenuItem(menu, SWT.PUSH);
		pauseItem.setText("pause");
		pauseListener = new PauseListener();
		pauseItem.addListener(SWT.Selection, pauseListener);

		MenuItem cancelItem = new MenuItem(menu, SWT.PUSH);
		cancelItem.setText("cancel");
		cancelListener = new CancelListener();
		cancelItem.addListener(SWT.Selection, cancelListener);

		MenuItem startItem = new MenuItem(menu, SWT.PUSH);
		startItem.setText("start");
		startListener = new StartListener();
		startItem.addListener(SWT.Selection, startListener);

		table.setMenu(menu);
        
        GridData gridData = new GridData(GridData.FILL_BOTH);
        table.setLayoutData(gridData);
        
        /*    
        IMenuManager menu = IVCApplication.getMenuManager();
        IContributionItem item = menu.findUsingPath("tools/scheduler");
        if(item != null){
            final IAction action = ((ActionContributionItem) item).getAction();
            action.setChecked(true);
        }

        //initialize based on data in the model
        refreshView();
        */  
    }

	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	public void algorithmError(Algorithm algorithm, Throwable error) {
		SchedulerTableItem schedulerTableItem = (SchedulerTableItem)algorithmToGuiItemMap.get(algorithm);
		schedulerTableItem.errorTableEntry(table.indexOf(schedulerTableItem.getTableItem()));
		refresh();
	}

	public void algorithmFinished(Algorithm algorithm, Data[] createdData) {
		SchedulerTableItem schedulerTableItem = (SchedulerTableItem)algorithmToGuiItemMap.get(algorithm);
		if (schedulerTableItem != null) {
			TableItem tableItem = schedulerTableItem.getTableItem();
			tableItemToAlgorithmMap.remove(tableItem);
			
			schedulerTableItem.finishTableEntry(-1);
			if (autoRemove) {
				schedulerTableItem.remove();
				algorithmToGuiItemMap.remove(algorithm);
			} else {
				 tableItem = schedulerTableItem.getTableItem();
				tableItemToAlgorithmMap.put(tableItem, algorithm);
				algorithmDoneList.add(algorithm);
			}
		}
		refresh();
	}

	public void algorithmRescheduled(Algorithm algorithm, Calendar time) {
		SchedulerTableItem schedulerTableItem = (SchedulerTableItem)algorithmToGuiItemMap.get(algorithm);
		schedulerTableItem.reschedule(time);
		refresh();		
	}

	public void algorithmScheduled(Algorithm algorithm, Calendar cal) {
		SchedulerTableItem schedulerTableItem = new SchedulerTableItem(schedulerService, algorithm, cal, table);
		schedulerTableItem.initTableEntry(0);
		algorithmToGuiItemMap.put(algorithm, schedulerTableItem);
		
		TableItem tableItem = schedulerTableItem.getTableItem();
		tableItemToAlgorithmMap.put(tableItem, algorithm);
		
		refresh();
	}

	public void algorithmStarted(Algorithm algorithm) {
		SchedulerTableItem schedulerTableItem = (SchedulerTableItem)algorithmToGuiItemMap.get(algorithm);
		schedulerTableItem.algorithmStarted();
		refresh();
	}

	public void algorithmUnscheduled(Algorithm algorithm) {
		SchedulerTableItem schedulerTableItem = (SchedulerTableItem)algorithmToGuiItemMap.get(algorithm);
		schedulerTableItem.remove();
		refresh();
	}

	public void schedulerCleared() {
    	for (Iterator i = algorithmToGuiItemMap.values().iterator(); i.hasNext();) {
			SchedulerTableItem schedulerTableItem = (SchedulerTableItem)i.next();
			schedulerTableItem.remove();
    	}
    	algorithmToGuiItemMap.clear();
    	tableItemToAlgorithmMap.clear();
		refresh();
	}

	public void schedulerRunStateChanged(boolean isRunning) {
		isActive = isRunning;
		if (isActive) {
			algorithmStateButton.setImage(pauseImage);			
		}
		else {
			algorithmStateButton.setImage(playImage);			
		}
		refresh();
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
        column.setWidth(90);

        // 4th column with time
        column = new TableColumn(table, SWT.LEFT, 3);
        column.setText("Time");
        column.setWidth(90);

        // 5th column with task PercentComplete 
        column = new TableColumn(table, SWT.CENTER, 4);
        column.setText("% Complete");
        column.setWidth(120);

//        //selection listener to keep currentSelection variable up to date
//        table.addSelectionListener(new SelectionAdapter() {
//                public void widgetSelected(SelectionEvent e) {
//                    TableItem[] selection = table.getSelection();
//                    currentSelection = new SchedulerItem[selection.length];
//
//                    for (int i = 0; i < selection.length; i++) {
//                        SchedulerItem item = SchedulerItem.getSchedulerItem(selection[i]);
//                        currentSelection[i] = item;
//                    }
//
//                    updateUpAndDown();
//                    refreshButtons();
//                }
//            });
//
//        //key listener to allow you to remove items with the delete key
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
    
    private void removeSelection() {
    	TableItem[] tableItems = table.getSelection();
    	
    	for (int i = 0; i < tableItems.length; ++i) {
    		for (Iterator j = algorithmToGuiItemMap.keySet().iterator(); j.hasNext();) {
    			Algorithm algorithm = (Algorithm)j.next();
    			SchedulerTableItem schedulerTableItem = 
    				(SchedulerTableItem)algorithmToGuiItemMap.get(algorithm);
    			if (tableItems[i].equals(schedulerTableItem.getTableItem())) {
    				if (algorithmIsProgressTrackable(algorithm)) {
    					ProgressMonitor monitor = ((ProgressTrackable)algorithm).getProgressMonitor();
    					monitor.setCanceled(true);
    				}
   					schedulerTableItem.remove();
   					algorithmToGuiItemMap.remove(algorithm);
    				break;
    			}
    		}
    	}
    }
    
    private void removeCompleted() {
    	for (Iterator i = algorithmDoneList.iterator(); i.hasNext();) {
    		Object pid = i.next();
			SchedulerTableItem schedulerTableItem = 
				(SchedulerTableItem)algorithmToGuiItemMap.get(pid);
			if (schedulerTableItem != null) {
				schedulerTableItem.remove();
				algorithmToGuiItemMap.remove(pid);
			}
    	}
    	algorithmDoneList.clear();
    }
    
    private void refresh() {		
    	for (Iterator i = algorithmToGuiItemMap.values().iterator(); i.hasNext();) {
			SchedulerTableItem schedulerTableItem = (SchedulerTableItem)i.next();
			schedulerTableItem.refresh();
    	}
    	refreshUpAndDownButtons();
    }
    
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
    
    private void setEnabledMenuItems(Algorithm algorithm) {
    	SchedulerTableItem schedulerTableItem = (SchedulerTableItem)algorithmToGuiItemMap.get(algorithm);
    	
    	if (!schedulerTableItem.isRunning()) {
    		for (int i = 0; i < menu.getItemCount(); ++i) {
    			MenuItem menuItem = menu.getItem(i);
    			menuItem.setEnabled(false);
    		}
    	}
    	else {
			MenuItem menuItem = menu.getItem(CANCEL_INDEX);
			menuItem.setEnabled(schedulerTableItem.isCancellable());

			if (schedulerTableItem.isPaused()) {
				menuItem = menu.getItem(PAUSE_INDEX);
				menuItem.setEnabled(schedulerTableItem.isPauseable());
				menuItem = menu.getItem(START_INDEX);
				menuItem.setEnabled(false);
			} else {
				menuItem = menu.getItem(PAUSE_INDEX);
				menuItem.setEnabled(false);
				menuItem = menu.getItem(START_INDEX);
				menuItem.setEnabled(schedulerTableItem.isPauseable());
			}
		}
    }
    
    private void moveTableItems(int ndxToMove, int destNdx) {
		TableItem item = table.getItem(ndxToMove);
		if (item != null) {
			Algorithm algorithm = (Algorithm) tableItemToAlgorithmMap
					.get(item);
			tableItemToAlgorithmMap.remove(item);

			SchedulerTableItem schedulerTableItem = (SchedulerTableItem) algorithmToGuiItemMap
					.get(algorithm);
			schedulerTableItem.moveTableEntry(destNdx);
			table.setSelection(destNdx);

			TableItem tableItem = schedulerTableItem.getTableItem();
			tableItemToAlgorithmMap.put(tableItem, algorithm);

			refresh();
		}    	
    }
    
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
    
	private void guiRun(Runnable run) {
		if (Thread.currentThread() == Display.getDefault().getThread()) {
			run.run();
		} else {
			Display.getDefault().syncExec(run);
		}
	}

    
    private class TableListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			TableItem[] items = table.getSelection();
			for (int i = 0; i < items.length; ++i) {
				TableItem item = items[i];
				Algorithm algorithm = (Algorithm) tableItemToAlgorithmMap
						.get(item);
				if (algorithmIsProgressTrackable(algorithm)) {
					removeButton.setEnabled(true);
					setEnabledMenuItems(algorithm);
				} else {
					SchedulerTableItem schedulerTableItem = (SchedulerTableItem) algorithmToGuiItemMap
							.get(algorithm);
					if (schedulerTableItem.isDone()) {
						removeButton.setEnabled(true);
					} else {
						removeButton.setEnabled(false);
						break;
					}
					setEnabledMenuItems(algorithm);
				}
			}
			refresh();
		}
	}

    
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
	
	private class UpButtonListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			int tblNdx = table.getSelectionIndex();
			if (tblNdx != -1) {
				moveTableItems(tblNdx, tblNdx-1);
//
//				TableItem item = table.getItem(tblNdx);
//				if (item != null && tblNdx > 0) {
//					Algorithm algorithm = (Algorithm) tableItemToAlgorithmMap
//							.get(item);
//					tableItemToAlgorithmMap.remove(item);
//					item.dispose();
//
//					SchedulerTableItem schedulerTableItem = (SchedulerTableItem) algorithmToGuiItemMap
//							.get(algorithm);
//					schedulerTableItem.createTableEntry(tblNdx-1);
//					table.setSelection(tblNdx-1);
//
//					TableItem tableItem = schedulerTableItem.getTableItem();
//					tableItemToAlgorithmMap.put(tableItem, algorithm);
//
//					refresh();
//				}
			}
		}		
	}
	
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
            if (e.button == 1) {
                down = true;

                TableItem item = table.getItem(new Point(e.x, e.y));
                if(item == null) return;
                
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
/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Nov 7, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder.builder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * 
 * @author Bruce Herr
 */
public class PallettedListBuilder {
    private Composite panel;
    private InputPallet pallet;
    private Table table;
    private BuilderDelegate delegate;
    
    public PallettedListBuilder(Composite parent, BuilderDelegate delegate) {
        this(parent, SWT.NONE, delegate);
    }
    
    public PallettedListBuilder(Composite parent, int style, BuilderDelegate delegate) {
        this.delegate = delegate;
        createGUI(parent, style);
    }

    private void createGUI(Composite parent, int style) {
        panel = new Composite(parent, style);
        
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        panel.setLayout(gridLayout);
        
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL, GridData.FILL_VERTICAL, true, true);
        
        table = createTable(panel);
        table.setLayoutData(gridData);
        setupTableDragging();
        setupTableDoubleClicking();
        setupTableDeleteKey();
        
        gridData = new GridData(GridData.CENTER, GridData.BEGINNING, false, false);
        
        pallet = new InputPallet(panel, style);
        pallet.getComposite().setLayoutData(gridData);
        
        
        Composite trashPanel = createTrashCan(panel);
        gridData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
        gridData.horizontalSpan = 2;
        
        trashPanel.setLayoutData(gridData);
    }
    
    private Composite createTrashCan(Composite parent) {
        Composite trashPanel = new Composite(parent, SWT.NONE);
        trashPanel.setLayout(new RowLayout());
        
        final Label trash = new Label(trashPanel, SWT.BORDER);
        trash.setText(" Trash Can ");
        
        int operations = DND.DROP_MOVE;
        DropTarget target = new DropTarget(trash, operations);
        
        Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
        target.setTransfer(types);
        
        target.addDropListener(new DropTargetAdapter() {
            public void dragEnter(DropTargetEvent event) {
                if (event.operations == DND.DROP_MOVE) {
                    event.detail = DND.DROP_MOVE;
                }
            }
            
            public void dragOver(DropTargetEvent event) {
                event.feedback = DND.FEEDBACK_SELECT;
            }
            
            public void drop(DropTargetEvent event) {
                int selection = table.getSelectionIndex();
                
                if (selection != -1 && table.getItemCount() > 0) {
                    table.remove(selection);
                }
            }
        });
        
        return trashPanel;
    }
    
    private Table createTable(Composite parent) {
        final Table table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
        
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        
        String[] titles = delegate.getColumns();
        for (int i=0; i < titles.length; i++) {
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText(titles[i]);
            
            column.pack();
            column.setWidth(100);
        }
        
        int operations = DND.DROP_COPY | DND.DROP_MOVE;
        DropTarget target = new DropTarget(table, operations);
        
        Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
        target.setTransfer(types);
        
        target.addDropListener(new DropTargetAdapter() {
            public void dragEnter(DropTargetEvent event) {
                if (event.operations == DND.DROP_MOVE) {
                    event.detail = DND.DROP_MOVE;
                } else {
                    event.detail = DND.DROP_COPY;
                }
            }

            public void dragOver(DropTargetEvent event) {
                event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
            }

            public void drop(DropTargetEvent event) {
                if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) { 
                    String[] text;
                    
                    if (event.detail == DND.DROP_COPY) {
                        text = delegate.createItem((String) event.data); 
                    } else if (event.detail == DND.DROP_MOVE) {
                        text = getText(getItem((String) event.data));
                    } else {
                        return;
                    }
                        
                    Point click = Display.getCurrent().map(null, table,event.x, event.y);
                    TableItem dest = table.getItem(click);
                    int destIndex;
                    
                    if (dest == null) {
                        destIndex = 0;
                    } else {
                        destIndex = table.indexOf(dest);
                    }
                    
                    int srcIndex = table.getSelectionIndex();
                    if (event.detail == DND.DROP_MOVE) {
                        if (destIndex > srcIndex) {
                            destIndex++;
                        } else if (destIndex < srcIndex) {
                            table.remove(srcIndex);            
                        } else {
                            //if dragging to itself, there is 
                            //no point in more processing
                            return;
                        }
                    }

                    TableItem item = new TableItem(table, SWT.NONE);
                    item.setText(text);
                    
                    TableItem[] items = table.getItems();
                    
                    for (int i=destIndex; i < items.length; i++) { 
                        String[] tmp = getText(items[i]);;
                        
                        items[i].setText(text);
                        text = tmp;
                    }
                    
                    if (event.detail == DND.DROP_COPY) {
                        //edit the newly dropped item
                        //delegate.edit(table.getItem(destIndex));
                    }

                    if (event.detail == DND.DROP_MOVE && destIndex > srcIndex) {
                        table.remove(srcIndex);
                        destIndex--;
                    }
                    
                    table.select(destIndex);
                    table.setSize(table.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                    table.redraw();
                }
            }});
        
        return table;
    }
    
    private TableItem getItem(String id) {
        TableItem[] items = table.getItems();
        
        if (id == null) {
            throw new IllegalArgumentException("Null ID!");
        }
        
        for (int i=0; i < items.length; i++) {
            if (id.equals(items[i].getText(0))) {
                return items[i];
            }
        }
        
        return null;
    }
    
    private String[] getText(TableItem item) {
        String[] text = new String[table.getColumnCount()];
        
        for (int i=0; i < text.length; i++) {
            text[i] = item.getText(i);
        }
        
        return text;
    }
    
    private void setupTableDragging() {
      int operations = DND.DROP_MOVE;
      DragSource src = new DragSource(table, operations);
      
      src.setData(table);
      Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
      src.setTransfer(types);
      
      src.addDragListener(new DragSourceListener() {
  
          public void dragStart(DragSourceEvent event) {
              int selection = table.getSelectionIndex();
              
              event.doit = table.getItemCount() > 0 && selection != -1;
              event.detail = DND.DROP_MOVE;
          }
  
          public void dragSetData(DragSourceEvent event) {
              if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
                  TableItem src = table.getItem(table.getSelectionIndex());

                  event.data = src.getText();
              }
          }
  
          public void dragFinished(DragSourceEvent event) {
          }});  
    }
    
    private void setupTableDoubleClicking() {
        table.addMouseListener(new MouseAdapter() {
            public void mouseDoubleClick(MouseEvent event) {
                TableItem dest = table.getItem(new Point(event.x, event.y));
                
                if (dest != null) {
                    delegate.edit(dest);
                }
            }
        });
    }
    
    private void setupTableDeleteKey() {
        table.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.character == SWT.DEL) {
                    int selection = table.getSelectionIndex();
                    
                    if (selection != -1) {
                        if (table.getItemCount() > 1) {
                           table.select(selection-1);
                        }
                        
                        table.remove(selection);
                    }
                }
            }
        });
    }
    
    public Table getTable() {
        return table;
    }
    
    public String[][] getItemTable() {
        TableItem[] items = table.getItems();
        
        String[][] itemTable = new String[items.length][table.getColumnCount()];
        
        for (int i=0; i < items.length; i++) {
            itemTable[i] = getText(items[i]);
        }
        
        return itemTable;
    }
    
    public Composite getComposite() {
        return panel;
    }
}

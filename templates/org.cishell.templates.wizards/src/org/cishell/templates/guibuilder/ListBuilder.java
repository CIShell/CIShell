/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 16, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.templates.guibuilder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class ListBuilder {
    private BuilderDelegate delegate;
    private Table table;
    private Composite panel;
    
    public ListBuilder(Composite parent, BuilderDelegate delegate) {
        this(parent, SWT.NONE, delegate);
    }
    
    public ListBuilder(Composite parent, int style, BuilderDelegate delegate) {
        this.delegate = delegate;
        
        createGUI(parent, style);
    }

    private void createGUI(Composite parent, int style) {
        panel = new Composite(parent, style);
        
        GridLayout panelLayout = new GridLayout(2, false);
        panel.setLayout(panelLayout);
        
        GridData tableData =
        	new GridData(GridData.FILL, GridData.FILL, true, true);
        tableData.verticalSpan = 10;
        
        table = createTable(panel);
        table.setLayoutData(tableData);
        setupTableDoubleClicking();
        setupTableDeleteKey();
        
        GridData addButtonLayoutData =
        	new GridData(GridData.FILL, GridData.BEGINNING, false, false);
        Button addButton = createAddButton(panel);
        addButton.setLayoutData(addButtonLayoutData);
        
        GridData editButtonLayoutData =
        	new GridData(GridData.FILL, GridData.BEGINNING, false, false);
        Button editButton = createEditButton(panel);
        editButton.setLayoutData(editButtonLayoutData);
        
        GridData removeButtonLayoutData =
        	new GridData(GridData.FILL, GridData.BEGINNING, false, false);
        Button removeButton = createRemoveButton(panel);
        removeButton.setLayoutData(removeButtonLayoutData);
        
        // This is a filler label.
        new Label(panel, SWT.NONE);
        
        GridData moveUpButtonLayoutData =
        	new GridData(GridData.FILL, GridData.BEGINNING, false, false);
        Button moveUpButton = createMoveUpButton(panel);
        moveUpButton.setLayoutData(moveUpButtonLayoutData);
        
        GridData moveDownButtonLayoutData =
        	new GridData(GridData.FILL, GridData.BEGINNING, false, false);
        Button moveDownButton = createMoveDownButton(panel);
        moveDownButton.setLayoutData(moveDownButtonLayoutData);
    }
    
    private Button createAddButton(Composite parent) {
        Button addButton = new Button(parent, SWT.PUSH);
        addButton.setText("Add...");
        
        addButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {
                widgetSelected(selectionEvent);
            }

            public void widgetSelected(SelectionEvent selectionEvent) {
                String[] item = delegate.createItem();
                
                if (item != null) {
                	addItem(item);
                }
            }});
        
        return addButton;
    }
    
    private Button createEditButton(Composite parent) {
        Button editButton = new Button(parent, SWT.PUSH);
        editButton.setText("Edit...");
        
        editButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {
                widgetSelected(selectionEvent);
            }

            public void widgetSelected(SelectionEvent selectionEvent) {
                TableItem[] tableItems = table.getSelection();
                
                if (tableItems.length > 0) {
                    delegate.edit(tableItems[0]);
                }
            }});
        
        return editButton;
    }
    
    private Button createRemoveButton(Composite parent) {
        Button removeButton = new Button(parent, SWT.PUSH);
        removeButton.setText("Remove");
        
        removeButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {
                widgetSelected(selectionEvent);
            }

            public void widgetSelected(SelectionEvent selectionEvent) {
                int index = table.getSelectionIndex();
                
                if (index != -1) {
                    remove(index);
                }
            }});
        
        return removeButton;
    }
    
    private Button createMoveUpButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText("Up");
        button.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

            public void widgetSelected(SelectionEvent e) {
                int index = table.getSelectionIndex();
                if (index > 0) {
                    String[] tmp = getText(table.getItem(index));
                    table.remove(index);
                    
                    index--;
                    TableItem item = new TableItem(table, SWT.NONE, index);
                    item.setText(tmp);
                    
                    table.select(index--);
                }
            }});
        
        return button;
    }

    private Button createMoveDownButton(Composite parent) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText("Down");
        button.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

            public void widgetSelected(SelectionEvent e) {
                int index = table.getSelectionIndex();
                if (index != -1 && index < table.getItemCount()-1) {
                    String[] tmp = getText(table.getItem(index));
                    table.remove(index);
                    
                    index++;
                    TableItem item = new TableItem(table, SWT.NONE, index);
                    item.setText(tmp);
                    
                    table.select(index);
                }
            }});
        
        return button;
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
        
        return table;
    }
    
    private void addItem(String[] item) {
        int index = table.getSelectionIndex();
        TableItem row;
        if (index == -1) {
            row = new TableItem(table, SWT.NONE);
            row.setText(item);
            table.select(table.getItemCount()-1);
        } else {
            row = new TableItem(table, SWT.NONE, index+1);
            row.setText(item);
            table.select(index+1);
        }
    }
    
    private void remove(int row) {
        if (row != -1) {
            table.remove(row);
            if (row == 0) {
                table.select(0);
            } else {
                table.select(row-1);
            }
        }
    }
    
    public Table getTable() {
        return table;
    }
    
    public Composite getPanel() {        
        return panel;
    }
        
    private String[] getText(TableItem item) {
        String[] text = new String[table.getColumnCount()];
        
        for (int i=0; i < text.length; i++) {
            text[i] = item.getText(i);
        }
        
        return text;
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
                            table.select(selection - 1);
                        }

                        table.remove(selection);
                    }
                }
            }
        });
    }
}

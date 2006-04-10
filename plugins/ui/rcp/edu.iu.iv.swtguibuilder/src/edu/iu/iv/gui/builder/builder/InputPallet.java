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
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.iu.iv.common.parameter.InputType;


/**
 * 
 * @author Bruce Herr
 */
public class InputPallet {
    Composite pallet;

    public InputPallet(Composite parent, int style) {
        pallet = new Composite(parent, SWT.NONE);
        
        pallet.setLayout(new RowLayout(SWT.VERTICAL));
        
        Label title = new Label(pallet, SWT.NONE | SWT.BORDER);
        title.setText("  Pallet:  ");
        
        createInputItems(pallet, style);
    }
    
    private void createInputItems(Composite parent, int style) {
        createItem(parent, style, InputType.BOOLEAN);
        createItem(parent, style, InputType.COLOR);
        createItem(parent, style, InputType.DIRECTORY);
        createItem(parent, style, InputType.DOUBLE);
        createItem(parent, style, InputType.FILE);
        createItem(parent, style, InputType.FLOAT);
        createItem(parent, style, InputType.INTEGER);
        createItem(parent, style, InputType.MULTI_CHOICE_LIST);
        createItem(parent, style, InputType.PASSWORD);
        createItem(parent, style, InputType.SINGLE_CHOICE_LIST);
        createItem(parent, style, InputType.TEXT);
    }
    
    private void createItem(Composite parent, int style, final InputType input) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(input.getName());
        
        int operations = DND.DROP_COPY;
        DragSource src = new DragSource(label, operations);
        
        Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
        src.setTransfer(types);
        
        src.addDragListener(new DragSourceAdapter() {
            public void dragStart(DragSourceEvent event) { 
                event.doit = true;
            }

            public void dragSetData(DragSourceEvent event) {
                if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
                    event.data = input.getName();
                }
            }});
    }

    public Composite getComposite() {
        return pallet;
    }
}

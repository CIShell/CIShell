package org.cishell.templates.wizards.pagepanels;

import java.util.Map;

import org.cishell.templates.guibuilder.BuilderDelegate;
import org.cishell.templates.guibuilder.ListBuilder;
import org.cishell.templates.wizards.staticexecutable.InputDataItem;
import org.cishell.templates.wizards.staticexecutable.StaticExecutableInputDataDelegate;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.TableItem;

/*
 * This panel provides the user an interface for managing input data via
 *  org.cishell.templates.guibuilder.ListBuilder, an appropriate delegate
 *  (org.cishell.templates.wizards.staticexecutable.
 *  StaticExecutableInputDataDelegate), and an appropriate editor
 *  (org.cishell.templates.wizards.staticexecutable.InputDataItemEditor).
 * The ListBuilder manages the GUI table and associated buttons.
 * The delegate provides the ListBuilder and editor the appropriate columns
 *  that represent the data items being managed.  It also stores the input data
 *  items.
 * The editor provides an interface for the user to edit the actual data items.
 */
public class AddInputDataPanel extends Composite {
	public static final String INPUT_DATA_LABEL_TEXT = "Input Data";
	
	private ListBuilder listBuilder;
    private StaticExecutableInputDataDelegate delegate;
	
	public AddInputDataPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(createLayoutForThis());
		
		createHeader();
		this.delegate = new StaticExecutableInputDataDelegate(parent);
        this.listBuilder = new ListBuilder(parent, SWT.NONE, delegate);
        
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        this.listBuilder.getPanel().setLayoutData(gridData);
	}
	
	public ListBuilder getListBuilder() {
		return this.listBuilder;
	}
	
	public BuilderDelegate getDelegate() {
		return this.delegate;
	}
	
	public InputDataItem[] getInputDataItems() {
		Display display = Display.getDefault();
        
        if (display != null) {
            GetInputDataAction action = new GetInputDataAction();
            display.syncExec(action);
            
            return action.inputDataItems;
        } else {
            return new InputDataItem[0];
        }
	}
	
	private Layout createLayoutForThis() {
		GridLayout layout = new GridLayout(1, true);
		layout.marginHeight = 0;
		layout.marginBottom = 0;
		layout.marginTop = 0;
		
		return layout;
	}
	
	private void createHeader() {
		Label inputDataLabel = new Label(this, SWT.NONE);
		inputDataLabel.setLayoutData(createInputDataLabelLayoutData());
		inputDataLabel.setText(INPUT_DATA_LABEL_TEXT);
		inputDataLabel.setFont(
			createInputDataLabelFont(inputDataLabel.getDisplay(),
									 inputDataLabel.getFont().getFontData()));
	}
	
	private Object createInputDataLabelLayoutData() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		
		return data;
	}
	
	private Font createInputDataLabelFont(Device device,
										  FontData[] oldFontData) {
		FontData[] newFontData = new FontData[oldFontData.length];
		
		for (int ii = 0; ii < oldFontData.length; ii++) {
			newFontData[ii] = new FontData(
				oldFontData[ii].getName(),
				oldFontData[ii].getHeight() + 2,
				oldFontData[ii].getStyle() | SWT.BOLD);
		}
		
		return new Font(device, newFontData);
	}
	
	private class GetInputDataAction implements Runnable {
        InputDataItem[] inputDataItems;

        public void run() {
            TableItem[] tableItems = listBuilder.getTable().getItems();
            inputDataItems = new InputDataItem[tableItems.length];
            Map idToInputDataItemMap = delegate.getIDToInputDataItemMap();
		
			for (int ii = 0; ii < tableItems.length; ii++) {
				inputDataItems[ii] = (InputDataItem)
					idToInputDataItemMap.get(tableItems[ii].getText(0));
			}
        }
    }
}
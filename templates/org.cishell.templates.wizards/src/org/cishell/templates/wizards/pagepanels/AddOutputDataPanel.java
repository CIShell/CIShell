package org.cishell.templates.wizards.pagepanels;

import java.util.Map;

import org.cishell.templates.guibuilder.BuilderDelegate;
import org.cishell.templates.guibuilder.ListBuilder;
import org.cishell.templates.wizards.staticexecutable.OutputDataItem;
import org.cishell.templates.wizards.staticexecutable.StaticExecutableOutputDataDelegate;
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
 * This panel provides the user an interface for managing output data via
 *  org.cishell.templates.guibuilder.ListBuilder, an appropriate delegate
 *  (org.cishell.templates.wizards.staticexecutable.
 *  StaticExecutableOutputDataDelegate), and an appropriate editor
 *  (org.cishell.templates.wizards.staticexecutable.OutputDataItemEditor).
 * The ListBuilder manages the GUI table and associated buttons.
 * The delegate provides the ListBuilder and editor the appropriate columns
 *  that represent the data items being managed.  It also stores the output
 *  data items.
 * The editor provides an interface for the user to edit the actual data items.
 */
public class AddOutputDataPanel extends Composite {
	public static final String OUTPUT_DATA_LABEL_TEXT = "Output Data";
	
	private ListBuilder listBuilder;
    private StaticExecutableOutputDataDelegate delegate;
	
	public AddOutputDataPanel(Composite parent, int style) {
		super(parent, style);
		
		setLayout(createLayoutForThis());
		
		createHeader();
		delegate = new StaticExecutableOutputDataDelegate(parent);
        listBuilder = new ListBuilder(parent, SWT.NONE, delegate);
        
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        this.listBuilder.getPanel().setLayoutData(gridData);
	}
	
	public ListBuilder getListBuilder() {
		return this.listBuilder;
	}
	
	public BuilderDelegate getDelegate() {
		return this.delegate;
	}
	
	public OutputDataItem[] getOutputDataItems() {
		Display display = Display.getDefault();
        
        if (display != null) {
            GetOutputDataAction action = new GetOutputDataAction();
            display.syncExec(action);
            
            return action.outputDataItems;
        } else {
            return new OutputDataItem[0];
        }
	}
	
	private Layout createLayoutForThis() {
		GridLayout layout = new GridLayout(6, true);
		
		return layout;
	}
	
	private void createHeader() {
		Label outputDataLabel = new Label(this, SWT.NONE);
		outputDataLabel.setLayoutData(createOutputDataLabelLayoutData());
		outputDataLabel.setText(OUTPUT_DATA_LABEL_TEXT);
		outputDataLabel.setFont(createOutputDataLabelFont(
			outputDataLabel.getDisplay(),
			outputDataLabel.getFont().getFontData()));
	}
	
	private Object createOutputDataLabelLayoutData() {
		GridData data = new GridData();
		data.horizontalAlignment = SWT.CENTER;
		data.horizontalSpan = 6;
		
		return data;
	}
	
	private Font createOutputDataLabelFont(Device device,
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
	
	private class GetOutputDataAction implements Runnable {
        OutputDataItem[] outputDataItems;

        public void run() {
            TableItem[] tableItems = listBuilder.getTable().getItems();
            outputDataItems = new OutputDataItem[tableItems.length];
            Map idToOutputDataItemMap = delegate.getIDToOutputDataItemMap();
		
			for (int ii = 0; ii < tableItems.length; ii++) {
				outputDataItems[ii] = (OutputDataItem)
					idToOutputDataItemMap.get(tableItems[ii].getText(0));
			}
        }
    }
}
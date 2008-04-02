package net.sf.commonclipse.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;


/**
 * @author fgiust
 * @version $Revision $ ($Author $)
 */
public abstract class TabbedFieldEditorPreferencePage extends FieldEditorPreferencePage
{

    /**
     * Tab folder.
     */
    private TabFolder folder;

    /**
     * Maximum number of columns for field editors.
     */
    private int maxNumOfColumns;

    /**
     * Creates a new field editor preference page with the given style, an empty title, and no image.
     * @param style either <code>GRID</code> or <code>FLAT</code>
     */
    protected TabbedFieldEditorPreferencePage(int style)
    {
        super(style);
    }

    /**
     * Creates a new field editor preference page with the given title and style, but no image.
     * @param title the title of this preference page
     * @param style either <code>GRID</code> or <code>FLAT</code>
     */
    protected TabbedFieldEditorPreferencePage(String title, int style)
    {
        super(title, style);
    }

    /**
     * Creates a new field editor preference page with the given title, image, and style.
     * @param title the title of this preference page
     * @param image the image for this preference page, or <code>null</code> if none
     * @param style either <code>GRID</code> or <code>FLAT</code>
     */
    protected TabbedFieldEditorPreferencePage(String title, ImageDescriptor image, int style)
    {
        super(title, image, style);
    }

    /**
     * Adds the given field editor to this page.
     * @param editor the field editor
     */
    protected void addField(FieldEditor editor)
    {
        // needed for layout, since there is no way to get fields editor from parent
        this.maxNumOfColumns = Math.max(this.maxNumOfColumns, editor.getNumberOfControls());
        super.addField(editor);
    }

    /**
     * Adjust the layout of the field editors so that they are properly aligned.
     */
    protected void adjustGridLayout()
    {
        if (folder != null)
        {
            TabItem[] items = folder.getItems();
            for (int j = 0; j < items.length; j++)
            {
                GridLayout layout = ((GridLayout) ((Composite) items[j].getControl()).getLayout());
                layout.numColumns = this.maxNumOfColumns;
                layout.marginHeight = 5;
                layout.marginWidth = 5;
            }
        }

        // need to call super.adjustGridLayout() since fieldEditor.adjustForNumColumns() is protected
        super.adjustGridLayout();

        // reset the main container to a single column
        ((GridLayout) super.getFieldEditorParent().getLayout()).numColumns = 1;
    }

    /**
     * Returns a parent composite for a field editor.
     * <p>
     * This value must not be cached since a new parent may be created each time this method called. Thus this method
     * must be called each time a field editor is constructed.
     * </p>
     * @return a parent
     */
    protected Composite getFieldEditorParent()
    {
        if (folder == null || folder.getItemCount() == 0)
        {
            return super.getFieldEditorParent();
        }
        return (Composite) folder.getItem(folder.getItemCount() - 1).getControl();
    }

    /**
     * Adds a tab to the page.
     * @param text the tab label
     */
    public void addTab(String text)
    {
        if (folder == null)
        {
            // initialize tab folder
            folder = new TabFolder(super.getFieldEditorParent(), SWT.NONE);
            folder.setLayoutData(new GridData(GridData.FILL_BOTH));
        }

        TabItem item = new TabItem(folder, SWT.NONE);
        item.setText(text);

        Composite currentTab = new Composite(folder, SWT.NULL);
        GridLayout layout = new GridLayout();
        currentTab.setLayout(layout);
        currentTab.setFont(super.getFieldEditorParent().getFont());
        currentTab.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        item.setControl(currentTab);
    }

}
/* ====================================================================
 *   Copyright 2003-2004 Fabrizio Giustina.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * ====================================================================
 */
package net.sf.commonclipse.preferences;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;


/**
 * Implementation identical to StringFieldEditor but using a combo instead of a Text field.
 * @author fgiust
 * @version $Revision: 1.4 $ ($Author: fgiust $)
 */
public class ComboFieldEditor extends FieldEditor
{

    /**
     * Text limit constant (value <code>-1</code>) indicating unlimited text limit and width.
     */
    public static final int UNLIMITED = -1;

    /**
     * The text field, or <code>null</code> if none.
     */
    Combo textField;

    /**
     * predefined values to be shown in list.
     */
    private String[] predefinedValues;

    /**
     * Cached valid state.
     */
    private boolean isValid;

    /**
     * Old text value.
     */
    private String oldValue;

    /**
     * Width of text field in characters; initially unlimited.
     */
    private int widthInChars = UNLIMITED;

    /**
     * Text limit of text field in characters; initially unlimited.
     */
    private int textLimit = UNLIMITED;

    /**
     * The error message, or <code>null</code> if none.
     */
    private String errorMessage;

    /**
     * Indicates whether the empty string is legal; <code>true</code> by default.
     */
    private boolean emptyStringAllowed = true;

    /**
     * Creates a new string field editor.
     */
    protected ComboFieldEditor()
    {
    }

    /**
     * Creates a string field editor. Use the method <code>setTextLimit</code> to limit the text.
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param width the width of the text input field in characters, or <code>UNLIMITED</code> for no limit
     * @param parent the parent of the field editor's control
     */
    public ComboFieldEditor(String name, String labelText, int width, Composite parent)
    {
        init(name, labelText);
        this.widthInChars = width;
        this.isValid = false;
        this.errorMessage = JFaceResources.getString("StringFieldEditor.errorMessage"); //$NON-NLS-1$
        createControl(parent);
    }

    /**
     * Creates a string field editor of unlimited width. Use the method <code>setTextLimit</code> to limit the text.
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    public ComboFieldEditor(String name, String labelText, Composite parent)
    {
        this(name, labelText, UNLIMITED, parent);
    }

    /**
     * Checks whether the text input field contains a valid value or not.
     * @return <code>true</code> if the field value is valid, and <code>false</code> if invalid
     */
    protected boolean checkState()
    {
        boolean result = false;
        if (this.emptyStringAllowed)
        {
            result = true;
        }

        if (this.textField == null)
        {
            result = false;
        }

        String txt = this.textField.getText();

        if (txt == null)
        {
            result = false;
        }

        result = (txt.trim().length() > 0) || this.emptyStringAllowed;

        // call hook for subclasses
        result = result && doCheckState();

        if (result)
        {
            clearErrorMessage();
        }
        else
        {
            showErrorMessage(this.errorMessage);
        }

        return result;
    }

    /**
     * @see org.eclipse.jface.preference.FieldEditor#doLoad()
     */
    protected void doLoad()
    {
        if (this.textField != null)
        {

            addDefaultOptions();
            String value = getPreferenceStore().getString(getPreferenceName());
            this.textField.setText(value);
            this.oldValue = value;

        }
    }

    /**
     * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
     */
    protected void doLoadDefault()
    {
        if (this.textField != null)
        {
            addDefaultOptions();
            String value = getPreferenceStore().getDefaultString(getPreferenceName());
            this.textField.setText(value);

        }
        valueChanged();
    }

    /**
     * @see org.eclipse.jface.preference.FieldEditor#doStore()
     */
    protected void doStore()
    {
        getPreferenceStore().setValue(getPreferenceName(), this.textField.getText());
    }

    /**
     * Returns the error message that will be displayed when and if an error occurs.
     * @return the error message, or <code>null</code> if none
     */
    public String getErrorMessage()
    {
        return this.errorMessage;
    }

    /**
     * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
     */
    public int getNumberOfControls()
    {
        return 2;
    }

    /**
     * Returns the field editor's value.
     * @return the current value
     */
    public String getStringValue()
    {
        if (this.textField != null)
        {
            return this.textField.getText();
        }
        return getPreferenceStore().getString(getPreferenceName());
    }

    /**
     * Returns this field editor's text control.
     * @return the text control, or <code>null</code> if no text field is created yet
     */
    protected Combo getTextControl()
    {
        return this.textField;
    }

    /**
     * Returns this field editor's text control.
     * <p>
     * The control is created if it does not yet exist
     * </p>
     * @param parent the parent
     * @return the text control
     */
    public Combo getTextControl(Composite parent)
    {
        if (this.textField == null)
        {
            this.textField = new Combo(parent, SWT.SINGLE | SWT.BORDER);

            this.textField.setFont(parent.getFont());

            this.textField.addKeyListener(new KeyAdapter()
            {

                public void keyReleased(KeyEvent e)
                {
                    valueChanged();
                }
            });
            this.textField.addSelectionListener(new SelectionListener()
            {

                public void widgetSelected(SelectionEvent e)
                {
                    valueChanged();
                }

                public void widgetDefaultSelected(SelectionEvent e)
                {
                    valueChanged();
                }

            });
            this.textField.addFocusListener(new FocusAdapter()
            {

                public void focusGained(FocusEvent e)
                {
                    refreshValidState();
                }

                public void focusLost(FocusEvent e)
                {
                    valueChanged();
                    clearErrorMessage();
                }
            });

            this.textField.addDisposeListener(new DisposeListener()
            {

                public void widgetDisposed(DisposeEvent event)
                {
                    ComboFieldEditor.this.textField = null;
                }
            });
            if (this.textLimit > 0)
            { // Only set limits above 0 - see SWT spec
                this.textField.setTextLimit(this.textLimit);
            }
        }
        else
        {
            checkParent(this.textField, parent);
        }
        return this.textField;
    }

    /**
     * Returns whether an empty string is a valid value.
     * @return <code>true</code> if an empty string is a valid value, and <code>false</code> if an empty string is
     * invalid
     * @see #setEmptyStringAllowed
     */
    public boolean isEmptyStringAllowed()
    {
        return this.emptyStringAllowed;
    }

    /**
     * @see org.eclipse.jface.preference.FieldEditor # isValid()
     */
    public boolean isValid()
    {
        return this.isValid;
    }

    /**
     * @see org.eclipse.jface.preference.FieldEditor#refreshValidState()
     */
    protected void refreshValidState()
    {
        this.isValid = checkState();
    }

    /**
     * Sets whether the empty string is a valid value or not.
     * @param b <code>true</code> if the empty string is allowed, and <code>false</code> if it is considered invalid
     */
    public void setEmptyStringAllowed(boolean b)
    {
        this.emptyStringAllowed = b;
    }

    /**
     * Sets the error message that will be displayed when and if an error occurs.
     * @param message the error message
     */
    public void setErrorMessage(String message)
    {
        this.errorMessage = message;
    }

    /**
     * @see org.eclipse.jface.preference.FieldEditor#setFocus()
     */
    public void setFocus()
    {
        if (this.textField != null)
        {
            this.textField.setFocus();
        }
    }

    /**
     * Sets this field editor's value.
     * @param value the new value, or <code>null</code> meaning the empty string
     */
    public void setStringValue(String value)
    {
        if (this.textField != null)
        {
            String newValue = value;
            if (newValue == null)
            {
                newValue = ""; //$NON-NLS-1$
            }

            this.oldValue = this.textField.getText();

            if (!this.oldValue.equals(newValue))
            {
                this.textField.setText(newValue);

                valueChanged();
            }
        }
    }

    /**
     * Sets this text field's text limit.
     * @param limit the limit on the number of character in the text input field, or <code>UNLIMITED</code> for no
     * limit
     */
    public void setTextLimit(int limit)
    {
        this.textLimit = limit;
        if (this.textField != null)
        {
            this.textField.setTextLimit(limit);
        }
    }

    /**
     * Shows the error message set via <code>setErrorMessage</code>.
     */
    public void showErrorMessage()
    {
        showErrorMessage(this.errorMessage);
    }

    /**
     * Informs this field editor's listener, if it has one, about a change to the value (<code>VALUE</code> property)
     * provided that the old and new values are different.
     * <p>
     * This hook is <em>not</em> called when the text is initialized (or reset to the default value) from the
     * preference store.
     * </p>
     */
    protected void valueChanged()
    {
        setPresentsDefaultValue(false);
        boolean oldState = this.isValid;
        refreshValidState();

        if (this.isValid != oldState)
        {
            fireStateChanged(IS_VALID, oldState, this.isValid);
        }

        String newValue = this.textField.getText();
        if (!newValue.equals(this.oldValue))
        {
            fireValueChanged(VALUE, this.oldValue, newValue);
            this.oldValue = newValue;
        }
    }

    /**
     * @see org.eclipse.jface.preference.FieldEditor#setEnabled(boolean,Composite).
     */
    public void setEnabled(boolean enabled, Composite parent)
    {
        super.setEnabled(enabled, parent);
        getTextControl(parent).setEnabled(enabled);
    }

    /**
     * Hook for subclasses to do specific state checks.
     * <p>
     * The default implementation of this framework method does nothing and returns <code>true</code>. Subclasses
     * should override this method to specific state checks.
     * </p>
     * @return <code>true</code> if the field value is valid, and <code>false</code> if invalid
     */
    protected boolean doCheckState()
    {
        return true;
    }

    /**
     * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
     */
    protected void adjustForNumColumns(int numColumns)
    {
        GridData gd = (GridData) this.textField.getLayoutData();
        gd.horizontalSpan = numColumns - 1;
        // We only grab excess space if we have to
        // If another field editor has more columns then
        // we assume it is setting the width.
        gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
    }

    /**
     * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(Composite, int)
     */
    protected void doFillIntoGrid(Composite parent, int numColumns)
    {
        getLabelControl(parent);

        this.textField = getTextControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns - 1;
        if (this.widthInChars != UNLIMITED)
        {
            GC gc = new GC(this.textField);
            try
            {
                Point extent = gc.textExtent("X"); //$NON-NLS-1$
                gd.widthHint = this.widthInChars * extent.x;
            }
            finally
            {
                gc.dispose();
            }
        }
        else
        {
            gd.horizontalAlignment = GridData.FILL;
            gd.grabExcessHorizontalSpace = true;
        }
        this.textField.setLayoutData(gd);
    }

    /**
     * Sets a list of predefined values that must be shown in the combo.
     * @param strings array of Strings added to the combo
     */
    public void setPredefinedValues(String[] strings)
    {
        this.predefinedValues = strings;
    }

    /**
     * Adds predefined options to the combo.
     */
    private void addDefaultOptions()
    {
        if (this.textField != null && this.predefinedValues != null)
        {
            this.textField.setItems(this.predefinedValues);
        }
    }

    /**
     * @see org.eclipse.jface.preference.FieldEditor#clearErrorMessage()
     */
    protected void clearErrorMessage()
    {
        super.clearErrorMessage();
    }

}

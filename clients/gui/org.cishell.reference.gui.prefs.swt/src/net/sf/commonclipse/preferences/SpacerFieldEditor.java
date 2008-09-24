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

import org.eclipse.swt.widgets.Composite;

/**
 * A field editor for adding space to a preference page.
 * @author fgiust
 * @version $Revision: 1.3 $ ($Author: fgiust $)
 */
public class SpacerFieldEditor extends LabelFieldEditor
{
    /**
     * Implemented as an empty label field editor.
     * @param parent Composite
     */
    public SpacerFieldEditor(Composite parent)
    {
        super("", parent); //$NON-NLS-1$
    }
}

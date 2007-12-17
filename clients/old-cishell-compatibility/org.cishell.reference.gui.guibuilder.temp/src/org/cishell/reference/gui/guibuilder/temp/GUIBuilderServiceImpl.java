/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 23, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.gui.guibuilder.temp;

import java.util.Dictionary;

import org.cishell.service.guibuilder.GUI;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.osgi.service.metatype.MetaTypeProvider;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class GUIBuilderServiceImpl implements GUIBuilderService {
    /**
     * @see org.cishell.service.guibuilder.GUIBuilderService#createGUI(java.lang.String, org.osgi.service.metatype.MetaTypeProvider)
     */
    public GUI createGUI(String id, MetaTypeProvider parameters) {
        return new GUIImpl(id, parameters);
    }

    /**
     * @see org.cishell.service.guibuilder.GUIBuilderService#createGUIandWait(java.lang.String, org.osgi.service.metatype.MetaTypeProvider)
     */
    public Dictionary createGUIandWait(String id, MetaTypeProvider parameters) {
        return createGUI(id, parameters).openAndWait();
    }

    /**
     * @see org.cishell.service.guibuilder.GUIBuilderService#showConfirm(java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean showConfirm(String title, String message, String detail) {
        return false;
    }

    /**
     * @see org.cishell.service.guibuilder.GUIBuilderService#showError(java.lang.String, java.lang.String, java.lang.String)
     */
    public void showError(String title, String message, String detail) {

    }

    /**
     * @see org.cishell.service.guibuilder.GUIBuilderService#showError(java.lang.String, java.lang.String, java.lang.Throwable)
     */
    public void showError(String title, String message, Throwable error) {

    }

    /**
     * @see org.cishell.service.guibuilder.GUIBuilderService#showInformation(java.lang.String, java.lang.String, java.lang.String)
     */
    public void showInformation(String title, String message, String detail) {

    }

    /**
     * @see org.cishell.service.guibuilder.GUIBuilderService#showQuestion(java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean showQuestion(String title, String message, String detail) {
        return false;
    }

    /**
     * @see org.cishell.service.guibuilder.GUIBuilderService#showWarning(java.lang.String, java.lang.String, java.lang.String)
     */
    public void showWarning(String title, String message, String detail) {
        
    }
}

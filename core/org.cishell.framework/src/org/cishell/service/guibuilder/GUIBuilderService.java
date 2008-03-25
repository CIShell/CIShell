/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 14, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - Initial API
 * ***************************************************************************/
package org.cishell.service.guibuilder;

import java.util.Dictionary;

import org.osgi.service.log.LogService;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * A service for creating simple GUIs for user interaction. This service 
 * provides several methods for popping up dialog boxes to get or give very 
 * simple information and a more flexible way to create {@link GUI}s using a 
 * standard OSGi {@link MetaTypeProvider}. The MetaTypeProvider basically lists 
 * what input is needed (String, Integer, Float, etc...), a description of the 
 * input, and a way to validate input. 
 * 
 * See the 
 * <a href="http://cishell.org/dev/docs/spec/cishell-spec-1.0.pdf">
 * CIShell Specification 1.0</a> for documentation on creating GUIs with this
 * service. 
 * 
 * Algorithm writers are encouraged to use this service if they need to get
 * additional input from the user rather than creating their own GUI. This is
 * to ensure a consistent user input method and so that the GUI can easily be 
 * routed to the user when running remotely.
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface GUIBuilderService {
	
	/**
	 * Creates a GUI for user interaction
	 * 
	 * @param id         The id to use to get the correct 
	 *                   {@link ObjectClassDefinition} from the provided 
	 *                   MetaTypeProvider
	 * @param parameters Provides the parameters needed to get information from 
	 *                   the user
	 * @return The created GUI
	 */
    public GUI createGUI(String id, MetaTypeProvider parameters);
    
    /**
     * Creates a GUI, gets data from the user, and returns what they entered.
     * This is a convenience method that first creates a GUI from the provided
     * {@link MetaTypeProvider}, then pops the GUI up to the user, who then 
     * enters in the needed information, which is then taken and put into a 
     * {@link Dictionary}, and is given to this method's caller.
     * 
	 * @param id         The id to use to get the correct 
	 *                   {@link ObjectClassDefinition} from the provided 
	 *                   MetaTypeProvider
	 * @param parameters Provides the parameters needed to get information from 
	 *                   the user
     * @return The data the user entered or <code>null</code> if the operation 
     * 		   was cancelled
     */
    public Dictionary createGUIandWait(String id, MetaTypeProvider parameters);
    
    /**
     * Pops up a confirmation box to the user with an 'Ok' and 'Cancel' button
     * 
     * @param title   The title of the pop-up
     * @param message The message to display
     * @param detail  Additional details
     * @return If they clicked "Ok," <code>true</code>, otherwise 
     *         <code>false</code>
     */
    public boolean showConfirm(String title, String message, String detail);
    
    /**
     * Pops up a question box to the user with a 'Yes' and 'No' button
     * 
     * @param title   The title of the pop-up
     * @param message The question to display
     * @param detail  Additional details
     * @return If they clicked "Yes," <code>true</code>, otherwise 
     *         <code>false</code>
     */
    public boolean showQuestion(String title, String message, String detail);
    
    /**
     * Pops up an information box to the user. This should only be used 
     * sparingly. Algorithms should try to use the {@link LogService} instead.
     * 
     * @param title   The title of the pop-up
     * @param message The message to display
     * @param detail  Additional details
     */
    public void showInformation(String title, String message, String detail);
    
    /**
     * Pops up a warning box to the user. This should only be used 
     * sparingly. Algorithms should try to use the {@link LogService} instead.
     * 
     * @param title   The title of the pop-up
     * @param message The message to display
     * @param detail  Additional details
     */
    public void showWarning(String title, String message, String detail);
    
    /**
     * Pops up an error box to the user. This should only be used 
     * sparingly. Algorithms should try to use the {@link LogService} instead.
     * 
     * @param title   The title of the pop-up
     * @param message The message to display
     * @param detail  Additional details
     */
    public void showError(String title, String message, String detail);
    
    /**
     * Pops up an error box to the user. This should only be used 
     * sparingly. Algorithms should try to use the {@link LogService} instead.
     * 
     * @param title   The title of the pop-up
     * @param message The message to display
     * @param error   The actual exception that was thrown
     */
    public void showError(String title, String message, Throwable error);
}

/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 21, 2005 at Indiana University.
 */
package edu.iu.iv.core.messaging;

/**
 * Defines the operations that are required of an IVC MessageHandler.
 * These methods can be called by IVC at the request of Plugins, and
 * different MessageHandlers can respond to them in different ways. For
 * example a GUI MessageHandler may use dialog boxes, whereas a 
 * totally non-GUI handler may simply write messages to the console.
 *
 * @author Team IVC
 */
public interface MessageHandler {
    
    /**
     * Show the given error Message.
     * 
     * @param message the Message for the error that has occured.
     */
	public void showError(Message message);

    /**
     * Show the given warning Message.
     * 
     * @param message the Message for the warning that has occured.
     */
	public void showWarning(Message message);
		
    /**
     * Show the given informational Message.
     * 
     * @param message the Message giving the desired information.
     */
	public void showInformation(Message message);

    /**
     * Show the given yes/no question Message.
     * 
     * @param message the Message containing the question to ask the user.
     * 
     * @return true if the user chose yes, false if not
     */
	public boolean showQuestion(Message message);
	
    /**
     * Show the given confirmation Message.
     * 
     * @param message the Message containing the confirmation question to
     * ask the user.
     * 
     * @return true if the user said OK to the confirmation question, false
     * if not.
     */
	public boolean showConfirm(Message message);
	
}

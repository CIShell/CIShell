/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 21, 2005 at Indiana University.
 */
package edu.iu.iv.messaging;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.iu.iv.core.messaging.Message;
import edu.iu.iv.core.messaging.MessageHandler;
import edu.iu.iv.core.messaging.MessageProperty;
import edu.iu.iv.gui.IVCApplication;
import edu.iu.iv.gui.IVCDialog;

/**
 *
 * @author Team IVC
 */
public class GUIMessageHandler implements MessageHandler {
    
    private boolean result = false;
    private Shell shell = IVCApplication.getShell();
    

	public void showError(Message message){
	    final String title = getTitle(message);
	    final String text = getText(message);
	    final String details = getDetails(message);
	    
	    Display.getDefault().syncExec(new Runnable(){
	        public void run(){	            
	            IVCDialog.openError(shell, title, text, details);
	        }
	    });
	}

	public void showWarning(Message message){
	    final String title = getTitle(message);
	    final String text = getText(message);
	    final String details = getDetails(message);
	    
	    Display.getDefault().syncExec(new Runnable(){
	        public void run(){
	            IVCDialog.openWarning(shell, title, text, details);
	        }
	    });	   
	}
	
	public void showInformation(Message message){
	    final String title = getTitle(message);
	    final String text = getText(message);
	    final String details = getDetails(message);
	    
	    Display.getDefault().syncExec(new Runnable(){
	        public void run(){
	            IVCDialog.openInformation(shell, title, text, details);
	        }
	    });	    
	}

	public boolean showQuestion(Message message){	  
	    final String title = getTitle(message);
	    final String text = getText(message);
	    final String details = getDetails(message);
	    
	    Display.getDefault().syncExec(new Runnable(){
	        public void run(){
	            result = IVCDialog.openQuestion(shell, title, text, details);
	        }
	    });	 	    
	    
	    return result;
	}
	
	public boolean showConfirm(Message message){
	    final String title = getTitle(message);
	    final String text = getText(message);
	    final String details = getDetails(message);
	    
	    Display.getDefault().syncExec(new Runnable(){
	        public void run(){
	            result = IVCDialog.openConfirm(shell, title, text, details);
	        }
	    });	 	    
	    
	    return result;
	}	
	
    private String getTitle(Message message){        
        String title = (String)message.getProperties().getPropertyValue(MessageProperty.TITLE);
        if (title == null) title = "";
        return title;
    }
  
    private String getText(Message message){        
        String text = (String)message.getProperties().getPropertyValue(MessageProperty.MESSAGE);
        if (text == null) text = "";
        return text;
    }
    
    private String getDetails(Message message){        
        String details = (String)message.getProperties().getPropertyValue(MessageProperty.DETAILS);
        if (details == null) details = "";
        return details;
    }
}

package org.cishell.reference.gui.persistence.viewwith;

import java.io.File;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.data.Data;
import org.cishell.reference.gui.persistence.save.SaveDataChooser;
import org.cishell.service.conversion.ConversionException;
import org.cishell.service.conversion.Converter;
import org.eclipse.swt.widgets.Shell;

/* 
 * @author Felix Terkhorn (terkhorn@gmail.com) 
 * 
 */
public class ViewWithDataChooser extends SaveDataChooser {
	private File tempFile;
	private boolean isSaved = false;
	private Data theData;
	
	public ViewWithDataChooser(String title, File tempFile, Shell parent, 
					Data data, Converter[] converters, CIShellContext context){
		super (data, parent, converters, title, context);
		
		this.tempFile = tempFile;
		this.theData = data;
	}

	protected void selectionMade(int selectedIndex){
		try {
			getShell().setVisible(false);
	        final Converter converter = converterArray[selectedIndex];
	        Data newData = converter.convert(theData);
	        //TODO: hey look, yet another copy method
	        isSaved = FileViewWith.copy((File)newData.getData(), tempFile);
	      	close(true);
		} catch (ConversionException e) {
			//TODO: RuntimeExceptioN?!?!?!
			throw new RuntimeException("Error: Unable to view data:\n    " + e.getMessage(), e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean isSaved(){
		return isSaved;
	}
	

}

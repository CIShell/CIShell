package org.cishell.reference.gui.persistence.viewwith;

import java.io.File;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.data.Data;
import org.cishell.reference.gui.persistence.save.SaveDataChooser;
import org.cishell.service.conversion.Converter;

import org.eclipse.swt.widgets.Shell;

/* 
 * @author Felix Terkhorn (terkhorn@gmail.com) 
 * 
 */
public class ViewWithDataChooser extends SaveDataChooser {
	private File tempFile;
	boolean isSaved = false;
	Data theData;
	
	public ViewWithDataChooser(String title, File tempFile, Shell parent, 
					Data data, Converter[] converters, CIShellContext context){
		super (data, parent, converters, title, context);
		
		this.tempFile = tempFile;
		this.theData = data;
	}

	protected void selectionMade(int selectedIndex) {
        getShell().setVisible(false);
        final Converter converter = converterArray[selectedIndex];
        Data newData = converter.convert(theData);    
        isSaved = FileViewWith.copy((File)newData.getData(), tempFile);
      	close(true);
	}
	
	public boolean isSaved(){
		return isSaved;
	}
	

}

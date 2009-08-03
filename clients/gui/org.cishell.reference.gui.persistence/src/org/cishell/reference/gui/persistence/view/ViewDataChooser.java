package org.cishell.reference.gui.persistence.view;

import java.io.File;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.reference.gui.persistence.FileUtil;
import org.cishell.reference.gui.persistence.save.SaveDataChooser;
import org.cishell.service.conversion.ConversionException;
import org.cishell.service.conversion.Converter;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.log.LogService;

/* 
 * @author Weixia(Bonnie) Huang (huangb@indiana.edu) 
 * 
 */
public class ViewDataChooser extends SaveDataChooser {
	boolean isSaved = false;
	LogService logger;
	Data theData;
	File outputFile;
	
	public ViewDataChooser(String title, Shell parent, 
					Data data, Converter[] converters, CIShellContext context, LogService logger){
		super (data, parent, converters, title, context);
		
		this.theData = data;
		this.logger = logger;
	}

	protected void selectionMade(int selectedIndex) {
        getShell().setVisible(false);
        final Converter converter = converterArray[selectedIndex];
        
        try {
        	Data newData = converter.convert(theData);
        	String label = (String) newData.getMetadata().get(DataProperty.LABEL);
	        String fileName = FileUtil.extractFileName(label);
	        String extension = FileUtil.extractExtension(newData.getFormat());
	        File tempFile = FileUtil.getTempFile(fileName, extension, logger);
	        
	        try {
				isSaved = FileView.copy((File)newData.getData(), tempFile);
			} catch (AlgorithmExecutionException e) {
				logger.log(LogService.LOG_ERROR, "Error copying view for view:\n    " + e.getMessage(), e);
				return;
			}
			
			outputFile = tempFile;
	      	close(true);			
        }  catch (ConversionException e) {
			logger.log(LogService.LOG_ERROR, "Error: Unable to view data:\n    " + e.getMessage(), e);
			return;
		}
	}
	
	public boolean isSaved(){
		return isSaved;
	}
	

}

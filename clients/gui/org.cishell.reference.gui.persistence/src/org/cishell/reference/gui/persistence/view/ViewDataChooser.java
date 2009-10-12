package org.cishell.reference.gui.persistence.view;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.data.Data;
import org.cishell.reference.gui.persistence.save.SaveDataChooser;
import org.cishell.service.conversion.Converter;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.log.LogService;

// TODO: Refactor this class.
public class ViewDataChooser extends SaveDataChooser {
	/*private boolean isSaved = false;
	private LogService logger;
	private Data theData;
	private File outputFile;*/
	private Converter selectedConverter = null;
	
	public ViewDataChooser(String title,
						   Shell parent, 
						   Data data,
						   Converter[] converters,
						   CIShellContext ciShellContext,
						   LogService logger){
		super (data, parent, converters, title, ciShellContext);
		
		/*this.theData = data;
		this.logger = logger;*/
	}
	//TODO: replace this stuff with convertToFile -- specifically, just ahve this return the darn selected format.
	protected void selectionMade(int selectedIndex) {
        getShell().setVisible(false);
        this.selectedConverter = converterArray[selectedIndex];
        close(true);
        /*final Converter selectedConverter = converterArray[selectedIndex];
        
        try {
        	Data newData = selectedConverter.convert(theData);
        	String label = (String) newData.getMetadata().get(DataProperty.LABEL);
	        String fileName = FileUtil.extractFileName(label);
	        String extension = FileUtil.extractExtension(newData.getFormat());
	        File tempFile = FileUtil.getTempFile(fileName, extension, logger);
	        
	        try {
				FileUtilities.copyFile((File)newData.getData(), tempFile);
				isSaved = true;
			} catch (FileCopyingException fileCopyingException) {
				logger.log(LogService.LOG_ERROR, "Error copying view for view:\n    " + fileCopyingException.getMessage(), fileCopyingException);
				return;
			}
			
			outputFile = tempFile;
	      	close(true);			
        }  catch (ConversionException e) {
			logger.log(LogService.LOG_ERROR, "Error: Unable to view data:\n    " + e.getMessage(), e);
			return;
		}*/
	}
	
	public Converter getSelectedConverter() {
		return this.selectedConverter;
	}
	
	/*public boolean isSaved(){
		return this.isSaved;
	}
	
	public File getOutputFile() {
		return this.outputFile;
	}*/
}

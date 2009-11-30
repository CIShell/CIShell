package org.cishell.reference.gui.persistence.view.core;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.data.Data;
import org.cishell.reference.gui.persistence.save.SaveDataChooser;
import org.cishell.service.conversion.Converter;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.log.LogService;

public class ViewDataChooser extends SaveDataChooser {
	private Converter selectedConverter = null;
	
	public ViewDataChooser(String title,
						   Shell parent, 
						   Data data,
						   Converter[] converters,
						   CIShellContext ciShellContext,
						   LogService logger){
		super(data, parent, converters, title, ciShellContext);
	}

	protected void selectionMade(int selectedIndex) {
        getShell().setVisible(false);
        this.selectedConverter = converters[selectedIndex];
        close(true);
	}
	
	public Converter getSelectedConverter() {
		return this.selectedConverter;
	}
}

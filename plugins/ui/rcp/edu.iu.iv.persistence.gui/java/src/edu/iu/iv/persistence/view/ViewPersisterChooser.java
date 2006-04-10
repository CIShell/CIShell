package edu.iu.iv.persistence.view;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.widgets.Shell;

import edu.iu.iv.core.datamodels.DataModel;
import edu.iu.iv.core.persistence.BasicFileResourceDescriptor;
import edu.iu.iv.core.persistence.PersistenceException;
import edu.iu.iv.core.persistence.Persister;
import edu.iu.iv.persistence.save.SavePersisterChooser;

public class ViewPersisterChooser extends SavePersisterChooser {
	private File tempFile;
	
	public ViewPersisterChooser(String title, DataModel model, Shell parent,
			Persister[] persisters, File tempFile) {
		super (title, model, parent, persisters);
		
        setDescription("The model you have selected can be viewed" +
                " using the following formats.\n" + "Please select one of them.");
        setDetails("This dialog allows the user to choose among all available " +
        		"formats for viewing the selected data model.  Choose any of the formats " +
        		"to continue viewing the data model.");
		
		this.tempFile = tempFile;
	}

	protected void selectionMade(int selectedIndex) {
		getShell().setVisible(false);
		final Persister persister = persisterArray[selectedIndex];
		try {
			persister.persist(this.model.getData(), new BasicFileResourceDescriptor(this.tempFile));
		} catch (PersistenceException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		close(true);
	}
}

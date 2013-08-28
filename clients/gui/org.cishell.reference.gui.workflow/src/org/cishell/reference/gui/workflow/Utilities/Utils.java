package org.cishell.reference.gui.workflow.Utilities;

import java.io.File;

import org.cishell.reference.gui.workflow.views.AlgorithmItemGUI;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Utils {
	
	
	public static Image getImage(String name, String brandPluginID) {
		if (Platform.isRunning()) {
			String imageLocation = String.format("%sicons%s%s", File.separator,
					File.separator, name);
			ImageDescriptor imageDescriptor = AbstractUIPlugin
					.imageDescriptorFromPlugin(brandPluginID, imageLocation);

			if (imageDescriptor != null) {
				return imageDescriptor.createImage();
			} else {
				String errorMessage = String
						.format("Could not find the icon '%s' in '%s'. Using the default image instead.",
								imageLocation, brandPluginID);
				System.err.println(errorMessage);
				// need to change
				return AlgorithmItemGUI.getDefaultImage();
			}

		} else {
			String format = "Could not obtain the image '%s' in '%s', since the platform was not "
					+ "running (?). Using the default image instead.";
			String errorMessage = String.format(format, name, brandPluginID);
			System.err.println(errorMessage);
			// need to change
			return AlgorithmItemGUI.getDefaultImage();
		}
	}

}

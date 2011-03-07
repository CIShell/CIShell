package org.cishell.reference.gui.datamanager;

import java.io.File;

import org.cishell.app.service.filesaver.FileSaveException;
import org.cishell.app.service.filesaver.FileSaverService;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.ConversionException;
import org.cishell.service.conversion.Converter;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/* I'm calling this CIShellDataTransfer to make it clear that it's domain-specific.
 */
public class CIShellDataTransfer extends ByteArrayTransfer {
	public static final String TYPE_NAME = "CIShellData";
	public static final int TYPE_ID = registerType(TYPE_NAME);

	private BundleContext bundleContext;

	public CIShellDataTransfer(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void javaToNative(Object object, TransferData transferData) {
		System.err.println(String.format(
			"javaToNative: %s (%s) %s", object, object.getClass().getName(), transferData)); 
		if ((object == null) || !(object instanceof Data[])) {
			return;
		}

		if (isSupportedType(transferData)) {
			Data[] data = (Data[]) object;
			Data[] convertedData = convertData(data);
			String[] convertedFilePaths = formConvertedFilePaths(convertedData);
			FileTransfer.getInstance().javaToNative(convertedFilePaths, transferData);
		}
	}

	public Object nativeToJava(TransferData transferData) {
		// TODO: I probably won't get to implementing this. Maybe throw an exception?
		return null;
	}

	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	protected int[] getTypeIds() {
		return new int[] { TYPE_ID };
	}

	private Data[] convertData(Data[] data) {
		Data[] convertedData = new Data[data.length];

		for (int ii = 0; ii < data.length; ii++) {
			try {
				convertedData[ii] = convertDatum(data[ii]);
			} catch (Throwable e) {
				// TODO: Log this.
			}
		}

		return convertedData;
	}

	private Data convertDatum(Data datum) throws ConversionException, FileSaveException {
		ServiceReference fileSaverServiceReference =
			this.bundleContext.getServiceReference(FileSaverService.class.getName());
		FileSaverService fileSaver =
			(FileSaverService) this.bundleContext.getService(fileSaverServiceReference);

		Converter converter =
			fileSaver.promptForConverter(datum, FileSaverService.ANY_FILE_EXTENSION);

		return converter.convert(datum);
	}

	private String[] formConvertedFilePaths(Data[] convertedData) {
		String[] convertedFilePaths = new String[convertedData.length];

		for (int ii = 0; ii < convertedData.length; ii++) {
			convertedFilePaths[ii] = ((File) convertedData[ii].getData()).getAbsolutePath();
		}

		return convertedFilePaths;
	}
}
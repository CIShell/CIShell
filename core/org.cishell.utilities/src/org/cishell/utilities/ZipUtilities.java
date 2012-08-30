package org.cishell.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
/**
 * @deprecated see
 *            {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities}
 */
@Deprecated
public class ZipUtilities {
	/* 2048, as gotten from:
	 * http://java.sun.com/developer/technicalArticles/Programming/compression/
	 */
	static final int BUFFER_SIZE = 2048;

	public static void zipFiles(Collection<File> files, ZipOutputStream zipOut)
			throws ZipIOException {
		try {
			for (File file : files) {
				writeFileToZipFile(file, file.getName(), zipOut);
			}

			zipOut.close();
		} catch (IOException e) {
			throw new ZipIOException(e.getMessage(), e);
		} finally {
			try {
				zipOut.close();
			} catch (IOException e) {
				throw new ZipIOException(e.getMessage(), e);
			}
		}
			
	}

	public static void zipFiles(Collection<File> files, File targetZipFile)
			throws ZipIOException {
		try {
			zipFiles(
				files,
				new ZipOutputStream(
					new BufferedOutputStream(new FileOutputStream(targetZipFile))));
		} catch (FileNotFoundException e) {
			throw new ZipIOException(e.getMessage(), e);
		}
	}

	public static void zipFilesWithNames(
			Map<File, String> fileToZippedName, ZipOutputStream zipOut) throws ZipIOException {
		try {
			for (File file : fileToZippedName.keySet()) {
				writeFileToZipFile(file, fileToZippedName.get(file), zipOut);
			}

			zipOut.close();
		} catch (IOException e) {
			throw new ZipIOException(e.getMessage(), e);
		} finally {
			try {
				zipOut.close();
			} catch (IOException e) {
				throw new ZipIOException(e.getMessage(), e);
			}
		}
	}

	public static void zipFilesWithNames(
			Map<File, String> fileToZippedName, File targetZipFile) throws ZipIOException {
		try {
			zipFilesWithNames(
				fileToZippedName,
				new ZipOutputStream(
					new BufferedOutputStream(new FileOutputStream(targetZipFile))));
		} catch (FileNotFoundException e) {
			throw new ZipIOException(e.getMessage(), e);
		}
	}

	/* Again, refer to:
	 * http://java.sun.com/developer/technicalArticles/Programming/compression/
	 * for a reference on where this solution came from.
	 */
	public static void writeFileToZipFile(File file, String zippedName, ZipOutputStream zipOut)
			throws ZipIOException {
		try {
			byte data[] = new byte[BUFFER_SIZE];
			BufferedInputStream fileInput =
				new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE);
			ZipEntry entry = new ZipEntry(zippedName);
			zipOut.putNextEntry(entry);
			int count;

			while ((count = fileInput.read(data, 0, BUFFER_SIZE)) != -1) {
				zipOut.write(data, 0, count);
			}

			fileInput.close();
		} catch (FileNotFoundException e) {
			throw new ZipIOException(e.getMessage(), e);
		} catch (IOException e) {
			throw new ZipIOException(e.getMessage(), e);
		}
	}

	public static File readFileFromZipFile(ZipEntry entry, ZipFile zipFile) throws IOException {
		long size = entry.getSize();

		if (size > 0) {
			BufferedInputStream reader = new BufferedInputStream(zipFile.getInputStream(entry));
			String fileName = new File(entry.getName()).getName();
			File outputFile =
				FileUtilities.createTemporaryFileInDefaultTemporaryDirectory(fileName, "tmp");
			BufferedOutputStream output =
				new BufferedOutputStream(new FileOutputStream(outputFile), BUFFER_SIZE);

			/* TODO Could we have:
			 * writeFileToZipFile(BufferedInputStream in, String zippedName, ZipOutputStream zipOut)
			 * and then have both:
			 * writeFileToZipFile(File file, String zippedName, ZipOutputStream zipOut)
			 * and this method use that as a common utility?
			 * (Maybe eventually, if someone wants to do this.)
			 */
			byte readBytes[] = new byte[BUFFER_SIZE];
			int readByteCount;

			while ((readByteCount = reader.read(readBytes, 0, BUFFER_SIZE)) != -1) {
				output.write(readBytes, 0, readByteCount);
			}

			output.close();

			return outputFile;
		} else {
			return null;
		}
	}

	public static Map<String, ZipEntry> mapFileNamesToEntries(
			ZipFile zipFile, boolean includeDirectories) throws IOException {
		Collection<ZipEntry> entries = collectEntries(zipFile);
		Map<String, ZipEntry> fileNamesToEntries = new HashMap<String, ZipEntry>();

		for (ZipEntry entry : entries) {
			if (includeDirectories) {
				if (entry.isDirectory()) {
					fileNamesToEntries.put(entry.getName(), entry);
				}
			} else if (!entry.isDirectory()) {
				fileNamesToEntries.put(entry.getName(), entry);
			}
		}

		return fileNamesToEntries;
	}

	@SuppressWarnings("unchecked")
	public static Collection<ZipEntry> collectEntries(ZipFile zipFile) throws IOException {
		Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zipFile.entries();

		return CollectionUtilities.collectionEnumerationElements(
			entries, new ArrayList<ZipEntry>());
	}
}
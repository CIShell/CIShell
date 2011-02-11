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

public class ZipUtilities {
	static final int BUFFER_SIZE = 2048;

	public static void zipFiles(Collection<File> files, ZipOutputStream zipOut)
			throws ZipIOException {
		try {
			for (File file : files) {
				writeFileToZipFile(file, zipOut);
			}

			zipOut.close();
		} catch (IOException e) {
			throw new ZipIOException(e.getMessage(), e);
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

	public static void writeFileToZipFile(File file, ZipOutputStream zipOut)
			throws ZipIOException {
		try {
			byte data[] = new byte[BUFFER_SIZE];
			BufferedInputStream fileInput =
				new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE);
			ZipEntry entry = new ZipEntry(file.getName());
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
				FileUtilities.createTemporaryFileInDefaultTemporaryDirectory(fileName, "");
			BufferedOutputStream output =
				new BufferedOutputStream(new FileOutputStream(outputFile), BUFFER_SIZE);

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

	public static void main(String[] args) {
		String filePath =
			"C:\\Documents and Settings\\pataphil\\Desktop\\org.cishell.utility.swt.zip";

		try {
			ZipFile zipFile = new ZipFile(filePath);
			Map<String, ZipEntry> entriesByName = mapFileNamesToEntries(zipFile, false);

			int count = 0;
			for (String key : entriesByName.keySet()) {
				System.err.println(key + ": " + entriesByName.get(key));
				count++;

				if (count == 2) {
					readFileFromZipFile(entriesByName.get(key), zipFile);
				}
			}
		} catch (IOException e) {
			System.err.println("Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
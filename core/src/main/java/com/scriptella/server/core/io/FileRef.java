package com.scriptella.server.core.io;

import scriptella.core.RuntimeIOException;

import java.io.*;

/**
 * Abstraction over {@link File} not limited to filesystem TODO Extract
 * interface
 */
public class FileRef implements Serializable {
	private static final long serialVersionUID = 1L;
	private File file;

	public FileRef(File file) {
		this.file = file;
	}

	public InputStream getInputStream() {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeIOException(e);
		}
	}

	public OutputStream getOutputStream() {
		try {
			return new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeIOException(e);
		}
	}

	public FileRef getRelative(String childName) {
		return new FileRef(new File(file, childName));
	}

	public File getFile() throws UnsupportedOperationException {
		return file;
	}

	boolean isLocalFS() {
		return true;
	}

	public boolean isDirectory() {
		return file.isDirectory();
	}
}

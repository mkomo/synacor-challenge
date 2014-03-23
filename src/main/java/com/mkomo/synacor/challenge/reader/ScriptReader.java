package com.mkomo.synacor.challenge.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ScriptReader implements Reader {

	private File file;

	private FileInputStream is;

	public ScriptReader(String string) {
		this.file = new File(string);
	}

	public int read() {
		try {
			return getInputStream().read();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private FileInputStream getInputStream() throws FileNotFoundException {
		if (is == null){
			is = new FileInputStream(file);
		}
		return is;
	}

}

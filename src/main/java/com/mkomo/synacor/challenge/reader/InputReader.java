package com.mkomo.synacor.challenge.reader;

import java.io.IOException;

public class InputReader implements Reader {

	public int read() {
		try {
			System.out.println("read");
			return System.in.read();
		} catch (IOException e) {
			throw new RuntimeException("failed to read console reader", e);
		}
	}

}

package com.mkomo.synacor.challenge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PreloadedStream implements Stream {

	private File binary;
	private byte[] buffer = new byte[2];
	private List<SynNum> chars = new ArrayList<SynNum>();
	private FileInputStream in;
	private int offset = 0;
	private int[] register = new int[8];
	private Stack<Integer> stack = new Stack<Integer>();

	public PreloadedStream(File file) {
		this.binary = file;
		load();
	}

	private void load() {
		System.err.println("loading");
		SynNum c;
		while ((c = lazyRead()) != null){
			chars.add(c);
		}
		offset = 0;
		System.err.println("done");
	}

	public SynNum read() {
		return offset < chars.size() ? chars.get(offset++) : null;
	}

	private SynNum read(int address) {
		return address < chars.size() ? chars.get(address) : null;
	}

	public SynNum lazyRead() {
		try {
			int out = getInputStream().read(buffer);
			if (out == -1) {
				SynVM.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! end of input at " + chars.size());
				return null;
			} else {
				int lower = buffer[0] < 0 ? 256 + buffer[0] : buffer[0];
				int upper = buffer[1] < 0 ? 256 + buffer[1] : buffer[1];
				int val = (upper << 8) + lower;

				if (buffer[1] != 0){
					SynVM.trace(String.format("buffer 1 is non-zero at index %d\n", chars.size()));
					SynVM.trace(String.format("%05d + %05d = %05d (%s)\n", buffer[1], buffer[0], val, (char) val));
				}

				return new SynNum(val);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private FileInputStream getInputStream() throws FileNotFoundException {
		if (this.in == null){
			this.in = new FileInputStream(binary);
		}
		return this.in;
	}

	public int offset() {
		return offset;
	}

	public void jmp(int offset) {
		this.offset = offset;
	}

	public int getRegister(int registerIndex) {
		if (registerIndex >= 0 && registerIndex < 8){
			return register[registerIndex];
		} else {
			throw new IllegalArgumentException("registers are numbered 0 through 7: " + registerIndex);
		}
	}

	public int readOrReg() {
		SynNum num = read();
		if (num.isRegister()){
			SynVM.trace(String.format("getting register %d (val: %d)", num.getRegisterIndex(), getRegister(num.getRegisterIndex())));
			return getRegister(num.getRegisterIndex());
		} else {
			return num.getVal();
		}
	}

	public int readOrReg(int address) {
		SynNum num = read(address);
		if (num.isRegister()){
			SynVM.trace(String.format("getting register %d (val: %d)", num.getRegisterIndex(), getRegister(num.getRegisterIndex())));
			return getRegister(num.getRegisterIndex());
		} else {
			return num.getVal();
		}
	}

	public void set(int registerIndex, int val) {
		if (registerIndex >= 0 && registerIndex < 8){
			SynVM.trace("setting register " + registerIndex + " to " + val);
			register[registerIndex] = val;
		} else {
			throw new IllegalArgumentException("registers are numbered 0 through 7: " + registerIndex);
		}
	}

	public void push(int val) {
		stack.push(val);
	}

	public int pop() {
		return stack.pop();
	}

	public void write(int address, int val) {
		chars.set(address, new SynNum(val));
	}

}

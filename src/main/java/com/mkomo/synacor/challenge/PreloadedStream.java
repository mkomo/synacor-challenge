package com.mkomo.synacor.challenge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class PreloadedStream implements Stream {

	private List<SynNum> chars = new ArrayList<SynNum>();
	private int address = 0;
	private int[] register = new int[8];
	private Stack<Integer> stack = new Stack<Integer>();

	public PreloadedStream(File file) {
		load(file);
	}

	private PreloadedStream() {
	}

	@Override
	public int hashCode(){
		return Arrays.hashCode(register) + stack.hashCode() + address + chars.hashCode();
	}

	@SuppressWarnings("unchecked")
	public Stream getCopy(){
		PreloadedStream ps = new PreloadedStream();
		ps.chars = new ArrayList<SynNum>(chars);
		ps.address = address;
		ps.register = Arrays.copyOf(register, register.length);
		ps.stack = (Stack<Integer>) stack.clone();
		return ps;
	}

	private void load(File binary) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(binary);
			SynNum c;
			while ((c = lazyRead(in)) != null){
				chars.add(c);
			}
			address = 0;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public SynNum lazyRead(FileInputStream in) {
		byte[] buffer = new byte[2];
		try {
			int out = in.read(buffer);
			if (out == -1) {
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

	public SynNum read() {
		SynNum val = read(address);
		address++;
		return val;
	}

	public SynNum read(int address) {
		return address < chars.size() ? chars.get(address) : null;
	}

	public int address() {
		return address;
	}

	public void jmp(int address) {
		this.address = address;
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

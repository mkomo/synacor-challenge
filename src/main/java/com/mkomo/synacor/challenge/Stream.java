package com.mkomo.synacor.challenge;

public interface Stream extends Cloneable {

	SynNum read();

	SynNum read(int offset);

	int offset();

	void jmp(int offset);

	int getRegister(int registerIndex);

	int readOrReg();

	int readOrReg(int address);

	void set(int registerIndex, int val);

	void push(int val);

	int pop();

	void write(int address, int val);

	Stream getCopy();

}

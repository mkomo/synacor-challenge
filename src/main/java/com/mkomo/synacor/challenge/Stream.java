package com.mkomo.synacor.challenge;

public interface Stream {

	SynNum read();

	int offset();

	void jmp(int offset);

	int getRegister(int registerIndex);

	int readOrReg();

}

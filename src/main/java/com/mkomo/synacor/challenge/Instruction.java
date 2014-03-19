package com.mkomo.synacor.challenge;

public abstract class Instruction {
	private int opcode;
	private String specString;

	public Instruction(int opcode, String specString) {
		this.opcode = opcode;
		this.specString = specString;
	}

	public abstract void execute(Stream s);
}

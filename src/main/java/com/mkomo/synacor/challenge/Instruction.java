package com.mkomo.synacor.challenge;


public abstract class Instruction {
	private int opcode;
	private String specString;
	private int numArgs;

	public Instruction(int opcode, String specString, int numArgs) {
		this.opcode = opcode;
		this.specString = specString;
		this.numArgs= numArgs;
	}

	public abstract void execute(Stream s);

	public void executeLoud(Stream stream) {
		System.out.println(describe(stream, stream.address()));
		execute(stream);
	}

	public String describe(Stream stream, int address) {
		StringBuilder sb = new StringBuilder(opcode + " " + String.format("%05d", address) + " " + specString + " ");

		for (int i = 0; i < numArgs; i++){
			SynNum val = stream.read(address + i);
			sb.append(String.format("%s ", val.toString()));
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return specString;
	}
}

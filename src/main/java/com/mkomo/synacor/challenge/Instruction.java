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
		StringBuilder sb = new StringBuilder("  " + stream.address() + "\t" + specString + " ");
		int address = stream.address();
		for (int i = 0; i < numArgs; i++){
			SynNum val = stream.read(address + i);
			sb.append(String.format("%s ", val.toString()));
		}
		System.out.println(sb.toString());
		execute(stream);
	}
}

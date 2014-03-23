package com.mkomo.synacor.challenge;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import com.mkomo.synacor.challenge.reader.Reader;
import com.mkomo.synacor.challenge.reader.ScriptReader;

public class SynVM {

	private static int guess = 1;
	private static boolean DEBUG = false;
	private static boolean TRACE = false;
	private static StringBuilder currentOutput = new StringBuilder();

	static Stream checkpoint;
//	private static Reader reader = new InputReader();
//	private static Reader reader = new PermutationReader();
	private static Reader reader = new ScriptReader("src/main/resources/actions");

	protected static int getNextInput(Stream s) {
		int val = getInput().read();
		return val;
	}

	public static void main(String[] args) throws Exception {

		Stream stream = new PreloadedStream(new File("src/main/resources/challenge.bin"));
		while (true){
			try {
				SynVM vm = new SynVM();
				vm.execute(stream);
			} catch (Exception e) {
				e.printStackTrace();
				if (checkpoint != null){
					stream = checkpoint.getCopy();
				}
			}
		}
	}
	private static boolean loud;
	private Map<Integer, Instruction> instructions = populateInstructions();

	public void execute(Stream stream) throws FileNotFoundException {
		SynNum next;
		while ((next = stream.read()) != null){
			Instruction instruction = instructions.get(next.getVal());
			if (instruction == null){
				major("instruction doesn't exist : " + next.getVal());
			} else {
				trace("execution START " + next.getVal());
				if (loud){
					instruction.executeLoud(stream);
				} else {
					instruction.execute(stream);
				}
				trace("execution DONE");
			}
		}

	}

	private static Map<Integer, Instruction> populateInstructions() {
		Map<Integer, Instruction> instructions = new HashMap<Integer, Instruction>();
		instructions.put(0, new Instruction(0, "halt", 0){

			@Override
			public void execute(Stream s) {
				debug("exiting at offset " + s.offset());
//				System.exit(0);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		});
		instructions.put(1, new Instruction(1, "set a b", 2){

			@Override
			public void execute(Stream s) {
				int reg = s.read().getRegisterIndex();
				int val = s.readOrReg();
				s.set(reg, val);
				debug("setting: " + val);
			}

		});
		instructions.put(2, new Instruction(2, "push a", 1){

			@Override
			public void execute(Stream s) {
				int val = s.readOrReg();
				s.push(val);
				debug("pushing: " + val);
			}

		});
		instructions.put(3, new Instruction(3, "pop a", 1){

			@Override
			public void execute(Stream s) {
				int reg = s.read().getRegisterIndex();
				int val = s.pop();
				s.set(reg, val);
				debug("popping: " + val);
			}

		});
		instructions.put(4, new Instruction(4, "eq a b c", 3){

			@Override
			public void execute(Stream s) {
				int reg = s.read().getRegisterIndex();
				int b = s.readOrReg();
				int c = s.readOrReg();
				if (b == c) {
					debug("b and c are equal: " + b);
					s.set(reg, 1);
				} else {
					debug("b and c are not equal: " + b + ", " + c);
					s.set(reg, 0);
				}
			}

		});
		instructions.put(5, new Instruction(5, "gt a b c", 3){

			@Override
			public void execute(Stream s) {
				int reg = s.read().getRegisterIndex();
				int b = s.readOrReg();
				int c = s.readOrReg();
				if (b > c) {
					debug("b > c: " + b + ", " + c);
					s.set(reg, 1);
				} else {
					debug("b <= c: " + b + ", " + c);
					s.set(reg, 0);
				}
			}

		});
		instructions.put(6, new Instruction(6, "jmp a", 1){

			@Override
			public void execute(Stream s) {
				int offset = s.readOrReg();

				debug("jumping to: " + offset);
				s.jmp(offset);
			}

		});
		instructions.put(7, new Instruction(7, "jt a b", 2){

			@Override
			public void execute(Stream s) {
				int cond = s.readOrReg();
				int offset = s.readOrReg();
				if (cond != 0) {
					debug(cond + " is nonzero; jumping to: " + offset);
					s.jmp(offset);
				} else {
					debug(cond + " is zero; not jumping to: " + offset);
				}
			}

		});
		instructions.put(8, new Instruction(8, "jf a b", 2){

			@Override
			public void execute(Stream s) {
				int cond = s.readOrReg();
				int offset = s.readOrReg();
				if (cond == 0) {
					debug(cond + " is zero; jumping to: " + offset);
					s.jmp(offset);
				} else {
					debug(cond + " is nonzero; not jumping to: " + offset);
				}
			}

		});
		instructions.put(9, new Instruction(9, "add a b c", 3){

			@Override
			public void execute(Stream s) {
				int reg = s.read().getRegisterIndex();
				int i = s.readOrReg();
				int j = s.readOrReg();
				int val = (i + j) % (1 << 15);
				debug(String.format("add %d + %d = %d", i, j, val));
				s.set(reg, val);
			}

		});
		instructions.put(10, new Instruction(10, "mult a b c", 3){

			@Override
			public void execute(Stream s) {
				int reg = s.read().getRegisterIndex();
				int i = s.readOrReg();
				int j = s.readOrReg();
				int val = (i * j) % (1 << 15);
				debug(String.format("mult %d x %d = %d", i, j, val));
				s.set(reg, val);
			}

		});
		instructions.put(11, new Instruction(11, "mod a b c", 3){

			@Override
			public void execute(Stream s) {
				int reg = s.read().getRegisterIndex();
				int i = s.readOrReg();
				int j = s.readOrReg();
				int val = (i % j) % (1 << 15);
				debug(String.format("%d mod %d = %d", i, j, val));
				s.set(reg, val);
			}

		});
		instructions.put(12, new Instruction(12, "and a b c", 3){

			@Override
			public void execute(Stream s) {
				int reg = s.read().getRegisterIndex();
				int i = s.readOrReg();
				int j = s.readOrReg();
				debug(String.format("%d & %d = %d", i, j, i & j));
				s.set(reg, i & j);
			}

		});
		instructions.put(13, new Instruction(13, "or a b c", 3){

			@Override
			public void execute(Stream s) {
				int reg = s.read().getRegisterIndex();
				int i = s.readOrReg();
				int j = s.readOrReg();
				debug(String.format("%d | %d = %d", i, j, i | j));
				s.set(reg, i | j);
			}

		});
		instructions.put(14, new Instruction(14, "not a b", 2){

			@Override
			public void execute(Stream s) {
				int reg = s.read().getRegisterIndex();
				int i = s.readOrReg();
				int val = (~i & ((1 << 15) - 1));
				debug(String.format("setting %d to bitwise not of %d: %d", reg, i, val));
				s.set(reg, val);
			}

		});
		instructions.put(15, new Instruction(15, "rmem a b", 2){

			@Override
			public void execute(Stream s) {
				int reg = s.read().getRegisterIndex();
				int address = s.readOrReg();
				int val = s.readOrReg(address);
				debug(String.format("read memory at address %d (%d) and write it to %d", address, val, reg));
				s.set(reg, val);
			}

		});
		instructions.put(16, new Instruction(16, "wmem a b", 2){

			@Override
			public void execute(Stream s) {
				int address = s.readOrReg();
				int val = s.readOrReg();
				debug(String.format("set memory address %d to %d", address, val));
				s.write(address, val);
			}

		});
		instructions.put(17, new Instruction(17, "call a", 1){

			@Override
			public void execute(Stream s) {
				int offset = s.readOrReg();
				s.push(s.offset());
				debug("call - jumping to: " + offset);
				s.jmp(offset);
			}

		});
		instructions.put(18, new Instruction(18, "ret", 0){

			@Override
			public void execute(Stream s) {
				int offset = s.pop();

				debug("ret - jumping to: " + offset);
				s.jmp(offset);
			}

		});
		instructions.put(19, new Instruction(19, "out a", 1){

			@Override
			public void execute(Stream s) {
				trace("'");
				char c = (char)s.readOrReg();
				output(c);
				currentOutput.append(c);
				trace("'");
			}

		});
		instructions.put(20, new Instruction(20, "in a", 1){

			@Override
			public void execute(Stream s) {
				int reg = s.read().getRegisterIndex();
				debug("reading char and saving it to: " + reg);
				int input = getNextInput(s);
				s.set(reg, input);
			}

		});
		instructions.put(21, new Instruction(21, "noop", 0){

			@Override
			public void execute(Stream s) {
				trace("noop");
			}

		});

		return instructions;
	}

	private static Reader getInput() {
		return reader;
	}

	private static void output(char read) {
		synchronized (System.out) {
			System.out.print(read);
		}
	}
	static void major(String string) {
		synchronized (System.out) {
			System.out.println("****(" + string + ")*****");
		}
	}
	static void debug(String string) {
		if (DEBUG) {
			synchronized (System.out) {
				System.out.println("****(" + string + ")*****");
			}
		}
	}
	static void trace(String string) {
		if (TRACE) {
			synchronized (System.out) {
				System.out.println("****(" + string + ")*****");
			}
		}
	}
}

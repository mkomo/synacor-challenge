package com.mkomo.synacor.challenge;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class SynVM {

	private static boolean DEBUG = false;
	private static boolean TRACE = false;

	private Map<Integer, Instruction> instructions = populateInstructions();

	public static void main(String[] args) throws Exception {

		SynVM vm = new SynVM();
		vm.execute(new File("src/main/resources/challenge.bin"));

	}



	public void execute(File file) {
		Stream s = new PreloadedStream(file);

		System.out.println("start at " + System.currentTimeMillis());
		SynNum next;
		while ((next = s.read()) != null){
			Instruction instruction = instructions.get(next.getVal());
			if (instruction == null){
				major("instruction doesn't exist : " + next.getVal());
			} else {
				trace("executing " + next.getVal());
				instruction.execute(s);
				trace("done ");
			}
		}

	}



	private static Map<Integer, Instruction> populateInstructions() {
		Map<Integer, Instruction> instructions = new HashMap<Integer, Instruction>();
		instructions.put(0, new Instruction(0, "halt: 0"){

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
		instructions.put(1, new Instruction(1, "set: 1 a b"){

			@Override
			public void execute(Stream s) {
				int reg = s.read().getRegisterIndex();
				int val = s.readOrReg();
				s.set(reg, val);
				debug("setting: " + val);
			}

		});
		instructions.put(2, new Instruction(2, "push: 2 a"){

			@Override
			public void execute(Stream s) {
				int val = s.readOrReg();
				s.push(val);
				debug("pushing: " + val);
			}

		});
		instructions.put(3, new Instruction(3, "pop: 3 a"){

			@Override
			public void execute(Stream s) {
				int reg = s.read().getRegisterIndex();
				int val = s.pop();
				s.set(reg, val);
				debug("popping: " + val);
			}

		});
		instructions.put(4, new Instruction(4, "eq: 4 a b c"){

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
		instructions.put(5, new Instruction(5, "gt: 5 a b c"){

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
		instructions.put(6, new Instruction(6, "jmp: 6 a"){

			@Override
			public void execute(Stream s) {
				int offset = s.readOrReg();

				debug("jumping to: " + offset);
				s.jmp(offset);
			}

		});
		instructions.put(7, new Instruction(7, "jt: 7 a b"){

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
		instructions.put(8, new Instruction(8, "jf: 8 a b"){

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
		instructions.put(9, new Instruction(9, "add: 9 a b c"){

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
		instructions.put(10, new Instruction(10, "mult: 10 a b c"){

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
		instructions.put(11, new Instruction(11, "mod: 11 a b c"){

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
		instructions.put(12, new Instruction(12, "and: 12 a b c"){

			@Override
			public void execute(Stream s) {
				int reg = s.read().getRegisterIndex();
				int i = s.readOrReg();
				int j = s.readOrReg();
				debug(String.format("%d & %d = %d", i, j, i & j));
				s.set(reg, i & j);
			}

		});
		instructions.put(13, new Instruction(13, "or: 13 a b c"){

			@Override
			public void execute(Stream s) {
				int reg = s.read().getRegisterIndex();
				int i = s.readOrReg();
				int j = s.readOrReg();
				debug(String.format("%d | %d = %d", i, j, i | j));
				s.set(reg, i | j);
			}

		});
		instructions.put(14, new Instruction(14, "not: 14 a b"){

			@Override
			public void execute(Stream s) {
				int reg = s.read().getRegisterIndex();
				int i = s.readOrReg();
				int val = (~i & ((1 << 15) - 1));
				debug(String.format("setting %d to bitwise not of %d: %d", reg, i, val));
				s.set(reg, val);
			}

		});
		instructions.put(15, new Instruction(15, "rmem: 15 a b"){

			@Override
			public void execute(Stream s) {
				int reg = s.read().getRegisterIndex();
				int address = s.readOrReg();
				int val = s.readOrReg(address);
				debug(String.format("read memory at address %d (%d) and write it to %d", address, val, reg));
				s.set(reg, val);
			}

		});
		instructions.put(16, new Instruction(16, "wmem: 16 a b"){

			@Override
			public void execute(Stream s) {
				int address = s.readOrReg();
				int val = s.readOrReg();
				debug(String.format("set memory address %d to %d", address, val));
				s.write(address, val);
			}

		});
		instructions.put(17, new Instruction(17, "call: 17 a"){

			@Override
			public void execute(Stream s) {
				int offset = s.readOrReg();
				s.push(s.offset());
				debug("call - jumping to: " + offset);
				s.jmp(offset);
			}

		});
		instructions.put(18, new Instruction(18, "ret: 18"){

			@Override
			public void execute(Stream s) {
				int offset = s.pop();

				debug("ret - jumping to: " + offset);
				s.jmp(offset);
			}

		});
		instructions.put(19, new Instruction(19, "out: 19 a"){

			@Override
			public void execute(Stream s) {
				trace("'");
				output((char)s.readOrReg());
				trace("'");
			}

		});
		instructions.put(20, new Instruction(20, "in: 20 a"){

			@Override
			public void execute(Stream s) {
				System.out.println("done at " + System.currentTimeMillis());
				int reg = s.read().getRegisterIndex();
				major("reading char and saving it to: " + reg);
				DEBUG = true;
				TRACE = true;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
		instructions.put(21, new Instruction(21, "noop: 21"){

			@Override
			public void execute(Stream s) {
				trace("noop");
			}

		});

		return instructions;
		/**
		 *
nonzero reg
no set op
no gt op
no stack
no bitwise and
no bitwise not
no rmem op
no wmem op
no call op
no modulo math during add or mult
not hitchhiking
no mult op
no mod op
		 */
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

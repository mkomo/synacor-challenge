package com.mkomo.synacor.challenge;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class SynVM {
	public static void main(String[] args) throws Exception {
		Stream s = new PreloadedStream(new File("src/main/resources/challenge.bin"));
		Map<Integer, Instruction> instructions = new HashMap<Integer, Instruction>();
		populateInstructions(instructions);
		SynNum next;
		int count = 0;
		while ((next = s.read()) != null){
			if (count >= 1000){
				System.exit(0);
			} else {
				count ++;
			}
			Instruction instruction = instructions.get(next.getVal());
			if (instruction == null){
				debug("instruction doesn't exist : " + next.getVal());
			} else {
				trace("executing " + next.getVal());
				instruction.execute(s);
				trace("done ");
			}
		}
	}

	private static void populateInstructions(
			Map<Integer, Instruction> instructions) {
		instructions.put(0, new Instruction(){

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
		instructions.put(6, new Instruction(){

			@Override
			public void execute(Stream s) {
				int offset = s.readOrReg();

				debug("jumping to: " + offset);
				s.jmp(offset);
			}

		});
		instructions.put(7, new Instruction(){

			@Override
			public void execute(Stream s) {
				int cond = s.readOrReg();
				int offset = s.readOrReg();
				if (cond != 0) {
					debug(cond + " is nonzero; jumping to: " + offset);
					s.jmp(offset);
				}
			}

		});
		instructions.put(8, new Instruction(){

			@Override
			public void execute(Stream s) {
				int cond = s.readOrReg();
				int offset = s.readOrReg();
				if (cond == 0) {
					debug(cond + " is nonzero; jumping to: " + offset);
					s.jmp(offset);
				}
			}

		});
		instructions.put(19, new Instruction(){

			@Override
			public void execute(Stream s) {
				trace("'");
				output((char)s.readOrReg());
				trace("'");
			}

		});
		instructions.put(21, new Instruction(){

			@Override
			public void execute(Stream s) {
				trace("noop");
			}

		});
	}

	static class theLock extends Object {
	}
	static public theLock lockObject = new theLock();
	private static void output(char read) {
		synchronized (System.out) {
			System.out.print(read);
		}
	}
	static void debug(String string) {
//		synchronized (System.out) {
//			System.out.println("****(" + string + ")*****");
//		}
	}
	static void trace(String string) {
//		synchronized (System.out) {
//			System.out.print("****(" + string + ")*****");
//		}
	}
}

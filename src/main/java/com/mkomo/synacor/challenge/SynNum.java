package com.mkomo.synacor.challenge;

public class SynNum {

	private int val;

	public SynNum(int val) {
		this.val = val;
	}

	public int getVal() {
		return val;
	}

	public boolean isRegister() {
		return val >= 1 << 15 && val < (1 << 15) + 7;
	}

	public int getRegisterIndex() {
		return val - (1 << 15);
	}

	public static void main(String[] args) {
		System.out.println(1 << 15);
	}

}

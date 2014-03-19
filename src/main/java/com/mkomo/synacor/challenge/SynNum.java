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
		return val >= 2 << 15 && val < (2 << 15) + 7;
	}

	public int getRegisterIndex() {
		return val - (2 << 15);
	}

}

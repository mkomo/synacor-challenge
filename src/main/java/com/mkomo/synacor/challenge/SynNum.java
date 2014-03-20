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
		return val >= 1 << 15 && val < (1 << 15) + 8;
	}

	public int getRegisterIndex() {
		return val - (1 << 15);
	}

	@Override
	public int hashCode(){
		return val;
	}

	@Override
	public String toString(){
		return isRegister() ? "REG_" + getRegisterIndex() : String.valueOf(val);
	}
}

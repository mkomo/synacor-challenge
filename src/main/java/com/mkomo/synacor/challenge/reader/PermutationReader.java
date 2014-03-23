package com.mkomo.synacor.challenge.reader;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Collections2;

public class PermutationReader implements Reader {

	static List<String> coinTypes = Arrays.asList("blue", "red", "shiny", "concave", "corroded");
	static Iterator<List<String>> coinPermutations = Collections2.permutations(coinTypes).iterator();
	static String currentCoinPermutationString = null;
	static int currentCoinPermuationCharIndex = 0;

	public int read() {
		if (currentCoinPermutationString == null || currentCoinPermuationCharIndex >= currentCoinPermutationString.length()){
//			if (!currentOutput.toString().contains("As you place the last coin, they are all released onto the floor.")){
//				return -1;
//			}
//			currentOutput = new StringBuilder();
			currentCoinPermutationString = getString(coinPermutations.next());
			currentCoinPermuationCharIndex = 0;
		}
		if (currentCoinPermutationString == null){
			return -1;
		}
		int val = currentCoinPermutationString.charAt(currentCoinPermuationCharIndex);
		currentCoinPermuationCharIndex++;
		return val;
	}

	private static String getString(List<String> next) {
		StringBuilder sb = new StringBuilder();
		for (String type : next){
			sb.append("take " + type + " coin\n");
			sb.append("use " + type + " coin\n");
		}
		return sb.toString();
	}

}

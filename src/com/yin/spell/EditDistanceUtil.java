package com.yin.spell;

import java.util.HashMap;
import java.util.Map;

public class EditDistanceUtil {

	private static EditDistanceUtil instance = null;
	private static final double SCORE_MIS_HIT = 0.5;
	private HashMap<Character, String> charSiblings;

	private EditDistanceUtil() {
		init();
	}

	/**
	 * 获得EditDistance 的实例
	 * 
	 * @return
	 */
	public static EditDistanceUtil getInstance() {
		if (instance == null) {
			instance = new EditDistanceUtil();
		}
		return instance;
	}

	private void init() {
		charSiblings = new HashMap<Character, String>();
		createKeyboradDistance();
	}

	/**
	 * return the minimum of a, b and c
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	private double min(double a, double b, double c) {
		double t = a < b ? a : b;
		return t < c ? t : c;
	}

	/**
	 * 计算编辑距离
	 * 
	 * @param word1
	 * @param word2
	 * @return
	 */
	public double calculate(String word1, String word2) {
		double distance = 0;

		int len1 = word1.length();
		int len2 = word2.length();
		double[][] matrix = new double[len1 + 1][len2 + 1];

		// 初始化行
		for (int i = 0; i < len1 + 1; i++) {
			matrix[i][0] = i;
		}

		// 初始化列
		for (int j = 0; j < len2 + 1; j++) {
			matrix[0][j] = j;
		}

		for (int i = 1; i < len1 + 1; i++) {
			for (int j = 1; j < len2 + 1; j++) {
				char c1 = word1.charAt(i - 1);
				char c2 = word2.charAt(j - 1);

				double del = matrix[i - 1][j] + 1;
				double ins = matrix[i][j - 1] + 1;
				double sub = matrix[i - 1][j - 1] + cost(c1, c2);

				matrix[i][j] = min(del, ins, sub);

			}
		}

		// for (int i = 0; i <= len1; i++) {
		// for (int j = 0; j <= len2; j++) {
		// int val = matrix[i][j];
		// if (val < 10) {
		// System.out.print("  " + val + "  ");
		// } else {
		// System.out.print("  " + val + " ");
		// }
		//
		// }
		// System.out.println();
		// }

		distance = matrix[len1][len2];
		return distance;
	}
	
	/**
	 * 计算来两个字符进行substitute操作的代价，将会考虑键盘中易混淆的字符
	 * @param c1
	 * @param c2
	 * @return
	 */
	private double cost(char c1, char c2) {
		if (c1 == c2) {
			return 0;
		}
//		String s = charSiblings.get(c1);
//		if (s != null && s.indexOf(c2) > -1) {
//			return SCORE_MIS_HIT;
//		}
		return 1;
	}

	private void createKeyboradDistance() {

		charSiblings.put('q', "was");
		charSiblings.put('w', "qsead");
		charSiblings.put('e', "wsdfr");
		charSiblings.put('r', "edfgt");
		charSiblings.put('t', "rfghy");
		charSiblings.put('y', "tghju");
		charSiblings.put('u', "yhjki");
		charSiblings.put('i', "ujklo");
		charSiblings.put('o', "ikl;p");
		charSiblings.put('p', "ol;'[");
		charSiblings.put('a', "qwsxz");
		charSiblings.put('s', "qazxcdew");
		charSiblings.put('d', "wsxcvfre");
		charSiblings.put('f', "edcvbgtr");
		charSiblings.put('g', "rfvbnhyt");
		charSiblings.put('h', "tgbnmjuy");
		charSiblings.put('j', "yhnm,kiu");
		charSiblings.put('k', "ujm,.loi");
		charSiblings.put('l', "ik,./;po");
		charSiblings.put('z', "asx");
		charSiblings.put('x', "zasdc");
		charSiblings.put('c', "xsdfv");
		charSiblings.put('v', "cdfgb");
		charSiblings.put('b', "vfghn");
		charSiblings.put('n', "bghjm");
		charSiblings.put('m', "nhjk,");
	}

	

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		EditDistanceUtil edited = EditDistanceUtil.getInstance();
		// for (int i = 0; i < 50000; i++) {
		// edited.calculate("intention", "execution");
		// }
		double distance = edited.calculate("an", "am");
		long end = System.currentTimeMillis();

		long delay = end - start;
		System.out.println("edit distance is: " + distance);
		System.out.println("delay: " + delay + "ms");
	}
}

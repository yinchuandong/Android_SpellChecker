package com.yin.spellchecker.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SpellChecker {

	static{
		System.loadLibrary("SpellChecker");
	}
	
	public native String[] init();
	public native void test();
	public native HashMap<String, String> getTranMap();
	
	/**
	 * 加载牛津词典
	 * @return
	 */
	public native HashSet<String> loadOxfordWords();
	
	/**
	 * 
	 * @param wordArr 原始句子切分的词语
	 * @return
	 */
	public native ArrayList<String> loadCandidateMap(String[] wordArr);
	/**
	 * 
	 * @param wordArr 所有候选词
	 * @return
	 */
	public native HashMap<String, Double> loadInitProb(String[] wordArr);
	/**
	 * 
	 * @param itemArr 转移项,两个单词之间用\t分隔,如：I|like, you|like,
	 * @return
	 */
	public native HashMap<String, Double> loadTranProb(String[] itemArr);
	
}

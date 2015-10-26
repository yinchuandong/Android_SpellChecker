package com.yin.spellchecker.lib;

public class SpellChecker {

	static{
		System.loadLibrary("SpellChecker");
	}
	
	public native String[] init();

	public native void loadDict();

	public native String findDict(String key);
}

package com.yin.spellchecker.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class SpellChecker {

	static{
		System.loadLibrary("SpellChecker");
	}
	
	public native String[] init();

	
}

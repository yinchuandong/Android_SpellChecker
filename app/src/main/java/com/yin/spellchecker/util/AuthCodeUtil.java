package com.yin.spellchecker.util;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AuthCodeUtil {

	public enum DiscuzAuthcodeMode {
		Encode, Decode
	};

	/**
	 * 
	 * @param str
	 * @param startIndex
	 * @param length
	 * @return
	 */
	public static String cutString(String str, int startIndex, int length) {
		if (startIndex >= 0) {
			if (length < 0) {
				length = length * -1;
				if (startIndex - length < 0) {
					length = startIndex;
					startIndex = 0;
				} else {
					startIndex = startIndex - length;
				}
			}

			if (startIndex > str.length()) {
				return "";
			}

		} else {
			if (length < 0) {
				return "";
			} else {
				if (length + startIndex > 0) {
					length = length + startIndex;
					startIndex = 0;
				} else {
					return "";
				}
			}
		}

		if (str.length() - startIndex < length) {

			length = str.length() - startIndex;
		}

		return str.substring(startIndex, startIndex + length);
	}

	public static String cutString(String str, int startIndex) {
		return cutString(str, startIndex, str.length());
	}

	public static boolean fileExists(String filename) {
		File f = new File(filename);
		return f.exists();
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String md5(String str) {
		// return md5.convert(str);
		StringBuffer sb = new StringBuffer();
		String part = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] md5 = md.digest(str.getBytes());

			for (int i = 0; i < md5.length; i++) {
				part = Integer.toHexString(md5[i] & 0xFF);
				if (part.length() == 1) {
					part = "0" + part;
				}
				sb.append(part);
			}

		} catch (NoSuchAlgorithmException ex) {
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static boolean strIsNullOrEmpty(String str) {
		// #if NET1
		if (str == null || str.trim().equals("")) {
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @param pass
	 * @param kLen
	 * @return
	 */
	static private byte[] getKey(byte[] pass, int kLen) {
		byte[] mBox = new byte[kLen];

		for (int i = 0; i < kLen; i++) {
			mBox[i] = (byte) i;
		}

		int j = 0;
		for (int i = 0; i < kLen; i++) {

			j = (j + (int) ((mBox[i] + 256) % 256) + pass[i % pass.length])
					% kLen;

			byte temp = mBox[i];
			mBox[i] = mBox[j];
			mBox[j] = temp;
		}

		return mBox;
	}

	/**
	 * 
	 * @param lens
	 * @return
	 */
	public static String randomString(int lens) {
		char[] CharArray = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k',
				'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
				'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		int clens = CharArray.length;
		String sCode = "";
		Random random = new Random();
		for (int i = 0; i < lens; i++) {
			sCode += CharArray[Math.abs(random.nextInt(clens))];
		}
		return sCode;
	}

	/**
	 * 
	 * @param source
	 * @param key
	 * @param expiry
	 * @return
	 */
	public static String encode(String source, String key, int expiry) {
		return authcode(source, key, DiscuzAuthcodeMode.Encode, expiry);

	}
	
	/**
	 * 
	 * @param source
	 * @param key
	 * @return
	 */
	public static String encode(String source, String key) {
		return authcode(source, key, DiscuzAuthcodeMode.Encode, 0);

	}

	/**
	 * 
	 * @param source
	 * @param key
	 * @return
	 */
	public static String decode(String source, String key) {
		return authcode(source, key, DiscuzAuthcodeMode.Decode, 0);

	}

	private static String authcode(String source, String key,
			DiscuzAuthcodeMode operation, int expiry) {
		try {
			if (source == null || key == null) {
				return "";
			}

			int ckey_length = 4;
			String keya, keyb, keyc, cryptkey, result;

			key = md5(key);

			keya = md5(cutString(key, 0, 16));

			keyb = md5(cutString(key, 16, 16));

			keyc = ckey_length > 0 ? (operation == DiscuzAuthcodeMode.Decode ? cutString(
					source, 0, ckey_length) : randomString(ckey_length))
					: "";

			cryptkey = keya + md5(keya + keyc);

			if (operation == DiscuzAuthcodeMode.Decode) {
				byte[] temp;

				temp = Base64.decode(cutString(source, ckey_length));
				result = new String(RC4(temp, cryptkey));
				if (cutString(result, 10, 16).equals(
						cutString(md5(cutString(result, 26) + keyb), 0, 16))) {
					return cutString(result, 26);
				} else {
					temp = Base64.decode(cutString(source + "=", ckey_length));
					result = new String(RC4(temp, cryptkey));
					if (cutString(result, 10, 16)
							.equals(cutString(
									md5(cutString(result, 26) + keyb), 0, 16))) {
						return cutString(result, 26);
					} else {
						temp = Base64.decode(cutString(source + "==",
								ckey_length));
						result = new String(RC4(temp, cryptkey));
						if (cutString(result, 10, 16).equals(
								cutString(md5(cutString(result, 26) + keyb), 0,
										16))) {
							return cutString(result, 26);
						} else {
							return "2";
						}
					}
				}
			} else {
				source = "0000000000" + cutString(md5(source + keyb), 0, 16)
						+ source;

				byte[] temp = RC4(source.getBytes("GBK"), cryptkey);

				return keyc + Base64.encode(temp);

			}
		} catch (Exception e) {
			return "";
		}

	}

	private static byte[] RC4(byte[] input, String pass) {
		if (input == null || pass == null)
			return null;

		byte[] output = new byte[input.length];
		byte[] mBox = getKey(pass.getBytes(), 256);

		// 加密
		int i = 0;
		int j = 0;

		for (int offset = 0; offset < input.length; offset++) {
			i = (i + 1) % mBox.length;
			j = (j + (int) ((mBox[i] + 256) % 256)) % mBox.length;

			byte temp = mBox[i];
			mBox[i] = mBox[j];
			mBox[j] = temp;
			byte a = input[offset];

			// byte b = mBox[(mBox[i] + mBox[j] % mBox.Length) % mBox.Length];
			// mBox[j] 一定比 mBox.Length 小，不需要在取模
			byte b = mBox[(toInt(mBox[i]) + toInt(mBox[j])) % mBox.length];

			output[offset] = (byte) ((int) a ^ (int) toInt(b));
		}

		return output;
	}

	public static int toInt(byte b) {
		return (int) ((b + 256) % 256);
	}

	public long getUnixTimestamp() {
		Calendar cal = Calendar.getInstance();
		return cal.getTimeInMillis() / 1000;
	}

	public static void main(String[] args) {

		String test = "123:hello go  to bed";
		String key = "123456";
		String afStr = AuthCodeUtil.encode(test, key);
		System.out.println("--------encode:");
		System.out.println(afStr);
		System.out.println(decode(afStr, key));
		long lStart = System.currentTimeMillis();
		System.out.println("解码后：" + AuthCodeUtil.decode(afStr, key));
		long lUseTime = System.currentTimeMillis() - lStart;
		System.out.println("加解密耗时：" + lUseTime + "毫秒");
		String deStr = AuthCodeUtil
				.decode(
						"0084tuF6jOu8bVvO//fcV6fXL/CCcUYVJby2nQOofjRasbvrqYNupR6eQJ2rDnhh1XvxWTft4Ub5TSdZA2Y3Ts0yhH8UrziYy5dXl3MHC5freHTOdAfgfFofcnQvLwo+BvD1hT7J9qw57Ral4NC+KNTc/Vj1CzPpftA5P6qUO3KB",
						key);
		System.out.println("--------decode:");
		System.out.println(deStr);
	}

}

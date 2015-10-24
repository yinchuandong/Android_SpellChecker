package com.yin.spellchecker.main;

/**
 * Created by yinchuandong on 15/10/23.
 */
public class C {
    public final static class api{
//        public final static String base = "http://192.168.10.104/php_spellchecker/index.php/";//company
//        public final static String base = "http://192.168.1.106/php_spellchecker/index.php/";//home
        public final static String base = "http://spellapi.superlib.cn/index.php/";//server

        public final static String correct = base + "Index/correct";
        public final static String register = base + "Index/register";
    }

    public final static class sp{
        public final static String KEY_SIGN = "is_signed";
    }
}

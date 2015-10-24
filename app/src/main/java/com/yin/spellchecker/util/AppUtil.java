package com.yin.spellchecker.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by yinchuandong on 15/10/24.
 */
public class AppUtil {
    public static String getLocalMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    public static String getToken(Context context, String key, String secret, long curTimeMillis){
        String mac = getLocalMacAddress(context);
        String source = secret + "-" + mac + "-" + curTimeMillis;
        return AuthCodeUtil.encode(source, key);
    }

    public static SharedPreferences getSharedPreferences(Context ctx) {
        return ctx.getSharedPreferences("com.yin.spellchecker.sp.global",
                Context.MODE_PRIVATE);
    }

}

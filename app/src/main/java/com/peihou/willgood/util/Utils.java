package com.peihou.willgood.util;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utils {

    private static SimpleDateFormat sdf = null;
    public  static String formatUTC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        if (sdf == null) {
            try {
                sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
            } catch (Throwable e) {
            }
        } else {
            sdf.applyPattern(strPattern);
        }
        return sdf == null ? "NULL" : sdf.format(l);
    }
    /**
     * 判断一个字符串是否是数字类型的
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        try {
            double num=Double.parseDouble(str);//把字符串强制转换为数字
            return true;//如果是数字，返回True
        } catch (Exception e) {
            return false;//如果抛出异常，返回False
        }
    }
}

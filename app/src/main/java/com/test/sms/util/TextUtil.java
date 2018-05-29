package com.test.sms.util;

/**
 * Created by 孙科技 on 2018/5/8.
 */
public class TextUtil {
    public static String proText(String text) {
        String newText = text.replace("+86", "");
        return newText;
    }

    public static void main(String[] args) {
        System.out.print(proText("13308334117+8"));
    }
}

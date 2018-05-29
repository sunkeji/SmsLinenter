package com.test.sms.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.test.sms.app.MyApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

/**
 * Created by 孙科技 on 2017/10/31.
 */

public class SPUtil {
    /**
     * 保存在手机里面的文件名
     */
    public static final String APP_COMMON_FILE_NAME = "yicai";

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public static boolean saveToApp(String key, Object object) {
        SharedPreferences sp = MyApplication.mContext
                .getSharedPreferences(APP_COMMON_FILE_NAME,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else if (object instanceof Set) {
            editor.putStringSet(key, (Set<String>) object);
        } else {
            editor.putString(key, "");
        }
        return editor.commit();
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object getFromApp(String key, Object defaultObject) {
        SharedPreferences sp = MyApplication.mContext
                .getSharedPreferences(APP_COMMON_FILE_NAME,
                        Context.MODE_PRIVATE);
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        } else if (defaultObject instanceof Set) {
            return sp.getStringSet(key, ((Set<String>) defaultObject));
        }
        return null;
    }

    /**
     * 序列化对象
     *
     * @return
     * @throws IOException
     */
    public static String serialize(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        String serStr = byteArrayOutputStream.toString("ISO-8859-1");
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
        objectOutputStream.close();
        byteArrayOutputStream.close();
        return serStr;
    }

    /**
     * 反序列化对象
     *
     * @param str
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deSerialization(String str) throws IOException, ClassNotFoundException {
        if (TextUtils.isEmpty(str))
            return null;
        String redStr = java.net.URLDecoder.decode(str, "UTF-8");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object object = (Object) objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();
        return object;
    }


    /**
     * 获取手机号
     *
     * @return
     */
    public static String getPhoneA() {
        String phone = (String) getFromApp("phoneA", "");
        if (TextUtils.isEmpty(phone)) {
            return "";
        }
        return phone;
    }

    /**
     * 获取手机号
     *
     * @return
     */
    public static String getPhoneD() {
        String phone = (String) getFromApp("phoneD", "");
        if (TextUtils.isEmpty(phone)) {
            return "";
        }
        return phone;
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param key
     */
    public static void remove(String key) {
        try {
            SharedPreferences sp = MyApplication.mContext.getSharedPreferences(
                    APP_COMMON_FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(key);
            editor.commit();
        } catch (Exception e) {
        }
    }
}

package com.test.sms.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by 孙科技 on 2018/5/4.
 */
public class MyApplication extends Application {
    private static MyApplication app;
    public static Context mContext;

    public synchronized static MyApplication getInstance() {
        if (app == null)
            app = new MyApplication();
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

    }



}
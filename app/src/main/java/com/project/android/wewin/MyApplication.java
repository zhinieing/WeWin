package com.project.android.wewin;

import android.app.Application;
import android.content.Context;

import com.project.android.wewin.data.local.db.AppDatabaseManager;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

import cn.bmob.v3.Bmob;


/**
 * Created by pengming on 2017/11/24.
 */

public class MyApplication extends Application{

    private static MyApplication INSTANCE = null;
    private static Context context;

    public static MyApplication getInstance() {
        return INSTANCE;
    }

    public static Context getContext() {
        return context;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        context = getApplicationContext();

        AppDatabaseManager.getInstance().createDB(this);

        Bmob.initialize(this, "b69feccd348afa82300261a7e587e7f2", "bmob");

        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://wd7253182455nvohji.wilddogio.com").build();
        WilddogApp.initializeApp(this, options);
    }
}

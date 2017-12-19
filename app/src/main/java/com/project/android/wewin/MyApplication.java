package com.project.android.wewin;

import android.app.Application;
import android.content.Context;

import com.project.android.wewin.data.local.db.AppDatabaseManager;

/**
 * Created by pengming on 2017/11/24.
 */

public class MyApplication extends Application{

    private static MyApplication INSTANCE = null;
    //private static Context context;

    public static MyApplication getInstance() {
        return INSTANCE;
    }

    /*public static Context getContext() {
        return context;
    }*/


    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        //context = getApplicationContext();
        AppDatabaseManager.getInstance().createDB(this);
    }
}

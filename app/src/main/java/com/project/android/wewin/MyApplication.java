package com.project.android.wewin;

import android.app.Application;
import android.content.Context;

import com.project.android.wewin.data.local.db.AppDatabaseManager;
import com.project.android.wewin.utils.Constants;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.bmob.v3.Bmob;


/**
 *
 * @author pengming
 * @date 2017/11/24
 */

public class MyApplication extends Application{

    private static MyApplication INSTANCE = null;
    private static Context context;
    public static IWXAPI api;

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

        Bmob.initialize(this, Constants.BMOB_APPID, "bmob");

        register2WX();
    }

    public void register2WX() {
        api = WXAPIFactory.createWXAPI(this, Constants.WEIXIN_APP_ID, false);
        api.registerApp(Constants.WEIXIN_APP_ID);
    }
}

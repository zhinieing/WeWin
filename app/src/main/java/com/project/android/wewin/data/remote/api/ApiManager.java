package com.project.android.wewin.data.remote.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pengming on 2017/11/24.
 */

public class ApiManager {

    private static final String BASE_URL = "http://www.a4print.cn/WeWin/";

    private static ApiManager INSTANCE;

    private static ApiOpenid sApiOpenid;

    private static ApiClass sApiClass;

    private static ApiQiniu sApiQiniu;

    private ApiManager() {
    }

    public static ApiManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ApiManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ApiManager();
                }
            }
        }
        return INSTANCE;
    }

    public ApiOpenid getApiOpenid() {
        if (sApiOpenid == null) {
            synchronized (ApiManager.class) {
                if (sApiOpenid == null) {
                    sApiOpenid = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(ApiOpenid.class);
                }
            }
        }
        return sApiOpenid;
    }

    public ApiClass getApiClass() {
        if (sApiClass == null) {
            synchronized (ApiManager.class) {
                if (sApiClass == null) {
                    sApiClass = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(ApiClass.class);
                }
            }
        }
        return sApiClass;
    }

    public ApiQiniu getApiQiniu() {
        if (sApiQiniu == null) {
            synchronized (ApiManager.class) {
                if (sApiQiniu == null) {
                    sApiQiniu = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(ApiQiniu.class);
                }
            }
        }
        return sApiQiniu;
    }
}

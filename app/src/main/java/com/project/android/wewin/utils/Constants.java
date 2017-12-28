package com.project.android.wewin.utils;

import com.project.android.wewin.MyApplication;
import com.project.android.wewin.R;

import java.nio.charset.Charset;

/**
 * SDK相关配置常量
 */
public final class Constants {

    public static final String[] CAMERA_GALLERY = {MyApplication.getContext().getString(R.string.alert_choose_from_camera), MyApplication.getContext().getString(R.string.alert_choose_from_gallery)};


    /**
     *  此为Bmob的APP_ID
     */
    public static final String BMOB_APPID = "b69feccd348afa82300261a7e587e7f2";

    /**
     *  微信平台的APPID,请自行前往微信开放平台注册申请应用
     */
    public static final String WEIXIN_APP_ID ="wx7ba5bc5eca6d2d94";

    /**
     * 微信平台的AppSecret
     */
    public static final String WEIXIN_APP_SECRET ="151f2902ebfceaa00eafa026d25e688c";

    /**
     *  微信平台的grant type，固定值：authorization_code
     */
    public static final String WEIXIN_GRANT_TYPE ="authorization_code";


    private Constants() {
    }
}


package com.project.android.wewin.utils;

import com.project.android.wewin.MyApplication;
import com.project.android.wewin.R;

import java.nio.charset.Charset;

/**
 * SDK相关配置常量
 */
public final class Constants {
    /**
     * 版本号
     */
    public static final String VERSION = "7.2.7";
    /**
     * 块大小，不能改变
     */
    public static final int BLOCK_SIZE = 4 * 1024 * 1024;
    /**
     * 所有都是UTF-8编码
     */
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    /**
     * 连接超时时间 单位秒(默认10s)
     */
    public static final int CONNECT_TIMEOUT = 10;
    /**
     * 写超时时间 单位秒(默认 0 , 不超时)
     */
    public static final int WRITE_TIMEOUT = 0;
    /**
     * 回复超时时间 单位秒(默认30s)
     */
    public static final int READ_TIMEOUT = 30;
    /**
     * 底层HTTP库所有的并发执行的请求数量
     */
    public static final int DISPATCHER_MAX_REQUESTS = 64;
    /**
     * 底层HTTP库对每个独立的Host进行并发请求的数量
     */
    public static final int DISPATCHER_MAX_REQUESTS_PER_HOST = 16;
    /**
     * 底层HTTP库中复用连接对象的最大空闲数量
     */
    public static final int CONNECTION_POOL_MAX_IDLE_COUNT = 32;
    /**
     * 底层HTTP库中复用连接对象的回收周期（单位分钟）
     */
    public static final int CONNECTION_POOL_MAX_IDLE_MINUTES = 5;

    public static final String[] CAMERA_GALLERY = {MyApplication.getContext().getString(R.string.alert_choose_from_camera), MyApplication.getContext().getString(R.string.alert_choose_from_gallery)};


    /**
     *  此为七牛的AccessKey和SecretKey
     */
    public static final String AccessKey = "j9I4TPsY2WW0uVdi6vBFAp_svIkf05jur6tYT6_G";
    public static final String SecretKey = "7oEW82Mfv9KI6mhOHdfad_yqsLdphUKEwxhwVkwQ";

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
    public static final String WEIXIN_APP_SECRET ="a964c2d1bcc6f562130f31b208e5d2b7";

    /**
     *  微信平台的grant type，固定值：authorization_code
     */
    public static final String WEIXIN_GRANT_TYPE ="authorization_code";


    private Constants() {
    }
}


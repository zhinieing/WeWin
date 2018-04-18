package com.project.android.wewin.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by zhoutao on 2018/4/18.
 */

public class L {
    private static String TAG = "WeWin";

    public static void initTAG(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        TAG = tag;
    }

    /**
     * log.d
     *
     * @param msg
     */
    public static void d(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Log.d(TAG, msg);
    }

    /**
     * log.e
     *
     * @param msg
     */
    public static void e(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Log.e(TAG, msg);
    }

    /**
     * log.e
     *
     * @param msg
     */
    public static void e(String msg, Throwable throwable) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Log.e(TAG, msg, throwable);
    }

    /**
     * log.w
     *
     * @param msg
     */
    public static void w(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Log.w(TAG, msg);
    }

    /**
     * Log.i
     *
     * @param msg
     */
    public static void i(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Log.i(TAG, msg);
    }

    /**
     * log.v
     *
     * @param msg
     */
    public static void v(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Log.v(TAG, msg);
    }
}

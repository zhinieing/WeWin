package com.project.android.wewin.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.project.android.wewin.MyApplication;
import com.project.android.wewin.R;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by pengming on 18/12/2017.
 */

public class Util {
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static void loadCircleImage(Uri imageUri, ImageView imageView){
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_person_img)
                .error(R.drawable.ic_person_img);

        Glide.with(MyApplication.getContext())
                .load(imageUri)
                .apply(requestOptions)
                .apply(RequestOptions.bitmapTransform(new CropCircleTransformation()))
                .into(imageView);
    }

    public static void showSnackbar(View parentView, String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Snackbar.make(parentView, msg, Snackbar.LENGTH_LONG).show();
    }
}

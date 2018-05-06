package com.project.android.wewin.data.remote.api;


import com.project.android.wewin.data.remote.model.Uptoken;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * @author pengming
 */
public interface ApiQiniu {

    @GET("access_qiniutoken")
    Call<Uptoken> getQiniuToken();

    @GET("access_downloadtoken")
    Call<String> getDownloadToken();
}

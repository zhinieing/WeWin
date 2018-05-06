package com.project.android.wewin.data.remote.api;

import com.project.android.wewin.data.remote.model.ClassData;
import com.project.android.wewin.data.remote.model.OpenidData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiOpenid {

    @GET("phoneToId")
    Call<OpenidData> getOpenid(@Query("phoneno") String phoneno);
}

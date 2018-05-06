package com.project.android.wewin.data.remote.api;

import com.project.android.wewin.data.remote.model.ClassData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiUser {

    @GET("showuserinfo")
    Call<ClassData> getUser(@Query("openid") String openid);

    @PUT("edituserinfo")
    Call<ClassData> updateUser(@Query("openid") String openid,
                               @Query("nickname") String nickname,
                               @Query("school") String school,
                               @Query("studentno") String studentno,
                               @Query("phoneno") String phoneno);
}

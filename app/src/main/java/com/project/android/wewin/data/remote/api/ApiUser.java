package com.project.android.wewin.data.remote.api;

import com.project.android.wewin.data.remote.model.ResultData;
import com.project.android.wewin.data.remote.model.UserInfoData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiUser {

    @GET("showuserinfo")
    Call<UserInfoData> getUser(@Query("openid") String openid);

    @PUT("edituserinfo")
    Call<ResultData> updateUser(@Query("openid") String openid,
                                @Query("nickname") String nickname,
                                @Query("school") String school,
                                @Query("studentno") String studentno,
                                @Query("phoneno") String phoneno);
}

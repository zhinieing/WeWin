package com.project.android.wewin.data.remote.api;

import com.project.android.wewin.data.remote.model.ClassData;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiHomework {

    @POST("homework_fb")
    Call<ClassData> postClass(@Query("openid") String openid,
                              @Query("classNo") Integer classNo,
                              @Query("title") String title,
                              @Query("endTime") String endTime);
}

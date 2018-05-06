package com.project.android.wewin.data.remote.api;

import com.project.android.wewin.data.remote.model.ClassData;
import com.project.android.wewin.data.remote.model.OpenidData;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiClass {

    @GET("class/findclasses_create")
    Call<ClassData> getClassesCreate(@Query("openid") String openid);

    @GET("class/findclasses_join")
    Call<ClassData> getClassesJoin(@Query("openid") String openid);

    @POST("class/new/addclass")
    Call<OpenidData> postClass(@Query("classname") String classname,
                               @Query("creatorid") String creatorid,
                               @Query("iconpath") String iconpath);

    @GET("class/setting/findclass")
    Call<ClassData> getClass(@Query("classId") Integer classId);

    @POST("class/setting/update")
    Call<OpenidData> updateClass(@Query("classname") String classname,
                                @Query("classId") Integer classId,
                                @Query("iconpath") String iconpath);

    @POST("class/deleteclass")
    Call<OpenidData> deleteClass(@Query("classId") Integer classId);

}

package com.project.android.wewin.data.remote.api;

import com.project.android.wewin.data.remote.model.ClassData;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiMember {

    @GET("findgroups")
    Call<ClassData> getGroups(@Query("classId") Integer classId);

    @GET("findusers")
    Call<ClassData> getUsers(@Query("groupId") Integer groupId);

    @POST("addgroup")
    Call<ClassData> postGroup(@Query("groupName") String groupName,
                              @Query("classId") Integer classId);

    @DELETE("deletegroup")
    Call<ClassData> deleteGroups(@Query("groupIdList") String[] groupIdList);

    @POST("addgroup_members")
    Call<ClassData> postGroupMembers(@Query("groupId") Integer groupId,
                                     @Query("openIdList") String[] openIdList);

    @DELETE("deletemember")
    Call<ClassData> deleteGroupMember(@Query("groupId") Integer groupId,
                                      @Query("openid") String openid);

    @GET("findmember_ingroup")
    Call<ClassData> getGroupMember(@Query("classId") Integer classId,
                                   @Query("openid") String openid);

    @GET("findgroup_and_members")
    Call<ClassData> getGroupAndMembers(@Query("classId") Integer classId);
}

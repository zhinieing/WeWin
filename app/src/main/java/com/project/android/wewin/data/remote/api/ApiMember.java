package com.project.android.wewin.data.remote.api;

import com.project.android.wewin.data.remote.model.GroupData;
import com.project.android.wewin.data.remote.model.GroupWithUserData;
import com.project.android.wewin.data.remote.model.ResultData;
import com.project.android.wewin.data.remote.model.UserData;
import com.project.android.wewin.data.remote.model.UserInfoData;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiMember {

    @GET("member/findgroups")
    Call<GroupData> getGroups(@Query("classId") Integer classId);

    @GET("member/findusers")
    Call<UserData> getGroupMembers(@Query("groupId") Integer groupId);

    @POST("member/addgroup")
    Call<ResultData> postGroup(@Query("groupName") String groupName,
                               @Query("classId") Integer classId);

    @POST("member/deletegroup")
    Call<ResultData> deleteGroups(@Query("groupIdList") Integer[] groupIdList);

    @POST("member/addgroup_members")
    Call<ResultData> postGroupMembers(@Query("groupId") Integer groupId,
                                      @Query("openIdList") String[] openIdList);

    @POST("member/deletemember")
    Call<ResultData> deleteGroupMember(@Query("groupId") Integer groupId,
                                       @Query("openid") String openid);

    @GET("member/findmember_ingroup")
    Call<UserInfoData> getGroupMember(@Query("classId") Integer classId,
                                      @Query("openid") String openid);

    @GET("member/findgroup_and_members")
    Call<GroupWithUserData> getGroupAndMembers(@Query("classId") Integer classId);
}

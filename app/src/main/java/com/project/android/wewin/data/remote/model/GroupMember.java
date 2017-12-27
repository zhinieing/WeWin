package com.project.android.wewin.data.remote.model;

import cn.bmob.v3.BmobObject;

/**
 *
 * @author pengming
 * @date 26/12/2017
 */

public class GroupMember extends BmobObject{
    private String userId;
    private String userName;
    private String userPhoto;
    private String userEmail;
    private String groupId;

    public GroupMember() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}

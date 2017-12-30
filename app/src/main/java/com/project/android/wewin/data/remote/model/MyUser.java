package com.project.android.wewin.data.remote.model;

import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by pengming on 28/12/2017.
 */

public class MyUser extends BmobUser {
    private String userPhoto;
    private List<Class> mClasses;
    private List<String> groupIds;

    public MyUser() {
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public List<Class> getmClasses() {
        return mClasses;
    }

    public void setmClasses(List<Class> mClasses) {
        this.mClasses = mClasses;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }
}

package com.project.android.wewin.data.remote.model;

import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by pengming on 28/12/2017.
 */

public class MyUser extends BmobUser {
    private String userPhoto;


    public MyUser() {
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

}

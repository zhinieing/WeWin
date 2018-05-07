package com.project.android.wewin.data.remote.model;

import com.project.android.wewin.data.local.db.entity.UserInfo;


/**
 * @author pengming
 */
public class UserInfoData {

    public final int state;

    public final String message;

    public final UserInfo data;

    public UserInfoData(int state, String message, UserInfo data) {
        this.state = state;
        this.message = message;
        this.data = data;
    }
}

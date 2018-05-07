package com.project.android.wewin.data.remote.model;

import com.project.android.wewin.data.local.db.entity.UserInfo;

import java.util.List;

/**
 * @author pengming
 */
public class UserData {

    public final int state;

    public final String message;

    public final List<UserInfo> data;

    public UserData(int state, String message, List<UserInfo> data) {
        this.state = state;
        this.message = message;
        this.data = data;
    }
}

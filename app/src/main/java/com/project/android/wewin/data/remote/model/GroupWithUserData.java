package com.project.android.wewin.data.remote.model;

import com.project.android.wewin.data.local.db.entity.GroupWithUser;

import java.util.List;

/**
 * @author pengming
 */
public class GroupWithUserData {

    public final int state;

    public final String message;

    public final List<GroupWithUser> data;

    public GroupWithUserData(int state, String message, List<GroupWithUser> data) {
        this.state = state;
        this.message = message;
        this.data = data;
    }
}

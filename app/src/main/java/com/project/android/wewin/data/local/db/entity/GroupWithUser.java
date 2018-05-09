package com.project.android.wewin.data.local.db.entity;

import java.util.List;

/**
 * @author pengming
 */
public class GroupWithUser extends Group {

    private List<UserInfo> users;

    public GroupWithUser() {}

    public List<UserInfo> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfo> users) {
        this.users = users;
    }
}

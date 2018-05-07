package com.project.android.wewin.data.remote.model;


import com.project.android.wewin.data.local.db.entity.GroupInfo;

import java.util.List;

/**
 * @author pengming
 */
public class GroupData {

    public final int state;

    public final String message;

    public final List<GroupInfo> data;

    public GroupData(int state, String message, List<GroupInfo> data) {
        this.state = state;
        this.message = message;
        this.data = data;
    }
}

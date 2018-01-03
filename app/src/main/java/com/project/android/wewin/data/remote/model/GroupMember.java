package com.project.android.wewin.data.remote.model;

import cn.bmob.v3.BmobObject;

/**
 *
 * @author pengming
 * @date 26/12/2017
 */

public class GroupMember extends BmobObject{
    private MyUser memberUser;
    private GroupInfo targetGroupInfo;

    public GroupMember() {}

    public MyUser getMemberUser() {
        return memberUser;
    }

    public void setMemberUser(MyUser memberUser) {
        this.memberUser = memberUser;
    }

    public GroupInfo getTargetGroupInfo() {
        return targetGroupInfo;
    }

    public void setTargetGroupInfo(GroupInfo targetGroupInfo) {
        this.targetGroupInfo = targetGroupInfo;
    }
}

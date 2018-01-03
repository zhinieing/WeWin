package com.project.android.wewin.data.remote.model;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 *
 * @author pengming
 * @date 26/12/2017
 */

public class GroupInfo extends BmobObject{
    private String groupName;
    private Integer memberSize;
    private Integer auth;
    private Class targetClass;
    private List<GroupMember> groupMembers;

    public GroupInfo() {}

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getMemberSize() {
        return memberSize;
    }

    public void setMemberSize(Integer memberSize) {
        this.memberSize = memberSize;
    }

    public Integer getAuth() {
        return auth;
    }

    public void setAuth(Integer auth) {
        this.auth = auth;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    public List<GroupMember> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<GroupMember> groupMembers) {
        this.groupMembers = groupMembers;
    }
}

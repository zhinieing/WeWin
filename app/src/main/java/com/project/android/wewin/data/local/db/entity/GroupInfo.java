package com.project.android.wewin.data.local.db.entity;

import java.io.Serializable;

/**
 * @author pengming
 */
public class GroupInfo implements Serializable{

    private Integer groupId;

    private String groupName;

    private Integer classId;

    private Integer memberSize;

    private Integer groupAuth;


    public GroupInfo() {}

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getMemberSize() {
        return memberSize;
    }

    public void setMemberSize(Integer memberSize) {
        this.memberSize = memberSize;
    }

    public Integer getGroupAuth() {
        return groupAuth;
    }

    public void setGroupAuth(Integer groupAuth) {
        this.groupAuth = groupAuth;
    }
}

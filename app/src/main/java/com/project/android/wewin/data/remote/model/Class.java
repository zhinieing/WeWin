package com.project.android.wewin.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * @author pengming
 * @date 26/12/2017
 */

public class Class extends BmobObject {
    private String className;
    private String classIcon;
    private Integer studentSize;
    private Integer teacherSize;
    private MyUser creatorUser;
    private List<GroupInfo> groupInfos;

    public Class() {
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassIcon() {
        return classIcon;
    }

    public void setClassIcon(String classIcon) {
        this.classIcon = classIcon;
    }

    public Integer getStudentSize() {
        return studentSize;
    }

    public void setStudentSize(Integer studentSize) {
        this.studentSize = studentSize;
    }

    public Integer getTeacherSize() {
        return teacherSize;
    }

    public void setTeacherSize(Integer teacherSize) {
        this.teacherSize = teacherSize;
    }

    public List<GroupInfo> getGroupInfos() {
        return groupInfos;
    }

    public void setGroupInfos(List<GroupInfo> groupInfos) {
        this.groupInfos = groupInfos;
    }

    public MyUser getCreatorUser() {
        return creatorUser;
    }

    public void setCreatorUser(MyUser creatorUser) {
        this.creatorUser = creatorUser;
    }

}

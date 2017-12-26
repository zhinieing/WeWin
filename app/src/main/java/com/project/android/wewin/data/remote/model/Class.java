package com.project.android.wewin.data.remote.model;

import java.util.List;

/**
 * Created by pengming on 26/12/2017.
 */

public class Class {
    private String className;
    private String creatorId;
    private String classIcon;
    private Integer studentSize;
    private Integer teacherSize;
    private List<GroupInfo> groupInfos;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
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
}

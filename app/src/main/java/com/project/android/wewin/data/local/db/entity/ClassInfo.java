package com.project.android.wewin.data.local.db.entity;

import java.io.Serializable;

public class ClassInfo implements Serializable{

    private Integer classId;

    private String className;

    private String creatorid;

    private String createTime;

    private String classIcon;

    private Integer studentSize;

    private Integer teacherSize;

    public ClassInfo() {}

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCreatorid() {
        return creatorid;
    }

    public void setCreatorid(String creatorid) {
        this.creatorid = creatorid;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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
}

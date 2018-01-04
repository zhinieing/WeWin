package com.project.android.wewin.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * @author pengming
 * @date 26/12/2017
 */

public class Class extends BmobObject implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(className);
        parcel.writeString(classIcon);
        parcel.writeInt(studentSize);
        parcel.writeInt(teacherSize);
        parcel.writeParcelable(creatorUser, i);
    }

    public static final Parcelable.Creator<Class> CREATOR = new Parcelable.Creator<Class>() {
        @Override
        public Class createFromParcel(Parcel parcel) {
            Class mClass = new Class();
            mClass.className = parcel.readString();
            mClass.classIcon = parcel.readString();
            mClass.studentSize = parcel.readInt();
            mClass.teacherSize = parcel.readInt();
            mClass.creatorUser = parcel.readParcelable(MyUser.class.getClassLoader());
            return mClass;
        }

        @Override
        public Class[] newArray(int i) {
            return new Class[i];
        }
    };

    public static Creator<Class> getCREATOR() {
        return CREATOR;
    }
}

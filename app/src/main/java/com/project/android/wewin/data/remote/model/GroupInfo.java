package com.project.android.wewin.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * @author pengming
 * @date 26/12/2017
 */

public class GroupInfo extends BmobObject {
    private String groupName;
    private Integer memberSize;
    private Integer auth;
    private Class targetClass;
    private List<GroupMember> groupMembers;

    public GroupInfo() {
    }

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

   /* @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(groupName);
        parcel.writeInt(memberSize);
        parcel.writeInt(auth);
        parcel.writeParcelable(targetClass, i);
    }

    public static final Parcelable.Creator<GroupInfo> CREATOR = new Parcelable.Creator<GroupInfo>() {
        @Override
        public GroupInfo createFromParcel(Parcel parcel) {
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.groupName = parcel.readString();
            groupInfo.memberSize = parcel.readInt();
            groupInfo.auth = parcel.readInt();
            groupInfo.targetClass = parcel.readParcelable(Class.class.getClassLoader());
            return groupInfo;
        }

        @Override
        public GroupInfo[] newArray(int i) {
            return new GroupInfo[i];
        }
    };

    public static Creator<GroupInfo> getCREATOR() {
        return CREATOR;
    }*/
}

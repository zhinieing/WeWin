package com.project.android.wewin.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * HomeWork class
 *
 * @author zhoutao
 * @date 2017/12/11
 */

public class HomeWork extends BmobObject {
    private String homeworkTitle;
    private String homeworkContent;
    private BmobDate homeworkDeadline;
    private MyUser creatorUser;
    private GroupInfo groupInfo;
    private List<String> attachmentPath;

    public HomeWork() {
    }


    public String getHomeworkTitle() {
        return homeworkTitle;
    }

    public void setHomeworkTitle(String homeworkTitle) {
        this.homeworkTitle = homeworkTitle;
    }

    public String getHomeworkContent() {
        return homeworkContent;
    }

    public void setHomeworkContent(String homeworkContent) {
        this.homeworkContent = homeworkContent;
    }

    public BmobDate getHomeworkDeadline() {
        return homeworkDeadline;
    }

    public void setHomeworkDeadline(BmobDate homeworkDeadline) {
        this.homeworkDeadline = homeworkDeadline;
    }

    public MyUser getCreatorUser() {
        return creatorUser;
    }

    public void setCreatorUser(MyUser creatorUser) {
        this.creatorUser = creatorUser;
    }

    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public List<String> getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(List<String> attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

/*
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(homeworkTitle);
        parcel.writeString(homeworkContent);
        parcel.writeString(homeworkDeadline);
        parcel.writeParcelable(creatorUser, i);
        //parcel.writeParcelable(groupInfo, i);
        parcel.writeStringList(attachmentPath);
    }

    public static final Parcelable.Creator<HomeWork> CREATOR = new Parcelable.Creator<HomeWork>() {

        @Override
        public HomeWork createFromParcel(Parcel parcel) {
            HomeWork homeWork = new HomeWork();
            homeWork.homeworkTitle = parcel.readString();
            homeWork.homeworkContent = parcel.readString();
            homeWork.homeworkDeadline = parcel.readString();
            homeWork.creatorUser = parcel.readParcelable(MyUser.class.getClassLoader());
            //homeWork.groupInfo = parcel.readParcelable(GroupInfo.class.getClassLoader());
            homeWork.attachmentPath = new ArrayList<String>();
            parcel.readStringList(homeWork.attachmentPath);
            return homeWork;
        }

        @Override
        public HomeWork[] newArray(int i) {
            return new HomeWork[i];
        }
    };

    public static Creator<HomeWork> getCREATOR() {
        return CREATOR;
    }*/
}

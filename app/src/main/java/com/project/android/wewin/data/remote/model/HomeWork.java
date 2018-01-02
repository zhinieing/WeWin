package com.project.android.wewin.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * HomeWork class
 *
 * @author zhoutao
 * @date 2017/12/11
 */

public class HomeWork extends BmobObject implements Parcelable {
    private String homeworkTitle;
    private String homeworkContent;
    private String homeworkDeadline;
    private String creatorId;
    private String creatorName;
    private String creatorPhoto;
    private String groupId;
    private List<String> attachmentPath;

    public HomeWork() {
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorPhoto() {
        return creatorPhoto;
    }

    public void setCreatorPhoto(String creatorPhoto) {
        this.creatorPhoto = creatorPhoto;
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

    public String getHomeworkDeadline() {
        return homeworkDeadline;
    }

    public void setHomeworkDeadline(String homeworkDeadline) {
        this.homeworkDeadline = homeworkDeadline;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public List<String> getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(List<String> attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(homeworkTitle);
        parcel.writeString(homeworkContent);
        parcel.writeString(homeworkDeadline);
        parcel.writeString(creatorId);
        parcel.writeString(creatorName);
        parcel.writeString(creatorPhoto);
        parcel.writeString(groupId);
        parcel.writeStringList(attachmentPath);
    }

    public static final Parcelable.Creator<HomeWork> CREATOR = new Parcelable.Creator<HomeWork>() {

        @Override
        public HomeWork createFromParcel(Parcel parcel) {
            HomeWork homeWork = new HomeWork();
            homeWork.homeworkTitle = parcel.readString();
            homeWork.homeworkContent = parcel.readString();
            homeWork.homeworkDeadline = parcel.readString();
            homeWork.creatorId = parcel.readString();
            homeWork.creatorName = parcel.readString();
            homeWork.creatorPhoto = parcel.readString();
            homeWork.groupId = parcel.readString();
            homeWork.attachmentPath = new ArrayList<String>();
            parcel.readStringList(homeWork.attachmentPath);
            return homeWork;
        }

        @Override
        public HomeWork[] newArray(int i) {
            return new HomeWork[i];
        }
    };
}

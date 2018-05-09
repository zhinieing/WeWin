package com.project.android.wewin.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;

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
    private List<BmobFile> attachmentPath;

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

    public List<BmobFile> getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(List<BmobFile> attachmentPath) {
        this.attachmentPath = attachmentPath;
    }


}

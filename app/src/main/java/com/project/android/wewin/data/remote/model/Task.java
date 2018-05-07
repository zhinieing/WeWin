package com.project.android.wewin.data.remote.model;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by zhoutao on 2018/4/18.
 */

public class Task extends BmobObject {
    private String taskTitle;

    private String taskContent;

    private BmobDate taskDeadline;

    private MyUser creatorUser;

    private MyUser receiverUser;

    private String taskCategory;

    private boolean completed;

    private double taskReward;

    private List<BmobFile> attachmentPath;

    public Task() {
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public BmobDate getTaskDeadline() {
        return taskDeadline;
    }

    public void setTaskDeadline(BmobDate taskDeadline) {
        this.taskDeadline = taskDeadline;
    }

    public MyUser getCreatorUser() {
        return creatorUser;
    }

    public void setCreatorUser(MyUser creatorUser) {
        this.creatorUser = creatorUser;
    }

    public MyUser getReceiverUser() {
        return receiverUser;
    }

    public void setReceiverUser(MyUser receiverUser) {
        this.receiverUser = receiverUser;
    }

    public String getTaskCategory() {
        return taskCategory;
    }

    public void setTaskCategory(String taskCategory) {
        this.taskCategory = taskCategory;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public double getTaskReward() {
        return taskReward;
    }

    public void setTaskReward(double taskReward) {
        this.taskReward = taskReward;
    }

    public List<BmobFile> getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(List<BmobFile> attachmentPath) {
        this.attachmentPath = attachmentPath;
    }
}

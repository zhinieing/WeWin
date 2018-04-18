package com.project.android.wewin.data.local.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.sql.Date;
import java.util.List;

/**
 * Created by zhoutao on 2018/4/18.
 */

@Entity(tableName = "tasks")
public class TaskRoom {
    @NonNull
    @PrimaryKey
    private String id;

    private String taskTitle;

    private String taskContent;

    private String taskDeadline;

    private String creatorUser;

    private String taskCategory;

    private double taskReward;

//    private List<String> attachmentPath;

    public TaskRoom() {
    }

    private TaskRoom(TaskRoom taskRoom) {
        this.id = taskRoom.getId();
        this.taskTitle = taskRoom.getTaskTitle();
        this.taskContent = taskRoom.getTaskContent();
        this.taskDeadline = taskRoom.getTaskDeadline();
        this.creatorUser = taskRoom.getCreatorUser();
        this.taskCategory = taskRoom.getTaskCategory();
        this.taskReward = taskRoom.getTaskReward();
//        this.attachmentPath = taskRoom.getAttachmentPath();
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
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

    public String getTaskDeadline() {
        return taskDeadline;
    }

    public void setTaskDeadline(String taskDeadline) {
        this.taskDeadline = taskDeadline;
    }

    public String getCreatorUser() {
        return creatorUser;
    }

    public void setCreatorUser(String creatorUser) {
        this.creatorUser = creatorUser;
    }

    public String getTaskCategory() {
        return taskCategory;
    }

    public void setTaskCategory(String taskCategory) {
        this.taskCategory = taskCategory;
    }

    public double getTaskReward() {
        return taskReward;
    }

    public void setTaskReward(double taskReward) {
        this.taskReward = taskReward;
    }

//    public List<String> getAttachmentPath() {
//        return attachmentPath;
//    }
//
//    public void setAttachmentPath(List<String> attachmentPath) {
//        this.attachmentPath = attachmentPath;
//    }
}

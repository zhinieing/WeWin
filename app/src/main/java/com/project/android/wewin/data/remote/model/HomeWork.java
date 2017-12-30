package com.project.android.wewin.data.remote.model;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * HomeWork class
 *
 * @author zhoutao
 * @date 2017/12/11
 */

public class HomeWork extends BmobObject {
    private String homeworkTitle;
    private String homeworkContent;
    private String homeworkDeadline;
    private String creatorId;
    private Integer viewCount;
    private List<String> accachmentPath;
    private String groupId;

    public HomeWork() {
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
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

    public List<String> getAccachmentPath() {
        return accachmentPath;
    }

    public void setAccachmentPath(List<String> accachmentPath) {
        this.accachmentPath = accachmentPath;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}

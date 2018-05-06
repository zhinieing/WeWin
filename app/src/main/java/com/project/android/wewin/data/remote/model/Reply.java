package com.project.android.wewin.data.remote.model;

import cn.bmob.v3.BmobObject;

public class Reply extends BmobObject {
    private Task mTask;
    private MyUser creatorUser;
    private String content;
    private Integer bestReply;

    public Integer getBestReply() {
        return bestReply;
    }

    public void setBestReply(Integer bestReply) {
        this.bestReply = bestReply;
    }

    public Task getmTask() {
        return mTask;
    }

    public void setmTask(Task mTask) {
        this.mTask = mTask;
    }

    public MyUser getCreatorUser() {
        return creatorUser;
    }

    public void setCreatorUser(MyUser creatorUser) {
        this.creatorUser = creatorUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

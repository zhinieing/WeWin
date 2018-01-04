package com.project.android.wewin.data.remote.model;

import cn.bmob.v3.BmobObject;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Commit class
 *
 * @author zhoutao
 * @date 2018/1/4
 */

public class Commit extends BmobObject implements Parcelable {
    private HomeWork mHomework;
    private MyUser creatorUser;
    private List<String> attachmentPath;

    public HomeWork getmHomework() {
        return mHomework;
    }

    public void setmHomework(HomeWork mHomework) {
        this.mHomework = mHomework;
    }

    public MyUser getCreatorUser() {
        return creatorUser;
    }

    public void setCreatorUser(MyUser creatorUser) {
        this.creatorUser = creatorUser;
    }

    public List<String> getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(List<String> attachmentPath) {
        this.attachmentPath = attachmentPath;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(mHomework, i);
        parcel.writeParcelable(creatorUser, i);
        parcel.writeStringList(attachmentPath);
    }

    public static final Parcelable.Creator<Commit> CREATOR = new Creator<Commit>() {
        @Override
        public Commit createFromParcel(Parcel parcel) {
            Commit commit = new Commit();
            commit.mHomework = parcel.readParcelable(HomeWork.class.getClassLoader());
            commit.creatorUser = parcel.readParcelable(MyUser.class.getClassLoader());
            commit.attachmentPath = new ArrayList<String>();
            parcel.readStringList(commit.attachmentPath);
            return null;
        }

        @Override
        public Commit[] newArray(int i) {
            return new Commit[i];
        }
    };

    public static Creator<Commit> getCREATOR() {
        return CREATOR;
    }

}

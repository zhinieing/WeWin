package com.project.android.wewin.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by pengming on 28/12/2017.
 */

public class MyUser extends BmobUser {
    private String userPhoto;


    public MyUser() {
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

   /* @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(userPhoto);
    }

    public static final Parcelable.Creator<MyUser> CREATOR = new Parcelable.Creator<MyUser>() {

        @Override
        public MyUser createFromParcel(Parcel parcel) {
            MyUser myUser = new MyUser();
            myUser.userPhoto = parcel.readString();
            return myUser;
        }

        @Override
        public MyUser[] newArray(int i) {
            return new MyUser[i];
        }
    };

    public static Creator<MyUser> getCREATOR() {
        return CREATOR;
    }*/
}

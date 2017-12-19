package com.project.android.wewin.data.remote.model;

/**
 * Created by pengming on 2017/10/11.
 */

public class LoginEvent {
    private Boolean mStatus;

    public LoginEvent(Boolean mLoginSuccess){
        this.mStatus = mLoginSuccess;
    }

    public Boolean getmStatus() {
        return mStatus;
    }

    public void setmStatus(Boolean mStatus) {
        this.mStatus = mStatus;
    }
}

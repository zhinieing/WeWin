package com.project.android.wewin.data.remote.model;

/**
 * @author pengming
 */
public class ResultData {

    public final int state;

    public final String message;

    public final String data;


    public ResultData(int state, String message, String data) {
        this.state = state;
        this.message = message;
        this.data = data;
    }
}

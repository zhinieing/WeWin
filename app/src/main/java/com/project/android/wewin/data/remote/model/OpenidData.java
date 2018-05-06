package com.project.android.wewin.data.remote.model;

/**
 * @author pengming
 */
public class OpenidData {

    public final int state;

    public final String message;

    public final String data;


    public OpenidData(int state, String message, String data) {
        this.state = state;
        this.message = message;
        this.data = data;
    }
}

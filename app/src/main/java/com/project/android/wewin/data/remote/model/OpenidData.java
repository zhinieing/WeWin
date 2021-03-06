package com.project.android.wewin.data.remote.model;

import java.util.List;

public class OpenidData {

    public final int state;

    public final String message;

    public final List<String> data;

    public OpenidData(int state, String message, List<String> data) {
        this.state = state;
        this.message = message;
        this.data = data;
    }
}

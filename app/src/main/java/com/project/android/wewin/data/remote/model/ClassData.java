package com.project.android.wewin.data.remote.model;

import com.project.android.wewin.data.local.db.entity.ClassInfo;

import java.util.List;

/**
 * @author pengming
 */
public class ClassData {

    public final int state;

    public final String message;

    public final List<ClassInfo> data;

    public ClassData(int state, String message, List<ClassInfo> data) {
        this.state = state;
        this.message = message;
        this.data = data;
    }
}

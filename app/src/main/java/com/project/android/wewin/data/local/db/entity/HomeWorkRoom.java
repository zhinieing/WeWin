package com.project.android.wewin.data.local.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by pengming on 2017/11/24.
 */

@Entity(tableName = "homeworks")
public class HomeWorkRoom {
    @NonNull
    @PrimaryKey
    private String id;

    private int type;

    private String ga_prefix;

    private String title;

    private String[] images;

    public HomeWorkRoom() {
    }

    public HomeWorkRoom(HomeWorkRoom homeWorkRoom) {
        this.id = homeWorkRoom.getId();
        this.type = homeWorkRoom.getType();
        this.ga_prefix = homeWorkRoom.getGa_prefix();
        this.title = homeWorkRoom.getTitle();
        this.images = homeWorkRoom.getImages();
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getGa_prefix() {
        return ga_prefix;
    }

    public void setGa_prefix(String ga_prefix) {
        this.ga_prefix = ga_prefix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

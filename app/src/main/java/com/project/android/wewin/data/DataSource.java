package com.project.android.wewin.data;

import android.arch.lifecycle.LiveData;

import com.project.android.wewin.data.local.db.entity.HomeWork;

import java.util.List;

/**
 * Created by pengming on 2017/11/24.
 */

public interface DataSource {

    LiveData<List<HomeWork>> getHomeWorkList(int index);

    LiveData<Boolean> isLoadingHomeWorkList();

}

package com.project.android.wewin.data;

import android.arch.lifecycle.LiveData;

import com.project.android.wewin.data.remote.model.Class;
import com.project.android.wewin.data.remote.model.HomeWork;

import java.util.List;

/**
 * Created by pengming on 2017/11/24.
 */

public interface DataSource {

    LiveData<List<HomeWork>> getHomeWorkList(int index);

    LiveData<Boolean> isLoadingHomeWorkList();


    LiveData<List<Class>> getClassList();
    LiveData<Boolean> isLoadingClassList();
}

package com.project.android.wewin.data;

import android.arch.lifecycle.LiveData;

import com.project.android.wewin.data.remote.model.Class;
import com.project.android.wewin.data.remote.model.HomeWork;

import java.util.List;

/**
 * Created by pengming on 2017/11/24.
 */

public interface DataSource {

    /*主界面*/

    LiveData<List<HomeWork>> getHomeWorkList(int index);
    LiveData<Boolean> isLoadingHomeWorkList();
    LiveData<Boolean> isClassTeacher();

    /*通讯录*/

    LiveData<List<Class>> getClassList();
    LiveData<Boolean> isLoadingClassList();


    /*发布作业*/

    LiveData<List<Class>> getStudentClassList();

}

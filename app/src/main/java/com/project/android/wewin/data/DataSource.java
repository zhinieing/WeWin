package com.project.android.wewin.data;

import android.arch.lifecycle.LiveData;

import com.project.android.wewin.data.remote.model.Class;
import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.data.remote.model.Task;

import java.util.List;

/**
 *
 * @author pengming
 * @date 2017/11/24
 */

public interface DataSource {

    /*主界面*/

    LiveData<Boolean> isClassTeacher();

    LiveData<List<HomeWork>> getHomeWorkList(int index);
    LiveData<Boolean> isLoadingHomeWorkList();

    LiveData<List<HomeWork>> getPostedHomeWorkList(int index);
    LiveData<Boolean> isLoadingPostedHomeWorkList();



    /*通讯录*/

    LiveData<List<Class>> getClassList();
    LiveData<Boolean> isLoadingClassList();


    /*发布作业*/

    LiveData<List<Class>> getStudentClassList();


    /*发布任务*/

    LiveData<List<Task>> getTaskList(int index);
    LiveData<Boolean> isLoadingTaskList();

    LiveData<List<Task>> getPostedTaskList(int index);
    LiveData<Boolean> isLoadingPostedTaskList();

    LiveData<List<Task>> getReceivedTaskList(int index);
    LiveData<Boolean> isLoadingReceivedTaskList();
}

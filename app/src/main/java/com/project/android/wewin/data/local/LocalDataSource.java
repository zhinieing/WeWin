package com.project.android.wewin.data.local;

import android.arch.lifecycle.LiveData;

import com.project.android.wewin.data.DataSource;
import com.project.android.wewin.data.local.db.AppDatabase;
import com.project.android.wewin.data.local.db.AppDatabaseManager;
import com.project.android.wewin.data.remote.model.Class;
import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.data.remote.model.Task;

import java.util.List;

/**
 * Created by pengming on 2017/11/24.
 */

public class LocalDataSource implements DataSource{

    private static LocalDataSource INSTANCE = null;

    private LocalDataSource() {
    }

    public static LocalDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (LocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalDataSource();
                }
            }
        }
        return INSTANCE;
    }


    @Override
    public LiveData<List<HomeWork>> getHomeWorkList(int index) {
        return AppDatabaseManager.getInstance().loadHomeWorkList();
    }

    @Override
    public LiveData<Boolean> isLoadingHomeWorkList() {
        return AppDatabaseManager.getInstance().isLoadingHomeWorkList();
    }



    @Override
    public LiveData<List<HomeWork>> getPostedHomeWorkList(int index) {
        return null;
    }

    @Override
    public LiveData<Boolean> isLoadingPostedHomeWorkList() {
        return null;
    }


    @Override
    public LiveData<Boolean> isClassTeacher() {
        return null;
    }

    @Override
    public LiveData<List<Class>> getClassList() {
        return null;
    }

    @Override
    public LiveData<Boolean> isLoadingClassList() {
        return null;
    }

    @Override
    public LiveData<List<Class>> getStudentClassList() {
        return null;
    }


    /*发布作业*/

    @Override
    public LiveData<List<Task>> getTaskList(int index) {
        return AppDatabaseManager.getInstance().loadTaskList();
    }

    @Override
    public LiveData<Boolean> isLoadingTaskList() {
        return AppDatabaseManager.getInstance().isLoadingTaskList();
    }

    @Override
    public LiveData<List<Task>> getPostedTaskList(int index) {
        return null;
    }

    @Override
    public LiveData<Boolean> isLoadingPostedTaskList() {
        return null;
    }

    @Override
    public LiveData<List<Task>> getReceivedTaskList(int index) {
        return null;
    }

    @Override
    public LiveData<Boolean> isLoadingReceivedTaskList() {
        return null;
    }


}

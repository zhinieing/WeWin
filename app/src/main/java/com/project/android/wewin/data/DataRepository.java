package com.project.android.wewin.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.project.android.wewin.data.remote.model.Class;
import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.data.remote.model.Task;
import com.project.android.wewin.utils.Util;

import java.util.List;

/**
 * Created by pengming on 2017/11/24.
 */

public class DataRepository {

    private static DataRepository INSTANCE = null;

    private final DataSource mRemoteDataSource;

    private final DataSource mLocalDataSource;

    private static Application sApplication = null;

    private DataRepository(@NonNull DataSource remoteDataSource,
                           @NonNull DataSource localDataSource) {
        mRemoteDataSource = remoteDataSource;
        mLocalDataSource = localDataSource;
    }

    static DataRepository getInstance(@NonNull DataSource remoteDataSource,
                                      @NonNull DataSource localDataSource,
                                      Application application) {
        if (INSTANCE == null) {
            synchronized (DataRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DataRepository(remoteDataSource, localDataSource);
                    sApplication = application;
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<List<HomeWork>> getHomeWorkList(int index) {
        if (Util.isNetworkConnected(sApplication.getApplicationContext())) {
            return mRemoteDataSource.getHomeWorkList(index);
        } else {
            return mLocalDataSource.getHomeWorkList(index);
        }
    }

    public LiveData<Boolean> isLoadingHomeWorkList() {
        if (Util.isNetworkConnected(sApplication.getApplicationContext())) {
            return mRemoteDataSource.isLoadingHomeWorkList();
        } else {
            return mLocalDataSource.isLoadingHomeWorkList();
        }
    }

    public LiveData<List<Task>> getTaskList(int index) {
        if (Util.isNetworkConnected(sApplication.getApplicationContext())) {
            return mRemoteDataSource.getTaskList(index);
        } else {
            return mLocalDataSource.getTaskList(index);
        }
    }

    public LiveData<Boolean> isLoadingTaskList() {
        if (Util.isNetworkConnected(sApplication.getApplicationContext())) {
            return mRemoteDataSource.isLoadingTaskList();
        } else {
            return mLocalDataSource.isLoadingTaskList();
        }
    }

    public LiveData<List<HomeWork>> getPostedHomeWorkList(int index) {
        if (Util.isNetworkConnected(sApplication.getApplicationContext())) {
            return mRemoteDataSource.getPostedHomeWorkList(index);
        } else {
            return mLocalDataSource.getPostedHomeWorkList(index);
        }
    }

    public LiveData<Boolean> isLoadingPostedHomeWorkList() {
        if (Util.isNetworkConnected(sApplication.getApplicationContext())) {
            return mRemoteDataSource.isLoadingPostedHomeWorkList();
        } else {
            return mLocalDataSource.isLoadingPostedHomeWorkList();
        }
    }

    public LiveData<List<Task>> getPostedTaskList(int index) {
        if (Util.isNetworkConnected(sApplication.getApplicationContext())) {
            return mRemoteDataSource.getPostedTaskList(index);
        } else {
            return mLocalDataSource.getPostedTaskList(index);
        }
    }

    public LiveData<Boolean> isLoadingPostedTaskList() {
        if (Util.isNetworkConnected(sApplication.getApplicationContext())) {
            return mRemoteDataSource.isLoadingPostedTaskList();
        } else {
            return mLocalDataSource.isLoadingPostedTaskList();
        }
    }

    public LiveData<List<Task>> getReceivedTaskList(int index) {
        if (Util.isNetworkConnected(sApplication.getApplicationContext())) {
            return mRemoteDataSource.getReceivedTaskList(index);
        } else {
            return mLocalDataSource.getReceivedTaskList(index);
        }
    }

    public LiveData<Boolean> isLoadingReceivedTaskList() {
        if (Util.isNetworkConnected(sApplication.getApplicationContext())) {
            return mRemoteDataSource.isLoadingReceivedTaskList();
        } else {
            return mLocalDataSource.isLoadingReceivedTaskList();
        }
    }


    public LiveData<Boolean> isClassTeacher() {
        if (Util.isNetworkConnected(sApplication.getApplicationContext())) {
            return mRemoteDataSource.isClassTeacher();
        } else {
            return mLocalDataSource.isClassTeacher();
        }
    }

    public LiveData<List<Class>> getClassList() {
        if (Util.isNetworkConnected(sApplication.getApplicationContext())) {
            return mRemoteDataSource.getClassList();
        } else {
            return mLocalDataSource.getClassList();
        }
    }

    public LiveData<Boolean> isLoadingClassList() {
        if (Util.isNetworkConnected(sApplication.getApplicationContext())) {
            return mRemoteDataSource.isLoadingClassList();
        } else {
            return mLocalDataSource.isLoadingClassList();
        }
    }

    public LiveData<List<Class>> getStudentClassList() {
        if (Util.isNetworkConnected(sApplication.getApplicationContext())) {
            return mRemoteDataSource.getStudentClassList();
        } else {
            return mLocalDataSource.getStudentClassList();
        }
    }
}

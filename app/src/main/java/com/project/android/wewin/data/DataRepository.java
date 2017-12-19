package com.project.android.wewin.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.project.android.wewin.data.local.db.entity.HomeWork;
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
}

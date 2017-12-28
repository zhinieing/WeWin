package com.project.android.wewin.data.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.project.android.wewin.data.DataSource;
import com.project.android.wewin.data.local.db.entity.HomeWorkRoom;

import java.util.List;

/**
 * Created by pengming on 2017/11/24.
 */

public class RemoteDataSource implements DataSource {

    private static RemoteDataSource INSTANCE = null;

    private final MutableLiveData<Boolean> mIsLoadingHomeWorkList = new MutableLiveData<>();

    private final MutableLiveData<List<HomeWorkRoom>> mHomeWorkList = new MutableLiveData<>();


    private RemoteDataSource() {
        //mApiGirl = ApiManager.getInstance().getApiGirl();
    }

    public static RemoteDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (RemoteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RemoteDataSource();
                }
            }
        }
        return INSTANCE;
    }


    @Override
    public LiveData<List<HomeWorkRoom>> getHomeWorkList(int index) {
        return null;
    }

    @Override
    public LiveData<Boolean> isLoadingHomeWorkList() {
        return null;
    }
}

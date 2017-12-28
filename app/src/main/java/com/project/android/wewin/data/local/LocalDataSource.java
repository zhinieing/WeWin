package com.project.android.wewin.data.local;

import android.arch.lifecycle.LiveData;

import com.project.android.wewin.data.DataSource;
import com.project.android.wewin.data.local.db.AppDatabaseManager;
import com.project.android.wewin.data.local.db.entity.HomeWorkRoom;

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
    public LiveData<List<HomeWorkRoom>> getHomeWorkList(int index) {
        return AppDatabaseManager.getInstance().loadHomeWorkList();
    }

    @Override
    public LiveData<Boolean> isLoadingHomeWorkList() {
        return AppDatabaseManager.getInstance().isLoadingHomeWorkList();
    }
}

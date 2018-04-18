package com.project.android.wewin.data;

import android.app.Application;

import com.project.android.wewin.data.local.LocalDataSource;
import com.project.android.wewin.data.remote.RemoteDataSource;

/**
 *
 * @author pengming
 * @date 18/12/2017
 */

public class Injection {
    public static DataRepository getDataRepository(Application application) {
        return DataRepository.getInstance(RemoteDataSource.getInstance(),
                LocalDataSource.getInstance(), application);
    }
}

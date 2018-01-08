package com.project.android.wewin.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.project.android.wewin.data.DataRepository;
import com.project.android.wewin.data.remote.model.HomeWork;

/**
 *
 * @author pengming
 * @date 08/01/2018
 */

public class DetailViewModel extends AndroidViewModel {

    private DataRepository mDataRepository;

    private final String mHomeWorkId;

    public DetailViewModel(@NonNull Application application, DataRepository dataRepository, String homeWorkId) {
        super(application);
        mHomeWorkId = homeWorkId;
        mDataRepository = dataRepository;
    }

    public LiveData<HomeWork> getHomeWorkDetail() {
        return mDataRepository.getHomeWorkDetail(mHomeWorkId);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final DataRepository mDataRepository;

        private final String mHomeWorkId;

        public Factory(@NonNull Application application,DataRepository dataRepository, String homeWorkId) {
            mDataRepository = dataRepository;
            mApplication = application;
            mHomeWorkId = homeWorkId;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new DetailViewModel(mApplication, mDataRepository, mHomeWorkId);
        }
    }
}

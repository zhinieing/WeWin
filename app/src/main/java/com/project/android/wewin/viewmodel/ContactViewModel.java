package com.project.android.wewin.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import com.project.android.wewin.data.DataRepository;
import com.project.android.wewin.data.remote.model.Class;


import java.util.List;

/**
 * Created by pengming on 03/01/2018.
 */

public class ContactViewModel extends AndroidViewModel {
    private LiveData<List<Class>> mClassList;

    private DataRepository mClassDataRepository = null;

    public ContactViewModel(Application application, DataRepository homeWorkDataRepository) {
        super(application);
        mClassDataRepository = homeWorkDataRepository;
    }

    public void loadClassList() {
        mClassList = mClassDataRepository.getClassList();
    }

    public LiveData<List<Class>> getClassList() {
        return mClassList;
    }

    public LiveData<Boolean> isLoadingClassList() {
        return mClassDataRepository.isLoadingClassList();
    }


    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final DataRepository mClassDataRepository;

        public Factory(@NonNull Application application, DataRepository classDataRepository) {
            mApplication = application;
            mClassDataRepository = classDataRepository;
        }

        @Override
        public <T extends ViewModel> T create(java.lang.Class<T> modelClass) {
            return (T) new ContactViewModel(mApplication, mClassDataRepository);
        }
    }
}

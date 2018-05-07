package com.project.android.wewin.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.util.Log;

import com.project.android.wewin.data.DataRepository;
import com.project.android.wewin.data.remote.model.Class;

import java.util.List;

/**
 *
 * @author pengming
 * @date 04/01/2018
 */

public class ReleaseHomeWorkViewModel extends AndroidViewModel {
    private final MutableLiveData<Integer> mRequestIndex = new MutableLiveData<>();

    private LiveData<List<Class>> mStudentClassList;

    private DataRepository mClassDataRepository = null;

    private ReleaseHomeWorkViewModel(Application application, DataRepository homeWorkDataRepository) {
        super(application);
        mClassDataRepository = homeWorkDataRepository;
//        mStudentClassList = Transformations.switchMap(mRequestIndex, new Function<Integer, LiveData<List<Class>>>() {
//            @Override
//            public LiveData<List<Class>> apply(Integer input) {
//                return mClassDataRepository.getStudentClassList();
//            }
//        });
    }

    public void loadStudentClassList() {
        mRequestIndex.setValue((mRequestIndex.getValue() == null ? 1 : mRequestIndex.getValue() + 1));
    }

    public LiveData<List<Class>> getStudentClassList() {
        return mStudentClassList;
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
            return (T) new ReleaseHomeWorkViewModel(mApplication, mClassDataRepository);
        }
    }
}

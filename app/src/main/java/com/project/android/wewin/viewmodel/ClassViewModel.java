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
import com.project.android.wewin.data.local.db.entity.ClassInfo;

import java.util.List;

/**
 * @author pengming
 */
public class ClassViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> mRequestIndex = new MutableLiveData<>();

    private LiveData<List<ClassInfo>> mCreatedClassList;

    private LiveData<List<ClassInfo>> mJoinedClassList;

    private DataRepository mClassDataRepository;

    private ClassViewModel(Application application, DataRepository homeWorkDataRepository) {
        super(application);
        mClassDataRepository = homeWorkDataRepository;
        mCreatedClassList = Transformations.switchMap(mRequestIndex, new Function<Integer, LiveData<List<ClassInfo>>>() {
            @Override
            public LiveData<List<ClassInfo>> apply(Integer input) {
                return mClassDataRepository.getCreatedClassList();
            }
        });
        mJoinedClassList = Transformations.switchMap(mRequestIndex, new Function<Integer, LiveData<List<ClassInfo>>>() {
            @Override
            public LiveData<List<ClassInfo>> apply(Integer input) {
                return mClassDataRepository.getJoinedClassList();
            }
        });
    }

    public void loadClassList() {
        mRequestIndex.setValue((mRequestIndex.getValue() == null ? 1 : mRequestIndex.getValue() + 1));
    }

    public LiveData<List<ClassInfo>> getCreatedClassList() {
        return mCreatedClassList;
    }

    public LiveData<List<ClassInfo>> getJoinedClassList() {
        return mJoinedClassList;
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
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new ClassViewModel(mApplication, mClassDataRepository);
        }
    }
}

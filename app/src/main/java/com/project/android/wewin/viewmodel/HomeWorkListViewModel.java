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

import com.project.android.wewin.MyApplication;
import com.project.android.wewin.data.DataRepository;
import com.project.android.wewin.data.local.db.entity.HomeWorkRoom;
import com.project.android.wewin.utils.Util;

import java.util.List;

/**
 * Created by pengming on 2017/11/24.
 */

public class HomeWorkListViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> mHomeWorkPageIndex = new MutableLiveData<>();

    private final LiveData<List<HomeWorkRoom>> mHomeWorkList;

    private DataRepository mHomeWorkDataRepository = null;

    public HomeWorkListViewModel(Application application, DataRepository homeWorkDataRepository) {
        super(application);
        mHomeWorkDataRepository = homeWorkDataRepository;
        mHomeWorkList = Transformations.switchMap(mHomeWorkPageIndex, new Function<Integer, LiveData<List<HomeWorkRoom>>>() {
            @Override
            public LiveData<List<HomeWorkRoom>> apply(Integer input) {
                return mHomeWorkDataRepository.getHomeWorkList(input);
            }
        });
    }

    public LiveData<List<HomeWorkRoom>> getHomeWorkListLiveData(){
        return mHomeWorkList;
    }

    public void refreshHomeWorkListData(){
        mHomeWorkPageIndex.setValue(1);
    }

    public void loadNextPageHomeWorkList() {
        if (!Util.isNetworkConnected(MyApplication.getInstance())) {
            return;
        }
        mHomeWorkPageIndex.setValue((mHomeWorkPageIndex.getValue() == null ? 1 : mHomeWorkPageIndex.getValue() + 1));
    }

    public LiveData<Boolean> getLoadMoreState() {
        return mHomeWorkDataRepository.isLoadingHomeWorkList();
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final DataRepository mHomeWorkDataRepository;

        public Factory(@NonNull Application application, DataRepository homeWorkDataRepository) {
            mApplication = application;
            mHomeWorkDataRepository = homeWorkDataRepository;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new HomeWorkListViewModel(mApplication, mHomeWorkDataRepository);
        }
    }
}

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

import com.project.android.wewin.MyApplication;
import com.project.android.wewin.data.DataRepository;

import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.utils.Util;

import java.util.List;

/**
 *
 * @author pengming
 * @date 2017/11/24
 */

public class HomeWorkListViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> mHomeWorkPageIndex = new MutableLiveData<>();

    private final LiveData<List<HomeWork>> mHomeWorkList;

    private final MutableLiveData<Integer> mPostedHomeWorkPageIndex = new MutableLiveData<>();

    private final LiveData<List<HomeWork>> mPostedHomeWorkList;

    private DataRepository mHomeWorkDataRepository = null;

    public HomeWorkListViewModel(Application application, DataRepository homeWorkDataRepository) {
        super(application);
        mHomeWorkDataRepository = homeWorkDataRepository;
        mHomeWorkList = Transformations.switchMap(mHomeWorkPageIndex, new Function<Integer, LiveData<List<HomeWork>>>() {
            @Override
            public LiveData<List<HomeWork>> apply(Integer input) {
                return mHomeWorkDataRepository.getHomeWorkList(input);
            }
        });

        mPostedHomeWorkList =Transformations.switchMap(mPostedHomeWorkPageIndex, new Function<Integer, LiveData<List<HomeWork>>>() {
            @Override
            public LiveData<List<HomeWork>> apply(Integer input) {
                return mHomeWorkDataRepository.getPostedHomeWorkList(input);
            }
        });
    }

    /*TaskFragment*/
    public LiveData<List<HomeWork>> getHomeWorkList(){
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

    public LiveData<Boolean> isLoadingHomeWorkList() {
        return mHomeWorkDataRepository.isLoadingHomeWorkList();
    }


    /*PostedTaskFragment*/
    public LiveData<List<HomeWork>> getPostedHomeWorkList(){
        return mPostedHomeWorkList;
    }

    public void refreshPostedHomeWorkListData(){
        mPostedHomeWorkPageIndex.setValue(1);
    }

    public void loadNextPostedPageHomeWorkList() {
        if (!Util.isNetworkConnected(MyApplication.getInstance())) {
            return;
        }
        mPostedHomeWorkPageIndex.setValue((mPostedHomeWorkPageIndex.getValue() == null ? 1 : mPostedHomeWorkPageIndex.getValue() + 1));
    }

    public LiveData<Boolean> isLoadingPostedHomeWorkList() {
        return mHomeWorkDataRepository.isLoadingPostedHomeWorkList();
    }


    /*MainFragment*/
    public LiveData<Boolean> isClassTeacher() {
        return mHomeWorkDataRepository.isClassTeacher();
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

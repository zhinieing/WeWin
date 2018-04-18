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
import com.project.android.wewin.data.remote.model.Task;
import com.project.android.wewin.utils.Util;

import java.util.List;

/**
 * Created by zhoutao on 2018/4/18.
 */

public class TaskListViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> mTaskPageIndex = new MutableLiveData<>();

    private final LiveData<List<Task>> mTaskList;

    private final MutableLiveData<Integer> mPostedTaskPageIndex = new MutableLiveData<>();

    private final LiveData<List<Task>> mPostedTaskList;

    private final MutableLiveData<Integer> mReceivedTaskPageIndex = new MutableLiveData<>();

    private final LiveData<List<Task>> mReceivedTaskList;

    private DataRepository mTaskDataRepository = null;

    public TaskListViewModel(Application application, DataRepository taskDataRepository) {
        super(application);
        mTaskDataRepository = taskDataRepository;

        mTaskList = Transformations.switchMap(mTaskPageIndex, new Function<Integer, LiveData<List<Task>>>() {
            @Override
            public LiveData<List<Task>> apply(Integer input) {
                return mTaskDataRepository.getTaskList(input);
            }
        });

        mPostedTaskList = Transformations.switchMap(mPostedTaskPageIndex, new Function<Integer, LiveData<List<Task>>>() {
            @Override
            public LiveData<List<Task>> apply(Integer input) {
                return mTaskDataRepository.getPostedTaskList(input);
            }
        });

        mReceivedTaskList = Transformations.switchMap(mReceivedTaskPageIndex, new Function<Integer, LiveData<List<Task>>>() {
            @Override
            public LiveData<List<Task>> apply(Integer input) {
                return mTaskDataRepository.getReceivedTaskList(input);
            }
        });
    }

    /*TaskFragment*/

    public LiveData<List<Task>> getTaskList() {
        return mTaskList;
    }

    public void refreshTaskListData() {
        mTaskPageIndex.setValue(1);
    }

    public void loadNextPageTaskList() {
        if (!Util.isNetworkConnected(MyApplication.getInstance())) {
            return;
        }
        mTaskPageIndex.setValue((mTaskPageIndex.getValue() == null ? 1 : mTaskPageIndex.getValue() + 1));
    }

    public LiveData<Boolean> isLoadingTaskList() {
        return mTaskDataRepository.isLoadingTaskList();
    }

    /*PostedTaskFragment*/

    public LiveData<List<Task>> getPostedTaskList() {
        return mPostedTaskList;
    }

    public void refreshPostedTaskListData() {
        mPostedTaskPageIndex.setValue(1);
    }

    public void loadNextPostedPageTaskList() {
        if (!Util.isNetworkConnected(MyApplication.getInstance())) {
            return;
        }
        mPostedTaskPageIndex.setValue((mPostedTaskPageIndex.getValue() == null ? 1 : mPostedTaskPageIndex.getValue() + 1));
    }

    public LiveData<Boolean> isLoadingPostedTaskList() {
        return mTaskDataRepository.isLoadingPostedTaskList();
    }

    /*ReceivedTaskFragment*/

    public LiveData<List<Task>> getReceivedTaskList() {
        return mReceivedTaskList;
    }

    public void refreshReceivedTaskListData() {
        mReceivedTaskPageIndex.setValue(1);
    }

    public void loadNextReceivedPageTaskList() {
        if (!Util.isNetworkConnected(MyApplication.getInstance())) {
            return;
        }
        mReceivedTaskPageIndex.setValue((mReceivedTaskPageIndex.getValue() == null ? 1 : mReceivedTaskPageIndex.getValue() + 1));
    }

    public LiveData<Boolean> isLoadingReceivedTaskList() {
        return mTaskDataRepository.isLoadingReceivedTaskList();
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final DataRepository mTaskDataRepository;

        public Factory(@NonNull Application application, DataRepository taskDataRepository) {
            mApplication = application;
            mTaskDataRepository = taskDataRepository;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new TaskListViewModel(mApplication, mTaskDataRepository);
        }
    }
}

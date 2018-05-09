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

import com.project.android.wewin.data.DataRepository;
import com.project.android.wewin.data.local.db.entity.Group;
import com.project.android.wewin.data.local.db.entity.UserInfo;

import java.util.List;

/**
 * @author pengming
 */
public class GroupViewModel extends AndroidViewModel {

    private final MutableLiveData<Integer> mRequestGroupIndex = new MutableLiveData<>();

    private final MutableLiveData<Integer> mRequestMemberIndex = new MutableLiveData<>();

    private LiveData<List<Group>> mGroupList;

    private LiveData<List<UserInfo>> mMemberList;

    private DataRepository mDataRepository;

    private GroupViewModel(Application application, DataRepository dataRepository, final Integer classId, final Integer groupId) {
        super(application);

        mDataRepository = dataRepository;

        mGroupList = Transformations.switchMap(mRequestGroupIndex, new Function<Integer, LiveData<List<Group>>>() {
            @Override
            public LiveData<List<Group>> apply(Integer input) {
                return mDataRepository.getGroupList(classId);
            }
        });

        mMemberList = Transformations.switchMap(mRequestMemberIndex, new Function<Integer, LiveData<List<UserInfo>>>() {
            @Override
            public LiveData<List<UserInfo>> apply(Integer input) {
                return mDataRepository.getMemberList(groupId);
            }
        });
    }


    public void loadGroupList() {
        mRequestGroupIndex.setValue((mRequestGroupIndex.getValue() == null ? 1 : mRequestGroupIndex.getValue() + 1));
    }


    public LiveData<List<Group>> getGroupList(){
        return mGroupList;
    }

    public LiveData<Boolean> isLoadingGroupList() {
        return mDataRepository.isLoadingGroupList();
    }


    public void loadMemberList() {
        mRequestMemberIndex.setValue((mRequestMemberIndex.getValue() == null ? 1 : mRequestMemberIndex.getValue() + 1));
    }

    public LiveData<List<UserInfo>> getMemberList(){
        return mMemberList;
    }

    public LiveData<Boolean> isLoadingMemberList() {
        return mDataRepository.isLoadingMemberList();
    }



    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final DataRepository mDataRepository;

        private final Integer mClassId;

        private final Integer mGroupId;


        public Factory(@NonNull Application application,DataRepository dataRepository, Integer classId, Integer groupId) {
            mDataRepository = dataRepository;
            mApplication = application;
            mClassId = classId;
            mGroupId = groupId;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new GroupViewModel(mApplication, mDataRepository, mClassId, mGroupId);
        }
    }
}

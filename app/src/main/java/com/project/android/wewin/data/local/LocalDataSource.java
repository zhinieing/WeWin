package com.project.android.wewin.data.local;

import android.arch.lifecycle.LiveData;

import com.project.android.wewin.data.DataSource;
import com.project.android.wewin.data.local.db.AppDatabaseManager;
import com.project.android.wewin.data.local.db.entity.ClassInfo;
import com.project.android.wewin.data.local.db.entity.Group;
import com.project.android.wewin.data.local.db.entity.GroupWithUser;
import com.project.android.wewin.data.local.db.entity.UserInfo;
import com.project.android.wewin.data.remote.model.Class;
import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.data.remote.model.Task;

import java.util.List;

/**
 *
 * @author pengming
 * @date 2017/11/24
 */

public class LocalDataSource implements DataSource {

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
    public LiveData<List<HomeWork>> getHomeWorkList(int index) {
        return AppDatabaseManager.getInstance().loadHomeWorkList();
    }

    @Override
    public LiveData<Boolean> isLoadingHomeWorkList() {
        return AppDatabaseManager.getInstance().isLoadingHomeWorkList();
    }



    @Override
    public LiveData<List<HomeWork>> getPostedHomeWorkList(int index) {
        return null;
    }

    @Override
    public LiveData<Boolean> isLoadingPostedHomeWorkList() {
        return null;
    }

    @Override
    public LiveData<List<ClassInfo>> getCreatedClassList() {
        return null;
    }

    @Override
    public LiveData<List<ClassInfo>> getJoinedClassList() {
        return null;
    }


    @Override
    public LiveData<UserInfo> getOpenid(String phoneno) {
        return null;
    }




    @Override
    public LiveData<Boolean> isLoadingClassList() {
        return null;
    }

    @Override
    public LiveData<List<GroupWithUser>> getGroupWithUser(Integer classId) {
        return null;
    }

    @Override
    public LiveData<Boolean> isLoadingGroupWithUserList() {
        return null;
    }

    @Override
    public LiveData<List<Group>> getGroupList(Integer classId) {
        return null;
    }

    @Override
    public LiveData<Boolean> isLoadingGroupList() {
        return null;
    }

    @Override
    public LiveData<List<UserInfo>> getMemberList(Integer groupId) {
        return null;
    }

    @Override
    public LiveData<Boolean> isLoadingMemberList() {
        return null;
    }

    @Override
    public LiveData<List<Class>> getStudentClassList() {
        return null;
    }




    /*发布作业*/

    @Override
    public LiveData<List<Task>> getTaskList(int index) {
        return AppDatabaseManager.getInstance().loadTaskList();
    }

    @Override
    public LiveData<Boolean> isLoadingTaskList() {
        return AppDatabaseManager.getInstance().isLoadingTaskList();
    }

    @Override
    public LiveData<List<Task>> getPostedTaskList(int index) {
        return null;
    }

    @Override
    public LiveData<Boolean> isLoadingPostedTaskList() {
        return null;
    }

    @Override
    public LiveData<List<Task>> getReceivedTaskList(int index) {
        return null;
    }

    @Override
    public LiveData<Boolean> isLoadingReceivedTaskList() {
        return null;
    }


}

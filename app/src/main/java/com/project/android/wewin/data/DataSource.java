package com.project.android.wewin.data;

import android.arch.lifecycle.LiveData;

import com.project.android.wewin.data.local.db.entity.ClassInfo;
import com.project.android.wewin.data.local.db.entity.GroupInfo;
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

public interface DataSource {


    LiveData<UserInfo> getOpenid(String phoneno);


    /*主界面*/

    LiveData<Boolean> isClassTeacher();

    LiveData<List<HomeWork>> getHomeWorkList(int index);
    LiveData<Boolean> isLoadingHomeWorkList();

    LiveData<List<HomeWork>> getPostedHomeWorkList(int index);
    LiveData<Boolean> isLoadingPostedHomeWorkList();



    /*班级*/

    LiveData<List<ClassInfo>> getCreatedClassList();
    LiveData<List<ClassInfo>> getJoinedClassList();
    LiveData<Boolean> isLoadingClassList();

    LiveData<List<GroupWithUser>> getGroupWithUser(Integer classId);
    LiveData<Boolean> isLoadingGroupWithUserList();


    LiveData<List<GroupInfo>> getGroupList(Integer classId);
    LiveData<Boolean> isLoadingGroupList();

    LiveData<List<UserInfo>> getMemberList(Integer groupId);
    LiveData<Boolean> isLoadingMemberList();


    /*发布作业*/

    //LiveData<List<Class>> getStudentClassList();


    /*发布任务*/

    LiveData<List<Task>> getTaskList(int index);
    LiveData<Boolean> isLoadingTaskList();

    LiveData<List<Task>> getPostedTaskList(int index);
    LiveData<Boolean> isLoadingPostedTaskList();

    LiveData<List<Task>> getReceivedTaskList(int index);
    LiveData<Boolean> isLoadingReceivedTaskList();
}

package com.project.android.wewin.data.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.project.android.wewin.data.DataSource;
import com.project.android.wewin.data.remote.model.Class;
import com.project.android.wewin.data.remote.model.GroupInfo;
import com.project.android.wewin.data.remote.model.GroupMember;
import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.data.remote.model.MyUser;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by pengming on 2017/11/24.
 */

public class RemoteDataSource implements DataSource {

    private static RemoteDataSource INSTANCE = null;

    private final MutableLiveData<Boolean> mIsLoadingHomeWorkList = new MutableLiveData<>();

    private final MutableLiveData<List<HomeWork>> mHomeWorkList = new MutableLiveData<>();


    private RemoteDataSource() {
        //mApiGirl = ApiManager.getInstance().getApiGirl();
    }

    public static RemoteDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (RemoteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RemoteDataSource();
                }
            }
        }
        return INSTANCE;
    }


    @Override
    public LiveData<List<HomeWork>> getHomeWorkList(final int index) {
        mIsLoadingHomeWorkList.setValue(true);

        final MyUser user = BmobUser.getCurrentUser(MyUser.class);

        if (user != null) {
            BmobQuery<GroupMember> query = new BmobQuery<>();
            query.addWhereEqualTo("memberUser", user);
            query.findObjects(new FindListener<GroupMember>() {
                @Override
                public void done(List<GroupMember> list, BmobException e) {
                    if (e == null) {
                        List<GroupInfo> groupInfos = new ArrayList<>();
                        for (GroupMember groupMember : list) {
                            groupInfos.add(groupMember.getTargetGroupInfo());
                        }

                        BmobQuery<HomeWork> query = new BmobQuery<>();
                        query.addWhereContainedIn("groupInfo", groupInfos);
                        query.setSkip(10*(index - 1));
                        query.setLimit(10);
                        query.order("createdAt");
                        query.findObjects(new FindListener<HomeWork>() {
                            @Override
                            public void done(List<HomeWork> list, BmobException e) {
                                mIsLoadingHomeWorkList.setValue(false);

                                if (e == null) {
                                    Log.d("wewein", "done: "+list.size());
                                    mHomeWorkList.setValue(list);
                                }
                            }
                        });

                    } else {
                        mIsLoadingHomeWorkList.setValue(false);
                    }
                }
            });
        } else {
            mIsLoadingHomeWorkList.setValue(false);
        }


        return mHomeWorkList;
    }

    @Override
    public LiveData<Boolean> isLoadingHomeWorkList() {
        return mIsLoadingHomeWorkList;
    }


    @Override
    public LiveData<List<Class>> getClassList() {
        return null;
    }

    @Override
    public LiveData<Boolean> isLoadingClassList() {
        return null;
    }
}

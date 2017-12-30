package com.project.android.wewin.ui.activity;

import android.content.Context;
import android.util.Log;

import com.project.android.wewin.MyApplication;
import com.project.android.wewin.data.remote.model.Class;
import com.project.android.wewin.data.remote.model.GroupInfo;
import com.project.android.wewin.data.remote.model.GroupMember;
import com.project.android.wewin.data.remote.model.MyUser;
import com.project.android.wewin.ui.adapter.ExpandListViewAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 *
 * @author pengming
 * @date 30/12/2017
 */

public class InitData {

    public static void initData(final MyUser user) {

        final List<Class> mClassData = new ArrayList<Class>();
        final List<String> mGroupIds = new ArrayList<String>();

        final BmobQuery<GroupMember> query1 = new BmobQuery<GroupMember>();
        query1.addWhereEqualTo("userId", user.getObjectId());
        query1.findObjects(new FindListener<GroupMember>() {
            @Override
            public void done(List<GroupMember> list, BmobException e) {
                if (e == null) {

                    if (list.size() == 0) {
                        MyUser newMyUser = new MyUser();
                        newMyUser.setmClasses(mClassData);
                        newMyUser.setGroupIds(mGroupIds);
                        newMyUser.update(user.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.d("wewein", "done0: ");
                                } else {
                                    Log.d("wewein", "error0: "+e);
                                }
                            }
                        });
                    }


                    for (GroupMember groupMember : list) {

                        mGroupIds.add(groupMember.getGroupId());


                        BmobQuery<GroupInfo> query2 = new BmobQuery<GroupInfo>();
                        query2.getObject(groupMember.getGroupId(), new QueryListener<GroupInfo>() {
                            @Override
                            public void done(GroupInfo groupInfo, BmobException e) {
                                if (e == null) {


                                    BmobQuery<Class> query3 = new BmobQuery<Class>();
                                    query3.getObject(groupInfo.getClassId(), new QueryListener<Class>() {
                                        @Override
                                        public void done(Class aClass, BmobException e) {
                                            if (e == null) {
                                                final List<GroupInfo> groupInfos = new ArrayList<GroupInfo>();
                                                aClass.setGroupInfos(groupInfos);
                                                mClassData.add(aClass);


                                                BmobQuery<GroupInfo> query4 = new BmobQuery<GroupInfo>();
                                                query4.addWhereEqualTo("classId", aClass.getObjectId());
                                                query4.findObjects(new FindListener<GroupInfo>() {
                                                    @Override
                                                    public void done(List<GroupInfo> list, BmobException e) {
                                                        if (e == null) {
                                                            for (GroupInfo groupInfo : list) {

                                                                final List<GroupMember> groupMembers = new ArrayList<GroupMember>();
                                                                groupInfo.setGroupMembers(groupMembers);
                                                                groupInfos.add(groupInfo);


                                                                BmobQuery<GroupMember> query5 = new BmobQuery<GroupMember>();
                                                                query5.addWhereEqualTo("groupId", groupInfo.getObjectId());
                                                                query5.findObjects(new FindListener<GroupMember>() {
                                                                    @Override
                                                                    public void done(List<GroupMember> list, BmobException e) {
                                                                        if (e == null) {
                                                                            groupMembers.addAll(list);

                                                                            // todo 判断最后一次才上传
                                                                            MyUser newMyUser = new MyUser();
                                                                            newMyUser.setmClasses(mClassData);
                                                                            newMyUser.setGroupIds(mGroupIds);
                                                                            newMyUser.update(user.getObjectId(), new UpdateListener() {
                                                                                @Override
                                                                                public void done(BmobException e) {
                                                                                    if (e == null) {
                                                                                        Log.d("wewein", "done6: ");
                                                                                    } else {
                                                                                        Log.d("wewein", "error6: "+e);
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}

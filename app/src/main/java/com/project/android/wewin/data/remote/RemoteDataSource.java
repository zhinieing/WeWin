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
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

/**
 *
 * @author pengming
 * @date 2017/11/24
 */

public class RemoteDataSource implements DataSource {

    private static RemoteDataSource INSTANCE = null;

    private final MutableLiveData<Boolean> mIsLoadingHomeWorkList = new MutableLiveData<>();

    private final MutableLiveData<List<HomeWork>> mHomeWorkList = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mIsLoadingPostedHomeWorkList = new MutableLiveData<>();

    private final MutableLiveData<List<HomeWork>> mPostedHomeWorkList = new MutableLiveData<>();

    private final MutableLiveData<HomeWork> mHomeWork = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mIsClassTeacher = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mIsLoadingClassList = new MutableLiveData<>();

    private final MutableLiveData<List<Class>> mClassList = new MutableLiveData<>();

    private final MutableLiveData<List<Class>> mStudentClassList = new MutableLiveData<>();

    private int indexClass;
    private int indexGroup;

    private RemoteDataSource() {
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
            query.include("targetGroupInfo");
            query.findObjects(new FindListener<GroupMember>() {
                @Override
                public void done(List<GroupMember> list, BmobException e) {
                    if (e == null && list.size() != 0) {
                        List<String> groupIds = new ArrayList<>();
                        for (GroupMember groupMember : list) {
                            groupIds.add(groupMember.getTargetGroupInfo().getObjectId());
                        }

                        BmobQuery<HomeWork> query = new BmobQuery<>();
                        BmobQuery<GroupInfo> innerQuery = new BmobQuery<>();
                        innerQuery.addWhereContainedIn("objectId", groupIds);
                        query.addWhereMatchesQuery("groupInfo", "GroupInfo", innerQuery);
                        query.include("creatorUser");
                        query.setSkip(10*(index - 1));
                        query.setLimit(10);
                        query.order("createdAt");
                        query.findObjects(new FindListener<HomeWork>() {
                            @Override
                            public void done(List<HomeWork> list, BmobException e) {
                                mIsLoadingHomeWorkList.setValue(false);

                                if (e == null && list.size() != 0) {
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
    public LiveData<List<HomeWork>> getPostedHomeWorkList(int index) {
        mIsLoadingPostedHomeWorkList.setValue(true);

        final MyUser user = BmobUser.getCurrentUser(MyUser.class);

        if (user != null) {
            BmobQuery<HomeWork> query =new BmobQuery<>();
            query.addWhereEqualTo("creatorUser", new BmobPointer(user));
            query.include("creatorUser");
            query.setSkip(10*(index - 1));
            query.setLimit(10);
            query.order("createdAt");
            query.findObjects(new FindListener<HomeWork>() {
                @Override
                public void done(List<HomeWork> list, BmobException e) {
                    mIsLoadingPostedHomeWorkList.setValue(false);

                    if (e == null && list.size() != 0) {
                        mPostedHomeWorkList.setValue(list);
                    }
                }
            });

        } else {
            mIsLoadingPostedHomeWorkList.setValue(false);
        }

        return mPostedHomeWorkList;
    }

    @Override
    public LiveData<Boolean> isLoadingPostedHomeWorkList() {
        return mIsLoadingPostedHomeWorkList;
    }



    @Override
    public LiveData<HomeWork> getHomeWorkDetail(String mHomeWorkId) {

        BmobQuery<HomeWork> query = new BmobQuery<>();
        query.include("creatorUser");
        query.getObject(mHomeWorkId, new QueryListener<HomeWork>() {
            @Override
            public void done(HomeWork homeWork, BmobException e) {
                if (e == null) {
                    mHomeWork.setValue(homeWork);
                }
            }
        });
        return mHomeWork;
    }


    @Override
    public LiveData<Boolean> isClassTeacher() {
        final MyUser user = BmobUser.getCurrentUser(MyUser.class);

        if (user != null) {
            BmobQuery<GroupMember> query = new BmobQuery<>();
            query.include("targetGroupInfo");
            query.addWhereEqualTo("memberUser", user);
            query.findObjects(new FindListener<GroupMember>() {
                @Override
                public void done(List<GroupMember> list, BmobException e) {
                    if (e == null && list.size() != 0) {
                        int index = 0;
                        for (GroupMember groupMember : list) {
                            index += groupMember.getTargetGroupInfo().getAuth();
                        }

                        if (index == 0) {
                            mIsClassTeacher.setValue(false);
                        } else {
                            mIsClassTeacher.setValue(true);
                        }
                    } else {
                        mIsClassTeacher.setValue(false);
                    }
                }
            });
        } else {
            mIsClassTeacher.setValue(false);
        }
        return mIsClassTeacher;
    }


    @Override
    public LiveData<List<Class>> getClassList() {
        mIsLoadingClassList.setValue(true);

        final MyUser user = BmobUser.getCurrentUser(MyUser.class);

        if (user != null) {
            final List<Class> mClassData = new ArrayList<>();

            BmobQuery<GroupMember> query = new BmobQuery<>();
            query.include("targetGroupInfo");
            query.addWhereEqualTo("memberUser", user);
            query.findObjects(new FindListener<GroupMember>() {
                @Override
                public void done(List<GroupMember> list, BmobException e) {
                    if (e == null && list.size() != 0) {

                        final int classNum = list.size();
                        indexClass = 0;

                        for (GroupMember groupMember : list) {

                            BmobQuery<Class> query = new BmobQuery<>();
                            query.getObject(groupMember.getTargetGroupInfo().getTargetClass().getObjectId(), new QueryListener<Class>() {
                                @Override
                                public void done(Class aClass, BmobException e) {
                                    if (e == null) {
                                        final List<GroupInfo> groupInfos = new ArrayList<>();
                                        aClass.setGroupInfos(groupInfos);
                                        mClassData.add(aClass);

                                        BmobQuery<GroupInfo> query = new BmobQuery<>();
                                        query.addWhereEqualTo("targetClass", new BmobPointer(aClass));
                                        query.findObjects(new FindListener<GroupInfo>() {
                                            @Override
                                            public void done(List<GroupInfo> list, BmobException e) {
                                                if (e == null) {

                                                    indexClass++;
                                                    final int groupNum = list.size();
                                                    indexGroup = 0;


                                                    for (GroupInfo groupInfo : list) {
                                                        final List<GroupMember> groupMembers = new ArrayList<>();
                                                        groupInfo.setGroupMembers(groupMembers);
                                                        groupInfos.add(groupInfo);

                                                        BmobQuery<GroupMember> query = new BmobQuery<>();
                                                        query.addWhereEqualTo("targetGroupInfo", new BmobPointer(groupInfo));
                                                        query.include("memberUser");
                                                        query.findObjects(new FindListener<GroupMember>() {
                                                            @Override
                                                            public void done(List<GroupMember> list, BmobException e) {
                                                                if (e == null) {
                                                                    groupMembers.addAll(list);

                                                                    indexGroup++;

                                                                    if (indexClass == classNum && indexGroup == groupNum) {
                                                                        Log.d("wewein", "done: final get class"+",indexClass="+indexClass+",indexGroup="+indexGroup);
                                                                        mIsLoadingClassList.setValue(false);
                                                                        mClassList.setValue(mClassData);
                                                                    }
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

                    } else {
                        mIsLoadingClassList.setValue(false);
                    }
                }
            });
        } else {
            mIsLoadingClassList.setValue(false);
        }

        return mClassList;
    }

    @Override
    public LiveData<Boolean> isLoadingClassList() {
        return mIsLoadingClassList;
    }

    @Override
    public LiveData<List<Class>> getStudentClassList() {
        final MyUser user = BmobUser.getCurrentUser(MyUser.class);

        if (user != null) {
            final List<Class> mClassData = new ArrayList<>();

            BmobQuery<GroupMember> query = new BmobQuery<>();
            query.addWhereEqualTo("memberUser", user);
            query.include("targetGroupInfo");
            query.findObjects(new FindListener<GroupMember>() {
                @Override
                public void done(List<GroupMember> list, BmobException e) {
                    if (e == null && list.size() != 0) {

                        final int classNum = list.size();
                        indexClass = 0;

                        for (GroupMember groupMember : list) {
                            BmobQuery<Class> query = new BmobQuery<>();
                            query.getObject(groupMember.getTargetGroupInfo().getTargetClass().getObjectId(), new QueryListener<Class>() {
                                @Override
                                public void done(final Class aClass, BmobException e) {
                                    if (e == null) {

                                        BmobQuery<GroupInfo> query = new BmobQuery<>();
                                        query.addWhereEqualTo("targetClass", new BmobPointer(aClass));
                                        query.addWhereEqualTo("auth", 0);
                                        query.findObjects(new FindListener<GroupInfo>() {
                                            @Override
                                            public void done(List<GroupInfo> list, BmobException e) {
                                                if (e == null) {

                                                    indexClass++;

                                                    if (list.size() != 0) {
                                                        aClass.setGroupInfos(list);
                                                        mClassData.add(aClass);
                                                    }

                                                    if (indexClass == classNum) {
                                                        Log.d("wewein", "done: final get student class" );
                                                        mStudentClassList.setValue(mClassData);
                                                    }
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

        return mStudentClassList;
    }
}

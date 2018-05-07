package com.project.android.wewin.data.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.project.android.wewin.data.DataSource;
import com.project.android.wewin.data.local.db.entity.ClassInfo;
import com.project.android.wewin.data.remote.api.ApiClass;
import com.project.android.wewin.data.remote.api.ApiManager;
import com.project.android.wewin.data.remote.api.ApiOpenid;
import com.project.android.wewin.data.remote.api.ApiQiniu;
import com.project.android.wewin.data.remote.model.Class;
import com.project.android.wewin.data.remote.model.ClassData;
import com.project.android.wewin.data.remote.model.GroupInfo;
import com.project.android.wewin.data.remote.model.GroupMember;
import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.data.remote.model.MyUser;
import com.project.android.wewin.data.remote.model.OpenidData;
import com.project.android.wewin.data.remote.model.Task;
import com.project.android.wewin.utils.L;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author pengming
 * @date 2017/11/24
 */

public class RemoteDataSource implements DataSource {

    private static RemoteDataSource INSTANCE = null;


    private final MutableLiveData<Boolean> mIsLoadingHomeWorkList = new MutableLiveData<>();

    private final MutableLiveData<List<HomeWork>> mHomeWorkList = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mIsLoadingPostedHomeWorkList = new MutableLiveData<>();

    private final MutableLiveData<List<HomeWork>> mPostedHomeWorkList = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mIsClassTeacher = new MutableLiveData<>();

    private final MutableLiveData<List<Class>> mStudentClassList = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mIsLoadingClassList = new MutableLiveData<>();

    private final MutableLiveData<List<ClassInfo>> mCreatedClassList = new MutableLiveData<>();

    private final MutableLiveData<List<ClassInfo>> mJoinedClassList = new MutableLiveData<>();



    /*发布任务*/

    private final MutableLiveData<Boolean> mIsLoadingTaskList = new MutableLiveData<>();

    private final MutableLiveData<List<Task>> mTaskList = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mIsLoadingPostedTaskList = new MutableLiveData<>();

    private final MutableLiveData<List<Task>> mPostedTaskList = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mIsLoadingReceivedTaskList = new MutableLiveData<>();

    private final MutableLiveData<List<Task>> mReceivedTaskList = new MutableLiveData<>();


    private final ApiOpenid mApiOpenid;

    private final ApiClass mApiClass;


    private int indexClass;
    private int indexGroup;
    private String result;

    private RemoteDataSource() {
        mApiOpenid = ApiManager.getInstance().getApiOpenid();
        mApiClass = ApiManager.getInstance().getApiClass();
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
    public String getOpenid(String phoneno) {

        mApiOpenid.getOpenid(phoneno)
                .enqueue(new Callback<OpenidData>() {
                    @Override
                    public void onResponse(@NonNull Call<OpenidData> call, @NonNull Response<OpenidData> response) {
                        if (response.isSuccessful() || response.body().state == 0) {
                            result = response.body().data;
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<OpenidData> call, @NonNull Throwable t) {

                    }
                });

        return result;
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

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = null;
                        try {
                            date = sdf.parse(sdf.format(new Date()));
                            Log.d("wewein", "done: " + date);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                        BmobQuery<HomeWork> query = new BmobQuery<>();
                        BmobQuery<GroupInfo> innerQuery = new BmobQuery<>();
                        innerQuery.addWhereContainedIn("objectId", groupIds);
                        query.addWhereMatchesQuery("groupInfo", "GroupInfo", innerQuery);
                        query.addWhereGreaterThan("homeworkDeadline", new BmobDate(date));
                        query.include("creatorUser");
                        query.setSkip(10 * (index - 1));
                        query.setLimit(10);
                        query.order("homeworkDeadline");
                        query.findObjects(new FindListener<HomeWork>() {
                            @Override
                            public void done(List<HomeWork> list, BmobException e) {
                                mIsLoadingHomeWorkList.setValue(false);

                                if (e == null) {
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
            BmobQuery<HomeWork> query = new BmobQuery<>();
            query.addWhereEqualTo("creatorUser", new BmobPointer(user));
            query.include("creatorUser");
            query.setSkip(10 * (index - 1));
            query.setLimit(10);
            query.order("-createdAt");
            query.findObjects(new FindListener<HomeWork>() {
                @Override
                public void done(List<HomeWork> list, BmobException e) {
                    mIsLoadingPostedHomeWorkList.setValue(false);

                    if (e == null) {
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
    public LiveData<List<ClassInfo>> getCreatedClassList() {
        mIsLoadingClassList.setValue(true);

        mApiClass.getClassesCreate("oiooR1kiOuI242pqm0N9TIFLugeg")
                .enqueue(new Callback<ClassData>() {
                    @Override
                    public void onResponse(@NonNull Call<ClassData> call, @NonNull Response<ClassData> response) {
                        mIsLoadingClassList.setValue(false);
                        if (response.isSuccessful() || response.body().state == 0) {
                            mCreatedClassList.setValue(response.body().data);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ClassData> call, @NonNull Throwable t) {
                        mIsLoadingClassList.setValue(false);
                    }
                });

        return mCreatedClassList;

        //final MyUser user = BmobUser.getCurrentUser(MyUser.class);

        /*if (user != null) {
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

                        if (list.size() == 0) {
                            mClassList.setValue(mClassData);
                        }
                    }
                }
            });
        } else {
            mIsLoadingClassList.setValue(false);
        }*/
    }

    @Override
    public LiveData<List<ClassInfo>> getJoinedClassList() {
        mIsLoadingClassList.setValue(true);

        mApiClass.getClassesJoin("oiooR1kiOuI242pqm0N9TIFLugeg")
                .enqueue(new Callback<ClassData>() {
                    @Override
                    public void onResponse(@NonNull Call<ClassData> call, @NonNull Response<ClassData> response) {
                        mIsLoadingClassList.setValue(false);
                        if (response.isSuccessful() || response.body().state == 0) {
                            mJoinedClassList.setValue(response.body().data);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ClassData> call, @NonNull Throwable t) {
                        mIsLoadingClassList.setValue(false);
                    }
                });

        return mJoinedClassList;
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

            BmobQuery<GroupInfo> innerQuery = new BmobQuery<>();
            innerQuery.addWhereEqualTo("auth", 1);

            query.addWhereMatchesQuery("targetGroupInfo", "GroupInfo", innerQuery);
            query.include("targetGroupInfo");
            query.findObjects(new FindListener<GroupMember>() {
                @Override
                public void done(List<GroupMember> list, BmobException e) {
                    if (e == null) {
                        if (list.size() == 0) {
                            mStudentClassList.setValue(mClassData);
                            return;
                        }

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

    @Override
    public LiveData<List<Task>> getTaskList(int index) {
        mIsLoadingTaskList.setValue(true);
        final MyUser user = BmobUser.getCurrentUser(MyUser.class);

        if (user != null) {
            BmobQuery<Task> query = new BmobQuery<>();
            //添加条件，查询completed字段为非true的任务
            query.include("creatorUser");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentDate = null;
            try {
                currentDate = sdf.parse(sdf.format(new Date()));
            } catch (ParseException e) {
                e.printStackTrace();
                L.i("date parse failed");
            }
            //查询taskDeadline大于或等于当前时间的任务
            query.addWhereGreaterThanOrEqualTo("taskDeadline", new BmobDate(currentDate));

            query.setSkip(10 * (index - 1));
            query.setLimit(10);
            query.order("taskDeadline");
            query.findObjects(new FindListener<Task>() {
                @Override
                public void done(List<Task> list, BmobException e) {
                    mIsLoadingTaskList.setValue(false);
                    if (e == null) {
                        mTaskList.setValue(list);
                    }
                }
            });
        } else {
            mIsLoadingTaskList.setValue(false);
        }

        return mTaskList;
    }

    @Override
    public LiveData<Boolean> isLoadingTaskList() {
        return mIsLoadingTaskList;
    }

    @Override
    public LiveData<List<Task>> getPostedTaskList(int index) {
        mIsLoadingPostedTaskList.setValue(true);
        final MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if (user != null) {
            BmobQuery<Task> query = new BmobQuery<>();
            query.addWhereEqualTo("creatorUser", new BmobPointer(user));
            query.include("creatorUser");
            query.setSkip(10 * (index - 1));
            query.setLimit(10);
            query.order("-createdAt");
            query.findObjects(new FindListener<Task>() {
                @Override
                public void done(List<Task> list, BmobException e) {
                    mIsLoadingPostedTaskList.setValue(false);
                    if (e == null) {
                        mPostedTaskList.setValue(list);
                    }
                }
            });
        } else {
            mIsLoadingPostedTaskList.setValue(false);
        }
        return mPostedTaskList;
    }

    @Override
    public LiveData<Boolean> isLoadingPostedTaskList() {
        return mIsLoadingPostedTaskList;
    }

    @Override
    public LiveData<List<Task>> getReceivedTaskList(int index) {
        mIsLoadingReceivedTaskList.setValue(true);
        final MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if (user != null) {
            BmobQuery<Task> query = new BmobQuery<>();
            query.addWhereEqualTo("receiverUser", new BmobPointer(user));
            query.include("receiverUser");
            query.include("creatorUser");
            query.setSkip(10 * (index - 1));
            query.setLimit(10);
            query.order("taskDeadline");
            query.findObjects(new FindListener<Task>() {
                @Override
                public void done(List<Task> list, BmobException e) {
                    mIsLoadingReceivedTaskList.setValue(false);
                    if (e == null) {
                        mReceivedTaskList.setValue(list);
                    }
                }
            });
        } else {
            mIsLoadingReceivedTaskList.setValue(false);
        }
        return mReceivedTaskList;
    }

    @Override
    public LiveData<Boolean> isLoadingReceivedTaskList() {
        return mIsLoadingReceivedTaskList;
    }
}

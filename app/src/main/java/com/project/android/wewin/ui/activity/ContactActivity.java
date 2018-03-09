package com.project.android.wewin.ui.activity;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.android.wewin.MyApplication;
import com.project.android.wewin.R;
import com.project.android.wewin.data.Injection;
import com.project.android.wewin.data.remote.model.Class;
import com.project.android.wewin.data.remote.model.Commit;
import com.project.android.wewin.data.remote.model.GroupInfo;
import com.project.android.wewin.data.remote.model.GroupMember;
import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.data.remote.model.MyUser;
import com.project.android.wewin.ui.adapter.ExpandListViewAdapter;
import com.project.android.wewin.utils.MyAlertDialog;
import com.project.android.wewin.utils.Util;
import com.project.android.wewin.viewmodel.ContactViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author pengming
 */
public class ContactActivity extends AppCompatActivity implements View.OnClickListener {

    private final String[] strings = {MyApplication.getContext().getString(R.string.contact_add_user),
            MyApplication.getContext().getString(R.string.contact_add_class)};


    private Class mClass;
    private GroupInfo groupInfo;
    private GroupMember groupMember;


    @BindView(R.id.contact_toolbar)
    Toolbar toolbar;
    @BindView(R.id.contact_fab)
    FloatingActionButton fab;
    @BindView(R.id.expanded_lv)
    ExpandableListView expandedLv;
    @BindView(R.id.null_class_found)
    TextView nullClassFound;
    @BindView(R.id.contact_spl)
    SwipeRefreshLayout mRefreshLayout;


    private MyUser user;
    private ExpandListViewAdapter mAdapter;
    private List<Class> mClassData = new ArrayList<>();

    private ContactViewModel mContactViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab.setOnClickListener(this);

        mRefreshLayout.setOnRefreshListener(new ContactSwipeListener());
        mRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mAdapter = new ExpandListViewAdapter(this);
        expandedLv.setAdapter(mAdapter);
        expandedLv.setOnItemLongClickListener(onItemLongClickListener);
    }


    @Override
    protected void onStart() {
        super.onStart();
        user = BmobUser.getCurrentUser(MyUser.class);

        subscribeUI();
    }


    private void subscribeUI() {

        ContactViewModel.Factory factory = new ContactViewModel
                .Factory(MyApplication.getInstance(),
                Injection.getDataRepository(MyApplication.getInstance()));
        mContactViewModel = ViewModelProviders.of(this, factory).get(ContactViewModel.class);
        mContactViewModel.getClassList().observe(this, new Observer<List<Class>>() {
            @Override
            public void onChanged(@Nullable List<Class> classes) {

                if (classes == null) {
                    return;
                }

                if (classes.size() != 0) {
                    nullClassFound.setVisibility(View.GONE);
                } else {
                    nullClassFound.setVisibility(View.VISIBLE);
                }

                mClassData.clear();
                mClassData.addAll(classes);

                mAdapter.clearClassList();
                mAdapter.setClassList(classes);

            }
        });

        mContactViewModel.isLoadingClassList().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == null) {
                    return;
                }

                mRefreshLayout.setRefreshing(aBoolean);

            }
        });

        mRefreshLayout.setRefreshing(true);
        mContactViewModel.loadClassList();

    }



    private class ContactSwipeListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            mRefreshLayout.setRefreshing(true);
            mContactViewModel.loadClassList();
        }
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.contact_fab:
                MyAlertDialog dialog = new MyAlertDialog(ContactActivity.this, strings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                addUser();
                                break;
                            case 1:
                                addClass();
                                break;
                            default:
                        }
                    }
                });

                dialog.initDialog();
                break;
            default:
        }
    }



    private ExpandableListView.OnItemLongClickListener onItemLongClickListener = new ExpandableListView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, final long l) {

            if (ExpandableListView.getPackedPositionType(l) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {

                long packedPosition = ((ExpandableListView) adapterView).getExpandableListPosition(i);
                final int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);

                final String[] modifyClass = {"编辑班级", "删除班级"};
                final MyAlertDialog dialog = new MyAlertDialog(view.getContext(), modifyClass, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:

                                View alertView = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_modify_username, null);
                                final EditText editText = alertView.findViewById(R.id.alert_modified_username);

                                AlertDialog mDialog;
                                AlertDialog.Builder build = new AlertDialog.Builder(view.getContext());
                                build.setTitle(R.string.alert_dialog_modify_classname);
                                build.setView(alertView);
                                build.setPositiveButton(R.string.alert_confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        mRefreshLayout.setRefreshing(true);

                                        Class mClass = new Class();
                                        mClass.setClassName(editText.getText().toString());
                                        mClass.update(mClassData.get(groupPosition).getObjectId(), new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e == null) {
                                                    mContactViewModel.loadClassList();
                                                }
                                            }
                                        });
                                    }
                                });

                                build.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                                mDialog = build.create();
                                mDialog.show();

                                break;
                            case 1:

                                if (mClassData.get(groupPosition).getCreatorUser().getObjectId().equals(user.getObjectId())) {

                                    AlertDialog myDialog;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
                                    builder.setTitle(getString(R.string.delete_class));
                                    builder.setMessage(getString(R.string.delete_class_message));
                                    builder.setPositiveButton(R.string.alert_confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            mRefreshLayout.setRefreshing(true);


                                            for (final GroupInfo groupInfo : mClassData.get(groupPosition).getGroupInfos()) {

                                                for (GroupMember groupMember : groupInfo.getGroupMembers()) {
                                                    groupMember.delete(new UpdateListener() {
                                                        @Override
                                                        public void done(BmobException e) {

                                                        }
                                                    });
                                                }


                                                BmobQuery<HomeWork> query = new BmobQuery<>();
                                                query.addWhereEqualTo("groupInfo", new BmobPointer(groupInfo));
                                                query.findObjects(new FindListener<HomeWork>() {
                                                    @Override
                                                    public void done(List<HomeWork> list, BmobException e) {
                                                        groupInfo.delete(new UpdateListener() {
                                                            @Override
                                                            public void done(BmobException e) {
                                                                if (e == null) {

                                                                    if (mClassData.get(groupPosition).getGroupInfos().indexOf(groupInfo) == mClassData.get(groupPosition).getGroupInfos().size() - 1) {

                                                                        Class mClass = new Class();
                                                                        mClass.setObjectId(mClassData.get(groupPosition).getObjectId());
                                                                        mClass.delete(new UpdateListener() {
                                                                            @Override
                                                                            public void done(BmobException e) {
                                                                                if (e == null) {
                                                                                    mContactViewModel.loadClassList();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }
                                                        });

                                                        if (e == null && list.size() != 0) {
                                                            for (final HomeWork homeWork : list) {

                                                                BmobQuery<Commit> query = new BmobQuery<>();
                                                                query.addWhereEqualTo("mHomework", new BmobPointer(homeWork));
                                                                query.findObjects(new FindListener<Commit>() {
                                                                    @Override
                                                                    public void done(List<Commit> list, BmobException e) {

                                                                        homeWork.delete(new UpdateListener() {
                                                                            @Override
                                                                            public void done(BmobException e) {

                                                                            }
                                                                        });

                                                                        if (e == null && list.size() != 0) {

                                                                            for (Commit commit : list) {
                                                                                commit.delete(new UpdateListener() {
                                                                                    @Override
                                                                                    public void done(BmobException e) {

                                                                                    }
                                                                                });
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

                                    builder.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                                    myDialog = builder.create();
                                    myDialog.show();

                                } else {

                                    AlertDialog myDialog;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
                                    builder.setTitle(getString(R.string.exit_class));
                                    builder.setMessage(getString(R.string.exit_class_message));
                                    builder.setPositiveButton(R.string.alert_confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            mRefreshLayout.setRefreshing(true);

                                            for (final GroupInfo groupInfo : mClassData.get(groupPosition).getGroupInfos()) {
                                                for (GroupMember groupMember : groupInfo.getGroupMembers()) {
                                                    if (groupMember.getMemberUser().getObjectId().equals(user.getObjectId())) {

                                                        groupMember.delete(new UpdateListener() {
                                                            @Override
                                                            public void done(BmobException e) {
                                                                if (e == null) {

                                                                    GroupInfo mGroupInfo = new GroupInfo();
                                                                    mGroupInfo.setMemberSize(groupInfo.getMemberSize() - 1);
                                                                    mGroupInfo.update(groupInfo.getObjectId(), new UpdateListener() {
                                                                        @Override
                                                                        public void done(BmobException e) {
                                                                            if (e == null) {

                                                                                Class mClass = new Class();
                                                                                if (groupInfo.getAuth() == 0) {
                                                                                    mClass.setStudentSize(mClassData.get(groupPosition).getStudentSize() - 1);
                                                                                } else {
                                                                                    mClass.setTeacherSize(mClassData.get(groupPosition).getTeacherSize() - 1);
                                                                                }
                                                                                mClass.update(mClassData.get(groupPosition).getObjectId(), new UpdateListener() {
                                                                                    @Override
                                                                                    public void done(BmobException e) {
                                                                                        if (e == null) {
                                                                                            mContactViewModel.loadClassList();
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

                                        }
                                    });

                                    builder.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                                    myDialog = builder.create();
                                    myDialog.show();
                                }

                                break;
                            default:
                        }
                    }
                });

                dialog.initDialog();


            }
            return false;
        }
    };



    /*添加用户开始*/

    private void addUser() {
        if (mClassData.size() == 0) {
            Toast.makeText(this, getString(R.string.null_class_while_add_group), Toast.LENGTH_SHORT).show();
            return;
        }

        mClass = new Class();
        groupInfo = new GroupInfo();
        groupMember = new GroupMember();

        final MyUser[] searchedUser = new MyUser[1];

        View alertView = LayoutInflater.from(this).inflate(R.layout.alert_add_user, null);
        final EditText editText = alertView.findViewById(R.id.alert_user_phone);
        final ImageView search = alertView.findViewById(R.id.alert_user_search);
        final ImageView add = alertView.findViewById(R.id.alert_user_add);

        final ImageView photo = alertView.findViewById(R.id.alert_user_photo);
        final TextView username = alertView.findViewById(R.id.alert_user_name);

        final RelativeLayout alertRlClass = alertView.findViewById(R.id.alert_group_class);
        final RelativeLayout alertRlGroup = alertView.findViewById(R.id.alert_group_group);

        final TextView tvClass = alertView.findViewById(R.id.alert_group_choose_class);
        final TextView tvGroup = alertView.findViewById(R.id.alert_group_choose_group);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BmobQuery<MyUser> query = new BmobQuery<>();
                query.addWhereEqualTo("mobilePhoneNumber", editText.getText().toString());
                query.findObjects(new FindListener<MyUser>() {
                    @Override
                    public void done(List<MyUser> list, BmobException e) {
                        if (e == null && list.size() != 0) {
                            add.setVisibility(View.VISIBLE);


                            Util.loadCircleImage(Uri.parse(list.get(0).getUserPhoto()), photo);

                            username.setText(list.get(0).getUsername());

                            groupMember.setMemberUser(list.get(0));

                            searchedUser[0] = list.get(0);
                        }
                    }
                });
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertRlClass.setVisibility(View.VISIBLE);
                alertRlGroup.setVisibility(View.VISIBLE);
            }
        });


        final String[] classNames = new String[mClassData.size()];
        for (int i = 0; i < mClassData.size(); i++) {
            classNames[i] = mClassData.get(i).getClassName();
        }

        final int[] index1 = new int[1];

        alertRlClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MyAlertDialog dialog = new MyAlertDialog(ContactActivity.this, classNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tvClass.setText(classNames[i]);

                        index1[0] = i;
                    }
                });

                dialog.initDialog();
            }
        });


        final String[] searchedGroupNames = new String[mClassData.get(index1[0]).getGroupInfos().size()];
        for (int i = 0; i < mClassData.get(index1[0]).getGroupInfos().size(); i++) {
            searchedGroupNames[i] = mClassData.get(index1[0]).getGroupInfos().get(i).getGroupName();
        }

        final String[] groupNames = {getString(R.string.alert_class_student), getString(R.string.alert_class_teacher)};

        final int[] index2 = new int[1];

        final boolean[] createGroupInfo = new boolean[1];

        alertRlGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MyAlertDialog dialog = new MyAlertDialog(ContactActivity.this, groupNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, final int i) {
                        tvGroup.setText(groupNames[i]);


                        switch (searchedGroupNames.length) {
                            case 0:
                                createGroupInfo[0] = true;
                                index2[0] = i;
                                break;
                            case 1:
                                if (groupNames[i].equals(searchedGroupNames[0])) {
                                    createGroupInfo[0] = false;
                                    index2[0] = 0;
                                } else {
                                    createGroupInfo[0] = true;
                                    index2[0] = i;
                                }
                                break;
                            case 2:
                                createGroupInfo[0] = false;
                                if (groupNames[i].equals(searchedGroupNames[i])) {
                                    index2[0] = i;
                                } else {
                                    index2[0] = 1 - i;
                                }
                                break;
                            default:
                        }


                        BmobQuery<Class> query = new BmobQuery<>();
                        query.getObject(mClassData.get(index1[0]).getObjectId(), new QueryListener<Class>() {
                            @Override
                            public void done(Class aClass, BmobException e) {
                                if (e == null) {
                                    if (i == 0) {
                                        mClass.setStudentSize(aClass.getStudentSize() + 1);
                                    } else {
                                        mClass.setTeacherSize(aClass.getTeacherSize() + 1);
                                    }
                                }
                            }
                        });

                    }
                });

                dialog.initDialog();
            }
        });


        AlertDialog myDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.contact_add_user);
        builder.setView(alertView);
        builder.setPositiveButton(R.string.alert_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {

                for (GroupInfo groupInfo : mClassData.get(index1[0]).getGroupInfos()) {
                    for (GroupMember groupMember : groupInfo.getGroupMembers()) {
                        if (groupMember.getMemberUser().getObjectId().equals(searchedUser[0].getObjectId())) {

                            Toast.makeText(ContactActivity.this, getString(R.string.alert_user_exist_error), Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                            return;
                        }
                    }
                }

                mClass.update(mClassData.get(index1[0]).getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {

                        }
                    }
                });

                if (createGroupInfo[0]) {

                    groupInfo.setTargetClass(mClassData.get(index1[0]));
                    groupInfo.setGroupName(groupNames[index2[0]]);
                    groupInfo.setMemberSize(1);
                    groupInfo.setAuth(index2[0]);
                    groupInfo.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {

                                BmobQuery<GroupInfo> query = new BmobQuery<>();
                                query.getObject(s, new QueryListener<GroupInfo>() {
                                    @Override
                                    public void done(GroupInfo groupInfo, BmobException e) {
                                        if (e == null) {
                                            createUserGroupMember(groupInfo);
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else {

                    groupInfo.setMemberSize(mClassData.get(index1[0]).getGroupInfos().get(index2[0]).getMemberSize() + 1);
                    groupInfo.update(mClassData.get(index1[0]).getGroupInfos().get(index2[0]).getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                createUserGroupMember(mClassData.get(index1[0]).getGroupInfos().get(index2[0]));
                            }
                        }
                    });
                }

                mRefreshLayout.setRefreshing(true);
            }
        });

        builder.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        myDialog = builder.create();
        myDialog.show();

    }


    private void createUserGroupMember(GroupInfo groupInfo) {
        groupMember.setTargetGroupInfo(groupInfo);
        groupMember.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {

                    mContactViewModel.loadClassList();
                }
            }
        });
    }

    /*添加用户结束*/




    /*添加班级开始*/

    private void addClass() {

        mClass = new Class();
        groupInfo = new GroupInfo();
        groupMember = new GroupMember();

        View alertView = LayoutInflater.from(this).inflate(R.layout.alert_add_class, null);
        final EditText editText = alertView.findViewById(R.id.alert_class_name);
        RadioGroup radioGroup = alertView.findViewById(R.id.alert_class_auth);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.alert_class_teacher:
                        mClass.setStudentSize(0);
                        mClass.setTeacherSize(1);
                        groupInfo.setGroupName(getString(R.string.alert_class_teacher));
                        groupInfo.setMemberSize(1);
                        groupInfo.setAuth(1);
                        break;
                    case R.id.alert_class_student:
                        mClass.setStudentSize(1);
                        mClass.setTeacherSize(0);
                        groupInfo.setGroupName(getString(R.string.alert_class_student));
                        groupInfo.setMemberSize(1);
                        groupInfo.setAuth(0);
                        break;
                    default:
                }
            }
        });


        AlertDialog myDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.contact_add_class);
        builder.setView(alertView);
        builder.setPositiveButton(R.string.alert_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mRefreshLayout.setRefreshing(true);

                mClass.setClassName(editText.getText().toString());

                mClass.setCreatorUser(user);
                mClass.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {

                            BmobQuery<Class> query = new BmobQuery<>();
                            query.getObject(s, new QueryListener<Class>() {
                                @Override
                                public void done(Class aClass, BmobException e) {
                                    if (e == null) {
                                        createGroupInfo(aClass);
                                    }
                                }
                            });
                        }
                    }
                });

            }
        });

        builder.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        myDialog = builder.create();
        myDialog.show();
    }


    private void createGroupInfo(Class mClass) {
        groupInfo.setTargetClass(mClass);
        groupInfo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {

                    BmobQuery<GroupInfo> query = new BmobQuery<>();
                    query.getObject(s, new QueryListener<GroupInfo>() {
                        @Override
                        public void done(GroupInfo groupInfo, BmobException e) {
                            if (e == null) {
                                createGroupMember(groupInfo);
                            }
                        }
                    });
                }
            }
        });
    }

    private void createGroupMember(GroupInfo groupInfo) {
        groupMember.setTargetGroupInfo(groupInfo);
        groupMember.setMemberUser(user);
        groupMember.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {

                    mContactViewModel.loadClassList();
                }
            }
        });
    }

    /*添加班级结束*/



    /*添加群组开始*/

    /*private void addGroup() {
        if (mClassData.size() == 0) {
            Toast.makeText(this, getString(R.string.null_class_while_add_group), Toast.LENGTH_SHORT).show();
            return;
        }

        mClass = new Class();
        groupInfo = new GroupInfo();


        View alertView = LayoutInflater.from(this).inflate(R.layout.alert_add_group, null);
        RelativeLayout alertRlClass = alertView.findViewById(R.id.alert_group_class);
        RelativeLayout alertRlGroup = alertView.findViewById(R.id.alert_group_group);

        final TextView tvClass = alertView.findViewById(R.id.alert_group_choose_class);
        final TextView tvGroup = alertView.findViewById(R.id.alert_group_choose_group);


        final String[] classNames = new String[mClassData.size()];
        for (int i = 0; i < mClassData.size(); i++) {
            classNames[i] = mClassData.get(i).getClassName();
        }

        final String[] groupNames = {getString(R.string.alert_class_teacher), getString(R.string.alert_class_student)};

        alertRlClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MyAlertDialog dialog = new MyAlertDialog(ContactActivity.this, classNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tvClass.setText(classNames[i]);

                        groupInfo.setTargetClass(mClassData.get(i));
                    }
                });

                dialog.initDialog();
            }
        });



        alertRlGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MyAlertDialog dialog = new MyAlertDialog(ContactActivity.this, groupNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, final int i) {
                        tvGroup.setText(groupNames[i]);

                        if (i == 0) {
                            groupInfo.setGroupName(getString(R.string.alert_class_teacher));
                            groupInfo.setAuth(1);
                        }
                        if (i == 1) {
                            groupInfo.setGroupName(getString(R.string.alert_class_student));
                            groupInfo.setAuth(0);
                        }
                    }
                });

                dialog.initDialog();
            }
        });


        AlertDialog myDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.contact_add_group);
        builder.setView(alertView);
        builder.setPositiveButton(R.string.alert_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                groupInfo.setMemberSize(0);
                groupInfo.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            InitData.initData(user);
                            Toast.makeText(ContactActivity.this, getString(R.string.contact_add_group_success), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });

        builder.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        myDialog = builder.create();
        myDialog.show();

    }
*/
    /*添加群组结束*/

}

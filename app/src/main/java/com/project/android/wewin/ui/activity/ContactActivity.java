package com.project.android.wewin.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.project.android.wewin.MyApplication;
import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.Class;
import com.project.android.wewin.data.remote.model.GroupInfo;
import com.project.android.wewin.data.remote.model.GroupMember;
import com.project.android.wewin.data.remote.model.MyUser;
import com.project.android.wewin.ui.adapter.ExpandListViewAdapter;
import com.project.android.wewin.utils.MyAlertDialog;
import com.project.android.wewin.utils.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.b.V;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author pengming
 */
public class ContactActivity extends AppCompatActivity implements View.OnClickListener{

    private final String[] strings = {MyApplication.getContext().getString(R.string.contact_add_user),
            MyApplication.getContext().getString(R.string.contact_add_class),
            MyApplication.getContext().getString(R.string.contact_add_group)};

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

    private MyUser user;
    private ExpandListViewAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab.setOnClickListener(this);

        user = BmobUser.getCurrentUser(MyUser.class);

        mAdapter = new ExpandListViewAdapter(user.getmClasses(), this);
        expandedLv.setAdapter(mAdapter);

        if (user.getmClasses().size() == 0) {
            nullClassFound.setVisibility(View.VISIBLE);
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
                            case 2:
                                addGroup();
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


    //添加用户开始

    private void addUser() {
        if (user.getmClasses().size() == 0) {
            Toast.makeText(this, getString(R.string.null_class_while_add_group), Toast.LENGTH_SHORT).show();
            return;
        }

        mClass = new Class();
        groupInfo = new GroupInfo();
        groupMember = new GroupMember();


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
                BmobQuery<MyUser> query = new BmobQuery<MyUser>();
                query.addWhereEqualTo("mobilePhoneNumber", editText.getText().toString());
                query.findObjects(new FindListener<MyUser>() {
                    @Override
                    public void done(List<MyUser> list, BmobException e) {
                        if (e == null && list.size() != 0) {
                            add.setVisibility(View.VISIBLE);

                            if (list.get(0).getUserPhoto() != null){
                                Util.loadCircleImage(Uri.parse(user.getUserPhoto()), photo);
                            } else {
                                Util.loadCircleImage(Uri.parse(""), photo);
                            }
                            username.setText(list.get(0).getUsername());

                            groupMember.setUserId(list.get(0).getObjectId());
                            groupMember.setUserName(list.get(0).getUsername());
                            groupMember.setUserPhoto(list.get(0).getUserPhoto());
                            groupMember.setUserPhoneNumber(list.get(0).getMobilePhoneNumber());
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


        final String[] classNames = new String[user.getmClasses().size()];
        for (int i = 0; i < user.getmClasses().size(); i++) {
            classNames[i] = user.getmClasses().get(i).getClassName();
        }

        final String[] groupNames = {getString(R.string.alert_class_teacher), getString(R.string.alert_class_student)};

        final String[] classId = new String[1];
        final String[] groupId = new String[1];

        alertRlClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MyAlertDialog dialog = new MyAlertDialog(ContactActivity.this, classNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tvClass.setText(classNames[i]);

                        classId[0] = user.getmClasses().get(i).getObjectId();
                    }
                });

                dialog.initDialog();
            }
        });


        //todo 选择已有的群组
        alertRlGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MyAlertDialog dialog = new MyAlertDialog(ContactActivity.this, groupNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, final int i) {
                        tvGroup.setText(groupNames[i]);

                        BmobQuery<GroupInfo> query1 = new BmobQuery<GroupInfo>();
                        query1.addWhereEqualTo("classId", classId[0]);
                        query1.addWhereEqualTo("auth", 1 - i);
                        query1.findObjects(new FindListener<GroupInfo>() {
                            @Override
                            public void done(List<GroupInfo> list, BmobException e) {
                                if (e == null) {
                                    groupId[0] = list.get(0).getObjectId();
                                    groupInfo.setMemberSize(list.get(0).getMemberSize() + 1);
                                }
                            }
                        });


                        BmobQuery<Class> query2 = new BmobQuery<Class>();
                        query2.getObject(classId[0], new QueryListener<Class>() {
                            @Override
                            public void done(Class aClass, BmobException e) {
                                if (e == null) {
                                    if (i == 0) {
                                        mClass.setTeacherSize(aClass.getTeacherSize() + 1);
                                    }
                                    if (i == 1) {
                                        mClass.setStudentSize(aClass.getStudentSize() + 1);
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
            public void onClick(DialogInterface dialogInterface, int i) {
                groupMember.setGroupId(groupId[0]);
                groupMember.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {

                            groupInfo.update(groupId[0], new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {

                                        mClass.update(classId[0], new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e == null) {
                                                    InitData.initData(user);

                                                    Toast.makeText(ContactActivity.this, getString(R.string.contact_add_user_success), Toast.LENGTH_SHORT).show();
                                                    finish();
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

    //添加用户结束




    //添加班级开始
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

                mClass.setClassName(editText.getText().toString());

                mClass.setCreatorId(user.getObjectId());
                mClass.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            createGroupInfo(s);
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


    private void createGroupInfo(final String classId) {
        groupInfo.setClassId(classId);
        groupInfo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    createGroupMember(s);
                }
            }
        });
    }

    private void createGroupMember(final String groupInfoId) {
        groupMember.setGroupId(groupInfoId);
        groupMember.setUserId(user.getObjectId());
        groupMember.setUserName(user.getUsername());
        groupMember.setUserPhoto(user.getUserPhoto());
        groupMember.setUserPhoneNumber(user.getMobilePhoneNumber());
        groupMember.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    if (nullClassFound.getVisibility() == View.VISIBLE) {
                        nullClassFound.setVisibility(View.GONE);
                    }
                    InitData.initData(user);
                    Toast.makeText(ContactActivity.this, getString(R.string.contact_add_class_success), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    //添加班级结束



    //添加群组开始

    private void addGroup() {
        if (user.getmClasses().size() == 0) {
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


        final String[] classNames = new String[user.getmClasses().size()];
        for (int i = 0; i < user.getmClasses().size(); i++) {
            classNames[i] = user.getmClasses().get(i).getClassName();
        }

        final String[] groupNames = {getString(R.string.alert_class_teacher), getString(R.string.alert_class_student)};

        final String[] classId = new String[1];


        alertRlClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MyAlertDialog dialog = new MyAlertDialog(ContactActivity.this, classNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tvClass.setText(classNames[i]);
                        groupInfo.setClassId(user.getmClasses().get(i).getObjectId());
                        classId[0] = user.getmClasses().get(i).getObjectId();
                    }
                });

                dialog.initDialog();
            }
        });


        //todo 隐藏已有的群组
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

    //添加群组结束

}

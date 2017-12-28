package com.project.android.wewin.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.project.android.wewin.MyApplication;
import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.Class;
import com.project.android.wewin.data.remote.model.GroupInfo;
import com.project.android.wewin.data.remote.model.GroupMember;
import com.project.android.wewin.data.remote.model.MyUser;
import com.project.android.wewin.utils.MyAlertDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
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

    private MyUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = BmobUser.getCurrentUser(MyUser.class);

        fab.setOnClickListener(this);
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

                            BmobQuery<Class> query = new BmobQuery<Class>();
                            query.getObject(s, new QueryListener<Class>() {
                                @Override
                                public void done(Class aClass, BmobException e) {
                                    if (e == null) {
                                        updateUser(aClass);
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


    private void createGroupInfo(final String classId) {
        groupInfo.setClassId(classId);
        groupInfo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    createGroupMember(s);

                    BmobQuery<GroupInfo> query = new BmobQuery<GroupInfo>();
                    query.getObject(s, new QueryListener<GroupInfo>() {
                        @Override
                        public void done(GroupInfo groupInfo, BmobException e) {
                            if (e == null) {
                                updateClass(groupInfo, classId);
                            }
                        }
                    });
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
                if (e == null){
                    BmobQuery<GroupMember> query = new BmobQuery<GroupMember>();
                    query.getObject(s, new QueryListener<GroupMember>() {
                        @Override
                        public void done(GroupMember groupMember, BmobException e) {
                            if (e == null) {
                                updateGroupInfo(groupMember, groupInfoId);
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateGroupInfo(GroupMember groupMember, String groupInfoId) {
        GroupInfo gi = new GroupInfo();
        gi.add("groupMembers", groupMember);
        gi.update(groupInfoId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                }
            }
        });
    }

    private void updateClass(GroupInfo groupInfo, String classId) {
        Class c = new Class();
        c.add("groupInfos", groupInfo);
        c.update(classId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                }
            }
        });
    }

    private void updateUser(Class mClass) {
        MyUser newMyUser = new MyUser();
        newMyUser.add("mClasses", mClass);
        newMyUser.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {

                }
            }
        });
    }

    //添加班级结束



    //添加群组开始
    private void addGroup() {

    }

    //添加群组结束
}

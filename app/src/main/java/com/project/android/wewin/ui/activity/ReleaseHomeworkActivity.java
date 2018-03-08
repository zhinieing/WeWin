package com.project.android.wewin.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.project.android.wewin.MyApplication;
import com.project.android.wewin.R;
import com.project.android.wewin.data.Injection;
import com.project.android.wewin.data.remote.model.Class;
import com.project.android.wewin.data.remote.model.GroupInfo;
import com.project.android.wewin.data.remote.model.GroupMember;
import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.data.remote.model.MyUser;
import com.project.android.wewin.utils.MyAlertDialog;
import com.project.android.wewin.utils.Util;
import com.project.android.wewin.viewmodel.ReleaseHomeWorkViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

/**
 * ReleaseHomeworkActivity class
 *
 * @author zhoutao
 * @date 2017/12/20
 */

public class ReleaseHomeworkActivity extends AppCompatActivity implements View.OnClickListener, OnDateSetListener {

    private final static int FILE_SELECT_CODE = 0;

    private final static String TAG = "Wewin";
    @BindView(R.id.release_homework_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_user_photo)
    ImageView toolbarUserPhoto;
    @BindView(R.id.release_homework_confirm)
    ImageButton mConfirm;

    @BindView(R.id.release_homework_title)
    EditText mHomeworkTitle;
    @BindView(R.id.release_homework_content)
    EditText mHomeworkContent;
    @BindView(R.id.release_homework_attachment)
    Button mAttachment;

    @BindView(R.id.release_homework_attachment_layout)
    LinearLayout mAttachmentLayout;

    @BindView(R.id.upload_add_target)
    RelativeLayout uploadAddTarget;
    @BindView(R.id.upload_deadline)
    RelativeLayout uploadDeadline;
    @BindView(R.id.release_deadline)
    TextView mDeadline;
    @BindView(R.id.release_add_target)
    TextView mAddTarget;


    private TimePickerDialog mDialogAll;

    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private HomeWork mHomeWork = new HomeWork();
    private MyUser user;

    private List<Class> mClassData = new ArrayList<>();
    private ReleaseHomeWorkViewModel mReleaseHomeWorkViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_homework);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_release_cancel);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        uploadAddTarget.setOnClickListener(this);
        uploadDeadline.setOnClickListener(this);
        mAttachment.setOnClickListener(this);
        mConfirm.setOnClickListener(this);


        user = BmobUser.getCurrentUser(MyUser.class);
        if (user.getUserPhoto() != null) {
            Util.loadCircleImage(Uri.parse(user.getUserPhoto()), toolbarUserPhoto);
        } else {
            Util.loadCircleImage(Uri.parse(""), toolbarUserPhoto);
        }

        initTimePicker();
        subscribeUI();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upload_deadline:
                mDialogAll.show(getSupportFragmentManager(), "all");
                break;
            case R.id.release_homework_attachment:
                addAttachment();
                break;
            case R.id.upload_add_target:
                addTarget();
                break;
            case R.id.release_homework_confirm:
                releaseConfirm();
                break;
            default:
                break;
        }
    }



    private void subscribeUI() {

        ReleaseHomeWorkViewModel.Factory factory = new ReleaseHomeWorkViewModel
                .Factory(MyApplication.getInstance(),
                Injection.getDataRepository(MyApplication.getInstance()));
        mReleaseHomeWorkViewModel = ViewModelProviders.of(this, factory).get(ReleaseHomeWorkViewModel.class);
        mReleaseHomeWorkViewModel.getStudentClassList().observe(this, new Observer<List<Class>>() {
            @Override
            public void onChanged(@Nullable List<Class> classes) {
                if (classes == null) {
                    return;
                }

                mClassData.clear();
                mClassData.addAll(classes);

                mAddTarget.setText(classes.get(0).getClassName());

                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_YEAR, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                mDeadline.setText(sf.format(cal.getTime()));
            }
        });

        mReleaseHomeWorkViewModel.loadStudentClassList();

    }

    private void releaseConfirm() {
        mHomeWork.setHomeworkTitle(mHomeworkTitle.getText().toString());
        mHomeWork.setHomeworkContent(mHomeworkContent.getText().toString());
        mHomeWork.setCreatorUser(user);
        if (ActivityCompat.checkSelfPermission(ReleaseHomeworkActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            if (isHomeworkValid()) {
                uploadAttachment();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uploadAttachment();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void uploadAttachment() {
        mConfirm.setClickable(false);

        int size = mAttachmentLayout.getChildCount();
        if (size == 0) {
            mHomeWork.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        Toast.makeText(ReleaseHomeworkActivity.this, getString(R.string.release_success), Toast.LENGTH_SHORT).show();
                        setResult(2);
                        finish();
                    } else {
                        Toast.makeText(ReleaseHomeworkActivity.this, getString(R.string.release_fail), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        final String[] filePaths = new String[size];
        for (int i = 0; i < size; i++) {
            filePaths[i] = mAttachmentLayout.getChildAt(i).getTag().toString();
        }
        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {
                if (list1.size() == filePaths.length) {
                    mHomeWork.addAll("attachmentPath", list1);
                    mHomeWork.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                Toast.makeText(ReleaseHomeworkActivity.this, "success upload:" + s, Toast.LENGTH_SHORT).show();
                                setResult(2);
                                finish();
                            } else {
                                Toast.makeText(ReleaseHomeworkActivity.this, "fail upload" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onProgress(int i, int i1, int i2, int i3) {
                Log.i("upload", "onProgress: " + "total:" + i2 + " percent:" + i3);
            }

            @Override
            public void onError(int i, String s) {
                mConfirm.setClickable(true);

                //Toast.makeText(ReleaseHomeworkActivity.this, "文件路径错误", Toast.LENGTH_SHORT).show();
                Log.i("error", "errorcode:" + i + " message:" + s);
            }
        });

    }

    private void addTarget() {
        if (mClassData.size() == 0) {
            Toast.makeText(this, getString(R.string.release_homework_none_student_error), Toast.LENGTH_SHORT).show();
            return;
        }


        final String[] classNames = new String[mClassData.size()];
        for (int i = 0; i < mClassData.size(); i++) {
            classNames[i] = mClassData.get(i).getClassName();
        }

        final MyAlertDialog dialog = new MyAlertDialog(ReleaseHomeworkActivity.this, classNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, final int i) {

                mAddTarget.setText(classNames[i]);
                mHomeWork.setGroupInfo(mClassData.get(i).getGroupInfos().get(0));

            }
        });

        dialog.initDialog();

    }

    private void addAttachment() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), FILE_SELECT_CODE);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FILE_SELECT_CODE) {
                Uri uri = data.getData();
                String fileName = uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1);
                final TextView textView = new TextView(this);
                textView.setText(fileName);
                textView.setTextSize(16);
                textView.setTag(uri.getPath());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(5, 5, 5, 5);
                textView.setLayoutParams(layoutParams);
                textView.setBackgroundColor(ContextCompat.getColor(this, R.color.attachmentBackground));
                textView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAttachmentLayout.removeView(textView);
                    }
                });
                mAttachmentLayout.addView(textView);
            }
        }
    }

    private void initTimePicker() {
        long fiveYears = 5L * 365 * 24L * 60 * 60 * 1000;
        mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("截止日期")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis())
                .setMaxMillseconds(System.currentTimeMillis() + fiveYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(ContextCompat.getColor(this, R.color.color_button_default))
                .setType(Type.ALL)
                .setWheelItemTextNormalColor(ContextCompat.getColor(this, R.color.textColorPrimary))
                .setWheelItemTextSelectorColor(ContextCompat.getColor(this, R.color.textColorPrimary))
                .setWheelItemTextSize(16)
                .build();
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        String text = getDateToString(millseconds);
        mDeadline.setText(text);
        mHomeWork.setHomeworkDeadline(text);
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }

    public boolean isHomeworkValid() {
        boolean homeworkValid = false;
        if (mHomeWork.getHomeworkTitle().isEmpty()) {
            Toast.makeText(this, "请输入作业标题", Toast.LENGTH_SHORT).show();
        } else if (mHomeWork.getHomeworkContent().isEmpty()) {
            Toast.makeText(this, "请输入作业内容", Toast.LENGTH_SHORT).show();
        } else if (mHomeWork.getHomeworkDeadline() == null) {
            Toast.makeText(this, "请选择截止日期", Toast.LENGTH_SHORT).show();
        } else if (mHomeWork.getGroupInfo() == null) {
            Toast.makeText(this, "请选择接收作业的班级", Toast.LENGTH_SHORT).show();
        } else {
            homeworkValid = true;
        }
        return homeworkValid;
    }
}

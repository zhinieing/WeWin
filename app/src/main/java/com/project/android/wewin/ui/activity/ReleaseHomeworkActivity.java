package com.project.android.wewin.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.HomeWork;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReleaseHomeworkActivity extends AppCompatActivity implements View.OnClickListener, OnDateSetListener {

    private final static int FILE_SELECT_CODE = 0;

    @BindView(R.id.release_homework_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;

    @BindView(R.id.release_homework_title)
    EditText mHomeworkTitle;

    @BindView(R.id.release_homework_content)
    EditText mHomeworkContent;

    @BindView(R.id.release_homework_attachment)
    ImageButton mAttachment;

    @BindView(R.id.release_attachment_layout)
    LinearLayout mAttachmentLayout;

    @BindView(R.id.release_deadline)
    Button mDeadline;

    @BindView(R.id.release_add_target)
    Button mAddTarget;

    @BindView(R.id.release_homework_confirm)
    Button mConfirm;

    private TimePickerDialog mDialogAll;

    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String mAccachmentPath;

    HomeWork mHomeWork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_homework);
        ButterKnife.bind(this);
        mToolbarTitle.setText(R.string.release);
        mToolbar.setNavigationIcon(R.drawable.ic_release_cancel);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mDeadline.setOnClickListener(this);
        mAttachment.setOnClickListener(this);
        mAddTarget.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        initTimePicker();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.release_deadline:
                mDialogAll.show(getSupportFragmentManager(), "all");
                break;
            case R.id.release_homework_attachment:
                addAttachment();
                break;
            case R.id.release_add_target:
                addTarget();
                break;
            case R.id.release_homework_confirm:
                releaseConfirm();
                break;
            default:
                break;
        }
    }

    private void releaseConfirm() {
        mAccachmentPath = "";
        for (int i = 0; i < mAttachmentLayout.getChildCount(); i++) {
            mAccachmentPath += mAttachmentLayout.getChildAt(i).getTag() + ";";
        }
        Toast.makeText(this, "path:" + mAccachmentPath, Toast.LENGTH_SHORT).show();
    }

    private void addTarget() {
    }

    private void addAttachment() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FILE_SELECT_CODE) {
                Uri uri = data.getData();
                Toast.makeText(this, "path:" + uri.getPath(), Toast.LENGTH_SHORT).show();
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
                .setThemeColor(ContextCompat.getColor(this, R.color.colorAccent))
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
        //mHomeWork.setHomeworkDeadline(text);
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }
}

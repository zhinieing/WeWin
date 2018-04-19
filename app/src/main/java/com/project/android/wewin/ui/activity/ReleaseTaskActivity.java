package com.project.android.wewin.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.MyUser;
import com.project.android.wewin.data.remote.model.Task;
import com.project.android.wewin.ui.adapter.FixedTextureVideoView;
import com.project.android.wewin.utils.L;
import com.project.android.wewin.utils.MyAlertDialog;
import com.project.android.wewin.utils.Util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

/**
 * ReleaseTaskActivity class
 *
 * @author zhoutao
 * @date 2017/12/20
 */
public class ReleaseTaskActivity extends AppCompatActivity implements View.OnClickListener, OnDateSetListener {

    private final static int FILE_SELECT_CODE = 0;

    private final static String TAG = "Wewin";
    @BindView(R.id.release_task_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_user_photo_task)
    ImageView toolbarUserPhoto;
    @BindView(R.id.release_task_confirm)
    ImageButton mConfirm;

    @BindView(R.id.release_task_title)
    EditText mTaskTitle;
    @BindView(R.id.release_task_content)
    EditText mTaskContent;
    @BindView(R.id.release_attachment_media_task)
    GridLayout releaseAttachmentMedia;
    @BindView(R.id.release_attachment_file_task)
    LinearLayout releaseAttachmentFile;
    @BindView(R.id.release_task_attachment)
    Button mAttachment;

    @BindView(R.id.upload_add_reward)
    RelativeLayout uploadAddReward;
    @BindView(R.id.upload_add_category)
    RelativeLayout uploadAddCategory;
    @BindView(R.id.upload_deadline_task)
    RelativeLayout uploadDeadline;
    @BindView(R.id.release_deadline_task)
    TextView mDeadline;
    @BindView(R.id.release_add_reward)
    TextView mAddReward;
    @BindView(R.id.release_add_category)
    TextView mAddCategory;

    private TimePickerDialog mDialogAll;

    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private MyUser user;

    private Task mTask = new Task();

    LinkedList<String> attachments = new LinkedList<>();

    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_task);
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

        uploadDeadline.setOnClickListener(this);
        mAttachment.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
        uploadAddReward.setOnClickListener(this);
        uploadAddCategory.setOnClickListener(this);

        user = BmobUser.getCurrentUser(MyUser.class);
        Util.loadCircleImage(Uri.parse(user.getUserPhoto()), toolbarUserPhoto);

        initTimePicker();
        checkPermissions();

        progressDialog = new Dialog(ReleaseTaskActivity.this, R.style.progress_dialog);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView msg = progressDialog.findViewById(R.id.id_tv_loadingmsg);
        msg.setText("正在上传中");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.release_task_attachment:
                addAttachment();
                break;
            case R.id.release_task_confirm:
                releaseConfirm();
                break;
            case R.id.upload_deadline_task:
                mDialogAll.show(getSupportFragmentManager(), "all");
                break;
            case R.id.upload_add_reward:
                addReward();
                break;
            case R.id.upload_add_category:
                addCategory();
                break;
            default:
                break;
        }
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
                String path = Util.getPath(this, uri);
                L.i(path);
                //保存附件路径
                attachments.add(path);

                String type = Util.fileType(path.substring(path.lastIndexOf(".") + 1));

                if ("image".equals(type) || "video".equals(type)) {
                    releaseAttachmentMedia.addView(initMediaView(path));
                } else {
                    releaseAttachmentFile.addView(initFileView(path));
                }
            }
        }
    }


    private void releaseConfirm() {
        mTask.setTaskTitle(mTaskTitle.getText().toString());
        mTask.setTaskContent(mTaskContent.getText().toString());
        mTask.setCreatorUser(user);
        mTask.setCompleted(false);
        mTask.setReceiverUser(null);

        L.i(attachments.toString());
        if (isTaskValid()) {
            uploadAttachment();
        }
    }

    private void uploadAttachment() {
        progressDialog.show();
        L.i("start upload");
        if (attachments.size() == 0) {
            mTask.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        progressDialog.dismiss();
                        Toast.makeText(ReleaseTaskActivity.this, "文件上传成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        } else {
            final String[] filePaths = new String[attachments.size()];
            for (int i = 0; i < attachments.size(); i++) {
                filePaths[i] = attachments.get(i);
            }
            L.i(filePaths[0]);
            BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> list, List<String> list1) {
                    if (list1.size() == filePaths.length) {
                        L.i(list1.toString());

                        mTask.setAttachmentPath(list1);
                        mTask.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                Toast.makeText(ReleaseTaskActivity.this, "Task上传成功", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                finish();
                            }
                        });
                    }
                }

                @Override
                public void onProgress(int i, int i1, int i2, int i3) {

                }

                @Override
                public void onError(int i, String s) {
                    L.i(s + " upload error code:" + i);
                }
            });
        }
    }

    private void addReward() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReleaseTaskActivity.this);
        View view = View.inflate(ReleaseTaskActivity.this, R.layout.dialog_reward, null);
        final EditText editText = view.findViewById(R.id.dialog_reward);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    mTask.setTaskReward(Double.parseDouble(editText.getText().toString()));
                    mAddReward.setText(editText.getText().toString());
                } catch (Exception e) {
                    L.i("null reward");
                }
            }
        });
        final AlertDialog dialog = builder.create();

        dialog.show();

    }

    private void addCategory() {
        final String[] categoryNames = new String[]{"经济金融", "健康生活", "娱乐休闲", "企业管理",
                "体育运动", "地区", "法律法规", "文化艺术", "心理分析", "社会民生", "电子数码", "医疗卫生",
                "科学教育", "电脑网络"};

        final MyAlertDialog dialog = new MyAlertDialog(ReleaseTaskActivity.this, categoryNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, final int i) {
                mAddCategory.setText(categoryNames[i]);
                mTask.setTaskCategory(categoryNames[i]);
            }
        });

        dialog.initDialog();
    }


    private View initMediaView(final String path) {
        releaseAttachmentMedia.setVisibility(View.VISIBLE);

        String type = Util.fileType(path.substring(path.lastIndexOf(".") + 1));

        final View itemMediaView = View.inflate(this, R.layout.item_attachment_media, null);
        ImageView itemImage = itemMediaView.findViewById(R.id.item_media_image);
        FixedTextureVideoView itemVideo = itemMediaView.findViewById(R.id.item_media_video);
        final ImageView itemDelete = itemMediaView.findViewById(R.id.item_media_delete);
        itemMediaView.setTag(path);

        if ("image".equals(type)) {
            itemImage.setImageBitmap(BitmapFactory.decodeFile(path));
            itemImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemDelete.setVisibility(View.VISIBLE);
                    return true;
                }
            });
        } else {
            itemVideo.setVisibility(View.VISIBLE);

            itemVideo.setFixedSize(Util.dip2px(this, 100), Util.dip2px(this, 100));
            itemVideo.invalidate();

            itemVideo.setVideoPath(path);
            itemVideo.start();

            itemVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mp.setLooping(true);
                }
            });

            itemVideo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemDelete.setVisibility(View.VISIBLE);
                    return true;
                }
            });
        }

        itemDelete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemDelete.setVisibility(View.GONE);
                return true;
            }
        });

        itemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseAttachmentMedia.removeView(itemMediaView);
                attachments.remove(path);
                if (releaseAttachmentMedia.getChildCount() == 0) {
                    releaseAttachmentMedia.setVisibility(View.GONE);
                }
            }
        });

        return itemMediaView;
    }


    private View initFileView(final String path) {
        releaseAttachmentFile.setVisibility(View.VISIBLE);

        File file = new File(path);
        String fileSize = Util.ShowLongFileSzie(file.length());
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        int fileIcon = Util.fileIcon(Util.fileType(path.substring(path.lastIndexOf(".") + 1)));

        final View itemFileView = View.inflate(this, R.layout.item_attachment_file, null);
        ImageView itemFileIcon = itemFileView.findViewById(R.id.item_file_icon);
        TextView itemFileName = itemFileView.findViewById(R.id.item_file_name);
        TextView itemFileSize = itemFileView.findViewById(R.id.item_file_size);
        itemFileView.setTag(path);

        itemFileIcon.setImageResource(fileIcon);
        itemFileName.setText(fileName);
        itemFileSize.setText(fileSize);
        itemFileView.findViewById(R.id.item_file_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseAttachmentFile.removeView(itemFileView);
                attachments.remove(path);
                if (releaseAttachmentFile.getChildCount() == 0) {
                    releaseAttachmentFile.setVisibility(View.GONE);
                }
            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, Util.dip2px(this, 8));
        itemFileView.setLayoutParams(layoutParams);

        return itemFileView;
    }

    private boolean isTaskValid() {
        if (mTask.getTaskTitle().isEmpty()) {
            Toast.makeText(this, "请输入问答标题", Toast.LENGTH_SHORT).show();
        } else if (mTask.getTaskContent().isEmpty()) {
            Toast.makeText(this, "请输入问答内容", Toast.LENGTH_SHORT).show();
        } else if (mTask.getTaskReward() <= 1e-7) {
            Toast.makeText(this, "请设置问答报酬", Toast.LENGTH_SHORT).show();
        } else if (mTask.getTaskDeadline() == null) {
            Toast.makeText(this, "请选择截止日期", Toast.LENGTH_SHORT).show();
        } else if (mTask.getTaskCategory().isEmpty()) {
            Toast.makeText(this, "请选择问题类型", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
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

        Log.d("wewein", "onDateSet: " + text);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date date = sdf.parse(text);
            Log.d("wewein", "onDateSet: " + date);
            mTask.setTaskDeadline(new BmobDate(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(ReleaseTaskActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}

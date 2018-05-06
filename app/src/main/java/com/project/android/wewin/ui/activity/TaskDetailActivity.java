package com.project.android.wewin.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.MyUser;
import com.project.android.wewin.data.remote.model.Reply;
import com.project.android.wewin.data.remote.model.Task;
import com.project.android.wewin.databinding.ActivityTaskDetailBinding;
import com.project.android.wewin.ui.fragment.ReplyDialogFragment;
import com.project.android.wewin.utils.L;
import com.project.android.wewin.utils.Util;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import static com.project.android.wewin.R.layout.activity_task_detail;

public class TaskDetailActivity extends AppCompatActivity {
    private final static int FILE_SELECT_CODE = 0;

    private Task mTask;
    private int menuIndex;
    private Reply mReply = new Reply();
    private MyUser user;
    private List<Reply> replies;
    private ActivityTaskDetailBinding binding;

    private CountDownTimer countDownTimer;

    private TextView mAttachmentWord;
    private LinearLayout mAttachmentMedia;
    private LinearLayout mAttachmentFile;
    private ImageView mAttachmentAdd;
    public String mReplyContent = "";
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, activity_task_detail);

        setSupportActionBar(binding.detailToolbarTask);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        binding.detailToolbarTask.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mTask = (Task) getIntent().getSerializableExtra("task_detail");
        binding.setTask(mTask);
        menuIndex = getIntent().getIntExtra("menu_index", 0);
        binding.setTaskDetailActivity(this);
        user = BmobUser.getCurrentUser(MyUser.class);
        initView();
        checkPermissions();
        queryReplies();
    }


    private void initView() {

        if (menuIndex == 0) {

        } else if (menuIndex == 1) {
            binding.detailReplyTask.setVisibility(View.GONE);
        } else {
            binding.detailReplyWordTask.setText(getString(R.string.modify));
        }
    }


    public void showAnswers(View view) {
        new ReplyDialogFragment(replies).show(getSupportFragmentManager(), "dialog");
    }

    private void queryReplies() {
        BmobQuery<Reply> query = new BmobQuery<>();
        query.addWhereEqualTo("mTask", new BmobPointer(mTask));
        query.setLimit(50);
        query.include("creatorUser");
        query.findObjects(new FindListener<Reply>() {
            @Override
            public void done(List<Reply> list, BmobException e) {
                if (e == null) {
                    L.i("replies total:" + list.size());
                    replies = new ArrayList<>(list);
                } else {
                    L.i("reply query failed");
                }
            }
        });
    }

    public void onReplyClick(View view) {
        if (TextUtils.isEmpty(binding.detailReplyContent.getText().toString())) {
            Toast.makeText(TaskDetailActivity.this, "回复不能为空", Toast.LENGTH_SHORT).show();
        } else {
            mReply.setBestReply(0);
            mReply.setmTask(mTask);
            mReply.setContent(binding.detailReplyContent.getText().toString());
            mReply.setCreatorUser(user);
            mReply.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        Toast.makeText(TaskDetailActivity.this, "upload success", Toast.LENGTH_SHORT).show();
                    } else {
                        L.i("upload failed");
                    }
                }
            });
            finish();
        }
    }


    public static void startDetailActivity(Activity activity, Task task, int index) {
        Intent intent = new Intent(activity, TaskDetailActivity.class);

        intent.putExtra("task_detail", task);
        intent.putExtra("menu_index", index);
        activity.startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        countDown();
    }

    private void countDown() {
        final long timeLeft = BmobDate.getTimeStamp(mTask.getTaskDeadline().getDate()) - System.currentTimeMillis();

        if (timeLeft < 0) {
            binding.countDownBackgroundTask.setBackgroundColor(Color.parseColor("#888888"));
            binding.setLeftTime(getString(R.string.deadline_passed));
            binding.setLeftSecond("");
            binding.detailReplyWordTask.setVisibility(View.GONE);
        }

        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long l) {
                long day = l / (24 * 60 * 60 * 1000);
                long hour = (l / (60 * 60 * 1000) - day * 24);
                long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
                long second = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

                binding.setLeftTime(getString(R.string.deadline_time_left) + "  " + day + "天" + hour + "小时" + min + "分钟");
                binding.setLeftSecond(second + "秒");

                ScaleAnimation animation = new ScaleAnimation(1.0f, 0.857f,
                        1.0f, 0.857f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(1000);
                binding.detailLeftSecondTask.startAnimation(animation);
            }

            @Override
            public void onFinish() {
                binding.countDownBackgroundTask.setBackgroundColor(Color.parseColor("#888888"));
                binding.setLeftTime(getString(R.string.deadline_passed));
                binding.setLeftSecond("");
                binding.detailReplyTask.setVisibility(View.GONE);
            }
        };

        countDownTimer.start();
    }


    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView imageView, String url) {
        Util.loadCircleImage(Uri.parse(url), imageView);
    }


    @Override
    protected void onStop() {
        super.onStop();
        countDownTimer.cancel();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(TaskDetailActivity.this,
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

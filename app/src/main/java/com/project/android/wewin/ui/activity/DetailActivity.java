package com.project.android.wewin.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.Commit;
import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.data.remote.model.MyUser;
import com.project.android.wewin.data.remote.model.Task;
import com.project.android.wewin.databinding.ActivityDetailBinding;
import com.project.android.wewin.ui.adapter.FixedTextureVideoView;
import com.project.android.wewin.utils.L;
import com.project.android.wewin.utils.OpenFileUtil;
import com.project.android.wewin.utils.Util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * DetailActivity class
 *
 * @author zhoutao
 * @date 2017/12/10
 */

public class DetailActivity extends AppCompatActivity {

    private final static int FILE_SELECT_CODE = 0;

    /*@BindView(R.id.detail_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;

    @BindView(R.id.detail_left_time)
    TextView detailLeftTime;
    @BindView(R.id.detail_left_second)
    TextView detailLeftSecond;

    @BindView(R.id.detail_user_img)
    ImageView mUserImg;
    @BindView(R.id.detail_user_name)
    TextView mUserName;
    @BindView(R.id.item_submit_time)
    TextView mSubmitTime;
    @BindView(R.id.detail_deadline)
    TextView mDeadline;

    @BindView(R.id.detail_title)
    TextView mDetailTitle;
    @BindView(R.id.detail_content)
    TextView mDetailContent;
    @BindView(R.id.detail_attachment_layout)
    LinearLayout mAttachmentLayout;

    @BindView(R.id.detail_reply)
    LinearLayout mReply;*/


    private HomeWork mHomeWork;
    private Task mTask;
    private int menuIndex;
    private Commit mCommit = new Commit();
    private MyUser user;

    private List<BmobFile> attachments = new ArrayList<>();

    private ActivityDetailBinding binding;
    private CountDownTimer countDownTimer;

    private TextView mAttachmentWord;
    private LinearLayout mAttachmentMedia;
    private LinearLayout mAttachmentFile;
    private ImageView mAttachmentAdd;

    private Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        setSupportActionBar(binding.detailToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        binding.detailToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mHomeWork = (HomeWork) getIntent().getSerializableExtra("homework_detail");
        binding.setHomework(mHomeWork);

        if (mHomeWork.getAttachmentPath() != null) {
            attachments = mHomeWork.getAttachmentPath();
        }

        menuIndex = getIntent().getIntExtra("menu_index", 0);
        binding.setDetailActivity(this);
        binding.setDetailActivity(this);

        user = BmobUser.getCurrentUser(MyUser.class);
        initView();
        checkPermissions();
    }


    private void initView() {
        if (menuIndex == 0) {

        } else if (menuIndex == 1) {
            binding.detailReply.setVisibility(View.GONE);
        } else {
            binding.detailReplyWord.setText(getString(R.string.modify));
        }

        mAttachmentFile = findViewById(R.id.detail_attachment_layout);
        for (BmobFile file : attachments) {
            mAttachmentFile.addView(initFileView(file));
        }
    }


    private View initFileView(final BmobFile file) {
        mAttachmentFile.setVisibility(View.VISIBLE);
        final String path = file.getFileUrl();
        final String name = file.getFilename();

        int fileIcon = Util.fileIcon(Util.fileType(path.substring(path.lastIndexOf(".") + 1)));

        final View itemFileView = View.inflate(this, R.layout.item_attachment_file, null);
        ImageView itemFileIcon = itemFileView.findViewById(R.id.item_file_icon);
        TextView itemFileName = itemFileView.findViewById(R.id.item_file_name);
        TextView itemFileSize = itemFileView.findViewById(R.id.item_file_size);
        itemFileView.setTag(path);

        itemFileIcon.setImageResource(fileIcon);
        itemFileName.setText(name);
        itemFileSize.setVisibility(View.GONE);
        itemFileView.findViewById(R.id.item_file_delete).setVisibility(View.GONE);

        itemFileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile(file);
            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, Util.dip2px(this, 8));
        itemFileView.setLayoutParams(layoutParams);

        return itemFileView;
    }


    private void downloadFile(BmobFile file) {
        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        final File saveFile = new File(Environment.getExternalStorageDirectory() + "/Wewin/download/homework/" + mHomeWork.getObjectId(), file.getFilename());
        if (saveFile.exists()) {
            startActivity(OpenFileUtil.openFile(saveFile.getPath(), DetailActivity.this));
        } else {
            BmobFile toDownload = new BmobFile(file.getFilename(), "", file.getFileUrl());
            L.i(file.getFileUrl());
            toDownload.download(saveFile, new DownloadFileListener() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        Toast.makeText(DetailActivity.this, "下载成功,保存路径:" + s, Toast.LENGTH_SHORT).show();
                        startActivity(OpenFileUtil.openFile(saveFile.getPath(), DetailActivity.this));
                    } else {
                        Toast.makeText(DetailActivity.this, "下载失败：" + e.getErrorCode() + "," + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onProgress(Integer integer, long l) {
                    Log.i("bmob", "下载进度：" + l);

                }
            });

        }
    }



    public static void startDetailActivity(Activity activity, HomeWork homeWork, int index) {
        Intent intent = new Intent(activity, DetailActivity.class);

        intent.putExtra("homework_detail", homeWork);

        intent.putExtra("menu_index", index);
        activity.startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        countDown();
    }

    private void countDown() {
        final long timeLeft = BmobDate.getTimeStamp(mHomeWork.getHomeworkDeadline().getDate()) - System.currentTimeMillis();

        if (timeLeft < 0) {
            binding.countDownBackground.setBackgroundColor(Color.parseColor("#888888"));
            binding.setLeftTime(getString(R.string.deadline_passed));
            binding.setLeftSecond("");
            binding.detailReply.setVisibility(View.GONE);
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
                binding.detailLeftSecond.startAnimation(animation);
            }

            @Override
            public void onFinish() {
                binding.countDownBackground.setBackgroundColor(Color.parseColor("#888888"));
                binding.setLeftTime(getString(R.string.deadline_passed));
                binding.setLeftSecond("");
                binding.detailReply.setVisibility(View.GONE);
            }
        };

        countDownTimer.start();
    }


    public void onReplyClick(View view) {
        toReply();
        //replyConfirm();
    }


    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView imageView, String url) {
        Util.loadCircleImage(Uri.parse(url), imageView);
    }


    /*private void initDetail(HomeWork homeWork) {
        mAttachmentLayout.removeAllViews();

        mUserName.setText(homeWork.getCreatorUser().getUsername());
        Log.i("homework get", "initDetail: " + homeWork.getAttachmentPath());
        mDeadline.setText(homeWork.getHomeworkDeadline().getDate());
        mDetailTitle.setText(homeWork.getHomeworkTitle());
        mDetailContent.setText(homeWork.getHomeworkContent());

        List<String> mDetailAttachment = homeWork.getAttachmentPath();
        for (int i = 0; i < mDetailAttachment.size(); i++) {
            final String url = mDetailAttachment.get(i);
            final String fileName = url.substring(url.lastIndexOf("/") + 1);
            final TextView textView = new TextView(this);
            textView.setText(fileName);
            textView.setTextSize(16);
            textView.setTag(url);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5, 5, 5, 5);
            textView.setLayoutParams(layoutParams);
            textView.setBackgroundColor(ContextCompat.getColor(this, R.color.attachmentBackground));
            textView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BmobFile bmobFile = new BmobFile(fileName, "", url);
                    if (ActivityCompat.checkSelfPermission(DetailActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(DetailActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    } else {
                        Log.i("file info", "onClick: " + bmobFile.getFilename() + "," + bmobFile.getFileUrl());
                        downloadFile(bmobFile);
                    }
                }
            });
            mAttachmentLayout.addView(textView);
        }
    }*/



    private void toReply() {
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.bottom_dialog_upload, null);

        LinearLayout mAttachment = dialogView.findViewById(R.id.detail_reply_attachment);
        mAttachmentWord = dialogView.findViewById(R.id.detail_reply_attachment_word);
        mAttachmentMedia = dialogView.findViewById(R.id.detail_reply_attachment_media);
        mAttachmentFile = dialogView.findViewById(R.id.detail_reply_attachment_file);
        mAttachmentAdd = dialogView.findViewById(R.id.detail_delete_file);
        mAttachmentAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAttachment();
            }
        });

        mAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getString(R.string.submit).equals(mAttachmentWord.getText())) {
                    dialog.dismiss();

                    return;
                }

                addAttachment();

            }
        });

        dialog.setContentView(dialogView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);


        /*禁止BottomSheetDialog向下滑动隐藏*/
        try {
            Field mBehaviorField = dialog.getClass().getDeclaredField("mBehavior");
            mBehaviorField.setAccessible(true);
            final BottomSheetBehavior behavior = (BottomSheetBehavior) mBehaviorField.get(dialog);
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        dialog.show();

    }


    private void replyConfirm() {
        mCommit.setCreatorUser(user);
        mCommit.setmHomework(mHomeWork);

    }

    private boolean isReplyValid() {
        boolean replyValid = false;
        if (mAttachmentFile.getChildCount() > 0 || mAttachmentMedia.getChildCount() > 0) {
            replyValid = true;
        } else {
            Toast.makeText(DetailActivity.this, "回复不能为空", Toast.LENGTH_SHORT).show();
        }
        return replyValid;
    }



    /*private void uploadAttachment() {
        mReply.setClickable(false);

        int size = mReplyLayout.getChildCount();
        if (size == 0) {
            mCommit.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        //Toast.makeText(DetailActivity.this, "success upload:" + s, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        //Toast.makeText(DetailActivity.this, "fail upload" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        final String[] filePaths = new String[size];
        for (int i = 0; i < size; i++) {
            filePaths[i] = mReplyLayout.getChildAt(i).getTag().toString();
        }
        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {
                if (list1.size() == filePaths.length) {
                    mCommit.addAll("attachmentPath", list1);
                    mCommit.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                //Toast.makeText(DetailActivity.this, "success upload:" + s, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                //Toast.makeText(DetailActivity.this, "fail upload" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                mReply.setClickable(true);

                //Toast.makeText(DetailActivity.this, "文件路径错误", Toast.LENGTH_SHORT).show();
                Log.i("error", "errorcode:" + i + " message:" + s);
            }
        });

    }*/


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
                uri = data.getData();
                getPath(uri);
                mAttachmentWord.setText(getString(R.string.submit));
                mAttachmentAdd.setVisibility(View.VISIBLE);
            }
        }
    }


    private void getPath(Uri uri) {
        Log.d("wewein", "onActivityResult: " + uri);
        String path = Util.getPath(this, uri);
        Log.d("wewein", "getPath: " + path);

        if (path == null) {
            return;
        }

        String type = Util.fileType(path.substring(path.lastIndexOf(".") + 1));

        if ("image".equals(type) || "video".equals(type)) {
            mAttachmentMedia.addView(initMediaView(path));
        } else {
            mAttachmentFile.addView(initFileView(path));
        }
    }


    private View initMediaView(String path) {
        if (path == null) {
            return null;
        }

        mAttachmentMedia.setVisibility(View.VISIBLE);

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
                mAttachmentMedia.removeView(itemMediaView);

                if (mAttachmentMedia.getChildCount() == 0) {
                    mAttachmentMedia.setVisibility(View.GONE);
                }

                if (mAttachmentMedia.getChildCount() == 0 && mAttachmentFile.getChildCount() == 0) {
                    mAttachmentWord.setText(getString(R.string.reply_submit));
                    mAttachmentAdd.setVisibility(View.GONE);
                }
            }
        });

        return itemMediaView;
    }


    private View initFileView(String path) {
        if (path == null) {
            return null;
        }

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
                mAttachmentFile.removeView(itemFileView);

                if (mAttachmentMedia.getChildCount() == 0 && mAttachmentFile.getChildCount() == 0) {
                    mAttachmentWord.setText(getString(R.string.reply_submit));
                    mAttachmentAdd.setVisibility(View.GONE);
                }
            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, Util.dip2px(this, 8));
        itemFileView.setLayoutParams(layoutParams);

        return itemFileView;
    }


    @Override
    protected void onStop() {
        super.onStop();
        countDownTimer.cancel();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(DetailActivity.this,
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

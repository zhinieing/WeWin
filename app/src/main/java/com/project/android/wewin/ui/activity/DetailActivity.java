package com.project.android.wewin.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.Commit;
import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.data.remote.model.MyUser;
import com.project.android.wewin.utils.Util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

/**
 * DetailActivity class
 *
 * @author zhoutao
 * @date 2017/12/10
 */

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int FILE_SELECT_CODE = 0;

    @BindView(R.id.detail_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;

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
    LinearLayout mReply;


    private HomeWork mHomeWork;
    private String mHomeWorkId;
    private Commit mCommit = new Commit();
    private MyUser user;
    //ImageButton mAttachment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mToolbarTitle.setText(R.string.homework);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //mAttachment.setOnClickListener(this);
        mReply.setOnClickListener(this);
        mHomeWorkId = getIntent().getStringExtra("homework_detail");
        user = BmobUser.getCurrentUser(MyUser.class);

        subscribeUI();
    }


    private void subscribeUI() {
        BmobQuery<HomeWork> query = new BmobQuery<>();
        query.include("creatorUser");
        query.getObject(mHomeWorkId, new QueryListener<HomeWork>() {
            @Override
            public void done(HomeWork homeWork, BmobException e) {
                if (e == null) {
                    if (homeWork != null) {
                        mHomeWork = homeWork;
                        initDetail(homeWork);
                    }
                }
            }
        });

    }


    private void initDetail(HomeWork homeWork) {
        mAttachmentLayout.removeAllViews();
        if (homeWork.getCreatorUser().getUserPhoto() != null) {
            Util.loadCircleImage(Uri.parse(homeWork.getCreatorUser().getUserPhoto()), mUserImg);
        } else {
            Util.loadCircleImage(Uri.parse(""), mUserImg);
        }
        mUserName.setText(homeWork.getCreatorUser().getUsername());
        Log.i("homework get", "initDetail: " + homeWork.getAttachmentPath());
        mDeadline.setText(homeWork.getHomeworkDeadline());
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
    }


    private void downloadFile(BmobFile file) {
        //允许设置下载文件的存储路径，默认下载文件的目录为：context.getApplicationContext().getCacheDir()+"/bmob/"
        File saveFile = new File(Environment.getExternalStorageDirectory() + "/Wewin/download/", file.getFilename());
        file.download(saveFile, new DownloadFileListener() {

            @Override
            public void onStart() {
                Log.i("download", "onStart: ");
            }

            @Override
            public void done(String savePath, BmobException e) {
                if (e == null) {
                    Toast.makeText(DetailActivity.this, "下载成功,保存路径:" + savePath, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailActivity.this, "下载失败：" + e.getErrorCode() + "," + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
                Log.i("bmob", "下载进度：" + value + "," + newworkSpeed);
            }

        });
    }

    public static void startDetailActivity(Activity activity, String homeWorkId) {
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra("homework_detail", homeWorkId);
        activity.startActivity(intent);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detail_reply:
                toReply();
//                addAttachment();
//                replyConfirm();
                break;
            default:
                break;
        }
    }


    private void toReply() {
        final BottomSheetDialog dialog=new BottomSheetDialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.bottom_dialog_upload, null);

        LinearLayout mAttachment = dialogView.findViewById(R.id.detail_reply_attachment);
        final TextView mAttachmentWord = dialogView.findViewById(R.id.detail_reply_word);

        mAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAttachmentWord.setText(getString(R.string.submit));

                if (getString(R.string.submit).equals(mAttachmentWord.getText())) {
                    dialog.dismiss();
                }
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
        if (ActivityCompat.checkSelfPermission(DetailActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            if (isReplyValid()) {
                //uploadAttachment();
            }
        }
    }

    private boolean isReplyValid() {
        boolean replyValid = false;
        //todo 判断回复是否有效，content和attachment
        if (mAttachmentLayout.getChildCount() > 0) {
            replyValid = true;
        } else {
            Toast.makeText(DetailActivity.this, "回复不能为空", Toast.LENGTH_SHORT).show();
        }
        return replyValid;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //uploadAttachment();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //download
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
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
                /*textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mReplyLayout.removeView(textView);
                    }
                });
                mReplyLayout.addView(textView);*/
            }
        }
    }
}

package com.project.android.wewin.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.HomeWork;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int FILE_SELECT_CODE = 0;

    @BindView(R.id.detail_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;

    @BindView(R.id.detail_user_img)
    ImageView mUserImg;

    @BindView(R.id.detail_user_name)
    TextView mUserName;

    @BindView(R.id.detail_release_date)
    TextView mReleaseDate;

    @BindView(R.id.detail_deadline)
    TextView mDeadline;

    @BindView(R.id.detail_view_count)
    TextView mViewCount;

    @BindView(R.id.detail_title)
    TextView mDetailTitle;

    @BindView(R.id.detail_content)
    TextView mDetailContent;

    @BindView(R.id.detail_attachment_layout)
    LinearLayout mAttachmentLayout;

    @BindView(R.id.detail_reply_attachment_layout)
    LinearLayout mReplyLayout;

    @BindView(R.id.detail_reply_attachment)
    ImageButton mAttachment;

    @BindView(R.id.detail_reply)
    Button mReply;

    private HomeWork mHomeWork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        mToolbar.setTitle("");
        mToolbarTitle.setText(R.string.content);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initDetail();
    }

    private void initDetail() {
        mHomeWork = getIntent().getParcelableExtra("homework_detail");
        mUserImg.setImageURI(Uri.parse(mHomeWork.getCreatorPhoto()));
        mUserName.setText(mHomeWork.getCreatorName());
        mReleaseDate.setText(mHomeWork.getCreatedAt());
        mDeadline.setText(mHomeWork.getHomeworkDeadline());
        mViewCount.setText(mHomeWork.getViewCount());
        mDetailTitle.setText(mHomeWork.getHomeworkTitle());
        mDetailContent.setText(mHomeWork.getHomeworkContent());
    }

    public void startDetailActivity(Activity activity, HomeWork homeWork) {
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra("homework_detail", (Parcelable) homeWork);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.detail_reply:
                replyConfirm();
                break;
            case R.id.detail_reply_attachment:
                addAttachment();
                break;
            default:
                break;
        }
    }

    private void replyConfirm() {
        if (ActivityCompat.checkSelfPermission(DetailActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            if (isReplyValid()) {
                uploadAttachment();
            }
        }
    }

    private boolean isReplyValid() {
        boolean replyValid = false;
        //todo 判断回复是否有效，content和attachment
        return replyValid;
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
        mReply.setClickable(false);
        mReply.setText(R.string.uploading);
        mReply.setBackgroundColor(ContextCompat.getColor(this, R.color.textColorSecondary));
        int size = mAttachmentLayout.getChildCount();
        final String[] filePaths = new String[size];
        for (int i = 0; i < size; i++) {
            filePaths[i] = mAttachmentLayout.getChildAt(i).getTag().toString();
            BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> list, List<String> list1) {
                    if (list1.size() == filePaths.length) {
                        //todo 回复内容的处理，更新数据库
//                        mHomeWork.addAll("attachmentPath", list1);
//                        mHomeWork.save(new SaveListener<String>() {
//                            @Override
//                            public void done(String s, BmobException e) {
//                                if (e == null) {
//                                    Toast.makeText(DetailActivity.this, "success upload:" + s, Toast.LENGTH_SHORT).show();
//                                    finish();
//                                } else {
//                                    Toast.makeText(DetailActivity.this, "fail upload" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
                    }
                }

                @Override
                public void onProgress(int i, int i1, int i2, int i3) {
                    Log.i("upload", "onProgress: " + "total:" + i2 + " percent:" + i3);
                }

                @Override
                public void onError(int i, String s) {
                    Log.i("error", "errorcode:" + i + " message:" + s);
                }
            });
        }
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
}

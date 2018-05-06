package com.project.android.wewin.ui.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.api.ApiClass;
import com.project.android.wewin.data.remote.api.ApiManager;
import com.project.android.wewin.data.remote.api.ApiQiniu;
import com.project.android.wewin.data.remote.model.MyUser;
import com.project.android.wewin.data.remote.model.OpenidData;
import com.project.android.wewin.data.remote.model.Uptoken;
import com.project.android.wewin.ui.adapter.FixedTextureVideoView;
import com.project.android.wewin.utils.Util;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author pengming
 */
public class ReleaseClassActivity extends AppCompatActivity implements View.OnClickListener{

    private final static int IMG_SELECT_CODE = 1;

    public final static int RESULT_CODE = 14;

    @BindView(R.id.toolbar_user_photo)
    ImageView userImg;
    @BindView(R.id.release_class_confirm)
    ImageButton releaseClassConfirm;
    @BindView(R.id.release_class_toolbar)
    Toolbar toolbar;
    @BindView(R.id.release_homework_title)
    EditText releaseHomeworkTitle;
    @BindView(R.id.release_class_icon)
    Button releaseClassIcon;
    @BindView(R.id.class_icon)
    LinearLayout classIcon;

    private MyUser user;
    private String className;
    private String classImg;
    private Integer classId;

    private View itemMediaView;

    private ApiQiniu mApiQiniu;
    private ApiClass mApiClass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_class);
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

        user = BmobUser.getCurrentUser(MyUser.class);
        Util.loadCircleImage(Uri.parse(user.getUserPhoto()), userImg);

        releaseClassConfirm.setOnClickListener(this);
        releaseClassIcon.setOnClickListener(this);

        className = getIntent().getStringExtra("class_name");
        classImg = getIntent().getStringExtra("class_icon");
        classId = getIntent().getIntExtra("class_id", 0);

        if (className != null) {
            releaseHomeworkTitle.setText(className);
        }
        if (classImg != null) {
            if (!classImg.isEmpty()) {
                classIcon.addView(initMediaView(classImg));
            }
        }

        mApiQiniu = ApiManager.getInstance().getApiQiniu();
        mApiClass = ApiManager.getInstance().getApiClass();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.release_class_confirm:
                releaseConfirm();
                break;
            case R.id.release_class_icon:
                addAttachment();
                break;
            default:
        }

    }


    private void addAttachment() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一张要上传的图片"), IMG_SELECT_CODE);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMG_SELECT_CODE) {
                Uri uri = data.getData();
                String path = Util.getPath(this, uri);

                classIcon.addView(initMediaView(path));
            }
        }
    }


    private View initMediaView(String path) {
        releaseClassIcon.setVisibility(View.GONE);
        classIcon.setVisibility(View.VISIBLE);

        itemMediaView = View.inflate(this, R.layout.item_attachment_media, null);
        final ImageView itemImage = itemMediaView.findViewById(R.id.item_media_image);
        final ImageView itemDelete = itemMediaView.findViewById(R.id.item_media_delete);
        itemMediaView.setTag(path);

        if ("http".equals(path.substring(0, 4))) {
            Util.loadNormalImage(Uri.parse(path), itemImage);
        } else {
            itemImage.setImageBitmap(BitmapFactory.decodeFile(path));
        }

        itemImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemDelete.setVisibility(View.VISIBLE);
                return true;
            }
        });

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
                classIcon.removeView(itemMediaView);
                classIcon.setVisibility(View.GONE);

                releaseClassIcon.setVisibility(View.VISIBLE);
            }
        });

        return itemMediaView;
    }


    private void releaseConfirm() {
        if (isClassValid()) {

            if (className == null && classImg == null) {
                getToken((String) itemMediaView.getTag());
            } else {

                if (!isClassNameChanged() && !isClassIconChanged()) {
                    setResult(0);
                    finish();
                    return;
                }

                if (isClassIconChanged()) {
                    getToken((String) itemMediaView.getTag());
                } else {
                    updateClass(classImg);
                }

            }

        }
    }



    private boolean isClassNameChanged() {
        if (className != null) {
            if (className.equals(releaseHomeworkTitle.getText().toString())) {
                return false;
            }
            return true;
        }
        return true;
    }

    private boolean isClassIconChanged() {
        if (classImg != null) {
            if (classImg.equals(itemMediaView.getTag())) {
                return false;
            }
            return true;
        }
        return true;
    }

    private boolean isClassValid() {
        if (releaseHomeworkTitle.getText().toString().isEmpty()) {
            Toast.makeText(this, "请输入班级名称", Toast.LENGTH_SHORT).show();
        } else if (itemMediaView == null) {
            Toast.makeText(this, "请添加班级图片", Toast.LENGTH_SHORT).show();
        } else if (itemMediaView.getTag() == null){
            Toast.makeText(this, "请添加班级图片", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }



    private void getToken(final String path) {
        mApiQiniu.getQiniuToken()
                .enqueue(new Callback<Uptoken>() {
                    @Override
                    public void onResponse(@NonNull Call<Uptoken> call, @NonNull Response<Uptoken> response) {
                        if (response.isSuccessful() || response.body().uptoken != null) {

                            uploadImg2QiNiu(path, response.body().uptoken);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Uptoken> call, @NonNull Throwable t) {

                    }
                });
    }



    private void uploadImg2QiNiu(String path, String token) {
        final UploadManager uploadManager = new UploadManager();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String key = "class_icon_" + sdf.format(new Date());

        uploadManager.put(path, key, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()) {
                    String url = "http://p2zhcnn8g.bkt.clouddn.com/" + key;

                    if (className == null && classImg == null) {
                        uploadClass(url);
                    } else {
                        updateClass(url);
                    }

                }
            }
        }, null);
    }



    private void uploadClass(String path) {

        mApiClass.postClass(releaseHomeworkTitle.getText().toString(),
                "oiooR1kiOuI242pqm0N9TIFLugeg",
                path)
                .enqueue(new Callback<OpenidData>() {
                    @Override
                    public void onResponse(@NonNull Call<OpenidData> call, @NonNull Response<OpenidData> response) {
                        if (response.isSuccessful() || response.body().state == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ReleaseClassActivity.this, getString(R.string.add_class_success), Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_CODE);
                                    finish();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<OpenidData> call, @NonNull Throwable t) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ReleaseClassActivity.this, getString(R.string.add_class_fail), Toast.LENGTH_SHORT).show();
                                setResult(0);
                                finish();
                            }
                        });
                    }
                });
    }


    private void updateClass(String path) {

        mApiClass.updateClass(releaseHomeworkTitle.getText().toString(), classId, path)
                .enqueue(new Callback<OpenidData>() {
                    @Override
                    public void onResponse(@NonNull Call<OpenidData> call, @NonNull Response<OpenidData> response) {
                        if (response.isSuccessful() || response.body().state == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ReleaseClassActivity.this, getString(R.string.update_class_success), Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_CODE);
                                    finish();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<OpenidData> call, @NonNull Throwable t) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ReleaseClassActivity.this, getString(R.string.update_class_fail), Toast.LENGTH_SHORT).show();
                                setResult(0);
                                finish();
                            }
                        });
                    }
                });
    }


}

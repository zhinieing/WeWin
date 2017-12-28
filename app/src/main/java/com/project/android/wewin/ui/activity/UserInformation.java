package com.project.android.wewin.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;
import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.MyUser;
import com.project.android.wewin.utils.Auth;
import com.project.android.wewin.utils.Constants;
import com.project.android.wewin.utils.MyAlertDialog;
import com.project.android.wewin.utils.Util;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author pengming
 */
public class UserInformation extends TakePhotoActivity implements View.OnClickListener {

    @BindView(R.id.modify_user_toolbar)
    Toolbar toolbar;
    @BindView(R.id.modified_user_photo)
    ImageView modifiedUserPhoto;
    @BindView(R.id.modifying_user_photo)
    LinearLayout modifyingUserPhoto;
    @BindView(R.id.modified_user_display_name)
    TextView modifiedUserDisplayName;
    @BindView(R.id.modifying_user_display_name)
    LinearLayout modifyingUserDisplayName;

    private TakePhoto takePhoto;

    private MyUser user;

    private boolean[] checks = new boolean[2];
    private String[] changes = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        ButterKnife.bind(this);

        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(R.string.modify_user_information);

        user = BmobUser.getCurrentUser(MyUser.class);

    }


    private void initView() {

        modifyingUserPhoto.setOnClickListener(this);
        modifyingUserDisplayName.setOnClickListener(this);

        Util.loadCircleImage(Uri.parse(user.getUserPhoto()), modifiedUserPhoto);
        modifiedUserDisplayName.setText(user.getUsername());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.modifying_user_photo:
                File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
                if (!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                final Uri imageUri = Uri.fromFile(file);

                takePhoto = getTakePhoto();
                final CropOptions cropOptions = new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(false).create();
                final CompressConfig compressConfig=new CompressConfig.Builder().setMaxSize(100*100).create();
                takePhoto.onEnableCompress(compressConfig, true);

                final MyAlertDialog dialog = new MyAlertDialog(this, Constants.CAMERA_GALLERY, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                takePhoto.onPickFromCaptureWithCrop(imageUri, cropOptions);
                                break;
                            case 1:
                                takePhoto.onPickFromGalleryWithCrop(imageUri, cropOptions);
                                break;
                            default:
                        }
                    }
                });

                dialog.initDialog();
                break;

            case R.id.modifying_user_display_name:
                View alertView = LayoutInflater.from(this).inflate(R.layout.alert_modify_username, null);
                final EditText editText = alertView.findViewById(R.id.alert_modified_username);

                editText.setText(user.getUsername());

                AlertDialog myDialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.alert_dialog_modify_username);
                builder.setView(alertView);
                builder.setPositiveButton(R.string.alert_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        modifiedUserDisplayName.setText(editText.getText().toString());

                        checks[1] = true;
                        changes[1] = editText.getText().toString();
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
                break;
            default:
        }
    }



    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        String iconPath = result.getImage().getOriginalPath();

        uploadImg2QiNiu(iconPath);
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        Toast.makeText(this, "Error:" + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }


    private void uploadImg2QiNiu(String path) {
        final UploadManager uploadManager = new UploadManager();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String key = "icon_" + sdf.format(new Date());

        uploadManager.put(path, key, Auth.create(Constants.AccessKey, Constants.SecretKey).uploadToken("wewin"), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                String imgPath = "http://p1itk7pe2.bkt.clouddn.com/" + key;

                Util.loadCircleImage(Uri.parse(imgPath), modifiedUserPhoto);

                checks[0] = true;
                changes[0] = imgPath;
            }
        }, null);
    }



    @Override
    protected void onStop() {
        super.onStop();
        Log.d("wewin", "onStop: ");

        MyUser newMyUser = new MyUser();
        if (checks[0]) {
            newMyUser.setUserPhoto(changes[0]);
        }
        if (checks[1]) {
            newMyUser.setUsername(changes[1]);
        }
        newMyUser.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {

                }
            }
        });
    }
}


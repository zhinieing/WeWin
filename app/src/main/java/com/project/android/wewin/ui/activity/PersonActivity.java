package com.project.android.wewin.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.MyUser;
import com.project.android.wewin.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;

/**
 * @author pengming
 */
public class PersonActivity extends AppCompatActivity implements View.OnClickListener {


    @BindView(R.id.user_photo)
    ImageView userPhoto;
    @BindView(R.id.user_modify)
    Button userModify;
    @BindView(R.id.person_toolbar)
    Toolbar toolbar;

    private MyUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        userModify.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        user = BmobUser.getCurrentUser(MyUser.class);

        initView();

    }

    private void initView() {

        Util.loadCircleImage(Uri.parse(user.getUserPhoto()), userPhoto);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_modify:
                startActivity(new Intent(PersonActivity.this, UserInformation.class));
                break;
            default:
        }
    }


}

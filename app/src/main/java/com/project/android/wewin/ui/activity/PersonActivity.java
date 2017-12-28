package com.project.android.wewin.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.MyUser;
import com.project.android.wewin.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;

/**
 * @author pengming
 */
public class PersonActivity extends AppCompatActivity {

    @BindView(R.id.person_toolbar)
    Toolbar toolbar;
    @BindView(R.id.user_photo)
    ImageView userPhoto;
    @BindView(R.id.user_modify)
    Button userModify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MyUser user = BmobUser.getCurrentUser(MyUser.class);

        initView(user);
    }

    private void initView(MyUser user) {

        Util.loadCircleImage(Uri.parse(user.getUserPhoto()), userPhoto);

        userModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PersonActivity.this, UserInformation.class));
            }
        });
    }
}

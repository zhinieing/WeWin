package com.project.android.wewin.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.Class;
import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogauth.model.WilddogUser;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author pengming
 */
public class ContactActivity extends AppCompatActivity {

    private WilddogAuth wilddogAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        wilddogAuth = WilddogAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        WilddogUser currentUser = wilddogAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(WilddogUser user) {
        /*Class mClass = new Class();
        mClass.setClassName("移动软件1班");
        mClass.setCreatorId(user.getUid());
        mClass.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null){
                    Toast.makeText(ContactActivity.this, "success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ContactActivity.this, "failed", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }
}

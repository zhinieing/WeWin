package com.project.android.wewin.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.project.android.wewin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReleaseHomeworkActivity extends AppCompatActivity {

    @BindView(R.id.release_homework_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;

    @BindView(R.id.release_homework_title)
    EditText mHomeworkTitle;

    @BindView(R.id.release_homework_content)
    EditText mHomeworkContent;

    @BindView(R.id.release_homework_attachment)
    ImageButton mAttachment;

    @BindView(R.id.release_deadline)
    Button mDeadline;

    @BindView(R.id.release_add_target)
    Button mAddTarget;

    @BindView(R.id.release_homework_confirm)
    Button mConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_homework);
        ButterKnife.bind(this);
        mToolbarTitle.setText(R.string.release);
        mToolbar.setNavigationIcon(R.drawable.ic_release_cancel);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}

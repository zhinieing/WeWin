package com.project.android.wewin.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.android.wewin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

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

    @BindView(R.id.detail_content)
    TextView mDetailContent;

    @BindView(R.id.detail_reply_content)
    EditText mReplyContent;

    @BindView(R.id.detail_homework_attachment)
    ImageButton mAttachment;

    @BindView(R.id.detail_reply)
    Button mReply;

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
    }
}

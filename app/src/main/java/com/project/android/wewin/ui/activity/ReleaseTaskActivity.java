package com.project.android.wewin.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.project.android.wewin.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ReleaseTaskActivity class
 *
 * @author zhoutao
 * @date 2017/12/20
 */
public class ReleaseTaskActivity extends AppCompatActivity implements View.OnClickListener {

    private final static int FILE_SELECT_CODE = 0;

    @BindView(R.id.release_task_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;

    @BindView(R.id.release_task_title)
    EditText mTaskTitle;

    @BindView(R.id.release_task_content)
    EditText mTaskContent;

    @BindView(R.id.release_task_attachment_layout)
    LinearLayout mAttachmentLayout;

    @BindView(R.id.release_task_attachment)
    ImageButton mAttachment;

    @BindView(R.id.release_task_confirm)
    Button mConfirm;

    @BindView(R.id.release_price)
    RelativeLayout mReleasePrice;

    @BindView(R.id.release_tag)
    RelativeLayout mReleaseTag;

    @BindView(R.id.addition_price)
    TextView mPrice;

    @BindView(R.id.addition_tag)
    TextView mTag;

    private String mAttachmentPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_task);
        ButterKnife.bind(this);
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon(R.drawable.ic_release_cancel);
        mToolbarTitle.setText(R.string.release);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mAttachment.setOnClickListener(this);
        mConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.release_task_attachment:
                break;
            case R.id.release_task_confirm:
                break;
            default:
                break;
        }
    }
}

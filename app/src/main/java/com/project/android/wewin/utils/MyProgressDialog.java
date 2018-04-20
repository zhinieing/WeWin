package com.project.android.wewin.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.project.android.wewin.R;

public class MyProgressDialog extends Dialog {

    private TextView progressText;

    public MyProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.setContentView(R.layout.progress_dialog);
        this.setCancelable(false);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView msg = this.findViewById(R.id.id_tv_loadingmsg);
        progressText = this.findViewById(R.id.progress_tv);
        progressText.setText("0%");
        msg.setText("正在上传中");
    }

    public void setProgress(String msg) {
        this.progressText.setText(msg);
    }
    public void hideProgress(){
        this.progressText.setVisibility(View.GONE);
    }
}

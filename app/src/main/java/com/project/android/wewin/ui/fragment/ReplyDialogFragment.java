package com.project.android.wewin.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.project.android.wewin.MyApplication;
import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.Reply;
import com.project.android.wewin.ui.adapter.OnItemClickListener;
import com.project.android.wewin.ui.adapter.ReplyRvAdapter;
import com.project.android.wewin.utils.L;
import com.project.android.wewin.utils.Util;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


public class ReplyDialogFragment extends BottomSheetDialogFragment {
    private BottomSheetBehavior behavior;
    private RecyclerView recyclerView;
    private ReplyRvAdapter adapter;
    private int index;
    private List<Reply> replyList = new ArrayList<>();

    private final OnItemClickListener<Reply> replyOnItemClickListener =
            new OnItemClickListener<Reply>() {
                @Override
                public void onClick(final Reply reply) {
                    if (index == 1) {
                        if (Util.isNetworkConnected(MyApplication.getInstance())) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("");
                            builder.setMessage("是否将此条回复作为最佳答案并支付报酬");
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    reply.setBestReply(1);
                                    reply.getmTask().setReceiverUser(reply.getCreatorUser());
                                    reply.getmTask().setCompleted(true);
                                    reply.getmTask().update(reply.getmTask().getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                L.i("set receiver success");
                                            } else {
                                                L.i("set receiver failed");
                                            }
                                        }
                                    });
                                    reply.update(reply.getObjectId(), new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                adapter.notifyDataSetChanged();
                                                L.i("reply update success");
                                            } else {
                                                L.i("reply update failed");
                                            }
                                        }
                                    });
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "您不是问题的主人，不可以设置最佳答案呢！", Toast.LENGTH_SHORT).show();
                    }
                }
            };


    @SuppressLint("ValidFragment")
    public ReplyDialogFragment(List<Reply> replyList, int index) {
        this.replyList = replyList;
        this.index = index;
    }

    public ReplyDialogFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.dialog_reply_fragment, null);
        recyclerView = view.findViewById(R.id.reply_list);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ReplyRvAdapter(getContext(), replyOnItemClickListener);
        adapter.setReplyList(replyList);
        recyclerView.setAdapter(adapter);

        dialog.setContentView(view);
        behavior = BottomSheetBehavior.from((View) view.getParent());
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

}

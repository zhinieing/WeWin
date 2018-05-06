package com.project.android.wewin.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.Reply;
import com.project.android.wewin.utils.L;
import com.project.android.wewin.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class ReplyRvAdapter extends RecyclerView.Adapter<ReplyRvAdapter.ViewHolder> {

    private Context context = null;
    private List<Reply> replyList = null;
    private OnItemClickListener<Reply> replyOnItemClickListener = null;

    public ReplyRvAdapter(Context context, OnItemClickListener<Reply> listener) {
        this.context = context;
        replyList = new ArrayList<>();
        replyOnItemClickListener = listener;
    }

    public void setReplyList(List<Reply> mReplyList) {
        if (mReplyList == null || mReplyList.size() == 0) {
            return;
        }
        replyList.addAll(mReplyList);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ReplyRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reply_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyRvAdapter.ViewHolder holder, int position) {
        final Reply reply = replyList.get(position);
        Util.loadCircleImage(Uri.parse(reply.getCreatorUser().getUserPhoto()), holder.userPhoto);
        holder.userName.setText(reply.getCreatorUser().getUsername());
        holder.submitTime.setText(reply.getCreatedAt().substring(0, 16));
        holder.content.setText(reply.getContent());

        try {
            if (reply.getBestReply().equals(1)) {
                holder.tag.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            L.i("holder null");
        }

        holder.replyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (replyOnItemClickListener != null) {
                    replyOnItemClickListener.onClick(reply);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return replyList == null ? 0 : replyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View replyItem;
        public ImageView userPhoto, tag;
        public TextView userName, submitTime, content;

        public ViewHolder(View itemView) {
            super(itemView);
            replyItem = itemView.findViewById(R.id.reply_item);
            userPhoto = itemView.findViewById(R.id.item_submitter_img_reply);
            userName = itemView.findViewById(R.id.item_submitter_name_reply);
            submitTime = itemView.findViewById(R.id.item_submit_time_reply);
            content = itemView.findViewById(R.id.item_submit_content_reply);
            tag = itemView.findViewById(R.id.item_tag);
        }
    }
}

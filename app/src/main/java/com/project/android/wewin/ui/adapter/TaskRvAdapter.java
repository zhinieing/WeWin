package com.project.android.wewin.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengming on 2017/11/4.
 */

public class TaskRvAdapter extends RecyclerView.Adapter<TaskRvAdapter.ViewHolder> {
    private Context context = null;
    private List<HomeWork> homeWorkList = null;
    private OnItemClickListener<HomeWork> homeWorkOnItemClickListener = null;


    public TaskRvAdapter(Context context, OnItemClickListener<HomeWork> listener){
        this.context = context;
        homeWorkList = new ArrayList<>();
        homeWorkOnItemClickListener = listener;

    }

    @Override
    public TaskRvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homework_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TaskRvAdapter.ViewHolder holder, int position) {
        final HomeWork homeWork = homeWorkList.get(position);

        if (homeWork.getCreatorPhoto() != null) {
            Util.loadCircleImage(Uri.parse(homeWork.getCreatorPhoto()), holder.userPhoto);
        } else {
            Util.loadCircleImage(Uri.parse(""), holder.userPhoto);
        }

        holder.userName.setText(homeWork.getCreatorName());
        holder.submitTime.setText(homeWork.getCreatedAt());
        holder.title.setText(homeWork.getHomeworkTitle());
        holder.viewCount.setText(homeWork.getViewCount());
        holder.homeWorkItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (homeWorkOnItemClickListener != null) {
                    homeWorkOnItemClickListener.onClick(homeWork);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return homeWorkList == null ? 0 : homeWorkList.size();

    }


    public void setHomeWorkList(List<HomeWork> mHomeWorkList) {
        if (mHomeWorkList == null || mHomeWorkList.size() == 0) {
            return;
        }
        homeWorkList.addAll(mHomeWorkList);
        notifyDataSetChanged();
    }

    public void clearHomeWorkList() {
        homeWorkList.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View homeWorkItem;
        public ImageView userPhoto;
        public TextView userName, submitTime, title, viewCount;

        public ViewHolder(View itemView) {
            super(itemView);

            homeWorkItem = itemView.findViewById(R.id.homework_item);
            userPhoto = itemView.findViewById(R.id.item_submitter_img);
            userName = itemView.findViewById(R.id.item_submitter_name);
            submitTime = itemView.findViewById(R.id.item_submit_time);
            title = itemView.findViewById(R.id.item_submit_title);
            viewCount = itemView.findViewById(R.id.item_view_count);

        }
    }
}

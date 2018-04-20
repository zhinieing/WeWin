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
import com.project.android.wewin.data.remote.model.Task;
import com.project.android.wewin.utils.Util;

import java.util.ArrayList;
import java.util.List;
/**
 * TaskRvAdapter
 *
 * @author zhoutao
 * @date 2018/4/20
 */

public class TaskRvAdapter extends RecyclerView.Adapter<TaskRvAdapter.ViewHolder> {

    private Context context = null;
    private List<Task> taskList = null;
    private OnItemClickListener<Task> taskOnItemClickListener = null;

    public TaskRvAdapter(Context context, OnItemClickListener<Task> listener) {
        this.context = context;
        taskList = new ArrayList<>();
        taskOnItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homework_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Task task = taskList.get(position);
        Util.loadCircleImage(Uri.parse(task.getCreatorUser().getUserPhoto()), holder.userPhoto);

        holder.userName.setText(task.getCreatorUser().getUsername());
        holder.submitTime.setText(task.getCreatedAt().substring(0, 16));
        holder.title.setText(task.getTaskTitle());
        String price = String.valueOf(task.getTaskReward()) + "元";
        holder.price.setText(price);
        holder.taskItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (taskOnItemClickListener != null) {
                    taskOnItemClickListener.onClick(task);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList == null ? 0 : taskList.size();
    }

    public void setTaskList(List<Task> mTaskList) {
        if (mTaskList == null || mTaskList.size() == 0) {
            return;
        }
        taskList.addAll(mTaskList);
        notifyDataSetChanged();
    }

    public void clearTaskList() {
        taskList.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View taskItem;
        public ImageView userPhoto;
        public TextView userName, submitTime, title, price;

        public ViewHolder(View itemView) {
            super(itemView);

            taskItem = itemView.findViewById(R.id.homework_item);
            userPhoto = itemView.findViewById(R.id.item_submitter_img);
            userName = itemView.findViewById(R.id.item_submitter_name);
            submitTime = itemView.findViewById(R.id.item_submit_time);
            title = itemView.findViewById(R.id.item_submit_title);
            price = itemView.findViewById(R.id.item_price);
        }
    }
}

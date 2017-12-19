package com.project.android.wewin.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.User;

import java.util.ArrayList;

/**
 * Created by pengming on 2017/11/4.
 */

public class TaskRvAdapter extends RecyclerView.Adapter<TaskRvAdapter.ViewHolder> {
    private Context context;
    private ArrayList<User> users;

    public TaskRvAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
    }

    @Override
    public TaskRvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_task, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TaskRvAdapter.ViewHolder holder, int position) {
        holder.userNameTV.setText(users.get(position).getUserName());
        holder.passWordTV.setText(users.get(position).getPassWord());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTV, passWordTV;

        public ViewHolder(View itemView) {
            super(itemView);
            userNameTV = itemView.findViewById(R.id.tv_username);
            passWordTV = itemView.findViewById(R.id.tv_password);
        }
    }
}

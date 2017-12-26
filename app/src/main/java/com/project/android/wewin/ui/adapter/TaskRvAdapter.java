package com.project.android.wewin.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.android.wewin.R;

import java.util.ArrayList;

/**
 * Created by pengming on 2017/11/4.
 */

public class TaskRvAdapter extends RecyclerView.Adapter<TaskRvAdapter.ViewHolder> {
    private Context context;


    public TaskRvAdapter(Context context){
        this.context = context;

    }

    @Override
    public TaskRvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return null;
    }

    @Override
    public void onBindViewHolder(TaskRvAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(View itemView) {
            super(itemView);

        }
    }
}

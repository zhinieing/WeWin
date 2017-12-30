package com.project.android.wewin.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project.android.wewin.R;
import com.project.android.wewin.ui.activity.MainActivity;
import com.project.android.wewin.ui.activity.ReleaseHomeworkActivity;
import com.project.android.wewin.ui.adapter.TaskRvAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by pengming on 2017/11/4.
 */

public class TaskFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    @BindView(R.id.task_list)
    RecyclerView taskList;
    Unbinder unbinder;

    private TaskRvAdapter taskRvAdapter;

    public TaskFragment() {

    }

    public static TaskFragment newInstance(int sectionNumber) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        unbinder = ButterKnife.bind(this, view);

        /*LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        taskList.setLayoutManager(llm);
        taskList.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));


        taskRvAdapter = new TaskRvAdapter(container.getContext());
        taskList.setAdapter(taskRvAdapter);*/


        return view;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

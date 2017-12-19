package com.project.android.wewin.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.android.wewin.R;
import com.project.android.wewin.ui.adapter.TaskRvAdapter;
import com.project.android.wewin.data.remote.model.User;

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
    private ArrayList<User> users;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        taskList.setLayoutManager(llm);
        taskList.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        users = new ArrayList<User>();
        for(int i = 0; i < 15; i++){
            User user = new User("pengming" + i % 10, "123456" + i % 10);
            users.add(user);
        }

        taskRvAdapter = new TaskRvAdapter(container.getContext(), users);
        taskList.setAdapter(taskRvAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

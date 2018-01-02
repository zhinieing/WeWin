package com.project.android.wewin.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project.android.wewin.MyApplication;
import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.data.remote.model.MyUser;
import com.project.android.wewin.ui.activity.DetailActivity;
import com.project.android.wewin.ui.adapter.OnItemClickListener;
import com.project.android.wewin.ui.adapter.TaskRvAdapter;
import com.project.android.wewin.utils.Util;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by pengming on 2017/11/4.
 */

public class TaskFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    @BindView(R.id.task_list)
    RecyclerView taskList;

    @BindView(R.id.homeworklist_spl)
    SwipeRefreshLayout mRefreshLayout;

    Unbinder unbinder;

    private TaskRvAdapter taskRvAdapter;
    private MyUser user;


    private final OnItemClickListener<HomeWork> homeWorkOnItemClickListener =
            new OnItemClickListener<HomeWork>() {
                @Override
                public void onClick(HomeWork homeWork) {
                    if (Util.isNetworkConnected(MyApplication.getInstance())) {
                        DetailActivity.startDetailActivity(getActivity(), homeWork);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
            };

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

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        taskList.setLayoutManager(llm);
        taskList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        mRefreshLayout.setOnRefreshListener(new HomeWorSwipeListener());
        mRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
            taskRvAdapter = new TaskRvAdapter(getActivity(), homeWorkOnItemClickListener);
            taskList.setAdapter(taskRvAdapter);
        } else {

        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        user = BmobUser.getCurrentUser(MyUser.class);

        initHomeWorkView();

    }

    private class HomeWorSwipeListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            taskRvAdapter.clearHomeWorkList();
            initHomeWorkView();
        }
    }

    private void initHomeWorkView() {
        if (user != null && user.getGroupIds() != null) {
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                BmobQuery<HomeWork> query = new BmobQuery<HomeWork>();
                query.addWhereContainedIn("groupId", user.getGroupIds());
                query.setLimit(10);
                query.order("createdAt");
                query.findObjects(new FindListener<HomeWork>() {
                    @Override
                    public void done(List<HomeWork> list, BmobException e) {
                        if (e == null) {
                            taskRvAdapter.setHomeWorkList(list);
                            mRefreshLayout.setRefreshing(false);
                            Log.d("wewein", "done: " + list.size());
                        } else {
                            Log.d("wewein", "done: " + e);
                        }
                    }
                });
            }
        }
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

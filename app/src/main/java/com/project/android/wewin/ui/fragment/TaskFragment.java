package com.project.android.wewin.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.project.android.wewin.MyApplication;
import com.project.android.wewin.R;
import com.project.android.wewin.data.Injection;
import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.data.remote.model.MyUser;
import com.project.android.wewin.ui.activity.DetailActivity;
import com.project.android.wewin.ui.adapter.OnItemClickListener;
import com.project.android.wewin.ui.adapter.TaskRvAdapter;
import com.project.android.wewin.utils.Util;
import com.project.android.wewin.viewmodel.HomeWorkListViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @author pengming
 * @date 2017/11/4
 */

public class TaskFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    @BindView(R.id.task_list)
    RecyclerView taskList;

    @BindView(R.id.homeworklist_spl)
    SwipeRefreshLayout mRefreshLayout;

    Unbinder unbinder;


    private TaskRvAdapter taskRvAdapter;

    private HomeWorkListViewModel mHomeWorkListViewModel = null;


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
        taskList.addOnScrollListener(new HomeWorkOnScrollListener());

        mRefreshLayout.setOnRefreshListener(new HomeWorSwipeListener());
        mRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
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


    private class HomeWorSwipeListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {

            mRefreshLayout.setRefreshing(true);
            mHomeWorkListViewModel.refreshHomeWorkListData();
        }
    }

    private class HomeWorkOnScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            LinearLayoutManager layoutManager = (LinearLayoutManager)
                    recyclerView.getLayoutManager();
            int lastPosition = layoutManager
                    .findLastVisibleItemPosition();
            if (lastPosition == taskRvAdapter.getItemCount() - 1) {

                mHomeWorkListViewModel.loadNextPageHomeWorkList();
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        subscribeUI();

    }


    private void subscribeUI() {
        if (!isAdded()) {
            return;
        }
        HomeWorkListViewModel.Factory factory = new HomeWorkListViewModel
                .Factory(MyApplication.getInstance(),
                Injection.getDataRepository(MyApplication.getInstance()));
        mHomeWorkListViewModel = ViewModelProviders.of(this, factory).get(HomeWorkListViewModel.class);
        mHomeWorkListViewModel.getHomeWorkList().observe(this, new Observer<List<HomeWork>>() {
            @Override
            public void onChanged(@Nullable List<HomeWork> homeWorks) {
                if (homeWorks == null || homeWorks.size() == 0) {
                    return;
                }
                Log.d("wewein", "onChanged : homeworks");
                taskRvAdapter.clearHomeWorkList();
                taskRvAdapter.setHomeWorkList(homeWorks);
            }
        });
        mHomeWorkListViewModel.isLoadingHomeWorkList().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean state) {
                if (state == null) {
                    return;
                }

                mRefreshLayout.setRefreshing(state);
            }
        });

        mRefreshLayout.setRefreshing(true);
        mHomeWorkListViewModel.refreshHomeWorkListData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

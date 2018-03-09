package com.project.android.wewin.ui.fragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.project.android.wewin.ui.activity.MainActivity;
import com.project.android.wewin.ui.adapter.OnItemClickListener;
import com.project.android.wewin.ui.adapter.TaskRvAdapter;
import com.project.android.wewin.utils.Util;
import com.project.android.wewin.viewmodel.HomeWorkListViewModel;

import java.util.ArrayList;
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

public class TaskFragment extends LazyLoadFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    @BindView(R.id.task_list)
    RecyclerView taskList;

    @BindView(R.id.homework_list_spl)
    SwipeRefreshLayout mRefreshLayout;

    Unbinder unbinder;


    private TaskRvAdapter taskRvAdapter;
    private HomeWorkListViewModel mHomeWorkListViewModel;
    private Context context;

    private boolean nextPage = false;


    private final OnItemClickListener<HomeWork> homeWorkOnItemClickListener =
            new OnItemClickListener<HomeWork>() {
                @Override
                public void onClick(HomeWork homeWork) {
                    if (Util.isNetworkConnected(MyApplication.getInstance())) {
                        DetailActivity.startDetailActivity((MainActivity)context, homeWork, 0);
                    } else {
                        Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.d("wewein", "onAttach: 1");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("wewein", "onCreate: 1");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("wewein", "onActivityCreated: 1");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("wewein", "onResume: 1");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("wewein", "onStart: 1");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("wewein", "onDestroy: 1");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("wewein", "onDetach: 1");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("wewein", "onCreateView: 1");
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        taskList.setLayoutManager(llm);
        taskList.setItemAnimator(new DefaultItemAnimator());
        taskList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        taskList.addOnScrollListener(new HomeWorkOnScrollListener());

        mRefreshLayout.setOnRefreshListener(new HomeWorSwipeListener());
        mRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
            taskRvAdapter = new TaskRvAdapter(context, homeWorkOnItemClickListener);
            taskList.setAdapter(taskRvAdapter);
        } else {

        }

        return view;
    }


    private class HomeWorSwipeListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            nextPage = false;
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

                nextPage = true;
                mHomeWorkListViewModel.loadNextPageHomeWorkList();

            }
        }
    }


    @Override
    public void requestData() {
        nextPage = false;
        if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
            subscribeUI();
        }
    }


    private void subscribeUI() {
        Log.d("wewein", "subscribeUI: TaskFragment");

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
                if (homeWorks == null) {
                    return;
                }


                if (!nextPage) {
                    taskRvAdapter.clearHomeWorkList();
                } else {
                    /*if (homeWorks.size() == 0) {
                        Toast.makeText(context, "数据加载完毕", Toast.LENGTH_SHORT).show();
                    }*/
                }

                taskRvAdapter.setHomeWorkList(homeWorks);
            }
        });
        mHomeWorkListViewModel.isLoadingHomeWorkList().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == null) {
                    return;
                }

                mRefreshLayout.setRefreshing(aBoolean);
            }
        });

        mRefreshLayout.setRefreshing(true);
        mHomeWorkListViewModel.refreshHomeWorkListData();


    }

    @Override
    public void onDestroyView() {
        Log.d("wewein", "onDestroyView: 1");
        super.onDestroyView();
        unbinder.unbind();
    }
}

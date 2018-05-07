package com.project.android.wewin.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.project.android.wewin.data.Injection;
import com.project.android.wewin.data.remote.model.Task;
import com.project.android.wewin.ui.activity.MainActivity;
import com.project.android.wewin.ui.activity.TaskDetailActivity;
import com.project.android.wewin.ui.adapter.ItemClickListener;
import com.project.android.wewin.ui.adapter.SingleTypeAdapter;
import com.project.android.wewin.utils.Util;
import com.project.android.wewin.viewmodel.HomeWorkListViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author pengming
 * @date 2037/33/4
 */

public class GotTaskFragment extends LazyLoadFragment implements ItemClickListener<Task> {

    @BindView(R.id.task_list)
    RecyclerView gotTaskList;

    @BindView(R.id.homework_list_spl)
    SwipeRefreshLayout mRefreshLayout;

    Unbinder unbinder;

    private List<Task> mTask = new ArrayList<>();
    private SingleTypeAdapter<Task> taskSingleTypeAdapter;
    private HomeWorkListViewModel mHomeWorkListViewModel;
    private Context context;

    private boolean nextPage = false;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.d("wewein", "onAttach: 3");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("wewein", "onCreateView: 3");
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        gotTaskList.setLayoutManager(llm);
        gotTaskList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        mRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        gotTaskList.addOnScrollListener(new TaskOnScrollListener());
        mRefreshLayout.setOnRefreshListener(new TaskSwipeListener());

        taskSingleTypeAdapter = new SingleTypeAdapter<>(context, R.layout.item_task_list);
        taskSingleTypeAdapter.setPresenter(this);

        gotTaskList.setAdapter(taskSingleTypeAdapter);
        return view;
    }

    @Override
    public void onItemClick(Task task) {
        if (Util.isNetworkConnected(MyApplication.getInstance())) {
            TaskDetailActivity.startDetailActivity((MainActivity) context, task, 2);
        } else {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }


    private class TaskSwipeListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            nextPage = false;
            mRefreshLayout.setRefreshing(true);
            mHomeWorkListViewModel.refreshReceivedTaskListData();
        }
    }

    private class TaskOnScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 0) {
                LinearLayoutManager layoutManager = (LinearLayoutManager)
                        recyclerView.getLayoutManager();

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                if (!nextPage && (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    nextPage = true;
                    mHomeWorkListViewModel.loadNextReceivedPageTaskList();
                }
            }
        }
    }


    @Override
    public void requestData() {
        nextPage = false;
        subscribeTaskUI();
    }

    private void subscribeTaskUI() {
        if (!isAdded()) {
            return;
        }
        HomeWorkListViewModel.Factory factory = new HomeWorkListViewModel
                .Factory(MyApplication.getInstance(),
                Injection.getDataRepository(MyApplication.getInstance()));
        mHomeWorkListViewModel = ViewModelProviders.of(this, factory).get(HomeWorkListViewModel.class);
        mHomeWorkListViewModel.getReceivedTaskList().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable List<Task> tasks) {
                if (tasks == null) {
                    return;
                }

                if (!nextPage) {
                    taskSingleTypeAdapter.clear();
                    mTask.clear();
                } else {

                }
                mTask.addAll(tasks);
                taskSingleTypeAdapter.set(mTask);

            }
        });
        mHomeWorkListViewModel.isLoadingReceivedTaskList().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == null) {
                    return;
                }
                mRefreshLayout.setRefreshing(aBoolean);
            }
        });

        mRefreshLayout.setRefreshing(true);
        mHomeWorkListViewModel.refreshReceivedTaskListData();
    }


    @Override
    public void onDestroyView() {
        Log.d("wewwin", "onDestroyView: 3");
        super.onDestroyView();
        unbinder.unbind();
    }


}

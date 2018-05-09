package com.project.android.wewin.ui.fragment;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.project.android.wewin.MyApplication;
import com.project.android.wewin.R;
import com.project.android.wewin.data.Injection;
import com.project.android.wewin.data.remote.model.Task;
import com.project.android.wewin.databinding.ItemTaskListBinding;
import com.project.android.wewin.ui.activity.MainActivity;
import com.project.android.wewin.ui.activity.ReleaseTaskActivity;
import com.project.android.wewin.ui.activity.TaskDetailActivity;
import com.project.android.wewin.ui.adapter.BaseViewAdapter;
import com.project.android.wewin.ui.adapter.BindingViewHolder;
import com.project.android.wewin.ui.adapter.ItemClickListener;
import com.project.android.wewin.ui.adapter.SingleTypeAdapter;
import com.project.android.wewin.utils.L;
import com.project.android.wewin.utils.Util;
import com.project.android.wewin.viewmodel.HomeWorkListViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * @author pengming
 * @date 2027/22/4
 */

public class PostedTaskFragment extends LazyLoadFragment implements ItemClickListener<Task> {

    @BindView(R.id.task_list)
    RecyclerView postedTaskList;

    @BindView(R.id.homework_list_spl)
    SwipeRefreshLayout mRefreshLayout;

    Unbinder unbinder;

    private List<Task> mTask = new ArrayList<>();
    private SingleTypeAdapter<Task> taskSingleTypeAdapter;
    private HomeWorkListViewModel mHomeWorkListViewModel;
    private Context context;
    private Task task;
    private boolean nextPage = false;
    private ItemTouchHelper.Callback callback;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.d("wewein", "onAttach: 2");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("wewein", "onCreateView: 2");
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        postedTaskList.setLayoutManager(llm);
        postedTaskList.setItemAnimator(new DefaultItemAnimator());
        postedTaskList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        mRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        postedTaskList.addOnScrollListener(new TaskOnScrollListener());
        mRefreshLayout.setOnRefreshListener(new TaskSwipeListener());

        taskSingleTypeAdapter = new SingleTypeAdapter<>(context, R.layout.item_task_list);
        taskSingleTypeAdapter.setPresenter(this);
        taskSingleTypeAdapter.setDecorator(new AdapterDecorator());

        postedTaskList.setAdapter(taskSingleTypeAdapter);
        return view;
    }

    @Override
    public void onItemClick(Task task) {
        if (Util.isNetworkConnected(MyApplication.getInstance())) {
            TaskDetailActivity.startDetailActivity((MainActivity) context, task, 1);
        } else {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteTask() {
        task.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    nextPage = false;
                    mRefreshLayout.setRefreshing(true);
                    mHomeWorkListViewModel.refreshPostedTaskListData();
                } else {
                    L.i("delete failed");
                }
            }
        });
    }


    private class AdapterDecorator implements BaseViewAdapter.Decorator {

        @Override
        public void decorator(BindingViewHolder holder, final int position, int viewType) {
            ItemTaskListBinding binding = (ItemTaskListBinding) holder.getBinding();
            task = mTask.get(position);
            binding.taskMenu.setVisibility(View.VISIBLE);
            binding.taskMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(getContext(), v);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.update_task:
                                    Intent intent = new Intent(getContext(), ReleaseTaskActivity.class);
                                    startActivity(intent);
                                    return true;
                                case R.id.delete_task:
                                    deleteTask();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.inflate(R.menu.taskmenu);
                    popup.show();
                }
            });
        }
    }

    private class TaskSwipeListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            nextPage = false;
            mRefreshLayout.setRefreshing(true);
            mHomeWorkListViewModel.refreshPostedTaskListData();
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
                    mHomeWorkListViewModel.loadNextPostedPageTaskList();
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
        mHomeWorkListViewModel.getPostedTaskList().observe(this, new Observer<List<Task>>() {
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
        mHomeWorkListViewModel.isLoadingPostedTaskList().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == null) {
                    return;
                }
                mRefreshLayout.setRefreshing(aBoolean);
            }
        });

        mRefreshLayout.setRefreshing(true);
        mHomeWorkListViewModel.refreshPostedTaskListData();
    }


    @Override
    public void onDestroyView() {
        Log.d("wewein", "onDestroyView: 2");
        super.onDestroyView();
        unbinder.unbind();
    }
}

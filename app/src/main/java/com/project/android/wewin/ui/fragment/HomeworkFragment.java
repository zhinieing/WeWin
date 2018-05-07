package com.project.android.wewin.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.ui.activity.DetailActivity;
import com.project.android.wewin.ui.activity.MainActivity;
import com.project.android.wewin.ui.adapter.ItemClickListener;
import com.project.android.wewin.ui.adapter.OnItemClickListener;
import com.project.android.wewin.utils.Util;
import com.project.android.wewin.viewmodel.HomeWorkListViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeworkFragment extends LazyLoadFragment implements ItemClickListener<HomeWork> {

    @BindView(R.id.task_list)
    RecyclerView taskList;

    @BindView(R.id.homework_list_spl)
    SwipeRefreshLayout mRefreshLayout;

    Unbinder unbinder;
    private HomeWorkListViewModel mHomeWorkListViewModel;
    private Context context;

    private boolean nextPage = false;

    private final OnItemClickListener<HomeWork> homeWorkOnItemClickListener =
            new OnItemClickListener<HomeWork>() {
                @Override
                public void onClick(HomeWork homeWork) {

                }
            };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
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

        mRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        taskList.addOnScrollListener(new HomeWorkOnScrollListener());
        mRefreshLayout.setOnRefreshListener(new HomeWorkSwipeListener());

//        homeworkRvAdapter = new HomeworkRvAdapter(context, homeWorkOnItemClickListener);
//        taskList.setAdapter(homeworkRvAdapter);

        return view;
    }

    @Override
    public void onItemClick(HomeWork homeWork) {
        if (Util.isNetworkConnected(MyApplication.getInstance())) {
            DetailActivity.startDetailActivity((MainActivity) context, homeWork, 0);
        } else {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    private class HomeWorkSwipeListener implements SwipeRefreshLayout.OnRefreshListener {
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
//            if (lastPosition == homeworkRvAdapter.getItemCount() - 1) {
//
//                nextPage = true;
//                mHomeWorkListViewModel.loadNextPageHomeWorkList();
//
//            }
        }
    }

    @Override
    public void requestData() {
        nextPage = false;
        subscribeUI();
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
//                    homeworkRvAdapter.clearHomeWorkList();
                } else {
                    /*if (homeWorks.size() == 0) {
                        Toast.makeText(context, "数据加载完毕", Toast.LENGTH_SHORT).show();
                    }*/
                }

//                homeworkRvAdapter.setHomeWorkList(homeWorks);

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
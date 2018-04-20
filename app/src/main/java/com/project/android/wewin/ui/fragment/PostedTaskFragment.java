package com.project.android.wewin.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
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
import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.ui.activity.DetailActivity;
import com.project.android.wewin.ui.activity.MainActivity;
import com.project.android.wewin.ui.adapter.OnItemClickListener;
import com.project.android.wewin.ui.adapter.HomeworkRvAdapter;
import com.project.android.wewin.utils.Util;
import com.project.android.wewin.viewmodel.HomeWorkListViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author pengming
 * @date 2027/22/4
 */

public class PostedTaskFragment extends LazyLoadFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    @BindView(R.id.posted_task_list)
    RecyclerView postedTaskList;

    @BindView(R.id.posted_homework_list_spl)
    SwipeRefreshLayout mRefreshLayout;

    Unbinder unbinder;

    private HomeworkRvAdapter homeworkRvAdapter;
    private HomeWorkListViewModel mHomeWorkListViewModel;
    private Context context;

    private boolean nextPage = false;


    private final OnItemClickListener<HomeWork> homeWorkOnItemClickListener =
            new OnItemClickListener<HomeWork>() {
                @Override
                public void onClick(HomeWork homeWork) {
                    if (Util.isNetworkConnected(MyApplication.getInstance())) {
                        DetailActivity.startDetailActivity((MainActivity)context, homeWork, 1);
                    } else {
                        Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
            };

    public PostedTaskFragment() {
    }

    public static PostedTaskFragment newInstance(int sectionNumber) {
        PostedTaskFragment fragment = new PostedTaskFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.d("wewein", "onAttach: 2");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("wewein", "onCreate: 2");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("wewein", "onActivityCreated: 2");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("wewein", "onResume: 2");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("wewein", "onStart: 2");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("wewein", "onDestroy: 2");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("wewein", "onDetach: 2");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (getArguments().getInt(ARG_SECTION_NUMBER) == 1 && resultCode == 2) {
            requestData();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("wewein", "onCreateView: 2");
        View view = inflater.inflate(R.layout.fragment_posted_task, container, false);
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        postedTaskList.setLayoutManager(llm);
        postedTaskList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        postedTaskList.addOnScrollListener(new PostedTaskFragment.HomeWorkOnScrollListener());

        mRefreshLayout.setOnRefreshListener(new PostedTaskFragment.HomeWorSwipeListener());
        mRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
            homeworkRvAdapter = new HomeworkRvAdapter(context, homeWorkOnItemClickListener);
            postedTaskList.setAdapter(homeworkRvAdapter);
        } else {

        }
        return view;
    }


    private class HomeWorSwipeListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            nextPage = false;
            mRefreshLayout.setRefreshing(true);
            mHomeWorkListViewModel.refreshPostedHomeWorkListData();

        }
    }

    private class HomeWorkOnScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            LinearLayoutManager layoutManager = (LinearLayoutManager)
                    recyclerView.getLayoutManager();
            int lastPosition = layoutManager
                    .findLastVisibleItemPosition();
            if (lastPosition == homeworkRvAdapter.getItemCount() - 1) {

                nextPage = true;
                mHomeWorkListViewModel.loadNextPostedPageHomeWorkList();
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
        Log.d("wewein", "subscribeUI: PostedTaskFragment");

        if (!isAdded()) {
            return;
        }
        HomeWorkListViewModel.Factory factory = new HomeWorkListViewModel
                .Factory(MyApplication.getInstance(),
                Injection.getDataRepository(MyApplication.getInstance()));
        mHomeWorkListViewModel = ViewModelProviders.of(this, factory).get(HomeWorkListViewModel.class);
        mHomeWorkListViewModel.getPostedHomeWorkList().observe(this, new Observer<List<HomeWork>>() {
            @Override
            public void onChanged(@Nullable List<HomeWork> homeWorks) {
                if (homeWorks == null) {
                    return;
                }


                if (!nextPage) {
                    homeworkRvAdapter.clearHomeWorkList();
                } else {
                    /*if (homeWorks.size() == 0) {
                        Toast.makeText(context, "数据加载完毕", Toast.LENGTH_SHORT).show();
                    }*/
                }
                homeworkRvAdapter.setHomeWorkList(homeWorks);
            }
        });

        mHomeWorkListViewModel.isLoadingPostedHomeWorkList().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == null) {
                    return;
                }

                mRefreshLayout.setRefreshing(aBoolean);
            }
        });

        mRefreshLayout.setRefreshing(true);
        mHomeWorkListViewModel.refreshPostedHomeWorkListData();

    }

    @Override
    public void onDestroyView() {
        Log.d("wewein", "onDestroyView: 2");
        super.onDestroyView();
        unbinder.unbind();
    }
}

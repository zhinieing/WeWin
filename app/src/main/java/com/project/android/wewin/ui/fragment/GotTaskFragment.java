package com.project.android.wewin.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import com.project.android.wewin.data.Injection;
import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.ui.activity.DetailActivity;
import com.project.android.wewin.ui.activity.MainActivity;
import com.project.android.wewin.ui.adapter.OnItemClickListener;
import com.project.android.wewin.ui.adapter.TaskRvAdapter;
import com.project.android.wewin.utils.Util;
import com.project.android.wewin.viewmodel.HomeWorkListViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *
 * @author pengming
 * @date 2037/33/4
 */

public class GotTaskFragment extends LazyLoadFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    @BindView(R.id.got_task_list)
    RecyclerView gotTaskList;

    @BindView(R.id.got_homework_list_spl)
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
                        DetailActivity.startDetailActivity((MainActivity)context, homeWork, 2);
                    } else {
                        Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
            };

    public GotTaskFragment() {

    }

    public static GotTaskFragment newInstance(int sectionNumber) {
        GotTaskFragment fragment = new GotTaskFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.d("wewein", "onAttach: 3");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("wewein", "onCreate: 3");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("wewein", "onActivityCreated: 3");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("wewein", "onResume: 3");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("wewein", "onStart: 3");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("wewein", "onDestroy: 3");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("wewein", "onDetach: 3");
    }
    

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("wewein", "onCreateView: 3");
        View view = inflater.inflate(R.layout.fragment_got_task, container, false);
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        gotTaskList.setLayoutManager(llm);
        gotTaskList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        gotTaskList.addOnScrollListener(new GotTaskFragment.HomeWorkOnScrollListener());

        mRefreshLayout.setOnRefreshListener(new GotTaskFragment.HomeWorSwipeListener());
        mRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
            taskRvAdapter = new TaskRvAdapter(context, homeWorkOnItemClickListener);
            gotTaskList.setAdapter(taskRvAdapter);
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
            if (lastPosition == taskRvAdapter.getItemCount() - 1) {

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
                    taskRvAdapter.clearHomeWorkList();
                } else {
                    /*if (homeWorks.size() == 0) {
                        Toast.makeText(context, "数据加载完毕", Toast.LENGTH_SHORT).show();
                    }*/
                }
                taskRvAdapter.setHomeWorkList(homeWorks);
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


    private void subscribeUI() {
        Log.d("wewein", "subscribeUI: GotTaskFragment");
    }


    @Override
    public void onDestroyView() {
        Log.d("wewwin", "onDestroyView: 3");
        super.onDestroyView();
        unbinder.unbind();
    }


}

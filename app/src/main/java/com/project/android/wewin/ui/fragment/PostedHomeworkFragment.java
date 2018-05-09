package com.project.android.wewin.ui.fragment;

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
import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.ui.activity.DetailActivity;
import com.project.android.wewin.ui.activity.MainActivity;
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
 */
public class PostedHomeworkFragment extends LazyLoadFragment implements ItemClickListener<HomeWork> {
    @BindView(R.id.task_list)
    RecyclerView postedHomeWorkList;

    @BindView(R.id.homework_list_spl)
    SwipeRefreshLayout mRefreshLayout;

    Unbinder unbinder;

    private List<HomeWork> mHomeWork = new ArrayList<>();
    private SingleTypeAdapter<HomeWork> homeworkSingleTypeAdapter;
    private HomeWorkListViewModel mHomeWorkListViewModel;
    private Context context;
    private HomeWork homework;
    private boolean nextPage = false;


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
        postedHomeWorkList.setLayoutManager(llm);
        postedHomeWorkList.setItemAnimator(new DefaultItemAnimator());
        postedHomeWorkList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        mRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        postedHomeWorkList.addOnScrollListener(new PostedHomeworkFragment.HomeWorkOnScrollListener());
        mRefreshLayout.setOnRefreshListener(new PostedHomeworkFragment.HomeWorkSwipeListener());

        homeworkSingleTypeAdapter = new SingleTypeAdapter<>(context, R.layout.item_homework_list);
        homeworkSingleTypeAdapter.setPresenter(this);
        homeworkSingleTypeAdapter.setDecorator(new PostedHomeworkFragment.AdapterDecorator());

        postedHomeWorkList.setAdapter(homeworkSingleTypeAdapter);
        return view;
    }

    @Override
    public void onItemClick(HomeWork homework) {
        if (Util.isNetworkConnected(MyApplication.getInstance())) {
            DetailActivity.startDetailActivity((MainActivity) context, homework, 1);
        } else {
            Toast.makeText(context, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteHomeWork() {
        homework.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    nextPage = false;
                    mRefreshLayout.setRefreshing(true);
                    mHomeWorkListViewModel.refreshPostedHomeWorkListData();
                } else {
                    L.i("delete failed");
                }
            }
        });
    }

    private class AdapterDecorator implements BaseViewAdapter.Decorator {

        @Override
        public void decorator(BindingViewHolder holder, final int position, int viewType) {
            /*ItemHomeWorkListBinding binding = (ItemHomeWorkListBinding) holder.getBinding();
            homework = mHomeWork.get(position);
            binding.homeworkMenu.setVisibility(View.VISIBLE);
            binding.homeworkMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(getContext(), v);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.update_homework:
                                    Intent intent = new Intent(getContext(), ReleaseHomeWorkActivity.class);
                                    startActivity(intent);
                                    return true;
                                case R.id.delete_homework:
                                    deleteHomeWork();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.inflate(R.menu.homeworkmenu);
                    popup.show();
                }
            });*/
        }
    }

    private class HomeWorkSwipeListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            nextPage = false;
            mRefreshLayout.setRefreshing(true);
            mHomeWorkListViewModel.refreshPostedHomeWorkListData();
        }
    }

    private class HomeWorkOnScrollListener extends RecyclerView.OnScrollListener {

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
                    mHomeWorkListViewModel.loadNextPostedPageHomeWorkList();
                }
            }
        }
    }

    @Override
    public void requestData() {
        nextPage = false;

        subscribeHomeWorkUI();
    }

    private void subscribeHomeWorkUI() {
        if (!isAdded()) {
            return;
        }
        HomeWorkListViewModel.Factory factory = new HomeWorkListViewModel
                .Factory(MyApplication.getInstance(),
                Injection.getDataRepository(MyApplication.getInstance()));
        mHomeWorkListViewModel = ViewModelProviders.of(this, factory).get(HomeWorkListViewModel.class);
        mHomeWorkListViewModel.getPostedHomeWorkList().observe(this, new Observer<List<HomeWork>>() {
            @Override
            public void onChanged(@Nullable List<HomeWork> homeworks) {
                if (homeworks == null) {
                    return;
                }

                if (!nextPage) {
                    homeworkSingleTypeAdapter.clear();
                    mHomeWork.clear();
                } else {

                }
                mHomeWork.addAll(homeworks);
                homeworkSingleTypeAdapter.set(mHomeWork);
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
        super.onDestroyView();
        unbinder.unbind();
    }
}

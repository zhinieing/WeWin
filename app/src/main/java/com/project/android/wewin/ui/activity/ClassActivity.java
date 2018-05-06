package com.project.android.wewin.ui.activity;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.project.android.wewin.MyApplication;
import com.project.android.wewin.R;
import com.project.android.wewin.data.Injection;
import com.project.android.wewin.data.local.db.entity.ClassInfo;
import com.project.android.wewin.data.remote.api.ApiClass;
import com.project.android.wewin.data.remote.api.ApiManager;
import com.project.android.wewin.data.remote.model.OpenidData;
import com.project.android.wewin.databinding.ItemClassListBinding;
import com.project.android.wewin.ui.adapter.BaseViewAdapter;
import com.project.android.wewin.ui.adapter.BindingViewHolder;
import com.project.android.wewin.ui.adapter.ItemClickListener;
import com.project.android.wewin.ui.adapter.SingleTypeAdapter;
import com.project.android.wewin.utils.Util;
import com.project.android.wewin.viewmodel.ClassViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author pengming
 */
public class ClassActivity extends AppCompatActivity implements ItemClickListener<ClassInfo>, PopupMenu.OnMenuItemClickListener{

    private final static int START_RELEASE_ACTIVITY = 13;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.class_list)
    RecyclerView classList;
    @BindView(R.id.class_list_spl)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private List<ClassInfo> mItems = new ArrayList<>();
    private SingleTypeAdapter<ClassInfo> mAdapter;
    private ClassInfo classInfo;

    private ClassViewModel mClassViewModel;

    private ApiClass mApiClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("班级");


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ClassActivity.this, ReleaseClassActivity.class), START_RELEASE_ACTIVITY);
            }
        });


        GridLayoutManager glm = new GridLayoutManager(this, 2);
        classList.setLayoutManager(glm);
        classList.setItemAnimator(new DefaultItemAnimator());

        mRefreshLayout.setOnRefreshListener(new ClassSwipeListener());
        mRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mAdapter = new SingleTypeAdapter<>(this, R.layout.item_class_list);
        mAdapter.setPresenter(this);
        mAdapter.setDecorator(new ClassAdapterDecorator());

        classList.setAdapter(mAdapter);

        subscribeUI();

        mApiClass = ApiManager.getInstance().getApiClass();
    }


    private void updateUI() {
        mRefreshLayout.setRefreshing(true);
        mClassViewModel.loadClassList();
    }



    private class ClassSwipeListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            updateUI();
        }
    }


    private void subscribeUI() {
        ClassViewModel.Factory factory = new ClassViewModel.Factory(MyApplication.getInstance(),
                Injection.getDataRepository(MyApplication.getInstance()));
        mClassViewModel = ViewModelProviders.of(this, factory).get(ClassViewModel.class);
        mClassViewModel.getJoinedClassList().observe(this, new Observer<List<ClassInfo>>() {
            @Override
            public void onChanged(@Nullable List<ClassInfo> classInfos) {
                if (classInfos == null || classInfos.size() == 0) {
                    return;
                }

                mItems = classInfos;
                mAdapter.set(classInfos);
            }
        });
        mClassViewModel.isLoadingClassList().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == null) {
                    return;
                }

                mRefreshLayout.setRefreshing(aBoolean);
            }
        });

        updateUI();

    }


    private class ClassAdapterDecorator implements BaseViewAdapter.Decorator {

        @Override
        public void decorator(BindingViewHolder holder, final int position, int viewType) {
            ItemClassListBinding binding = (ItemClassListBinding) holder.getBinding();

            binding.classMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    classInfo = mItems.get(position);

                    PopupMenu popup = new PopupMenu(ClassActivity.this, v);
                    popup.setOnMenuItemClickListener(ClassActivity.this);
                    popup.inflate(R.menu.classmenu);
                    popup.show();
                }
            });
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update_class:
                Intent intent = new Intent(this, ReleaseClassActivity.class);
                intent.putExtra("class_name", classInfo.getClassName());
                intent.putExtra("class_icon", classInfo.getClassIcon());
                intent.putExtra("class_id", classInfo.getClassId());
                startActivityForResult(intent, START_RELEASE_ACTIVITY);
                return true;
            case R.id.delete_class:
                deleteClass();
                return true;
            default:
                return false;
        }

    }


    @Override
    public void onItemClick(ClassInfo mClass) {
        Toast.makeText(ClassActivity.this, "item clicked", Toast.LENGTH_SHORT).show();
    }


    @BindingAdapter({"classImg"})
    public static void loadClassImage(ImageView imageView, String url) {
        Util.loadNormalImage(Uri.parse(url), imageView);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ReleaseClassActivity.RESULT_CODE) {
            if (requestCode == START_RELEASE_ACTIVITY) {
                updateUI();
            }
        }
    }

    private void deleteClass() {
        mApiClass.deleteClass(classInfo.getClassId())
                .enqueue(new Callback<OpenidData>() {
                    @Override
                    public void onResponse(@NonNull Call<OpenidData> call, @NonNull Response<OpenidData> response) {
                        if (response.isSuccessful() || response.body().state == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ClassActivity.this, getString(R.string.delete_class_success), Toast.LENGTH_SHORT).show();
                                    updateUI();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<OpenidData> call, @NonNull Throwable t) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ClassActivity.this, getString(R.string.delete_class_fail), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });
    }
}

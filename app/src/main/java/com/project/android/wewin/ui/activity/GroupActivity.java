package com.project.android.wewin.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.project.android.wewin.R;
import com.project.android.wewin.data.local.db.entity.ClassInfo;
import com.project.android.wewin.data.local.db.entity.GroupInfo;
import com.project.android.wewin.data.remote.api.ApiManager;
import com.project.android.wewin.data.remote.api.ApiMember;
import com.project.android.wewin.data.remote.api.ApiOpenid;
import com.project.android.wewin.data.remote.api.ApiUser;
import com.project.android.wewin.data.remote.model.OpenidData;
import com.project.android.wewin.data.remote.model.ResultData;
import com.project.android.wewin.data.remote.model.UserData;
import com.project.android.wewin.data.remote.model.UserInfoData;
import com.project.android.wewin.ui.fragment.GroupFragment;
import com.project.android.wewin.ui.fragment.MemberFragment;
import com.project.android.wewin.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author pengming
 */
public class GroupActivity extends AppCompatActivity implements GroupFragment.OnFragmentInteractionListener, MemberFragment.OnFragmentReturnInteractionListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.container)
    CoordinatorLayout container;
    @BindView(R.id.class_icon)
    ImageView classIcon;

    private FragmentManager fragmentManager;
    private ClassInfo classInfo;

    private GroupFragment groupFragment;
    private MemberFragment memberFragment;

    private ApiMember mApiMember;
    private ApiOpenid mApiOpenid;
    private ApiUser mApiUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        classInfo = (ClassInfo) getIntent().getSerializableExtra("class_info");

        Util.loadCircleImage(Uri.parse(classInfo.getClassIcon()), classIcon);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        groupFragment = new GroupFragment();
        transaction.add(R.id.fragment, groupFragment);
        transaction.commit();

        mApiMember = ApiManager.getInstance().getApiMember();
        mApiOpenid = ApiManager.getInstance().getApiOpenid();
        mApiUser = ApiManager.getInstance().getApiUser();

        /*LinearLayoutManager llm = new LinearLayoutManager(this);
        groupList.setLayoutManager(llm);
        groupList.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new SingleTypeAdapter<>(this, R.layout.item_group_list, true);
        mAdapter.setPresenter(this);
        groupList.setAdapter(mAdapter);

        callback = new SimpleItemTouchHelperCallback<>(mAdapter, this);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(groupList);


        classInfo = (ClassInfo) getIntent().getSerializableExtra("class_info");

        getSupportActionBar().setTitle(classInfo.getClassName());
        Util.loadCircleImage(Uri.parse(classInfo.getClassIcon()), classIcon);

        subscribeUI();

        mApiMember = ApiManager.getInstance().getApiMember();*/
    }


    @Override
    public void onFragmentInteraction(GroupInfo g) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.hide(groupFragment);
        memberFragment = MemberFragment.newInstance(g);
        transaction.add(R.id.fragment, memberFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onFragmentReturnInteraction() {
        fragmentManager.popBackStack();
    }




    public void addGroup(String groupName) {
        mApiMember.postGroup(groupName, classInfo.getClassId())
                .enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(Call<ResultData> call, Response<ResultData> response) {
                        if (response.isSuccessful() || response.body().state == 0) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(container, R.string.add_group_success, Snackbar.LENGTH_SHORT).show();
                                    ((GroupFragment)fragmentManager.findFragmentById(R.id.fragment)).updateUI();
                                }
                            });

                        }
                    }

                    @Override
                    public void onFailure(Call<ResultData> call, Throwable t) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(container, R.string.add_group_fail, Snackbar.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
    }


    public void deleteGroup(Integer[] groups) {
        mApiMember.deleteGroups(groups)
                .enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(@NonNull Call<ResultData> call, @NonNull Response<ResultData> response) {
                        if (response.isSuccessful() || response.body().state == 0) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((GroupFragment)fragmentManager.findFragmentById(R.id.fragment)).updateUI();
                                }
                            });

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResultData> call, @NonNull Throwable t) {
                    }
                });
    }


    public void loadMembers(Integer groupId) {

        mApiMember.getGroupMembers(groupId)
                .enqueue(new Callback<UserData>() {
                    @Override
                    public void onResponse(@NonNull Call<UserData> call, @NonNull final Response<UserData> response) {
                        Log.d("wewein", "onResponse: " + response.body());

                        if (response.isSuccessful() || response.body().state == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((MemberFragment)fragmentManager.findFragmentById(R.id.fragment)).setUI(response.body().data);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserData> call, @NonNull Throwable t) {
                        Log.d("wewein", "onResponse: " + t);

                    }
                });

    }


    public void findUser(String phoneno) {
        mApiOpenid.getOpenid(phoneno)
                .enqueue(new Callback<OpenidData>() {
                    @Override
                    public void onResponse(@NonNull Call<OpenidData> call, @NonNull Response<OpenidData> response) {
                        if (response.isSuccessful() || response.body().state == 0) {
                            mApiUser.getUser(response.body().data.get(0))
                                    .enqueue(new Callback<UserInfoData>() {
                                        @Override
                                        public void onResponse(@NonNull Call<UserInfoData> call, @NonNull final Response<UserInfoData> response) {
                                            Log.d("wewein", "onResponse: "+response.toString());
                                            if (response.isSuccessful()) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ((MemberFragment)fragmentManager.findFragmentById(R.id.fragment)).showSearchUser(response.body().data);
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<UserInfoData> call, @NonNull Throwable t) {
                                            Log.d("wewein", "onResponse3: "+ t);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<OpenidData> call, @NonNull Throwable t) {

                    }
                });
    }


    public void addMember(Integer groupId, String[] openid) {
        mApiMember.postGroupMembers(groupId, openid)
                .enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(@NonNull Call<ResultData> call, @NonNull Response<ResultData> response) {
                        if (response.isSuccessful() || response.body().state == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((MemberFragment)fragmentManager.findFragmentById(R.id.fragment)).updateUI();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResultData> call, @NonNull Throwable t) {

                    }
                });
    }

    public void deleteMember(Integer groupId, String openid) {
        mApiMember.deleteGroupMember(groupId, openid)
                .enqueue(new Callback<ResultData>() {
                    @Override
                    public void onResponse(@NonNull Call<ResultData> call, @NonNull Response<ResultData> response) {
                        if (response.isSuccessful() || response.body().state == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((MemberFragment)fragmentManager.findFragmentById(R.id.fragment)).updateUI();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResultData> call, @NonNull Throwable t) {

                    }
                });
    }
}

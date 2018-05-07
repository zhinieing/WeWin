package com.project.android.wewin.ui.fragment;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.android.wewin.MyApplication;
import com.project.android.wewin.R;
import com.project.android.wewin.data.Injection;
import com.project.android.wewin.data.local.db.entity.GroupInfo;
import com.project.android.wewin.data.local.db.entity.UserInfo;
import com.project.android.wewin.data.remote.api.ApiManager;
import com.project.android.wewin.data.remote.api.ApiMember;
import com.project.android.wewin.data.remote.model.MyUser;
import com.project.android.wewin.data.remote.model.UserData;
import com.project.android.wewin.databinding.ItemClassListBinding;
import com.project.android.wewin.databinding.ItemMemberListBinding;
import com.project.android.wewin.ui.activity.ClassActivity;
import com.project.android.wewin.ui.activity.GroupActivity;
import com.project.android.wewin.ui.adapter.BaseViewAdapter;
import com.project.android.wewin.ui.adapter.BindingViewHolder;
import com.project.android.wewin.ui.adapter.ItemClickListener;
import com.project.android.wewin.ui.adapter.SingleTypeAdapter;
import com.project.android.wewin.utils.Util;
import com.project.android.wewin.viewmodel.GroupViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * @author pengming
 */
public class MemberFragment extends Fragment implements ItemClickListener<GroupInfo> {

    private static final String ARG_SECTION_NUMBER = "section_number";


    @BindView(R.id.member_list)
    RecyclerView memberList;
    @BindView(R.id.container)
    FrameLayout container;
    Unbinder unbinder;
    @BindView(R.id.return_back)
    ImageView returnBack;


    private OnFragmentReturnInteractionListener mListener;
    private Context context;

    private FloatingActionButton fab;

    private SingleTypeAdapter<UserInfo> mAdapter;
    private GroupViewModel mGroupViewModel;

    private String openid = null;

    private List<UserInfo> mItems = new ArrayList<>();
    private GroupInfo groupInfo;
    private UserInfo userInfo;

    private AlertDialog myDialog;
    private ImageView add, photo;
    private TextView username;


    public MemberFragment() {
    }

    public static MemberFragment newInstance(GroupInfo groupInfo) {
        MemberFragment fragment = new MemberFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SECTION_NUMBER, groupInfo);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member, container, false);
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        memberList.setLayoutManager(llm);
        memberList.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new SingleTypeAdapter<>(context, R.layout.item_group_list);
        mAdapter.setPresenter(this);
        mAdapter.setDecorator(new MemberAdapterDecorator());
        memberList.setAdapter(mAdapter);

        groupInfo = (GroupInfo) getArguments().getSerializable(ARG_SECTION_NUMBER);

        ((AppCompatActivity) context).getSupportActionBar().setTitle(groupInfo.getGroupName());
        fab = ((AppCompatActivity) context).findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (openid == null) {
                    addMemberDialog();
                } else {
                    ((GroupActivity)context).deleteMember(groupInfo.getGroupId(), openid);
                }

            }
        });
        returnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFragmentReturnInteraction();
                }
            }
        });

        subscribeUI();

        return view;
    }


    private void subscribeUI() {

        if (context != null) {
            ((GroupActivity)context).loadMembers(groupInfo.getGroupId());
        }

        GroupViewModel.Factory factory = new GroupViewModel.Factory(MyApplication.getInstance(),
                Injection.getDataRepository(MyApplication.getInstance()), 0, groupInfo.getGroupId());
        mGroupViewModel = ViewModelProviders.of(this, factory).get(GroupViewModel.class);
        mGroupViewModel.getMemberList().observe(this, new Observer<List<UserInfo>>() {
            @Override
            public void onChanged(@Nullable List<UserInfo> userInfos) {

                if (userInfos == null) {
                    return;
                }

                mItems = userInfos;
                mAdapter.set(userInfos);
            }
        });
        mGroupViewModel.isLoadingMemberList().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == null) {
                    return;
                }
            }
        });

    }

    public void setUI(List<UserInfo> ui) {
        mItems = ui;
        mAdapter.set(ui);
    }

    public void updateUI() {
        mGroupViewModel.loadMemberList();
    }



    private class MemberAdapterDecorator implements BaseViewAdapter.Decorator {

        @Override
        public void decorator(BindingViewHolder holder, final int position, int viewType) {
            final ItemMemberListBinding binding = (ItemMemberListBinding) holder.getBinding();
            binding.itemUser.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (mItems.get(position).equals(v.getTag())) {
                        binding.itemUser.setTranslationZ(-6f);
                        ObjectAnimator animator = ObjectAnimator.ofFloat(fab, "rotation", -135, 20, 0);
                        animator.setDuration(500);
                        animator.start();

                        openid = null;
                    } else {
                        v.setTag(mItems.get(position).getOpenid());
                        binding.itemUser.setTranslationZ(6f);
                        ObjectAnimator animator = ObjectAnimator.ofFloat(fab, "rotation", 0, -155, -135);
                        animator.setDuration(500);
                        animator.start();

                        openid = mItems.get(position).getOpenid();
                    }

                    return true;
                }
            });
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFragmentReturnInteractionListener) {
            mListener = (OnFragmentReturnInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    private void addMemberDialog() {
        if (context != null) {
            View alertView = LayoutInflater.from(context).inflate(R.layout.alert_add_user, null);
            final EditText editText = alertView.findViewById(R.id.alert_user_phone);
            final ImageView search = alertView.findViewById(R.id.alert_user_search);
            add = alertView.findViewById(R.id.alert_user_add);

            photo = alertView.findViewById(R.id.alert_user_photo);
            username = alertView.findViewById(R.id.alert_user_name);

            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((GroupActivity)context).findUser(editText.getText().toString());
                }
            });

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDialog.cancel();
                    String[] openid = {userInfo.getOpenid()};
                    ((GroupActivity)context).addMember(groupInfo.getGroupId(), openid);

                }
            });


            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(alertView);

            myDialog = builder.create();
            myDialog.show();

        }

    }


    public void showSearchUser(UserInfo userInfo) {
        this.userInfo = userInfo;
        add.setVisibility(View.VISIBLE);
        Util.loadCircleImage(Uri.parse(userInfo.getAvatar()), photo);
        username.setText(userInfo.getNickname());
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(GroupInfo groupInfo) {

    }


    public interface OnFragmentReturnInteractionListener {
        void onFragmentReturnInteraction();
    }





}

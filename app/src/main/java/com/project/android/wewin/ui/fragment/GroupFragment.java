package com.project.android.wewin.ui.fragment;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.project.android.wewin.MyApplication;
import com.project.android.wewin.R;
import com.project.android.wewin.data.Injection;
import com.project.android.wewin.data.local.db.entity.ClassInfo;
import com.project.android.wewin.data.local.db.entity.GroupInfo;
import com.project.android.wewin.ui.activity.GroupActivity;
import com.project.android.wewin.ui.adapter.ItemClickListener;
import com.project.android.wewin.ui.adapter.ItemDissmissListener;
import com.project.android.wewin.ui.adapter.SimpleItemTouchHelperCallback;
import com.project.android.wewin.ui.adapter.SingleTypeAdapter;
import com.project.android.wewin.viewmodel.GroupViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author pengming
 */
public class GroupFragment extends Fragment implements ItemClickListener<GroupInfo>, ItemDissmissListener {

    @BindView(R.id.group_list)
    RecyclerView groupList;
    Unbinder unbinder;
    @BindView(R.id.container)
    LinearLayout container;

    private OnFragmentInteractionListener mListener;
    private Context context;

    private ClassInfo classInfo;
    private List<GroupInfo> mItems = new ArrayList<>();

    private SingleTypeAdapter<GroupInfo> mAdapter;
    private GroupViewModel mGroupViewModel;
    private ItemTouchHelper.Callback callback;

    public GroupFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager llm = new LinearLayoutManager(context);
        groupList.setLayoutManager(llm);
        groupList.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new SingleTypeAdapter<>(context, R.layout.item_group_list, true);
        mAdapter.setPresenter(this);
        groupList.setAdapter(mAdapter);

        callback = new SimpleItemTouchHelperCallback<>(mAdapter, this);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(groupList);


        classInfo = (ClassInfo) ((AppCompatActivity) context).getIntent().getSerializableExtra("class_info");

        ((AppCompatActivity) context).getSupportActionBar().setTitle(classInfo.getClassName());
        FloatingActionButton fab = ((AppCompatActivity) context).findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addGroupDialog();
            }
        });

        subscribeUI();

        return view;
    }


    private void subscribeUI() {
        GroupViewModel.Factory factory = new GroupViewModel.Factory(MyApplication.getInstance(),
                Injection.getDataRepository(MyApplication.getInstance()), classInfo.getClassId(), 0);
        mGroupViewModel = ViewModelProviders.of(this, factory).get(GroupViewModel.class);
        mGroupViewModel.getGroupList().observe(this, new Observer<List<GroupInfo>>() {
            @Override
            public void onChanged(@Nullable List<GroupInfo> groupInfos) {
                if (groupInfos == null || groupInfos.size() == 0) {
                    return;
                }

                mItems = groupInfos;
                mAdapter.set(groupInfos);
                ((SimpleItemTouchHelperCallback<GroupInfo>)callback).set(groupInfos);
            }
        });
        mGroupViewModel.isLoadingGroupList().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == null) {
                    return;
                }
            }
        });

        updateUI();
    }


    public void updateUI() {
        mGroupViewModel.loadGroupList();
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(GroupInfo groupInfo) {
        if (mListener != null) {
            mListener.onFragmentInteraction(groupInfo);
        }
    }



    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        Integer[] groups = {mItems.get(position).getGroupId()};
        mItems.remove(position);
        if (context != null) {
            ((GroupActivity)context).deleteGroup(groups);
        }
    }


    private void addGroupDialog() {
        if (context != null) {
            View alertView = LayoutInflater.from(context).inflate(R.layout.alert_modify_username, null);
            final EditText editText = alertView.findViewById(R.id.alert_modified_username);
            editText.setHint(getString(R.string.alert_add_group));

            AlertDialog myDialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.add_group);
            builder.setView(alertView);
            builder.setPositiveButton(R.string.alert_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((GroupActivity)context).addGroup(editText.getText().toString());
                }
            });

            builder.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            myDialog = builder.create();
            myDialog.show();
        }

    }





    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(GroupInfo groupInfo);
    }


}

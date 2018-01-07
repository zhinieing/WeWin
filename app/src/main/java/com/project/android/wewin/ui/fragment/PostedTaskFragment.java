package com.project.android.wewin.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.android.wewin.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *
 * @author pengming
 * @date 2017/11/4
 */

public class PostedTaskFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    @BindView(R.id.posted_task_list)
    RecyclerView postedTaskList;
    Unbinder unbinder;

    public PostedTaskFragment() {

    }

    public static PostedTaskFragment newInstance(int sectionNumber) {
        PostedTaskFragment fragment = new PostedTaskFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posted_task, container, false);
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        postedTaskList.setLayoutManager(llm);
        postedTaskList.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

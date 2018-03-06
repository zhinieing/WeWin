package com.project.android.wewin.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
 * @date 2037/33/4
 */

public class GotTaskFragment extends LazyLoadFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    @BindView(R.id.got_task_list)
    RecyclerView gotTaskList;
    Unbinder unbinder;

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

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        gotTaskList.setLayoutManager(llm);
        gotTaskList.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

        return view;
    }


    @Override
    public void requestData() {
        if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
            subscribeUI();
        }
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

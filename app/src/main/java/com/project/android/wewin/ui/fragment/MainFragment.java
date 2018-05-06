package com.project.android.wewin.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.project.android.wewin.MyApplication;
import com.project.android.wewin.R;
import com.project.android.wewin.data.Injection;
import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.ui.activity.ReleaseHomeworkActivity;
import com.project.android.wewin.ui.activity.ReleaseTaskActivity;
import com.project.android.wewin.ui.adapter.LazyFragmentPagerAdapter;
import com.project.android.wewin.viewmodel.HomeWorkListViewModel;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 *
 * @author pengming
 * @date 2017/11/3
 */

public class MainFragment extends Fragment implements LazyFragmentPagerAdapter.Laziable{

    private static final String ARG_SECTION_NUMBER = "section_number";

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private HomeWorkListViewModel mHomeWorkListViewModel;

    private boolean isTeacher = false;


    Unbinder unbinder;
    @BindView(R.id.main_tabs)
    TabLayout mainTabs;
    @BindView(R.id.tabs_viewpager)
    ViewPager tabsViewpager;

    public static MainFragment newInstance(int sectionNumber) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null);
        unbinder = ButterKnife.bind(this, view);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        tabsViewpager.setAdapter(mSectionsPagerAdapter);

        mainTabs.setupWithViewPager(tabsViewpager);

        mainTabs.post(new Runnable() {
            @Override
            public void run() {
                setIndicator(mainTabs, 60);
            }
        });

        if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
            tabsViewpager.addOnPageChangeListener(mOnPageChangeListener);
        }

        return view;
    }


    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {
            FloatingActionButton fab = getActivity().findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (position) {
                        case 0:
                            if (!isTeacher) {
                                Toast.makeText(view.getContext(), getString(R.string.is_not_any_class_teacher), Toast.LENGTH_SHORT).show();
                            } else {
                                startActivityForResult(new Intent(view.getContext(), ReleaseHomeworkActivity.class), 1);
                            }
                            break;
                        case 1:
                            startActivityForResult(new Intent(view.getContext(), ReleaseTaskActivity.class), 2);
                            break;
                        default:
                    }
                }
            });
        }

        @Override
        public void onPageSelected(final int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
            subscribeUI();
        }

    }


    private void subscribeUI() {
        if (!isAdded()) {
            return;
        }
        HomeWorkListViewModel.Factory factory = new HomeWorkListViewModel
                .Factory(MyApplication.getInstance(),
                Injection.getDataRepository(MyApplication.getInstance()));
        mHomeWorkListViewModel = ViewModelProviders.of(this, factory).get(HomeWorkListViewModel.class);
        mHomeWorkListViewModel.isClassTeacher().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == null) {
                    return;
                }

                isTeacher = aBoolean;
            }
        });

    }


    public static void setIndicator(TabLayout tabLayout, int marginDip) {
        try {
            Field mTabStripField = tabLayout.getClass().getDeclaredField("mTabStrip");
            mTabStripField.setAccessible(true);
            LinearLayout mTabStrip = (LinearLayout) mTabStripField.get(tabLayout);

            int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginDip, Resources.getSystem().getDisplayMetrics());

            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                View tabView = mTabStrip.getChildAt(i);
                tabView.setPadding(0, 0, 0, 0);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                params.leftMargin = margin;
                params.rightMargin = margin;
                tabView.setLayoutParams(params);
                tabView.invalidate();
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    if (position == 1){
                        return new TaskFragment();
                    }else {
                        return new HomeworkFragment();
                    }
                case 2:
                    if (position == 1){
                        return new PostedTaskFragment();
                    }else {
                        return new PostedHomeworkFragment();
                    }
                case 3:
                    if (position == 1){
                        return new GotTaskFragment();
                    }else {
                        return new GotHomeworkFragment();
                    }
                default:
            }
            return null;

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "作业";
                case 1:
                    return "问答";
                default:
            }
            return null;
        }
    }
}

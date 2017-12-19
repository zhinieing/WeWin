package com.project.android.wewin.ui.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.project.android.wewin.R;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by pengming on 2017/11/3.
 */

public class MainFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private SectionsPagerAdapter mSectionsPagerAdapter;

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

        return view;
    }


    public static void setIndicator(TabLayout tabLayout, int marginDip){
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
            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case 1:
                    return TaskFragment.newInstance(position + 1);
                case 2:
                    return PostedTaskFragment.newInstance(position + 1);
                case 3:
                    return GotTaskFragment.newInstance(position + 1);
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

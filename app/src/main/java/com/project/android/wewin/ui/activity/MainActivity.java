package com.project.android.wewin.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.project.android.wewin.R;
import com.project.android.wewin.data.remote.model.HomeWork;
import com.project.android.wewin.data.remote.model.MyUser;
import com.project.android.wewin.ui.adapter.LazyFragmentPagerAdapter;
import com.project.android.wewin.ui.adapter.LazyViewPager;
import com.project.android.wewin.ui.fragment.MainFragment;
import com.project.android.wewin.ui.menu.DrawerAdapter;
import com.project.android.wewin.ui.menu.DrawerItem;
import com.project.android.wewin.ui.menu.PersonItem;
import com.project.android.wewin.ui.menu.SimpleItem;
import com.project.android.wewin.ui.menu.SpaceItem;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
//The main screen for WeWin

/**
 * @author pengming
 */
public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    @BindView(R.id.main_viewpager)
    LazyViewPager mainViewpager;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private static final int POS_PERSON = 0;
    private static final int POS_CONTACT = 1;
    private static final int POS_LIKE = 2;
    private static final int POS_SET = 3;
    private static final int POS_HELP = 4;
    private static final int POS_EXIT = 6;


    private String[] screenTitles;
    private Drawable[] screenIcons;

    private MenuItem prevMenuItem;

    private SlidingRootNav slidingRootNav;


    private DrawerAdapter drawerAdapter;
    private List<DrawerItem> items;

    private MyUser user;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            mainViewpager.setCurrentItem(item.getOrder());
            return true;
        }

    };

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (prevMenuItem != null) {
                prevMenuItem.setChecked(false);
            } else {
                navigation.getMenu().getItem(0).setChecked(false);
            }
            navigation.getMenu().getItem(position).setChecked(true);
            prevMenuItem = navigation.getMenu().getItem(position);

            if (position == 0) {
                fab.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);


        user = BmobUser.getCurrentUser(MyUser.class);
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        /*底部导航栏设置*/
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        disableShiftMode(navigation);

        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        };

        int[] colors = new int[]{
                color(R.color.textColorSecondary),
                color(R.color.color_button_default)
        };

        ColorStateList csl = new ColorStateList(states, colors);
        navigation.setItemTextColor(csl);
        navigation.setItemIconTintList(csl);



        /*Viewpager页面设置*/
        mainViewpager.addOnPageChangeListener(mOnPageChangeListener);
        mainViewpager.setAdapter(new CustomLazyFragmentPagerAdapter(getSupportFragmentManager()));



        /*侧边导航栏设置*/
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();


        items = new ArrayList<DrawerItem>();
        items.addAll(Arrays.asList(
                createPersonItemFor(Uri.parse(""), ""),
                createItemFor(POS_CONTACT),
                createItemFor(POS_LIKE),
                createItemFor(POS_SET),
                createItemFor(POS_HELP),
                new SpaceItem(30),
                createItemFor(POS_EXIT)));

        drawerAdapter = new DrawerAdapter(items);
        drawerAdapter.setListener(this);


        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(drawerAdapter);


    }


    /*disableShiftMode函数是为了消除当menuitem>3时产生的动画效果*/

    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        user = BmobUser.getCurrentUser(MyUser.class);

        if (user != null) {
            PersonItem personItem = (PersonItem) items.get(0);
            personItem.setImageUri(Uri.parse(user.getUserPhoto()));
            personItem.setUsername(user.getUsername());

            drawerAdapter.notifyDataSetChanged();
        }

    }


    /*自定义懒加载FragmentPagerAdapter*/
    private static class CustomLazyFragmentPagerAdapter extends LazyFragmentPagerAdapter {

        private CustomLazyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(ViewGroup container, int position) {
            return MainFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }


    /*侧边栏点击事件*/
    @Override
    public void onItemSelected(int position) {
        if (position == POS_EXIT) {
            MyUser.logOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        slidingRootNav.closeMenu();

        switch (position) {
            case POS_PERSON:
                startActivity(new Intent(MainActivity.this, PersonActivity.class));
                break;
            case POS_CONTACT:
                startActivity(new Intent(MainActivity.this, ContactActivity.class));
                break;
            default:

        }
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }

    private PersonItem createPersonItemFor(Uri imageUri, String username){
        return new PersonItem(imageUri, username);
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /*向双层嵌套Fragment分发onActivityResult*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fragmentManager=getSupportFragmentManager();
        for(int indext=0;indext<fragmentManager.getFragments().size();indext++)
        {
            Fragment fragment=fragmentManager.getFragments().get(indext);
            if (fragment != null) {
                handleResult(fragment,requestCode,resultCode,data);
            }
        }
    }

    private void handleResult(Fragment fragment,int requestCode,int resultCode,Intent data)
    {
        fragment.onActivityResult(requestCode, resultCode, data);

        List<Fragment> childFragment = fragment.getChildFragmentManager().getFragments();
        if(childFragment!=null) {
            for(Fragment f:childFragment) {
                if(f!=null)
                {
                    handleResult(f, requestCode, resultCode, data);
                }
            }
        }

    }
}

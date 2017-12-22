package com.project.android.wewin.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.project.android.wewin.R;
import com.project.android.wewin.ui.adapter.ViewPagerAdapter;
import com.project.android.wewin.ui.fragment.MainFragment;
import com.project.android.wewin.ui.menu.DrawerAdapter;
import com.project.android.wewin.ui.menu.DrawerItem;
import com.project.android.wewin.ui.menu.PersonItem;
import com.project.android.wewin.ui.menu.SimpleItem;
import com.project.android.wewin.ui.menu.SpaceItem;
import com.wilddog.wilddogauth.WilddogAuth;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.lang.reflect.Field;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
//The main screen for WeWin

public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    @BindView(R.id.main_toolbar)
    Toolbar toolbar;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    @BindView(R.id.main_viewpager)
    ViewPager mainViewpager;
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

    private WilddogAuth wilddogAuth;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_task:
                    mainViewpager.setCurrentItem(0);
                    return true;
                case R.id.navigation_send:
                    mainViewpager.setCurrentItem(1);
                    return true;
                case R.id.navigation_receive:
                    mainViewpager.setCurrentItem(2);
                    return true;
                case R.id.navigation_message:
                    //mainViewpager.setCurrentItem(3);
                    return true;
            }
            return false;
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

        wilddogAuth = WilddogAuth.getInstance();

        //底部导航栏设置
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



        //Viewpager页面设置
        mainViewpager.addOnPageChangeListener(mOnPageChangeListener);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(MainFragment.newInstance(1));
        viewPagerAdapter.addFragment(MainFragment.newInstance(2));
        viewPagerAdapter.addFragment(MainFragment.newInstance(3));
        mainViewpager.setAdapter(viewPagerAdapter);



        //侧边导航栏设置
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        //http://7xwels.com1.z0.glb.clouddn.com/cover/IMG_20160520_001714.jpg
        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createPersonItemFor("http://7xwels.com1.z0.glb.clouddn.com/cover/IMG_20160520_001714.jpg", "小明"),
                createItemFor(POS_CONTACT),
                createItemFor(POS_LIKE),
                createItemFor(POS_SET),
                createItemFor(POS_HELP),
                new SpaceItem(30),
                createItemFor(POS_EXIT)));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo 根据fragment判断启动ReleaseHomeworkActivity还是ReleaseTaskActivity

                startActivity(new Intent(MainActivity.this,ReleaseHomeworkActivity.class));
                Toast.makeText(MainActivity.this, "fab", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //disableShiftMode函数是为了消除当menuitem>3时产生的动画效果
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

    //侧边栏点击事件
    @Override
    public void onItemSelected(int position) {
        if (position == POS_EXIT) {
            wilddogAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            //finish();
        }
        slidingRootNav.closeMenu();

        switch (position) {
            case POS_PERSON:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
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

    private PersonItem createPersonItemFor(String imageUrl, String username){
        return new PersonItem(imageUrl, username);
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

}

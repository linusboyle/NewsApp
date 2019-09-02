/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.ui.activity;

import android.os.Bundle;

import com.example.newsclientapp.R;
import com.example.newsclientapp.core.PermissionUtils;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.example.newsclientapp.storage.StorageManager;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import butterknife.BindView;
import java.util.HashMap;
import com.example.newsclientapp.ui.fragment.*;
import com.example.newsclientapp.ui.fragment.FragmentFactory.FragmentEnum;
import com.example.newsclientapp.core.ExceptionHandler;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar) Toolbar mToolbar;

    private HashMap<FragmentEnum, Fragment> mFragments = new HashMap<>(6);
    private ExceptionHandler exceptionHandler;

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

        // exception handler
        this.exceptionHandler = ExceptionHandler.getInstance();
        this.exceptionHandler.init(this);

        // permissions
        PermissionUtils.verifyStoragePermissions(this);

        // storage
        StorageManager.getInstance().init(this);

        // toolbar
        this.mToolbar.setTitle(R.string.display_news);
        setSupportActionBar(this.mToolbar);

        // drawer_layout
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // nav_view
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, this.mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // fragment
        setDefaultFragment(FragmentEnum.NEWS_TAB_FRAGMENT);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        // TODO
        if (id == R.id.nav_news_tab_fragment) {
            this.setDefaultFragment(FragmentEnum.NEWS_TAB_FRAGMENT);
        } else if (id == R.id.nav_cache) {
            this.setDefaultFragment(FragmentEnum.CACHE_FRAGMENT);
        } else if (id == R.id.nav_favorite) {
            this.setDefaultFragment(FragmentEnum.FAVORITE_FRAGMENT);
        } else if (id == R.id.nav_search) {
            this.setDefaultFragment(FragmentEnum.SEARCH_FRAGMENT);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        this.mToolbar.setTitle(item.getTitle());
        return true;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        try {
            for (FragmentEnum fe : FragmentEnum.values()) {
                Fragment target = this.mFragments.get(fe);
                Class<? extends BaseFragment> targetClass = FragmentFactory.getFragmentClass(fe);
                if (target == null && targetClass.equals(fragment.getClass())) {
                    this.mFragments.put(fe, targetClass.getConstructor().newInstance());
                    break;
                }
            }
        } catch (Exception e) {
            this.exceptionHandler.uncaughtException(Thread.currentThread(), e);
        }
    }


    /**
     * 设置默认的Fragment
     * @param fIndex 选项卡的enum标号：id
     */
    private void setDefaultFragment(FragmentEnum fIndex){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideFragments(fragmentTransaction);

        try {
            Fragment fragment = this.mFragments.get(fIndex);
            if (fragment != null) {
                fragmentTransaction.show(fragment);
            } else {
                this.mFragments.put(fIndex, FragmentFactory.getFragmentInstance(fIndex));
                fragmentTransaction.add(R.id.fragment_content, this.mFragments.get(fIndex));
            }
        } catch (Exception e) {
            this.exceptionHandler.uncaughtException(Thread.currentThread(), e);
        }

        fragmentTransaction.commit();
    }

    /**
     * 隐藏Fragment
     * @param fragmentTransaction 事务
     */
    private void hideFragments(FragmentTransaction fragmentTransaction) {
        for (Fragment fragment : this.mFragments.values()) {
            if (fragment != null) {
                fragmentTransaction.hide(fragment);
            }
        }
    }
}

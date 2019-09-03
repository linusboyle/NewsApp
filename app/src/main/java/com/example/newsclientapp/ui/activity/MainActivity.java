/*
 * Copyright (c) 2019 Zhilei Han and Yihao Chen.
 * This software falls under the GNU general public license version 3 or later.
 * It comes WITHOUT ANY WARRANTY WHATSOEVER. For details, see the file LICENSE
 * in the root directory or <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.example.newsclientapp.ui.activity;

import android.os.Bundle;

import com.example.newsclientapp.R;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.util.Log;
import android.view.MenuItem;

import com.example.newsclientapp.core.PermissionUtils;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import butterknife.BindView;
import java.util.HashMap;
import com.example.newsclientapp.ui.fragment.*;
import com.example.newsclientapp.ui.fragment.FragmentFactory.FragmentEnum;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.nav_view) NavigationView mNavigationView;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawer;

    private static final String TAG = "MainActivity";
    private HashMap<FragmentEnum, Fragment> mFragments = new HashMap<>();;
    private static int checkedNaviPosition = 0;


    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        // permissions
        PermissionUtils.verifyStoragePermissions(this);

        // toolbar
        this.mToolbar.setTitle(R.string.display_news);
	    createToolBarPopupMenu(mToolbar);

        // nav_view
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, this.mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

        // default fragment
        onNavigationItemSelected(mNavigationView.getMenu().getItem(checkedNaviPosition).setChecked(true));
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
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
            checkedNaviPosition = 0;
        } else if (id == R.id.nav_cache) {
            this.setDefaultFragment(FragmentEnum.CACHE_FRAGMENT);
            checkedNaviPosition = 1;
        } else if (id == R.id.nav_favorite) {
            this.setDefaultFragment(FragmentEnum.FAVORITE_FRAGMENT);
            checkedNaviPosition = 2;
        } else if (id == R.id.nav_search) {
            this.setDefaultFragment(FragmentEnum.SEARCH_FRAGMENT);
            checkedNaviPosition = 3;
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        mDrawer.closeDrawer(GravityCompat.START);
        this.mToolbar.setTitle(item.getTitle());
        return true;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        try {
            for (FragmentEnum fe : FragmentEnum.values()) {
                // Fragment target = this.mFragments.get(fe);
                Class<? extends BaseFragment> targetClass = FragmentFactory.getFragmentClass(fe);
                if (/*target == null &&*/ targetClass.equals(fragment.getClass())) {
                    // this.mFragments.put(fe, targetClass.getConstructor().newInstance());
                    this.mFragments.put(fe, fragment);
                    break;
                }
            }
        } catch (Exception e) {
            //noinspection ConstantConditions
            Log.e(TAG, e.getMessage());
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
            Log.e(TAG, e.getMessage());
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

    @Override
    public void recreate() {
        // to prevent the fragments from recreating
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : mFragments.values())
            fragmentTransaction.remove(fragment);
        fragmentTransaction.commitNow();
        mFragments = new HashMap<>();

        super.recreate();
    }
}

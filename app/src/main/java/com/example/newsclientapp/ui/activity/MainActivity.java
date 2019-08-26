package com.example.newsclientapp.ui.activity;

import android.os.Bundle;

import com.example.newsclientapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;

import butterknife.BindView;
import java.util.HashMap;
import com.example.newsclientapp.ui.fragment.*;
import com.example.newsclientapp.ui.fragment.FragmentFactory.FragmentEnum;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar) Toolbar mToolbar;

    private HashMap<FragmentEnum, Fragment> mFragments = new HashMap<>(6);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar
        this.mToolbar = findViewById(R.id.toolbar);
        this.mToolbar.setTitle(R.string.display_news);
        setSupportActionBar(this.mToolbar);

        // fab
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        try {
            for (FragmentEnum fe : FragmentEnum.values()) {
                Fragment target = this.mFragments.get(fe);
                Class<? extends BaseFragment> targetClass = FragmentFactory.getFragmentClass(fe);
                if (target == null && targetClass.equals(target.getClass())) {
                    this.mFragments.put(fe, targetClass.getConstructor().newInstance());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.print(e);
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
            if (this.mFragments.get(fIndex) != null) {
                fragmentTransaction.show(this.mFragments.get(fIndex));
            } else {
                this.mFragments.put(fIndex, FragmentFactory.getFragmentInstance(fIndex));
                fragmentTransaction.add(R.id.fragment_content, this.mFragments.get(fIndex));
            }
        } catch (Exception e) {
            // TODO: replace with ExceptionHandler
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

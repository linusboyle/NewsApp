package com.example.newsclientapp.ui.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.example.newsclientapp.ui.view.BaseView;

public abstract class BaseFragment extends RxFragment implements BaseView{

    static
    private Unbinder mUnBinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        mUnBinder = ButterKnife.bind(this, view);
        initData(container, savedInstanceState);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 布局id
     * @return id
     */
    protected abstract int getLayoutId();

    /**
     * 初始化
     * @param container viewGroup容器
     * @param savedInstanceState 保存
     */
    protected abstract void initData(ViewGroup container, Bundle savedInstanceState);

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnBinder != null) {
            mUnBinder.unbind();
        }
    }

    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return bindToLifecycle();
    }
}

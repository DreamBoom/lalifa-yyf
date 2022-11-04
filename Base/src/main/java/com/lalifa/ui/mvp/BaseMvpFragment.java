package com.lalifa.ui.mvp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.lalifa.ui.BaseFragment;
import com.lalifa.widget.loading.LoadTag;

/**
 * @author gyn
 * @date 2022/2/14
 */
public abstract class BaseMvpFragment<P extends BasePresenter> extends BaseFragment implements IBaseView {
    public P present;
    private LoadTag mLoadTag;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLoadTag = new LoadTag(activity);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        present = createPresent();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void showLoading(String msg) {
        mLoadTag.show(msg);
    }

    @Override
    public void dismissLoading() {
        mLoadTag.dismiss();
    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }

    public abstract P createPresent();
}

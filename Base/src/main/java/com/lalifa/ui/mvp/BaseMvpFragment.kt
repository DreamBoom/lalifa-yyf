package com.lalifa.ui.mvp

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.lalifa.ui.BaseFragment
import com.lalifa.ui.mvp.BasePresenter
import com.lalifa.ui.mvp.IBaseView
import com.lalifa.widget.loading.LoadTag

/**
 * @author gyn
 * @date 2022/2/14
 */
abstract class BaseMvpFragment<P : BasePresenter<*>?> : BaseFragment(), IBaseView {
    var present: P? = null
    private var mLoadTag: LoadTag? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mLoadTag = LoadTag(activity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        present = createPresent()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun showLoading(msg: String) {
        mLoadTag!!.show(msg)
    }

    override fun dismissLoading() {
        mLoadTag!!.dismiss()
    }

    override fun showEmpty() {}
    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    abstract fun createPresent(): P
}
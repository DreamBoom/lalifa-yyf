package com.lalifa.ui.mvp

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import com.lalifa.ui.mvp.IBaseView
import com.lalifa.ui.mvp.IPresenter
import com.lalifa.ui.mvp.ILifeCycle
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

/**
 * @author gyn
 * @date 2021/9/24
 */
abstract class BasePresenter<V : IBaseView?>(mView: V, lifecycle: Lifecycle) : IPresenter<V>,
    ILifeCycle, LifecycleObserver {
    var mView: V?
    private val compositeDisposable = CompositeDisposable()
    override fun attachView(mView: V, lifecycle: Lifecycle) {}
    override fun detachView() {}
    val isViewAttached: Boolean
        get() = mView != null

    fun addSubscription(disposable: Disposable?) {
        compositeDisposable.add(disposable)
    }

    override fun onCreate() {}
    override fun onDestroy() {
        mView = null
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

    override fun onStart() {}
    override fun onStop() {}
    override fun onResume() {}

    init {
        this.mView = mView
        lifecycle.addObserver(this)
    }
}
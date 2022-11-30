package com.lalifa.ui.mvp

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import com.lalifa.ui.mvp.BasePresenter
import com.lalifa.ui.mvp.ILifeCycle
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class BaseModel<V : BasePresenter<*>?>(present: V, lifecycle: Lifecycle) : ILifeCycle,
    LifecycleObserver {
    var present: V?
    private val compositeDisposable = CompositeDisposable()
    fun addSubscription(disposable: Disposable?) {
        compositeDisposable.add(disposable)
    }

    override fun onCreate() {}
    override fun onDestroy() {
        present = null
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

    override fun onStart() {}
    override fun onStop() {}
    override fun onResume() {}

    init {
        this.present = present
        lifecycle.addObserver(this)
    }
}
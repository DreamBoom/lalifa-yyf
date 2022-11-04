package com.lalifa.wapper;


import com.drake.logcat.LogCat;

import java.lang.ref.WeakReference;

/**
 * 弱引用任务
 *
 * @param <T>
 */
public abstract class WeakTask<T> implements Runnable {
    private static final String TAG = "WeakTask";
    private WeakReference<T> weakReference;

    public WeakTask(T inst) {
        weakReference = new WeakReference<>(inst);
    }

    @Override
    public final void run() {
        T ins = weakReference.get();
        if (null == ins) {
            LogCat.e(TAG, "ins is null !");
            return;
        }
        run(ins);
    }

    public abstract void run(T ins);
}
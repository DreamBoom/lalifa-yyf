package com.lalifa.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.lalifa.widget.loading.LoadTag

/**
 *
 * @ClassName BaseFragment
 * @Author lanlan
 * @Email 985334276@qq.com
 * @Date 2022/6/30 17:09
 * @Des
 */
abstract class BaseFragment<T : ViewBinding>() : Fragment() {
    lateinit var binding: T
    var loadTag: LoadTag?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadTag = LoadTag(
            requireActivity(),getString(
                R.string.text_loading
            )
        )
        initView()
        onClick()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getViewBinding(inflater, container)
        return binding.root
    }

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): T
    abstract fun initView()
    open fun onClick() {}
}
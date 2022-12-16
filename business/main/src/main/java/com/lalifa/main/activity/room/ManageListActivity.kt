package com.lalifa.main.activity.room

import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.models
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseActivity
import com.lalifa.base.BaseTitleActivity
import com.lalifa.extension.*
import com.lalifa.main.R
import com.lalifa.main.api.*
import com.lalifa.main.databinding.ActivityManageListBinding
import com.lalifa.main.fragment.adapter.roomManagerAdapter
import java.text.FieldPosition

class ManageListActivity : BaseActivity<ActivityManageListBinding>() {
    override fun getViewBinding() = ActivityManageListBinding.inflate(layoutInflater)
    var roomId = ""
    var isManage = true
    override fun initView() {
        roomId = getIntentString("roomId")
        getManage()
    }

    override fun onClick() {
        super.onClick()
        binding.apply {
            tvSearch.onClick {
                if (search.text().isEmpty()) {
                    toast("请输入被添加的人员ID")
                    return@onClick
                }
                searchManage(search.text())
            }
        }
        binding.close.onClick {
            if (!isManage) {
                getManage()
            } else {
                finish()
            }
        }
    }

    private fun getManage() {
        loadTag!!.show()
        scopeNetLife {
            isManage = true
            val manage = getManageList(roomId.noEN())
            if (null != manage && manage.isNotEmpty()) {
                loadTag!!.dismiss()
                binding.empty.gone()
                binding.manageList.roomManagerAdapter(true).apply {
                    R.id.btn.onClick {
                        removeManage(layoutPosition)
                    }
                }.models = manage
            } else {
                loadTag!!.dismiss()
                binding.empty.visible()
            }
        }
    }

    private fun searchManage(id: String) {
        loadTag!!.show()
        isManage = false
        scopeNetLife {
            val manage = getManage(id, roomId.noEN())
            if (null != manage && manage.isNotEmpty()) {
                loadTag!!.dismiss()
                binding.empty.gone()
                binding.manageList.roomManagerAdapter(false).apply {
                    R.id.btn.onClick {
                        addManage(layoutPosition)
                    }
                }.models = manage
            } else {
                loadTag!!.dismiss()
                binding.empty.visible()
            }
        }
    }
    private fun addManage(position: Int) {
        loadTag!!.show()
        val adapter = binding.manageList.bindingAdapter
        scopeNetLife {
            val add = addManage(
                adapter.getModel<ManageListBean>(position).id.toString(),
                roomId.noEN())
            if(null!=add){
                loadTag!!.dismiss()
                adapter.removeAt(position)
            }else{
                loadTag!!.dismiss()
            }
        }
    }

    private fun removeManage(position: Int) {
        loadTag!!.show()
        val adapter = binding.manageList.bindingAdapter
        scopeNetLife {
            val add = removeManage(
                adapter.getModel<ManageListBean>(position).id.toString(),
                roomId.noEN())
            if(null!=add){
                loadTag!!.dismiss()
                adapter.removeAt(position)
            }else{
                loadTag!!.dismiss()
            }
        }
    }
}
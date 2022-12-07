package com.lalifa.main.activity.che

import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseTitleActivity
import com.lalifa.ext.Config
import com.lalifa.extension.*
import com.lalifa.main.R
import com.lalifa.main.fragment.adapter.cheInfoList
import com.lalifa.main.api.*
import com.lalifa.main.databinding.ActivityCheInfoBinding

class CheInfoActivity : BaseTitleActivity<ActivityCheInfoBinding>() {
    override fun getViewBinding() = ActivityCheInfoBinding.inflate(layoutInflater)
    var inId = -1
    override fun initView() {
        inId = getIntentInt("id", -1)
        initData()
        binding.apply {
            like.onClick {
                scopeNetLife {
                    dzChe(inId)
                }
            }
        }
    }
    var bean: CheInfoBean?=null
    private fun initData() {
        scopeNetLife {
            bean = cheInfo(inId)!!
            binding.apply {
                header.load(bean!!.avatar)
                name.text = bean!!.userName
                info.text = bean!!.content
                bean!!.image.forEach {
                    Config.FILE_PATH + it
                }
                lookNum.text = "浏览量:${bean!!.browse}"
                gridImg.imagesAdapter(bean!!.image, true)
                share.text = "${bean!!.share}"
                like.text = "${bean!!.fabulous}"
                pl.text = "${bean!!.comment_count}"
                comment.text = "全部评论(${bean!!.comment_count})"
                commentList.cheInfoList().apply {
                    R.id.itemPl.onClick {
                        plId = getModel<Comment>().id
                    }
                    R.id.like.onClick {
                        scopeNetLife {
                            dzPl(getModel<Comment>().id.toString())
                            bean = cheInfo(inId)!!
                            models = bean!!.comment
                        }
                    }
                }.models = bean!!.comment
            }
        }
    }

    var plId = 0
    override fun onClick() {
        super.onClick()
        binding.apply {
            send.onClick {
                if (etPl.isEmp()) {
                    toast("请输入评论内容")
                    return@onClick
                }
                scopeNetLife {
                    plChe(inId.toString(), etPl.text(), plId.toString())
                    etPl.setText("")
                    initData()
                }
            }
        }
    }
}
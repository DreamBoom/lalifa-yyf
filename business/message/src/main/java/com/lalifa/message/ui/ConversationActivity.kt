package com.lalifa.message.ui

import android.view.MenuItem
import com.lalifa.base.BaseActivity
import com.lalifa.extension.toast
import com.lalifa.message.R
import com.lalifa.message.databinding.ActivityConBinding
import io.rong.imkit.conversation.ConversationFragment

class ConversationActivity : BaseActivity<ActivityConBinding>() {
    override fun getViewBinding() = ActivityConBinding.inflate(layoutInflater)

    override fun initView() {
        //唯一有用的代码，加载一个 layout
        // 添加会话界面
//        val  conversation = ConversationFragment()
//        val manager = supportFragmentManager
//        val transaction = manager.beginTransaction()
//        transaction.replace(R.id.mCon, conversation)
//        transaction.commit();
//        toast("11111")
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        finish()
//        return super.onOptionsItemSelected(item)
//    }
}
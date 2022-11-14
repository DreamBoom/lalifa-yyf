package com.lalifa.message.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.lalifa.base.BaseActivity
import com.lalifa.message.databinding.ActivityConversationBinding

class ConversationActivity : BaseActivity<ActivityConversationBinding>() {
    override fun getViewBinding() = ActivityConversationBinding.inflate(layoutInflater)

    override fun initView() {
        //唯一有用的代码，加载一个 layout

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }
}
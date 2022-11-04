package com.lalifa.main.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lalifa.base.BaseActivity
import com.lalifa.main.databinding.ActivityModelBinding

class ModelActivity : BaseActivity<ActivityModelBinding>() {
    override fun getViewBinding() = ActivityModelBinding.inflate(layoutInflater)

    override fun initView() {

    }

}
package cn.rongcloud.roomkit.ui.other.fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import cn.rongcloud.roomkit.adapter.phAdapter
import cn.rongcloud.roomkit.api.ranking
import cn.rongcloud.roomkit.api.roomList
import cn.rongcloud.roomkit.databinding.FragmentPhBoyBinding
import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseFragment
import com.lalifa.ext.Config
import com.lalifa.extension.gone
import com.lalifa.extension.invisible
import com.lalifa.extension.load
import com.lalifa.extension.visible

/**
 * @param category 1：日榜  2：周榜 3月榜
 * @param type 1--财富榜  2-魅力榜
 * */
class PhBoyFragment(private val category:Int,private val type:Int): BaseFragment<FragmentPhBoyBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentPhBoyBinding.inflate(layoutInflater)
    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.apply {
            scopeNetLife {
                val ranking = ranking(type.toString(), category.toString())
                if(ranking!=null&&ranking.isNotEmpty()){
                    if(ranking.size>3){
                        header1.load(Config.FILE_PATH+ranking[0].avatar)
                        header2.load(Config.FILE_PATH+ranking[1].avatar)
                        header3.load(Config.FILE_PATH+ranking[2].avatar)
                        name1.text = ranking[0].userName
                        name2.text = ranking[1].userName
                        name3.text = ranking[2].userName
                        cfz1.text = "财富值:${ranking[0].yield}"
                        cfz2.text = "财富值:${ranking[1].yield}"
                        cfz3.text = "财富值:${ranking[2].yield}"
                        val subList = ranking.subList(3, ranking.size - 1)
                        list.phAdapter().models = subList
                    }else{
                        when(ranking.size){
                            1->{
                                header1.load(Config.FILE_PATH+ranking[0].avatar)
                                name1.text = ranking[0].userName
                                cfz1.text = "财富值:${ranking[0].yield}"
                                co2.invisible()
                                hg2.invisible()
                                co3.invisible()
                                hg3.invisible()
                            }
                            2->{
                                header1.load(Config.FILE_PATH+ranking[0].avatar)
                                header2.load(Config.FILE_PATH+ranking[1].avatar)
                                name1.text = ranking[0].userName
                                name2.text = ranking[1].userName
                                cfz1.text = "财富值:${ranking[0].yield}"
                                cfz2.text = "财富值:${ranking[1].yield}"
                                co3.invisible()
                                hg3.invisible()
                            }
                            3->{
                                header1.load(Config.FILE_PATH+ranking[0].avatar)
                                header2.load(Config.FILE_PATH+ranking[1].avatar)
                                header3.load(Config.FILE_PATH+ranking[2].avatar)
                                name1.text = ranking[0].userName
                                name2.text = ranking[1].userName
                                name3.text = ranking[2].userName
                                cfz1.text = "财富值:${ranking[0].yield}"
                                cfz2.text = "财富值:${ranking[1].yield}"
                                cfz3.text = "财富值:${ranking[2].yield}"
                            }
                        }
                    }
                }
            }

        }
    }
}
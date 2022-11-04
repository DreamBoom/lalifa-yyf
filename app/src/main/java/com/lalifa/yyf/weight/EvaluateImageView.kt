package com.lalifa.yyf.weight

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.drake.brv.annotaion.DividerOrientation
import com.drake.brv.utils.divider
import com.drake.brv.utils.grid
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.lalifa.extension.applyVisible
import com.lalifa.extension.dp
import com.lalifa.extension.load
import com.lalifa.extension.preview
import com.lalifa.yyf.R
import com.lalifa.yyf.databinding.ItemEvaluateImgBinding

class EvaluateImageView : RecyclerView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    @SuppressLint("SetTextI18n")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        grid(5).divider {
            orientation = DividerOrientation.GRID
            setDivider(6.dp)
        }.setup {
            addType<String>(R.layout.item_evaluate_img)

            onBind {
                getBinding<ItemEvaluateImgBinding>().apply {
                    cover.load(getModel())
                    hideCount.apply {
                        applyVisible(modelPosition==models!!.size-1)
                        text = "+${this@EvaluateImageView.hideCount}"
                    }
                }
            }

            /*R.id.hide_count.onClick {
                isHide = true
                addModels(arrayListOf<Int>().apply {
                    addAll(dataList.subList(5, dataList.size))
                })
            }*/
            R.id.item.onClick {
                context.preview(modelPosition, dataList)
            }
        }
        setDataList(
            arrayListOf(
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F016a345bbaad7fa801213dea8653fa.jpg%401280w_1l_2o_100sh.jpg&refer=http%3A%2F%2Fimg.zcool.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1665048006&t=daa7551dac63e524aa1574e248376343",
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F015e9d58f072fca8012049efd08150.jpg%401280w_1l_2o_100sh.jpg&refer=http%3A%2F%2Fimg.zcool.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1665048048&t=8a11de8eded39cca9ed306928bc0d8ae",
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg3.doubanio.com%2Fview%2Frichtext%2Flarge%2Fpublic%2Fp128140710.jpg&refer=http%3A%2F%2Fimg3.doubanio.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1665048065&t=0ef1aa6a01b978e4a4a8f38609a4ff61",
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F016a345bbaad7fa801213dea8653fa.jpg%401280w_1l_2o_100sh.jpg&refer=http%3A%2F%2Fimg.zcool.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1665048006&t=daa7551dac63e524aa1574e248376343",
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.zcool.cn%2Fcommunity%2F015e9d58f072fca8012049efd08150.jpg%401280w_1l_2o_100sh.jpg&refer=http%3A%2F%2Fimg.zcool.cn&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1665048048&t=8a11de8eded39cca9ed306928bc0d8ae",
                "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg3.doubanio.com%2Fview%2Frichtext%2Flarge%2Fpublic%2Fp128140710.jpg&refer=http%3A%2F%2Fimg3.doubanio.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1665048065&t=0ef1aa6a01b978e4a4a8f38609a4ff61",
            )
        )
    }

    fun setDataList(list: ArrayList<String>) {
        dataList.clear()
        dataList.addAll(list)
        models = arrayListOf<String>().apply {
            addAll(dataList.subList(0, dataList.size.coerceAtMost(5)))
        }
    }

    private val dataList = arrayListOf<String>()
    private val hideCount: Int
        get() = if (dataList.size <= 5) 0 else dataList.size - 5

    private var isHide: Boolean = false

}
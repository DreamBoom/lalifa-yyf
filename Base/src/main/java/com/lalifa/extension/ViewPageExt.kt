package com.lalifa.extension

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.math.abs

/**
 *
 * @ClassName ViewPageExt
 * @Author lwj
 * @Email 1036046880@qq.com
 * @Date 2022/1/11 12:46
 *
 */
fun ViewPager.fragmentAdapter(
    fm: FragmentManager,
    mTitles: ArrayList<String>,
    mFragment: ArrayList<Fragment>.() -> Unit,
):ViewPager {
    val mFragments = ArrayList<Fragment>().apply(mFragment)
    offscreenPageLimit = if (mTitles.size >= 2) mTitles.size - 1 else mTitles.size
    adapter = object : FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mTitles[position]
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }
    }
    return this
}

fun ViewPager.fragmentAdapter(
    fm: FragmentManager,
    mFragment: ArrayList<Fragment>.() -> Unit
):ViewPager {
    val mFragments = ArrayList<Fragment>().apply(mFragment)
    offscreenPageLimit = if (mFragments.size >= 2) mFragments.size - 1 else mFragments.size
    adapter = object : FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return ""
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }
    }
    return this
}

fun ViewPager.fragmentStateAdapter(
    fm: FragmentManager,
    mTitles: ArrayList<String>,
    mFragment: ArrayList<Fragment>.() -> Unit
) {
    val mFragments = ArrayList<Fragment>().apply(mFragment)
    offscreenPageLimit = if (mTitles.size >= 2) mTitles.size - 1 else mTitles.size
    adapter = object : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mTitles[position]
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }
    }
}

fun ViewPager2.fragmentAdapter(
    orientation: Int = ViewPager2.ORIENTATION_HORIZONTAL,
    mFragment: ArrayList<Fragment>.() -> Unit,
) {
    val mFragments = ArrayList<Fragment>().apply(mFragment)
    this.orientation = orientation
    adapter = object : FragmentStateAdapter(context as FragmentActivity) {
        override fun getItemCount(): Int {
            return mFragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return mFragments[position]
        }
    }
    offscreenPageLimit = 3
}

fun TabLayout.setViewPager(viewPager2: ViewPager2, mTitles: ArrayList<String>) {
    TabLayoutMediator(this, viewPager2) { tab, position ->
        tab.text = mTitles[position]
    }.attach()
}

/**
 * 切换动画
 * @receiver ViewPager
 */
fun ViewPager.zoomPageTransformer() {
    setPageTransformer(true) { page, position ->
        val MIN_ALPHA = 0.5f
        val MIN_SCALE = 0.85f
        page.apply {
            when {
                position < -1 -> alpha = 0f
                position <= 1 -> {
                    val scaleFactor = MIN_ALPHA.coerceAtLeast(1 - abs(position))
                    val vertMargin = height * (1 - scaleFactor) / 2
                    val horzMargin = width * (1 - scaleFactor) / 2
                    translationX =
                        if (position < 0) horzMargin - vertMargin / 2 else -horzMargin + vertMargin / 2
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                    alpha =
                        MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA)
                }
                else -> alpha = 0f
            }

        }
    }
}

fun ViewPager.pageChangedListener(
    onPageScrolled: (
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) -> Unit = { _, _, _ -> },
    onPageScrollStateChanged: (state: Int) -> Unit = { _ -> },
    onPageSelected: (position: Int) -> Unit = { _ -> },
) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            onPageScrolled.invoke(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            onPageSelected.invoke(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            onPageScrollStateChanged.invoke(state)
        }

    })
}

fun ViewPager.viewAdapter(
    mTitles: ArrayList<String>,
    views: ArrayList<View>.() -> Unit,
) {
    val list = arrayListOf<View>().apply(views)
    offscreenPageLimit = if (mTitles.size >= 2) mTitles.size - 1 else mTitles.size
    adapter = object : PagerAdapter() {
        override fun getCount(): Int = list.size

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            container.addView(list[position])
            return list[position]
        }

        override fun getPageTitle(position: Int) = mTitles[position]
    }
}

fun ViewPager.viewAdapter(
    views: ArrayList<View>.() -> Unit,
) :ViewPager{
    val list = arrayListOf<View>().apply(views)
    offscreenPageLimit = if (list.size >= 2) list.size - 1 else list.size
    adapter = object : PagerAdapter() {
        override fun getCount(): Int = list.size

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            container.addView(list[position])
            return list[position]
        }
    }
    return this
}
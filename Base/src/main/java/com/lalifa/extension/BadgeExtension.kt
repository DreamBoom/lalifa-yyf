@file:Suppress("unused")

package com.lalifa.extension

import android.graphics.Color
import android.view.Gravity
import android.view.View
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView

/**
 * BottomNavigationView extensions
 *
 * @author Classic
 * @version v1.0, 2019-05-20 15:48
 */

fun BadgeDrawable?.applyNumber(v: Int) {
    // val badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.xxx)
    this?.apply {
        if (v > 0) {
            isVisible = true
            number = 99
        } else {
            isVisible = false
            clearNumber()
        }
    }
}

fun BottomNavigationView.toggleBadge(
    viewIndex: Int,
    number: Int = 0,
    targetBadge: Badge? = null
): Badge? { // 具体child的查找和view的嵌套结构请在源码中查看
    // 从bottomNavigationView中获得BottomNavigationMenuView
    val menuView = getChildAt(0) as BottomNavigationMenuView
    // 从BottomNavigationMenuView中获得child view, BottomNavigationItemView
    if (viewIndex < menuView.childCount) { // 获得viewIndex对应子tab
        val view: View = menuView.getChildAt(viewIndex)
        val badge = targetBadge ?: view.createBadge()
        badge.showOrHide(number)
        return badge
    }
    return null
}

fun View.createBadge(): Badge {
    val offsetX = (this.measuredWidth / 4).toFloat()
    return QBadgeView(context).bindTarget(this)
        .setShowShadow(false)
        .setBadgeBackgroundColor(Color.parseColor("#EE403D"))
        .setBadgeGravity(Gravity.END or Gravity.TOP)
        .setGravityOffset(offsetX, 0F, false)
}
fun Badge.showOrHide(number: Int) {
    if (number > 0) {
        this.badgeNumber = number
    } else hide(false)
}
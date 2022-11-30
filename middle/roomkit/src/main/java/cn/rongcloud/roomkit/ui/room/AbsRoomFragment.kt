package cn.rongcloud.roomkit.ui.room

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import cn.rongcloud.roomkit.R
import com.drake.logcat.LogCat.d
import com.lalifa.ui.mvp.BasePresenter
import com.lalifa.ui.mvp.BaseMvpFragment
import cn.rongcloud.roomkit.ui.room.SwitchRoomListener
import cn.rongcloud.roomkit.ui.miniroom.MiniRoomManager
import io.rong.imkit.utils.PermissionCheckUtil
import com.lalifa.widget.dialog.dialog.VRCenterDialog
import java.util.*

/**
 * @author gyn
 * @date 2021/9/17
 */
abstract class AbsRoomFragment<P : BasePresenter<*>?> : BaseMvpFragment<P>(), SwitchRoomListener {
    // 是否执行了joinRoom
    private var isExecuteJoinRoom = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        d("==================================onCreate:$tag")
    }

    private val bottomMargin = 0
    override fun preJoinRoom() {
        if (!isExecuteJoinRoom) {
            isExecuteJoinRoom = true
            joinRoom()
        }
    }

    override fun onStart() {
        super.onStart()
        MiniRoomManager.getInstance().close()
        d("==================================onStart:$tag")
    }

    override fun destroyRoom() {
        isExecuteJoinRoom = false
    }

    override fun onResume() {
        super.onResume()
        // viewPager2的onPageSelected，当从列表点击最后一个时，viewpager2选中最后一个时，会先执行onPageSelected，才执行fragment的onCreate,
        // 导致addSwitchRoomListener没执行，joinRoom就不会执行，这里判断一下没执行再执行一下。
        preJoinRoom()
        d("==================================onResume:$tag")
    }

    override fun onPause() {
        super.onPause()
        d("==================================onPause:$tag")
    }

    override fun onStop() {
        super.onStop()
        d("==================================onStop:$tag")
    }

    // 是否是请求开启悬浮窗权限的过程中
    private var checkingOverlaysPermission = false
    fun checkDrawOverlaysPermission(needOpenPermissionSetting: Boolean): Boolean {
        return if (Build.BRAND.lowercase(Locale.getDefault())
                .contains("xiaomi") || Build.VERSION.SDK_INT >= 23
        ) {
            if (PermissionCheckUtil.canDrawOverlays(requireContext(), needOpenPermissionSetting)) {
                checkingOverlaysPermission = false
                true
            } else {
                if (needOpenPermissionSetting && !Build.BRAND.lowercase(Locale.getDefault())
                        .contains("xiaomi")
                ) {
                    checkingOverlaysPermission = true
                }
                false
            }
        } else {
            checkingOverlaysPermission = false
            true
        }
    }

    fun showOpenOverlaysPermissionDialog() {
        val dialog = VRCenterDialog(requireActivity(), null)
        dialog.replaceContent(getString(R.string.text_open_suspend_permission),
            getString(R.string.cancel),
            null,
            getString(
                R.string.confirm
            ),
            {
                val intent = Intent(
                    "android.settings.action.MANAGE_OVERLAY_PERMISSION",
                    Uri.parse("package:" + requireActivity().packageName)
                )
                requireActivity().startActivity(intent)
            },
            null
        )
        dialog.show()
    }

    override fun initListener() {
        addSwitchRoomListener()
    }

    override fun onDestroyView() {
        removeSwitchRoomListener()
        super.onDestroyView()
    }

    companion object {
        const val ROOM_ID = "ROOM_ID"
    }
}
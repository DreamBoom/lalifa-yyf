package cn.rongcloud.roomkit.ui.room;

import android.content.Intent;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.drake.logcat.LogCat;
import com.lalifa.ui.BaseActivity;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.rongcloud.config.DataShareManager;
import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.api.RoomDetailBean;
import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.intent.IntentWrap;
import cn.rongcloud.roomkit.provider.VoiceRoomProvider;
import cn.rongcloud.roomkit.service.RTCNotificationService;
import cn.rongcloud.roomkit.ui.RoomType;
import io.rong.imkit.utils.StatusBarUtil;


/**
 * @author gyn
 * @date 2021/9/14
 */
public abstract class AbsRoomActivity extends BaseActivity {

    private ViewPager2 mViewPager;
    private RoomVPAdapter mRoomAdapter;
    private HashMap<String, SwitchRoomListener> switchRoomListenerMap = new HashMap<>();
    private String currentRoomId;
    private SmartRefreshLayout refreshLayout;
    private boolean canRefreshAndLoadMore;
    int mCurrentPosition;

    @Override
    public int setLayoutId() {
        return R.layout.activity_room;
    }

    @Override
    protected void onDestroy() {
        // 取消忽略av call
        DataShareManager.get().setIgnoreIncomingCall(false);
        stopService(new Intent(this, RTCNotificationService.class));
        super.onDestroy();
    }

    @Override
    public void init() {
        // 忽略av call
        DataShareManager.get().setIgnoreIncomingCall(true);
        initRoom();
        // 状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        getWrapBar().setHide(true).work();
        // 下拉刷新和加载更多
        refreshLayout = getView(R.id.layout_refresh);
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setOnRefreshListener(refreshLayout -> refresh());
        refreshLayout.setOnLoadMoreListener(refreshLayout -> loadMore());
        initRefreshAndLoadMore();
        refreshLayout.setEnableRefresh(canRefreshAndLoadMore);
        refreshLayout.setEnableLoadMore(canRefreshAndLoadMore);

        // 初始化viewpager并设置数据和监听
        mViewPager = getView(R.id.vp_room);
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mCurrentPosition = position;
                if (canRefreshAndLoadMore) {
                    refreshLayout.setEnableLoadMore(mCurrentPosition == mRoomAdapter.getItemCount() - 1);
                    refreshLayout.setEnableRefresh(mCurrentPosition == 0);
                }
                getIntent().putExtra(IntentWrap.KEY_ROOM_POSITION, position);
                String roomId = mRoomAdapter.getItemData(position);
                LogCat.d("==================end选中了第几个：" + position + ",current:" + roomId);
                switchViewPager(roomId);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        mRoomAdapter = new RoomVPAdapter(this);
        mViewPager.setAdapter(mRoomAdapter);
        List<String> roomIds = loadData();
        mRoomAdapter.setData(roomIds);
        mCurrentPosition = getCurrentItem();
        mViewPager.setCurrentItem(mCurrentPosition, false);
        startService();
    }

    private void startService() {
        Intent intent = new Intent(this, RTCNotificationService.class);
        switch (getRoomType()) {
            case RADIO_ROOM:
                intent.putExtra(RTCNotificationService.ACTION, IntentWrap.getRadioRoomAction(activity));
                break;
            case VOICE_ROOM:
                intent.putExtra(RTCNotificationService.ACTION, IntentWrap.getVoiceRoomAction(activity));
                break;
            case LIVE_ROOM:
                intent.putExtra(RTCNotificationService.ACTION, IntentWrap.getLiveRoomAction(activity));
                break;
        }
        this.startService(intent);
    }

    protected void refresh() {

    }

    protected void loadMore() {

    }

    private void switchViewPager(String roomId) {
        if (!TextUtils.equals(roomId, currentRoomId)) {
            if (currentRoomId != null && switchRoomListenerMap.containsKey(currentRoomId)) {
                switchRoomListenerMap.get(currentRoomId).destroyRoom();
                LogCat.d("==================destroyRoom:" + currentRoomId);
            }
            currentRoomId = roomId;
            LogCat.d("==================joinRoom:" + switchRoomListenerMap.containsKey(currentRoomId));
            if (switchRoomListenerMap.containsKey(currentRoomId)) {
                switchRoomListenerMap.get(currentRoomId).preJoinRoom();
                LogCat.e("==================joinRoom:" + currentRoomId);
            }
        }
    }

    protected void refreshViewPagerFinished(List<String> ids) {
        if (ids == null) return;
        if (ids.size() > 0) {
            String roomId = ids.get(0);
            if (!TextUtils.equals(roomId, currentRoomId)) {
                if (currentRoomId != null && switchRoomListenerMap.containsKey(currentRoomId)) {
                    switchRoomListenerMap.get(currentRoomId).destroyRoom();
                    LogCat.d("==================destroyRoom:" + currentRoomId);
                }
                currentRoomId = roomId;
            }
            mRoomAdapter.setData(ids);
            getIntent().putExtra(IntentWrap.KEY_ROOM_IDS, mRoomAdapter.getData());
        }
        refreshLayout.finishRefresh();
        refreshLayout.setNoMoreData(false);
    }

    protected void loadMoreViewPagerFinished(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            refreshLayout.finishLoadMore();
            refreshLayout.setNoMoreData(true);
            return;
        }
        mRoomAdapter.addData(ids);
        getIntent().putExtra(IntentWrap.KEY_ROOM_IDS, mRoomAdapter.getData());
        refreshLayout.finishLoadMore();
    }

    protected abstract void initRoom();

    // 当前页的位置
    protected int getCurrentItem() {
        if (getIntent().hasExtra(IntentWrap.KEY_ROOM_POSITION)) {
            return getIntent().getIntExtra(IntentWrap.KEY_ROOM_POSITION, 0);
        }
        return 0;
    }

    // 返回要初始化的Fragment
    protected abstract Fragment getFragment(String roomId);

    // 加载数据
    public List<String> loadData() {
        if (getIntent().hasExtra(IntentWrap.KEY_ROOM_IDS)) {
            ArrayList<String> ids = getIntent().getStringArrayListExtra(IntentWrap.KEY_ROOM_IDS);
            return ids;
        }
        return null;
    }

    protected abstract RoomType getRoomType();

    protected SmartRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    protected void initRefreshAndLoadMore() {
        canRefreshAndLoadMore = false;
        List<String> ids = loadData();
        if (ids != null && ids.size() > 0) {
            // 要打开的房间id
            String targetId = ids.get(getCurrentItem());
            // 从缓存中拿voiceRoomBean，判断是不是房主自己
            RoomDetailBean bean = VoiceRoomProvider.provider().getSync(targetId);
            if (bean != null) {
                if (TextUtils.equals(bean.getUserInfo().getUserId(),
                        UserManager.get().getUserId()) || bean.getPassword_type()==1) {
                    canRefreshAndLoadMore = false;
                } else {
                    canRefreshAndLoadMore = true;
                }
            } else {
                canRefreshAndLoadMore = false;
            }
        }
    }

    // 控制是否可以上下滑动，不能上下滑动也不能刷新和加载
    public void setCanSwitch(boolean canSwitch) {
        mViewPager.setUserInputEnabled(canSwitch);
        if (canSwitch) {
            if (canRefreshAndLoadMore) {
                refreshLayout.setEnableLoadMore(mCurrentPosition == mRoomAdapter.getItemCount() - 1);
                refreshLayout.setEnableRefresh(mCurrentPosition == 0);
            }
        } else {
            refreshLayout.setEnableRefresh(false);
            refreshLayout.setEnableLoadMore(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (switchRoomListenerMap.containsKey(currentRoomId)) {
            switchRoomListenerMap.get(currentRoomId).onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public void addSwitchRoomListener(String roomId, SwitchRoomListener switchRoomListener) {
        switchRoomListenerMap.put(roomId, switchRoomListener);
        LogCat.d("=================addSwitchRoomListener");
    }

    public void removeSwitchRoomListener(String roomId) {
        switchRoomListenerMap.remove(roomId);
        LogCat.d("=================removeSwitchRoomListener");
    }


    public void switchOtherRoom(String roomId) {
        mViewPager.setCurrentItem(mRoomAdapter.getItemPosition(roomId), false);
    }
}

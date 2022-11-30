package cn.rongcloud.roomkit.ui.roomlist;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.RecyclerView;

import com.lalifa.adapter.BannerImageAdapter;
import com.lalifa.ui.mvp.BaseMvpFragment;
import com.lalifa.widget.dialog.dialog.VRCenterDialog;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.youth.banner.Banner;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.rongcloud.config.UserManager;
import cn.rongcloud.config.api.RoomDetailBean;
import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.intent.IntentWrap;
import cn.rongcloud.roomkit.provider.VoiceRoomProvider;
import cn.rongcloud.roomkit.ui.OnItemClickRoomListListener;
import cn.rongcloud.roomkit.ui.RoomType;
import cn.rongcloud.roomkit.ui.miniroom.MiniRoomManager;
import cn.rongcloud.roomkit.ui.other.MainSearchActivity;
import cn.rongcloud.roomkit.ui.other.MySxActivity;
import cn.rongcloud.roomkit.ui.other.PHActivity;
import cn.rongcloud.roomkit.widget.InputPasswordDialog;
import io.rong.imkit.picture.tools.ToastUtils;

/**
 * @author gyn
 * @date 2021/9/15
 */
public abstract class AbsRoomListFragment extends BaseMvpFragment
        implements OnItemClickRoomListListener<RoomDetailBean>{

    private RoomListAdapter mAdapter;
    private RecyclerView mRoomList;
    private CreateRoomDialog mCreateRoomDialog;
    private VRCenterDialog confirmDialog;
    private SmartRefreshLayout refreshLayout;
    private View emptyView;
    private InputPasswordDialog inputPasswordDialog;

    private ActivityResultLauncher mLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result != null
                                && result.getData() != null
                                && result.getData().getData() != null
                                && mCreateRoomDialog != null) {
                            mCreateRoomDialog.setCoverUri(result.getData().getData());
                        }
                    });

    @Override
    public void init() {
        ArrayList arrayList = new ArrayList(3);
        Banner banner = getView(R.id.banner);
        banner.addBannerLifecycleObserver(requireActivity())
                .setAdapter(new BannerImageAdapter<Integer>(arrayList) {
                    @Override
                    public void onBindView(BannerImageHolder holder, Integer data, int position, int size) {
                        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        holder.imageView.setImageResource(com.lalifa.base.R.mipmap.banner1);
                    }
                })
                .setIndicator(new CircleIndicator(getContext()));
        ImageView search = getView(R.id.search);
        ImageView ph = getView(R.id.ph);
        ImageView money = getView(R.id.money);
        search.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(getActivity(), MainSearchActivity.class);
            startActivity(intent);
        });
        ph.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(getActivity(), PHActivity.class);
            startActivity(intent);
        });
        money.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(getActivity(), MySxActivity.class);
            startActivity(intent);
        });

        mRoomList = getView(R.id.xrv_room);
        refreshLayout = getView(R.id.layout_refresh);
        emptyView = getView(R.id.layout_empty);
        mAdapter = new RoomListAdapter(getContext(), R.layout.item_room);
        mAdapter.setOnItemClickListener(this);
        mRoomList.setAdapter(mAdapter);
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            loadRoomList(true);
        });
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            loadRoomList(false);
        });
        emptyView.setOnClickListener(v -> {
            loadRoomList(true);
        });
        checkUserRoom();

    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_room_list;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRoomList(true);
    }

    public abstract RoomType getRoomType();

    /**
     * 请求房间列表数据
     *
     * @param isRefresh 是否是刷新，否则是加载更多
     */
    private void loadRoomList(boolean isRefresh) {
        if (isRefresh) {
            refreshLayout.resetNoMoreData();
        }
        VoiceRoomProvider.provider()
                .loadPage(
                        isRefresh,
                        getRoomType(),
                        voiceRoomBeans -> {
                            mAdapter.setData(voiceRoomBeans, isRefresh);

                            if (VoiceRoomProvider.provider().getPage() <= 2) {
                                refreshLayout.finishRefresh();
                            } else {
                                refreshLayout.finishLoadMore();
                            }

                            if (voiceRoomBeans != null && !voiceRoomBeans.isEmpty()) {
                                emptyView.setVisibility(View.GONE);
                            } else {
                                refreshLayout.setNoMoreData(true);
                                if (VoiceRoomProvider.provider().getPage() == 1) {
                                    emptyView.setVisibility(View.VISIBLE);
                                }
                            }
                        });
    }


    public void onCreateSuccess(RoomDetailBean voiceRoomBean) {
        mAdapter.getData().add(0, voiceRoomBean);
        mAdapter.notifyItemInserted(0);
        clickItem(voiceRoomBean, 0, true, Arrays.asList(voiceRoomBean));
    }

    public void onCreateExist(RoomDetailBean voiceRoomBean) {
        confirmDialog = new VRCenterDialog(requireActivity(), null);
        confirmDialog.replaceContent(getString(R.string.text_you_have_created_room), getString(R.string.cancel), null,
                getString(R.string.confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        jumpRoom(voiceRoomBean);
                    }
                }, null);
        confirmDialog.show();
    }

    private void createRoom() {
        //todo 222
//        showLoading("");
//        // 创建之前检查是否已有创建的房间
//        OkApi.put(
//                VRApi.ROOM_CREATE_CHECK,
//                null,
//                new WrapperCallBack() {
//                    @Override
//                    public void onResult(Wrapper result) {
//                        dismissLoading();
//                        if (result.ok()) {
//                            showCreateRoomDialog();
//                        } else if (result.getCode() == 30016) {
//                            RoomDetailBean voiceRoomBean = result.get(RoomDetailBean.class);
//                            if (voiceRoomBean != null) {
//                                onCreateExist(voiceRoomBean);
//                            } else {
//                                showCreateRoomDialog();
//                            }
//                        }
//                    }
//                });
    }

    /**
     * 展示创建房间弹窗
     */
    private void showCreateRoomDialog() {
        if (getRoomType() != RoomType.LIVE_ROOM) {
            mCreateRoomDialog =
                    new CreateRoomDialog(
                            requireActivity(),
                            mLauncher,
                            null);
            mCreateRoomDialog.show();
        } else {
            //如果是直播房，是直接进入直播间界面的
            ArrayList list = new ArrayList();
            list.add("-1");
            launchRoomActivity("", list, 0, true);
        }
    }

    @Override
    public boolean onLongClickItem(RoomDetailBean item, int position, boolean isCreate, List<RoomDetailBean> list) {
        return true;
    }

    @Override
    public void clickItem(
            RoomDetailBean item,
            int position,
            boolean isCreate,
            List<RoomDetailBean> voiceRoomBeans) {
        if (TextUtils.equals(item.getUserInfo().getUserId(), UserManager.get().getUserId())) {
            ArrayList list = new ArrayList();
            list.add(item.getChatroom_id());
            launchRoomActivity(item.getChatroom_id(), list, 0, isCreate);
        } else if (item.getPassword_type()==1) {
            inputPasswordDialog =
                    new InputPasswordDialog(requireContext(), false, new InputPasswordDialog.OnClickListener() {
                        @Override
                        public void clickCancel() {

                        }

                        @Override
                        public void clickConfirm(String password) {
                            if (TextUtils.isEmpty(password)) {
                                return;
                            }
                            if (password.length() < 4) {
                                ToastUtils.s(requireContext(), requireContext().getString(R.string.text_please_input_four_number));
                                return;
                            }
                            if (TextUtils.equals(password, item.getPassword())) {
                                inputPasswordDialog.dismiss();
                                ArrayList list = new ArrayList();
                                list.add(item.getChatroom_id());
                                launchRoomActivity(item.getChatroom_id(), list, 0, false);
                            } else {
                                showToast("密码错误");
                            }
                        }
                    });
            inputPasswordDialog.show();
        } else {
            ArrayList<String> list = new ArrayList<>();
            for (RoomDetailBean voiceRoomBean : voiceRoomBeans) {
                if (!voiceRoomBean.getUserInfo().getUserId().equals(UserManager.get().getUserId())
                        && voiceRoomBean.getPassword_type()==0) {
                    // 过滤掉上锁的房间和自己创建的房间
                    list.add(voiceRoomBean.getChatroom_id());
                }
            }
            int p = list.indexOf(item.getChatroom_id());
            if (p < 0) p = 0;
            launchRoomActivity(item.getChatroom_id(), list, p, false);
        }
    }

    public void launchRoomActivity(
            String roomId, ArrayList<String> roomIds, int position, boolean isCreate) {
        // 如果在其他房间有悬浮窗，先关闭再跳转
        MiniRoomManager.getInstance().finish(roomId, () -> {
            IntentWrap.launchRoom(requireContext(),
                    roomIds, position, isCreate);
        });
    }

    /**
     * 检查用户之前是否在某个房间内
     */
    private void checkUserRoom() {
        if (MiniRoomManager.getInstance().isShowing()) {
            // 如果有小窗口存在的情况下，不显示
            return;
        }

        //todo 222
//        Map<String, Object> params = new HashMap<>(2);
//        OkApi.get(VRApi.USER_ROOM_CHECK, params, new WrapperCallBack() {
//
//            @Override
//            public void onResult(Wrapper result) {
//                if (result.ok()) {
//                    RoomDetailBean voiceRoomBean = result.get(RoomDetailBean.class);
//                    if (voiceRoomBean != null) {
//                        // 说明已经在房间内了，那么给弹窗
//                        confirmDialog = new VRCenterDialog(requireActivity(), null);
//                        confirmDialog.replaceContent("您正在直播的房间中\n是否返回？", getString(R.string.cancel), new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        changeUserRoom();
//                                    }
//                                },
//                                getString(R.string.confirm), new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        jumpRoom(voiceRoomBean);
//                                    }
//                                }, null);
//                        confirmDialog.show();
//                    }
//                }
//            }
//        });
    }

    /**
     * 跳转到相应的房间
     *
     * @param voiceRoomBean
     */
    private void jumpRoom(RoomDetailBean voiceRoomBean) {
   //     IntentWrap.launchRoom(requireContext(), voiceRoomBean.getRoomType(), voiceRoomBean.getRoomId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VoiceRoomProvider.provider().clear();
    }

    // 更改所属房间
    private void changeUserRoom() {
        //todo 222
//        HashMap<String, Object> params = new OkParams().add("roomId", "").build();
//        OkApi.get(VRApi.USER_ROOM_CHANGE, params, new WrapperCallBack() {
//            @Override
//            public void onResult(Wrapper result) {
//            }
//        });
    }
}

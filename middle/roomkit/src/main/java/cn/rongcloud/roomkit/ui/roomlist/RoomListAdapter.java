package cn.rongcloud.roomkit.ui.roomlist;

import android.content.Context;

import com.jakewharton.rxbinding4.view.RxView;
import com.lalifa.adapter.adapter.RcyHolder;
import com.lalifa.adapter.adapter.RcySAdapter;
import com.lalifa.ext.Config;
import com.lalifa.utils.ImageLoader;

import java.util.concurrent.TimeUnit;

import cn.rongcloud.config.api.RoomDetailBean;
import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.ui.OnItemClickRoomListListener;
import io.reactivex.rxjava3.functions.Consumer;
import kotlin.Unit;

/**
 * @author gyn
 * @date 2021/9/15
 */
public class RoomListAdapter extends RcySAdapter<RoomDetailBean, RcyHolder> {

    private OnItemClickRoomListListener<RoomDetailBean> mOnItemClickListener;

    public RoomListAdapter(Context context, int itemLayoutId) {
        super(context, itemLayoutId);
    }

    public void setOnItemClickListener(OnItemClickRoomListListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public void convert(RcyHolder holder, RoomDetailBean voiceRoomBean, int position) {
        ImageLoader.loadUrl(holder.getView(R.id.iv_room_cover), Config.FILE_PATH+voiceRoomBean.getBackground(), R.drawable.img_default_room_cover);
        ImageLoader.loadUrl(holder.getView(R.id.iv_room_creator), voiceRoomBean.getUserInfo().getPortraitUrl(), R.drawable.default_portrait);
        holder.setText(R.id.tv_room_name, voiceRoomBean.getTitle());
        holder.setText(R.id.tv_room_creator_name, voiceRoomBean.component1().getUserName());
        //todo 房间人数
       // holder.setText(R.id.tv_room_people_number, String.valueOf(voiceRoomBean.getUserTotal()));
        holder.setVisible(R.id.iv_room_locked, voiceRoomBean.getPassword_type()==1);
        RxView.clicks(holder.itemView).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Throwable {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.clickItem(voiceRoomBean, position, false, getData());
                        }
                    }
                });
        if (mOnItemClickListener != null) {
            holder.itemView.setOnLongClickListener(v -> mOnItemClickListener.onLongClickItem(voiceRoomBean, position, false, getData()));
        }
    }
}

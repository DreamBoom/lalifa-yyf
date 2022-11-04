package cn.rongcloud.roomkit.ui.room.fragment;

import android.text.TextUtils;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.lalifa.adapter.adapter.RcyHolder;
import com.lalifa.adapter.adapter.RcySAdapter;
import com.lalifa.utils.ImageLoader;
import com.lalifa.utils.UiUtils;
import com.lalifa.widget.dialog.dialog.BaseBottomSheetDialog;

import java.util.Locale;

import cn.rongcloud.roomkit.R;
import cn.rongcloud.roomkit.manager.LocalDataManager;
import cn.rongcloud.roomkit.widget.GridSpacingItemDecoration;


/**
 * @author gyn
 * @date 2021/10/8
 */
public class BackgroundSettingFragment extends BaseBottomSheetDialog {

    private AppCompatTextView mTvConfirm;
    private RecyclerView mRvBackgroundList;
    private String currentBg;
    private OnSelectBackgroundListener listener;

    public BackgroundSettingFragment(String currentBg, OnSelectBackgroundListener listener) {
        super(R.layout.fragment_background_setting);
        this.currentBg = currentBg;
        this.listener = listener;
    }

    @Override
    public void initView() {
        mTvConfirm = (AppCompatTextView) getView().findViewById(R.id.tv_confirm);
        mRvBackgroundList = (RecyclerView) getView().findViewById(R.id.rv_background_list);
        RcySAdapter adapter = new RcySAdapter<String, RcyHolder>(getContext(), R.layout.item_backround) {
            @Override
            public void convert(RcyHolder holder, String s, int position) {
                ImageLoader.loadUrl(holder.getView(R.id.iv_background), s, 0);
                holder.setVisible(R.id.tv_is_gif, s.toLowerCase(Locale.ROOT).endsWith("gif"));
                holder.setChecked(R.id.cb_is_selected, TextUtils.equals(currentBg, s));
                holder.itemView.setOnClickListener(v -> {
                    currentBg = s;
                    notifyDataSetChanged();
                });
            }
        };
        mRvBackgroundList.setAdapter(adapter);

        mRvBackgroundList.addItemDecoration(new GridSpacingItemDecoration(((GridLayoutManager) mRvBackgroundList.getLayoutManager()).getSpanCount(),
                UiUtils.dp2px(15), true));
        adapter.setData(LocalDataManager.getBackGroundUrlList(), true);
        mTvConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.selectBackground(currentBg);
                dismiss();
            }
        });
    }

    public interface OnSelectBackgroundListener {
        void selectBackground(String url);
    }
}

package cn.rongcloud.roomkit.provider;

import com.lalifa.wapper.IResultBack;

import java.util.List;

import cn.rongcloud.roomkit.ui.RoomType;


/**
 * 页面列表数据¬
 *
 * @param <T>
 */
public interface IListProvider<T> {
    int PAGE_SIZE = 10;

    /**
     * load by page
     *
     * @param isRefresh
     * @param roomType
     */
    void loadPage(boolean isRefresh, RoomType roomType);
}

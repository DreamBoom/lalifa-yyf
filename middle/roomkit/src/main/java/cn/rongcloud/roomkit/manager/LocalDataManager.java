package cn.rongcloud.roomkit.manager;

import com.lalifa.utils.GsonUtil;
import com.lalifa.utils.SPUtil;

import java.util.List;

/**
 * @author gyn
 * @date 2022/2/15
 */
public class LocalDataManager {
    private final static String ROOM_BG = "ROOM_BG";

    public static List<String> getBackGroundUrlList() {
        String json = SPUtil.get(ROOM_BG);
        return GsonUtil.json2List(json, String.class);
    }

    public static void saveBackGroundUrl(List<String> list) {
        if (list != null) {
            SPUtil.set(ROOM_BG, GsonUtil.obj2Json(list));
        }
    }

    public static String getBackgroundByIndex(int index) {
        List<String> backGroundUrlList = getBackGroundUrlList();
        if (index < 0 || backGroundUrlList == null || index >= backGroundUrlList.size()) {
            return null;
        } else {
            return backGroundUrlList.get(index);
        }
    }
}

package cn.rongcloud.config;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.lalifa.ui.IBasis;
import com.lalifa.ui.UIStack;
import com.lalifa.utils.ObjToSP;

import java.util.List;

import cn.rongcloud.config.provider.user.User;
import cn.rongcloud.config.provider.user.UserProvider;
import cn.rongcloud.config.router.RouterPath;
import io.rong.imkit.RongIM;

/**
 * @author: BaiCQ
 * @ClassName: UserManager
 * @Description: 信息的缓存辅助类
 */
public class UserManager extends ObjToSP<User> {
    private final static UserManager _manager = new UserManager();

    private UserManager() {
        super("SP_USER");
    }

    private User current;

    @Nullable
    public static User get() {
        return _manager.getUser();
    }

    public static void save(@NonNull User user) {
        _manager.saveUser(user);
        UserProvider.provider().update(user.toUserInfo());
    }

    public static void logout() {
        _manager.clear();
        //通知ui
        List<IBasis> iBasess = UIStack.getInstance().getIbasiss();
        for (IBasis b : iBasess) {
            b.onLogout();
        }
        RongIM.getInstance().disconnect();
        RongIM.getInstance().logout();
    }

    /**
     * 保存当前用户信息
     *
     * @param user User
     */
    private void saveUser(User user) {
        if (null != user) {
            current = user;
            super.saveEntity(TAG, current);
        }
    }

    /**
     * 获取最新用户的信息
     */
    private User getUser() {
        if (null != current) {
            return current;
        }
        User user = super.getEntity(TAG);
        if (null != user) {
            current = user;
        }
        return current;
    }

    private void clear() {
        current = null;
        super.deleteFast(TAG);
    }
}

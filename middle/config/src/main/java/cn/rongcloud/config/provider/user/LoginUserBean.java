package cn.rongcloud.config.provider.user;

import java.io.Serializable;

public class LoginUserBean implements Serializable {
    private User userinfo;

    public User getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(User userinfo) {
        this.userinfo = userinfo;
    }

    @Override
    public String toString() {
        return "LoginUserBean{" +
                "userinfo=" + userinfo +
                '}';
    }
}

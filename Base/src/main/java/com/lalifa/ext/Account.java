package com.lalifa.ext;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.Objects;

public class Account implements Serializable {
    String userId;
    String userName;
    String avatar;
    String imToken;

    public Account(String userId, String userName, String avatar, String imToken) {
        this.userId = userId;
        this.userName = userName;
        this.avatar = avatar;
        this.imToken = imToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getAvatar() {
        return TextUtils.isEmpty(avatar) ? "" : Config.FILE_PATH + avatar;
    }

    public String getImToken() {
        return imToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account accout = (Account) o;
        return Objects.equals(userId, accout.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}

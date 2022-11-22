package cn.rongcloud.config.provider.user;

import android.net.Uri;
import android.text.TextUtils;

import com.lalifa.ext.Config;

import java.io.Serializable;

import cn.rongcloud.config.ApiConfig;
import cn.rongcloud.config.provider.wrapper.Provide;
import io.rong.imlib.model.UserInfo;

/**
 * 通用用户信息
 */
public class User implements Serializable, Provide {
    private int id;//用户ID
    private String userName;//用户名
    private String nickname;//昵称
    private String mobile;//手机号
    private String avatar;//头像
    private int score;//账户余额
    private String imToken;//融云TOKEN
    private String userId;
    private String token;
    private String user_id;
    private String createtime;
    private Long expiretime;
    private Long expires_in;
    private int gender = 0;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", nickname='" + nickname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", avatar='" + avatar + '\'' +
                ", score=" + score +
                ", imToken='" + imToken + '\'' +
                ", userId='" + userId + '\'' +
                ", token='" + token + '\'' +
                ", user_id='" + user_id + '\'' +
                ", createtime='" + createtime + '\'' +
                ", expiretime=" + expiretime +
                ", expires_in=" + expires_in +
                ", gender=" + gender +
                '}';
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAuthorization() {
        return "";
    }

    public void setAuthorization(String authorization) {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getImToken() {
        return imToken;
    }

    public void setImToken(String imToken) {
        this.imToken = imToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public Long getExpiretime() {
        return expiretime;
    }

    public void setExpiretime(Long expiretime) {
        this.expiretime = expiretime;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Long expires_in) {
        this.expires_in = expires_in;
    }

    public String getPortraitUrl() {
        return avatar;
    }

    public Uri getPortraitUri() {
        return Uri.parse(avatar);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return !TextUtils.isEmpty(userId) && userId.equals(user.userId);
    }

    public UserInfo toUserInfo() {
        return new UserInfo(userId, userName, getPortraitUri());
    }

    @Override
    public String getKey() {
        return userId;
    }
}

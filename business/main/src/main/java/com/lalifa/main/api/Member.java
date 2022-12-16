package com.lalifa.main.api;

import android.net.Uri;
import android.text.TextUtils;
import com.lalifa.ext.User;
import java.io.Serializable;
import io.rong.imlib.model.UserInfo;

/**
 * @author gyn
 * @date 2021/10/9
 */
public class Member implements Serializable {
    private String userId;
    private String userName;
    private String avatar;
    private String level;
    private String frame;//头像框
    private String car;//座驾
    private String bubble;//气泡
    private String sound;//音波
    private int gender;
    // 是否是管理
    private int manageType = 0;
    // 是否已关注
    private int collectionType = 0;
    // 麦位
    private int seatIndex = -1;
    public  boolean isFollow = getCollectionType()==1;
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPortraitUrl() {
        return avatar;
    }

    public int getManageType() {
        return manageType;
    }

    public void setManageType(int manageType) {
        this.manageType = manageType;
    }

    public int getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(int collectionType) {
        this.collectionType = collectionType;
    }

    public int getSeatIndex() {
        return seatIndex;
    }

    public void setSeatIndex(int seatIndex) {
        this.seatIndex = seatIndex;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getBubble() {
        return bubble;
    }

    public void setBubble(String bubble) {
        this.bubble = bubble;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return !TextUtils.isEmpty(userId) && userId.equals(member.getUserId());
    }

    public UserInfo toUserInfo() {
        return new UserInfo(userId, userName, Uri.parse(getPortraitUrl()));
    }

    public User toUser() {
        User user = new User();
        user.setUserId(userId);
        user.setUserName(userName);
        user.setAvatar(avatar);
        return user;
    }

    public static Member fromUser(User user) {
        Member member = new Member();
        member.setUserId(user.getUserId());
        member.setUserName(user.getUserName());
        member.setAvatar(user.getAvatar());
        return member;
    }
}

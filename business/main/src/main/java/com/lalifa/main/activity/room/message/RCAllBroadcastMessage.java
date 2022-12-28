package com.lalifa.main.activity.room.message;

import android.os.Parcel;

import com.lalifa.utils.GsonUtil;

import java.nio.charset.StandardCharsets;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * @author gyn
 * @date 2021/10/18
 * 全服广播
 */
@MessageTag(value = "RC:RCGiftBroadcastMsg")
public class RCAllBroadcastMessage extends MessageContent {
    public static final Creator<RCAllBroadcastMessage> CREATOR = new Creator<RCAllBroadcastMessage>() {
        @Override
        public RCAllBroadcastMessage createFromParcel(Parcel source) {
            return new RCAllBroadcastMessage(source);
        }

        @Override
        public RCAllBroadcastMessage[] newArray(int size) {
            return new RCAllBroadcastMessage[size];
        }
    };
    private static final String TAG = "RCAllBroadcastMessage";
    private String userId;
    private String userName;
    private String info;
    private String roomId;
    private String roomType;
    private String isPrivate;

    public RCAllBroadcastMessage() {
    }

    public RCAllBroadcastMessage(byte[] data) {
        super(data);
        String jsonStr = new String(data, StandardCharsets.UTF_8);
        RCAllBroadcastMessage message = GsonUtil.json2Obj(jsonStr, RCAllBroadcastMessage.class);
        if (message != null) {
            this.userId = message.userId;
            this.userName = message.userName;
            this.info = message.info;
            this.roomId = message.roomId;
            this.roomType = message.roomType;
            this.isPrivate = message.isPrivate;
        }
    }

    protected RCAllBroadcastMessage(Parcel source) {
        setUserId(ParcelUtils.readFromParcel(source));
        setUserName(ParcelUtils.readFromParcel(source));
        setInfo(ParcelUtils.readFromParcel(source));
        setRoomId(ParcelUtils.readFromParcel(source));
        setRoomType(ParcelUtils.readFromParcel(source));
        setIsPrivate(ParcelUtils.readFromParcel(source));
    }

    @Override
    public byte[] encode() {
        return GsonUtil.obj2Json(this).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, userId);
        ParcelUtils.writeToParcel(dest, userName);
        ParcelUtils.writeToParcel(dest, info);
        ParcelUtils.writeToParcel(dest, roomId);
        ParcelUtils.writeToParcel(dest, roomType);
        ParcelUtils.writeToParcel(dest, isPrivate);
    }

    public void readFromParcel(Parcel source) {
        setUserId(ParcelUtils.readFromParcel(source));
        setUserName(ParcelUtils.readFromParcel(source));
        setInfo(ParcelUtils.readFromParcel(source));
        setRoomId(ParcelUtils.readFromParcel(source));
        setRoomType(ParcelUtils.readFromParcel(source));
        setIsPrivate(ParcelUtils.readFromParcel(source));
    }

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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(String isPrivate) {
        this.isPrivate = isPrivate;
    }

    @Override
    public String toString() {
        return GsonUtil.obj2Json(this);
    }
}

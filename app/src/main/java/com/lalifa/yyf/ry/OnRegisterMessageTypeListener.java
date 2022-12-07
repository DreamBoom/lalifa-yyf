package com.lalifa.yyf.ry;

import io.rong.imlib.model.Message;

public interface OnRegisterMessageTypeListener {
    void onRegisterMessageType();

    default void onReceivedMessage(Message message) {
    }
}
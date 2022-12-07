package com.lalifa.yyf.ry;

public interface IModule extends OnRegisterMessageTypeListener {
    void onInit();

    void onUnInit();

    @Override
    default void onRegisterMessageType() {
    }
}
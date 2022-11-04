package com.lalifa.oklib.api.callback;



import com.lalifa.oklib.api.OCallBack;

import okhttp3.Response;

public abstract class StringIOCallBack extends OCallBack<String> {
    @Override
    public String onParse(Response response) throws Exception {
        return response.body().string();
    }
}

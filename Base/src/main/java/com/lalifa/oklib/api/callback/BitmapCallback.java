package com.lalifa.oklib.api.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import com.lalifa.oklib.api.OCallBack;

import okhttp3.Response;

public abstract class BitmapCallback extends OCallBack<Bitmap> {
    @Override
    public Bitmap onParse(Response response) throws Exception {
        return BitmapFactory.decodeStream(response.body().byteStream());
    }

}
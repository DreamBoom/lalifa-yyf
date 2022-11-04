package com.lalifa.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

public class URLDrawable extends BitmapDrawable {
    public Bitmap bitmap;

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, getPaint());
        }
    }
}
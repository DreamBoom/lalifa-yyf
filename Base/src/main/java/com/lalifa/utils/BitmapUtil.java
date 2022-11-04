package com.lalifa.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public final class BitmapUtil {

    public static Bitmap compressImage(Bitmap image, int targetSize) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (os.toByteArray().length / 1024 > targetSize) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            os.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, os);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(os.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null);
    }

    /**
     * Creates a bitmap from the supplied view.
     *
     * @param view The view to get the bitmap.
     * @param width The width for the bitmap.
     * @param height The height for the bitmap.
     *
     * @return The bitmap from the supplied drawable.
     */
    public @NonNull
    static Bitmap createBitmapFromView(@NonNull View view, int width, int height) {
        if (width > 0 && height > 0) {
            // view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            //              View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
            view.measure(View.MeasureSpec.makeMeasureSpec(convertDpToPixels(width), View.MeasureSpec.EXACTLY),
                         View.MeasureSpec.makeMeasureSpec(convertDpToPixels(height), View.MeasureSpec.EXACTLY));
        }
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                                            view.getMeasuredHeight(), getConfig());
        Canvas canvas = new Canvas(bitmap);
        Drawable background = view.getBackground();
        if (background != null) {
            background.draw(canvas);
        }
        view.draw(canvas);
        return bitmap;
    }

    public @NonNull
    static Bitmap createBitmapFromViewWithPx(@NonNull View view, int width, int height) {
        if (width > 0 && height > 0) {
            view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                         View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        }
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                                            view.getMeasuredHeight(), getConfig());
        Canvas canvas = new Canvas(bitmap);
        Drawable background = view.getBackground();
        if (background != null) {
            background.draw(canvas);
        }
        view.draw(canvas);
        return bitmap;
    }

    private static Bitmap.Config getConfig() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Bitmap.Config.ARGB_8888;
        }
        return Bitmap.Config.RGB_565;
    }

    /**
     * Converts DP into pixels.
     *
     * @param dp The value in DP to be converted into pixels.
     *
     * @return The converted value in pixels.
     */
    private static int convertDpToPixels(float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                    dp, Resources.getSystem().getDisplayMetrics()));
    }
}

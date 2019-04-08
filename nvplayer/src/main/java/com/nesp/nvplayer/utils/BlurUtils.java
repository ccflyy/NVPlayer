package com.nesp.nvplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.hoko.blur.HokoBlur;
import com.hoko.blur.task.AsyncBlurTask;

import static com.nesp.nvplayer.utils.BitmapUtils.drawableToBitmap;


/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-1-24 下午4:47
 * @project FishMovie
 **/
public class BlurUtils {

    public static void BlurBitmap(Context context, Bitmap bitmap) {
        HokoBlur.with(context)
                .processor().blur(bitmap);
    }

    public static void BlurBitmap(Context context, Bitmap bitmap, int radius, float scale) {
        HokoBlur.with(context)
                .radius(radius)
                .sampleFactor(scale)
                .processor().blur(bitmap);
    }

    public static void BlurBitmap(Context context, Bitmap bitmap, int radius, float scale, AsyncBlurTask.Callback callback) {
        HokoBlur.with(context)
                .radius(radius)
                .sampleFactor(scale)
                .processor()
                .asyncBlur(bitmap, callback);
    }

    public static void BlurDrawable(Context context, Drawable drawable) {
        BlurBitmap(context, drawableToBitmap(drawable));
    }

    public static void BlurDrawable(Context context, Drawable drawable, int radius, float scale) {
        BlurBitmap(context, drawableToBitmap(drawable), radius, scale);
    }

    public static void BlurDrawable(Context context, Drawable drawable, int radius, float scale, AsyncBlurTask.Callback callback) {
        BlurBitmap(context, drawableToBitmap(drawable), radius, scale, callback);
    }

    public static void BlurView(Context context, View view) {
        HokoBlur.with(context)
                .processor().blur(view);
    }

    public static void BlurView(Context context, View view, int radius, float scale) {
        HokoBlur.with(context)
                .radius(radius)
                .sampleFactor(scale)
                .processor().blur(view);
    }
}

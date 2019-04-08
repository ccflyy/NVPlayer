package com.nesp.nvplayer.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import com.nesp.nvplayer.R;

public class NVPlayerVolume extends View {

    private int volumeColor = Color.RED;

    private int height;
    private int width;

    public NVPlayerVolume(Context context) {
        super(context);
        init(null, 0);
    }

    public NVPlayerVolume(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public NVPlayerVolume(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.NVPlayerVolume, defStyle, 0);

        volumeColor = a.getColor(R.styleable.NVPlayerVolume_volumeColor, volumeColor);

        a.recycle();
    }


    /**
     * 计算子view的宽和高，以及设置自己的宽和高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = widthMeasureSpec;
        height = heightMeasureSpec;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(volumeColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeWidth(1);
        canvas.drawLine(0, (float) height / 3, 0, (float) 2 * height / 3, paint);
        canvas.drawLine(0, (float) height / 3, (float) width / 3, (float) height / 3, paint);
        canvas.drawLine((float) width / 3, (float) height / 3, width, 0, paint);
        canvas.drawLine(width, 0, width, height, paint);
        canvas.drawLine(width, height, (float) width / 3, (float) 2 * height / 3, paint);
        canvas.drawLine((float) width / 3, (float) 2 * height / 3, 0, (float) 2 * height / 3, paint);
    }


    //通过设置偏移量使menu隐藏

    /**
     * 决定子View的布局
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


    public int getVolumeColor() {
        return volumeColor;
    }


    public void setVolumeColor(int exampleColor) {
        volumeColor = exampleColor;
        invalidate();
    }

}

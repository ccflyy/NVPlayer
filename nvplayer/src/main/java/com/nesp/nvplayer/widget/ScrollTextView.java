package com.nesp.nvplayer.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-4-10 上午9:58
 * @project IQiYiPlayWidget
 **/
public class ScrollTextView extends View {

    private final Paint paint;
    private final float textStartX;
    private final Thread scrollThread;
    private String text = "广告千万条，安全第一条，轻信小广告，口袋两空空";
    private int height;
    private int width;
    private float textX;
    private double speed;
    private int loopCount = 1;
    private int loopCurrentCount = 0;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            invalidate();
        }
    };
    private boolean isDrawed = false;
    private boolean isMeasured = false;

    public ScrollTextView(Context context) {
        this(context, null);
    }

    public ScrollTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        float textWidth = paint.measureText(text);

        textX = textStartX = -4f * textWidth;

        scrollThread = new Thread(() -> {
            while (true) {
                if (isDrawed && isMeasured && (loopCurrentCount < loopCount)) {
                    try {
                        textX = textX + 5;
                        if (textX > width) {
                            ++loopCurrentCount;
                            textX = textStartX;
                        }
                        Thread.sleep(10);
                        handler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        isMeasured = true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        isDrawed = true;
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(16);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setTextSize(45);
        canvas.drawText(text, textX, height / 2f + 1.5f * paint.getFontMetrics().bottom, paint);
    }

    public void startScroll() {
        if (scrollThread.isAlive()){
            return;
        }
        scrollThread.start();
    }

    public void stopScroll() {
        scrollThread.interrupt();
    }


}

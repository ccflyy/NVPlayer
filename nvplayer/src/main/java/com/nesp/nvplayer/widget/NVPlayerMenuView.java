/*
 *
 *   Copyright (c) 2019  NESP Technology Corporation. All rights reserved.
 *
 *   This program is free software; you can redistribute it and/or modify it
 *   under the terms and conditions of the GNU General Public License,
 *   version 2, as published by the Free Software Foundation.
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License.See the License for the specific language governing permission and
 *   limitations under the License.
 *
 *   If you have any questions or if you find a bug,
 *   please contact the author by email or ask for Issues.
 *
 *   Author:JinZhaolu <1756404649@qq.com>
 */

package com.nesp.nvplayer.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.nesp.nvplayer.R;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-4-8 下午4:15
 * @project IQiYiPlayWidget
 **/
public class NVPlayerMenuView extends View {

    private static final String TAG = "MenuView";
    private int pointPressColor;
    private float pointRidus;
    private int pointColor;

    private int width, height;
    private int paintColor;

    public NVPlayerMenuView(Context context) {
        this(context, null);
    }

    public NVPlayerMenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NVPlayerMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);

    }

    public NVPlayerMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.NVPlayerMenuView, defStyleAttr, 0);
        pointColor = typedArray.getColor(R.styleable.NVPlayerMenuView_pointColor, Color.WHITE);
        pointPressColor = typedArray.getColor(R.styleable.NVPlayerMenuView_pointColor, Color.BLUE);
        pointRidus = typedArray.getDimension(R.styleable.NVPlayerMenuView_pointRadius, 5);
        paintColor = pointColor;
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(paintColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setDither(true);

        canvas.drawCircle(pointRidus, (float) (height * 0.5), pointRidus, paint);
        canvas.drawCircle((float) (width * 0.5), (float) (height * 0.5), pointRidus, paint);
        canvas.drawCircle(width - pointRidus, (float) (height * 0.5), pointRidus, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                paintColor = pointPressColor;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                paintColor = pointColor;
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

}

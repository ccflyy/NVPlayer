/*
 *
 *  Copyright (c) 2018  NESP Technology Corporation. All rights reserved.
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms and conditions of the GNU General Public License,
 *  version 2, as published by the Free Software Foundation.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License.See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  If you have any questions or if you find a bug,
 *  please contact the author by email or ask for Issues.
 *
 *  Author:JinZhaolu <1756404649@qq.com>
 *
 */

package com.nesp.nvplayer.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.*;
import android.os.BatteryManager;
import android.util.AttributeSet;
import android.view.View;
import com.nesp.nvplayer.R;


/**
 * @Team: NESP Technology
 * @Author: 靳兆鲁
 * Email: 1756404649@qq.com
 * @Time: Created 2018/11/28 8:55
 * @Project software_android_assistant
 **/
public class NVPlayerBatteryView extends View {

    private Matrix matrixLighting;
    private Bitmap bitmapLighting;
    private int height, width, lightingWidth, lightingHeight;
    private int mColor;
    private int orientation;
    private int mPower;
    private boolean isCharging = false;
    private float offsetElectricity;
    private float fullPowerWidth;
    private float strokeWidth;
    private Bitmap bitmapNewLighting;
    private boolean chargingAnimorEnable = false;

    public NVPlayerBatteryView(Context context) {
        this(context, null);
    }

    public NVPlayerBatteryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NVPlayerBatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NVPlayerBatteryView);
        mColor = typedArray.getColor(R.styleable.NVPlayerBatteryView_NVPlayerBatteryColor, 0xFFFFFFFF);
        orientation = typedArray.getInt(R.styleable.NVPlayerBatteryView_NVPlayerBatteryOrientation, 0);
        mPower = typedArray.getInt(R.styleable.NVPlayerBatteryView_NVPlayerBatteryPower, 100);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(new PowerReceiver(), filter);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //对View上的內容进行测量后得到的View內容占据的宽度
        width = getMeasuredWidth();
        //对View上的內容进行测量后得到的View內容占据的高度
        height = getMeasuredHeight();
        strokeWidth = width / 20.f;

        matrixLighting = new Matrix();
        bitmapLighting = BitmapFactory.decodeResource(getResources(), R.drawable.ic_lighting);
        lightingWidth = bitmapLighting.getWidth();
        lightingHeight = bitmapLighting.getHeight();
        matrixLighting.postScale(2 * height / (3f * lightingHeight), 2 * height / (3f * lightingHeight));
        bitmapNewLighting = Bitmap.createBitmap(bitmapLighting, 0, 0, lightingWidth, lightingHeight, matrixLighting, true);
        fullPowerWidth = width - strokeWidth * 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //判断电池方向    horizontal: 0   vertical: 1
        if (orientation == 0) {
            drawHorizontalBattery(canvas);
        } else {
            drawVerticalBattery(canvas);
        }
    }

    /**
     * 绘制水平电池
     *
     * @param canvas
     */
    private void drawHorizontalBattery(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.STROKE);

        float strokeWidth_2 = strokeWidth / 2;
        paint.setStrokeWidth(strokeWidth);
        RectF r1 = new RectF(strokeWidth_2, strokeWidth_2, width - strokeWidth - strokeWidth_2, height - strokeWidth_2);
        //设置外边框颜色为黑色
        paint.setColor(Color.WHITE);
        canvas.drawRect(r1, paint);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);
        //画电池内矩形电量
        if (!chargingAnimorEnable)
        offsetElectricity = (width - strokeWidth * 2) * mPower / 100.f;
        RectF r2 = new RectF(strokeWidth, strokeWidth, offsetElectricity, height - strokeWidth);
        //根据电池电量决定电池内矩形电量颜色
        if (mPower < 30) {
            paint.setColor(Color.RED);
        }
        if (mPower >= 30 && mPower < 50) {
            paint.setColor(Color.BLUE);
        }
        if (mPower >= 50) {
            paint.setColor(Color.GREEN);
        }
        if (isCharging)
            paint.setColor(Color.GREEN);
        canvas.drawRect(r2, paint);
        //画电池头
        RectF r3 = new RectF(width - strokeWidth, height * 0.25f, width, height * 0.75f);
        //设置电池头颜色为黑色
        paint.setColor(Color.WHITE);
        canvas.drawRect(r3, paint);

//        绘制文字
        if (!isCharging) {
            paint.setColor(Color.WHITE);
            paint.setTextSize(width / 3f);
            canvas.drawText(String.valueOf(mPower), width / 2 - paint.measureText(String.valueOf(mPower)) / 2, height / 2 + 1.5f * paint.getFontMetrics().bottom, paint);
        } else {
            canvas.drawBitmap(bitmapNewLighting, (width - bitmapNewLighting.getWidth()) / 2.0f, (height - bitmapNewLighting.getHeight()) / 2.0f, paint);
            if (chargingAnimorEnable) {
                //充电动画
                if (offsetElectricity <= fullPowerWidth - strokeWidth) {
                    offsetElectricity = offsetElectricity + fullPowerWidth / 10;
                } else {
                    offsetElectricity = (width - strokeWidth * 2) * mPower / 100.f;
                }
            }
        }
    }

    /**
     * 绘制垂直电池
     *
     * @param canvas
     */
    private void drawVerticalBattery(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(mColor);
        paint.setStyle(Paint.Style.STROKE);
        float strokeWidth = height / 20.0f;
        float strokeWidth2 = strokeWidth / 2;
        paint.setStrokeWidth(strokeWidth);
        int headHeight = (int) (strokeWidth + 0.5f);
        RectF rect = new RectF(strokeWidth2, headHeight + strokeWidth2, width - strokeWidth2, height - strokeWidth2);
        canvas.drawRect(rect, paint);
        paint.setStrokeWidth(0);
        float topOffset = (height - headHeight - strokeWidth) * (100 - mPower) / 100.0f;
        RectF rect2 = new RectF(strokeWidth, headHeight + strokeWidth + topOffset, width - strokeWidth, height - strokeWidth);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rect2, paint);
        RectF headRect = new RectF(width / 4.0f, 0, width * 0.75f, headHeight);
        canvas.drawRect(headRect, paint);
    }

    /**
     * 设置电池电量
     *
     * @param power
     */
    public void setPower(int power, Boolean isCharging) {
        this.isCharging = isCharging;
        this.mPower = power;
        if (mPower < 0) {
            mPower = 100;
        }
        invalidate();//刷新VIEW
    }


    /**
     * 设置电池颜色
     *
     * @param color
     */
    public void setColor(int color) {
        this.mColor = color;
        invalidate();
    }

    /**
     * 获取电池电量
     *
     * @return
     */
    public int getPower() {
        return mPower;
    }

    private static final String TAG = "BatteryView";

    private class PowerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    setPower(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1), true);
                } else {
                    setPower(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1), false);
                }
            } else {
                setPower(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1), false);
            }
        }
    }
}

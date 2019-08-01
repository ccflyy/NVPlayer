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

package com.nesp.nvplayer.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * @Team : NESP Technology
 * @Author : nesp
 * Email : 1756404649@qq.com
 * @Time : 18-4-5 下午2:19
 */
public class WindowUtils {

    /**************************************************************************
     * 设置EditText自动获取焦点并弹出键盘
     * @param editText editText
     *
     *try {
     *editTextModifyDpi.setSelection(editTextModifyDpi.getText().length());
     *Timer timer = new Timer();
     *timer.schedule(new TimerTask() {
     *@Override
     *public void run() {
     *WindowUtils.autoShowSoftInputFromWindow(editTextModifyDpi);
     *}
     *}, 200);
     *} catch (Exception e) {
     *e.printStackTrace();
     *}
     *
    /******************************************************************************/
    public static void autoShowSoftInputFromWindow(EditText editText) {
        if (editText != null) {
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.requestFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(editText, 0);
        }
    }

    public static void autoHideSoftInputFromWindow(EditText editText) {
        if (editText != null) {
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
            editText.clearFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }

    /******************
     * 设置隐藏标题栏
     *
     * @param activity
     *****************/
    public static void setNoTitleBar(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
    }


    /**
     * 设置全屏
     *
     * @param window
     */
    public static void setFullScreen(Window window) {
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 取消全屏
     *
     * @param window
     */
    public static void clearFullScreen(Window window) {
        window.clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    public static void hideBottomVirtualKey(Activity activity) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public static void showBottomVirtualKey(Activity activity) {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.VISIBLE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void setWindowStatusBarColor(Activity activity, int colorResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(colorResId));

            //底部导航栏
            //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
        }

    }

    public static void setWindowStatusBarColor(Dialog dialog, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = dialog.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(dialog.getContext().getResources().getColor(colorResId));

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static float getActionBarHeight(Context context) {
        float actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
//            //方法一
//            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
//
//            //方法二
//            int[] attribute = new int[] { android.R.attr.actionBarSize };
//            TypedArray array = context.obtainStyledAttributes(tv.resourceId, attribute);
//            int actionBarHeight1 = array.getDimensionPixelSize(0 /* index */, -1 /* default size */);
//            array.recycle();

            //方法三
            TypedArray actionbarSizeTypedArray = context.obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
            actionBarHeight = actionbarSizeTypedArray.getDimension(0, 0);
            actionbarSizeTypedArray.recycle();
        }
        return actionBarHeight;
    }


}
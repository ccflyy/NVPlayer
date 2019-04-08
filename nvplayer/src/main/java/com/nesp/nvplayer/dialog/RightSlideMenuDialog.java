package com.nesp.nvplayer.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.nesp.nvplayer.R;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-4-6 下午2:12
 * @project NVPlayerDemo
 **/
public class RightSlideMenuDialog extends Dialog {

    private static final String TAG = "RightSlideMenuDialog";

    private int gravity = Gravity.END;
    private View contentView;
    private Context context;

    public RightSlideMenuDialog(@NonNull Context context, View contentView) {
        this(context, R.style.nvplayer_dialog);
        this.contentView = contentView;
        this.context = context;
    }

    public RightSlideMenuDialog(@NonNull Context context, View contentView, int gravity) {
        this(context, R.style.nvplayer_dialog);
        this.gravity = gravity;
        this.contentView = contentView;
        this.context = context;
    }

    public RightSlideMenuDialog(@NonNull Context context, int themeResIdView, View contentView) {
        this(context, themeResIdView);
        this.contentView = contentView;
        this.context = context;
    }

    public RightSlideMenuDialog(@NonNull Context context, int themeResIdView, int gravity, View contentView) {
        this(context, themeResIdView);
        this.gravity = gravity;
        this.contentView = contentView;
        this.context = context;
    }

    public RightSlideMenuDialog(@NonNull Context context) {
        this(context, R.style.nvplayer_dialog);
        this.context = context;
    }


    private RightSlideMenuDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected RightSlideMenuDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
//        window.getDecorView().setPadding(0,-200,0,0);
        window.setGravity(gravity);
        if (gravity == Gravity.END)
            window.setWindowAnimations(R.style.nesp_nvplayer_right_dialog_animation);
        else
            window.setWindowAnimations(R.style.nesp_nvplayer_center_dialog_animation);

        if (contentView != null) setContentView(contentView);
        setWidth(1000);
        setCancelable(true);
    }

    public void offset(int left, int top, int right, int bottom) {
        getWindow().getDecorView().setPadding(left,top,right,bottom);
    }

    public void setWidth(int width) {
        Window window = getWindow();
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        layoutParams.width = width;
        layoutParams.height = displayMetrics.heightPixels;
        window.setAttributes(layoutParams);

    }

    public void setFullSize() {
        Window window = getWindow();
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        layoutParams.width = displayMetrics.widthPixels;
        layoutParams.height = displayMetrics.heightPixels;
        window.setAttributes(layoutParams);

    }

    public void setSize(int width, int height) {
        Window window = getWindow();
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int maxWidth = (int) (displayMetrics.widthPixels * 0.6);
        int maxHeight = (int) (displayMetrics.heightPixels * 0.8);
        layoutParams.width = width > maxWidth ? maxWidth : width;
        layoutParams.height = height > maxHeight ? maxHeight : height;
        window.setAttributes(layoutParams);

    }

    public void setSizePercent(double widthPercent, double heightPercent) {
        Window window = getWindow();
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        layoutParams.width = (int) (displayMetrics.widthPixels * widthPercent / 100);
        layoutParams.height = (int) (displayMetrics.heightPixels * heightPercent / 100);

        window.setAttributes(layoutParams);

    }

}
